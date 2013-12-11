package com.alma42.mapgen.zone.types.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Random;

import com.alma42.mapgen.island_shape.IIslandShape;
import com.alma42.mapgen.river.IRiverCreator;
import com.alma42.mapgen.utils.geometry.Center;
import com.alma42.mapgen.utils.geometry.Corner;
import com.alma42.mapgen.utils.geometry.Edge;
import com.alma42.mapgen.utils.geometry.Point;
import com.alma42.mapgen.utils.geometry.Rectangle;
import com.alma42.mapgen.utils.voronoi.LineSegment;
import com.alma42.mapgen.utils.voronoi.Voronoi;
import com.alma42.mapgen.zone.Zone;
import com.alma42.mapgen.zone.selectors.IZoneSelector;
import com.alma42.mapgen.zone.types.IZoneType;

public class Island implements IZoneType {

  /**
   * 0 to 1, fraction of water corners for water polygon
   */
  public static final double LAKE_THRESHOLD = 0.3;

  private Zone               zone;
  private int                size;
  private int                zoneNumber;
  private Random             seed;
  private IIslandShape       islandShape;
  private IRiverCreator      riverCreator;
  private IZoneSelector      zoneSelector;
  final public Rectangle     bounds;

  @Override
  public void createZone() {
    ArrayList<Zone> zones;
    // Generate the initial random set of zones
    System.out.println("Generate Zones ...");
    zones = zoneSelector.generateZones(size, zoneNumber);

    // Create a graph structure from the Voronoi edge list. The
    // methods in the Voronoi object are somewhat inconvenient for
    // my needs, so I transform that data into the data I actually
    // need: edges connected to the Delaunay triangles and the
    // Voronoi polygons, a reverse map from those four points back
    // to the edge, a map from these four points to the points
    // they connect to (both along the edge and crosswise).
    System.out.println("Build Graph ...");
    // Voronoi voronoi = new Voronoi(points, null, new Rectangle(0, 0, SIZE, SIZE));
    buildGraph(zones, voronoi);
    improveCorners();
    voronoi.dispose();
    voronoi = null;
    zones = null;

    System.out.println("Assign elevations...");
    // Determine the elevations and water at Voronoi corners.
    assignCornerElevations();

    // Determine polygon and corner type: ocean, coast, land.
    assignOceanCoastAndLand();

    // Rescale elevations so that the highest is 1.0, and they're
    // distributed well. We want lower elevations to be more common
    // than higher elevations, in proportions approximately matching
    // concentric rings. That is, the lowest elevation is the
    // largest ring around the island, and therefore should more
    // land area than the highest elevation, which is the very
    // center of a perfectly circular island.
    redistributeElevations(landCorners(this.zone.getCorners()));

    // Assign elevations to non-land corners
    for (Corner q : this.zone.getCorners()) {
      if (q.ocean || q.coast) {
        q.elevation = 0.0;
      }
    }

    // Polygon elevations are the average of their corners
    assignElevations();

    System.out.println("Assign moisture ...");
    // Determine downslope paths.
    calculateDownslopes();

    // Determine watersheds: for every corner, where does it flow
    // out into the ocean?
    calculateWatersheds();

    // Create rivers.
    createRivers();

    // Determine moisture at corners, starting at rivers
    // and lakes, but not oceans. Then redistribute
    // moisture to cover the entire range evenly from 0.0
    // to 1.0. Then assign polygon moisture as the average
    // of the corner moisture.
    assignCornerMoisture();
    redistributeMoisture(landCorners(this.zone.getCorners()));
    assignMoisture();

    System.out.println("Assign Biomes ...");
    this.zone.assignBiomes();
  }

  private void buildGraph(final Voronoi v) {
    final HashMap<Point, Center> pointCenterMap = new HashMap<Point, Center>();
    final ArrayList<Point> points = v.siteCoords();
    for (final Point p : points) {
      final Center c = new Center();
      final Zone newZone = new Zone(c, this.size);
      c.loc = p;
      c.index = this.zone.getZones().size();
      this.zone.addZone(newZone);
      pointCenterMap.put(p, c);
    }

    // bug fix
    for (final Zone z : this.zone.getZones()) {
      v.region(z.getCenter().loc);
    }

    final ArrayList<com.alma42.mapgen.utils.voronoi.Edge> libedges = v
        .edges();
    final HashMap<Integer, Corner> pointCornerMap = new HashMap<Integer, Corner>();

    for (final com.alma42.mapgen.utils.voronoi.Edge libedge : libedges) {
      final LineSegment vEdge = libedge.voronoiEdge();
      final LineSegment dEdge = libedge.delaunayLine();

      final Edge edge = new Edge();
      edge.index = this.edges.size();
      this.zone.getEdges().add(edge);

      edge.v0 = makeCorner(pointCornerMap, vEdge.p0);
      edge.v1 = makeCorner(pointCornerMap, vEdge.p1);
      edge.d0 = pointCenterMap.get(dEdge.p0);
      edge.d1 = pointCenterMap.get(dEdge.p1);

      // Centers point to edges. Corners point to edges.
      if (edge.d0 != null) {
        edge.d0.borders.add(edge);
      }
      if (edge.d1 != null) {
        edge.d1.borders.add(edge);
      }
      if (edge.v0 != null) {
        edge.v0.protrudes.add(edge);
      }
      if (edge.v1 != null) {
        edge.v1.protrudes.add(edge);
      }

      // Centers point to centers.
      if ((edge.d0 != null) && (edge.d1 != null)) {
        addToCenterList(edge.d0.neighbors, edge.d1);
        addToCenterList(edge.d1.neighbors, edge.d0);
      }

      // Corners point to corners
      if ((edge.v0 != null) && (edge.v1 != null)) {
        addToCornerList(edge.v0.adjacent, edge.v1);
        addToCornerList(edge.v1.adjacent, edge.v0);
      }

      // Centers point to corners
      if (edge.d0 != null) {
        addToCornerList(edge.d0.corners, edge.v0);
        addToCornerList(edge.d0.corners, edge.v1);
      }
      if (edge.d1 != null) {
        addToCornerList(edge.d1.corners, edge.v0);
        addToCornerList(edge.d1.corners, edge.v1);
      }

      // Corners point to centers
      if (edge.v0 != null) {
        addToCenterList(edge.v0.touches, edge.d0);
        addToCenterList(edge.v0.touches, edge.d1);
      }
      if (edge.v1 != null) {
        addToCenterList(edge.v1.touches, edge.d0);
        addToCenterList(edge.v1.touches, edge.d1);
      }
    }
  }

  // Helper functions for the following for loop; ideally these
  // would be inlined
  private static void addToCornerList(final ArrayList<Corner> list, final Corner c) {
    if ((c != null) && !list.contains(c)) {
      list.add(c);
    }
  }

  private static void addToCenterList(final ArrayList<Center> list, final Center c) {
    if ((c != null) && !list.contains(c)) {
      list.add(c);
    }
  }

  // ensures that each corner is represented by only one corner object
  private Corner makeCorner(final HashMap<Integer, Corner> pointCornerMap,
      final Point p) {
    if (p == null) {
      return null;
    }
    final int index = (int) ((int) p.x + ((int) (p.y) * this.bounds.width * 2));
    Corner c = pointCornerMap.get(index);
    if (c == null) {
      c = new Corner();
      c.loc = p;
      c.border = this.bounds.liesOnAxes(p);
      c.index = this.zone.getCorners().size();
      this.zone.addCorners(c);
      pointCornerMap.put(index, c);
    }
    return c;
  }

  /**
   * Although Lloyd relaxation improves the uniformity of polygon
   * sizes, it doesn't help with the edge lengths. Short edges can
   * be bad for some games, and lead to weird artifacts on
   * rivers. We can easily lengthen short edges by moving the
   * corners, but **we lose the Voronoi property**. The corners are
   * moved to the average of the polygon centers around them. Short
   * edges become longer. Long edges tend to become shorter. The
   * polygons tend to be more uniform after this step.
   */
  public void improveCorners() {
    ArrayList<Point> newCorners = new ArrayList<Point>();
    Point point;

    // First we compute the average of the centers next to each corner.
    for (Corner q : this.zone.getCorners()) {
      if (q.border) {
        newCorners.set(q.index, q.loc);
      } else {
        point = new Point(0.0, 0.0);
        for (Center r : q.touches) {
          point.x += r.loc.x;
          point.y += r.loc.y;
        }
        point.x /= q.touches.size();
        point.y /= q.touches.size();
        newCorners.set(q.index, point);
      }
    }

    // Move the corners to the new locations.
    for (int i = 0; i < this.zone.getCorners().size(); i++) {
      this.zone.getCorners().get(i).loc = newCorners.get(i);
    }

    // The edge midpoints were computed for the old corners and need
    // to be recomputed.
    for (Edge edge : this.zone.getEdges()) {
      if (edge.v0.equals(edge.v1)) {
        edge.midpoint = Point.interpolate(edge.v0.loc, edge.v1.loc, 0.5);
      }
    }
  }

  /**
   * Create an array of corners that are on land only, for use by
   * algorithms that work only on land. We return an array instead
   * of a vector because the redistribution algorithms want to sort
   * this array using Array.sortOn.
   */
  public ArrayList<Corner> landCorners(ArrayList<Corner> corners) {
    ArrayList<Corner> locations = new ArrayList<Corner>();
    for (Corner q : this.zone.getCorners()) {
      if (!q.ocean && !q.coast) {
        locations.add(q);
      }
    }
    return locations;
  }

  /**
   * Determine elevations and water at Voronoi corners. By
   * construction, we have no local minima. This is important for
   * the downslope vectors later, which are used in the river
   * construction algorithm. Also by construction, inlets/bays
   * push low elevation areas inland, which means many rivers end
   * up flowing out through them. Also by construction, lakes
   * often end up on river paths because they don't raise the
   * elevation as much as other terrain does.
   */
  private void assignCornerElevations() {
    // var q:Corner, s:Corner;
    double newElevation;
    ArrayList<Corner> queue = new ArrayList<Corner>();
    for (Corner q : this.zone.getCorners()) {
      q.water = !isInside(q.loc);
      // The edges of the map are elevation 0
      if (q.border) {
        q.elevation = 0.0;
        queue.add(q);
      } else {
        q.elevation = Double.POSITIVE_INFINITY;
      }
    }
    // Traverse the graph and assign elevations to each point. As we
    // move away from the map border, increase the elevations. This
    // guarantees that rivers always have a way down to the coast by
    // going downhill (no local minima).
    ListIterator<Corner> iterator = queue.listIterator();
    while (iterator.hasNext()) {
      Corner q = iterator.next(); // must be called before you can call i.remove()

      for (Corner s : q.adjacent) {
        // Every step up is epsilon over water or 1 over land. The
        // number doesn't matter because we'll rescale the
        // elevations later.
        newElevation = 0.01 + q.elevation;
        if (!q.water && !s.water) {
          newElevation += 1;
          if (needsMoreRandomness()) {
            // HACK: the map looks nice because of randomness of
            // points, randomness of rivers, and randomness of
            // edges. Without random point selection, I needed to
            // inject some more randomness to make maps look
            // nicer. I'm doing it here, with elevations, but I
            // think there must be a better way. This hack is only
            // used with square/hexagon grids.
            newElevation += this.seed.nextDouble();
          }
        }
        // If this point changed, we'll add it to the queue so
        // that we can process its neighbors too.
        if (newElevation < s.elevation) {
          s.elevation = newElevation;
          iterator.add(s);
        }
      }

      iterator.remove();
    }
  }

  private boolean needsMoreRandomness() {
    return this.zone.needMoreRandomness();
  }

  /**
   * Change the overall distribution of elevations so that lower
   * elevations are more common than higher
   * elevations. Specifically, we want elevation X to have frequency
   * (1-X). To do this we will sort the corners, then set each
   * corner to its desired elevation.
   */
  private static void redistributeElevations(ArrayList<Corner> locations) {
    // SCALE_FACTOR increases the mountain area. At 1.0 the maximum
    // elevation barely shows up on the map, so we set it to 1.1.
    double SCALE_FACTOR = 1.1;
    double x, y;

    Collections.sort(locations, new LocationComparator());

    for (int i = 0; i < locations.size(); i++) {
      // Let y(x) be the total area that we want at elevation <= x.
      // We want the higher elevations to occur less than lower
      // ones, and set the area to be y(x) = 1 - (1-x)^2.
      y = i / (locations.size() - 1);
      // Now we have to solve for x, given the known y.
      // * y = 1 - (1-x)^2
      // * y = 1 - (1 - 2x + x^2)
      // * y = 2x - x^2
      // * x^2 - 2x + y = 0
      // From this we can use the quadratic equation to get:
      x = Math.sqrt(SCALE_FACTOR) - Math.sqrt(SCALE_FACTOR * (1 - y));
      if (x > 1.0)
        x = 1.0; // TODO: does this break downslopes?
      locations.get(i).elevation = x;
    }
  }

  // Change the overall distribution of moisture to be evenly distributed.
  private static void redistributeMoisture(ArrayList<Corner> locations) {

    Collections.sort(locations, new MoistureComparator());
    for (int i = 0; i < locations.size(); i++) {
      locations.get(i).moisture = i / (locations.size() - 1);
    }
  }

  /** Determine polygon and corner types: ocean, coast, land. */
  private void assignOceanCoastAndLand() {
    // Compute polygon attributes 'ocean' and 'water' based on the
    // corner attributes. Count the water corners per
    // polygon. Oceans are all polygons connected to the edge of the
    // map. In the first pass, mark the edges of the map as ocean;
    // in the second pass, mark any water-containing polygon
    // connected an ocean as ocean.
    ArrayList<Center> queue = new ArrayList<Center>();
    int numWater, numOcean, numLand;
    for (Zone zone : this.zone.getZones()) {
      Center p = zone.getCenter();
      numWater = 0;
      for (Corner q : p.corners) {
        if (q.border) {
          p.border = true;
          p.ocean = true;
          q.water = true;
          queue.add(p);
        }
        if (q.water) {
          numWater += 1;
        }
      }
      p.water = (p.ocean || numWater >= p.corners.size() * LAKE_THRESHOLD);
    }

    ListIterator<Center> iterator = queue.listIterator();
    while (iterator.hasNext()) {
      Center p = iterator.next(); // must be called before you can call i.remove()
      for (Center r : p.neighbors) {
        if (r.water && !r.ocean) {
          r.ocean = true;
          iterator.add(r);
        }
      }
      iterator.remove();
    }

    // Set the polygon attribute 'coast' based on its neighbors. If
    // it has at least one ocean and at least one land neighbor,
    // then this is a coastal polygon.
    for (Zone zone : this.zone.getZones()) {
      Center p = zone.getCenter();
      numOcean = 0;
      numLand = 0;
      for (Center r : p.neighbors) {
        if (r.ocean) {
          numOcean++;
        }
        if (!r.water) {
          numLand++;
        }
      }
      p.coast = (numOcean > 0) && (numLand > 0);
    }

    // Set the corner attributes based on the computed polygon
    // attributes. If all polygons connected to this corner are
    // ocean, then it's ocean; if all are land, then it's land;
    // otherwise it's coast.
    for (Corner q : this.zone.getCorners()) {
      numOcean = 0;
      numLand = 0;
      for (Center p : q.touches) {

        if (p.ocean) {
          numOcean++;
        }
        if (!p.water) {
          numLand++;
        }
      }
      q.ocean = (numOcean == q.touches.size());
      q.coast = (numOcean > 0) && (numLand > 0);
      q.water = q.border || ((numLand != q.touches.size()) && !q.coast);
    }
  }

  /**
   * Calculate moisture. Freshwater sources spread moisture: rivers
   * and lakes (not oceans). Saltwater sources have moisture but do
   * not spread it (we set it at the end, after propagation).
   */
  private void assignCornerMoisture() {
    double newMoisture;
    ArrayList<Corner> queue = new ArrayList<Corner>();
    // Fresh water
    for (Corner corner : this.zone.getCorners()) {
      if ((corner.water || corner.river > 0) && !corner.ocean) {
        corner.moisture = corner.river > 0 ? Math.min(3.0, (0.2 * corner.river)) : 1.0;
        queue.add(corner);
      } else {
        corner.moisture = 0.0;
      }
    }

    ListIterator<Corner> iterator = queue.listIterator();
    while (iterator.hasNext()) {
      Corner corner = iterator.next(); // must be called before you can call i.remove()
      for (Corner adjacentCorner : corner.adjacent) {
        newMoisture = corner.moisture * 0.9;
        if (newMoisture > adjacentCorner.moisture) {
          adjacentCorner.moisture = newMoisture;
          iterator.add(adjacentCorner);
        }
      }
      iterator.remove();
    }

    // Salt water
    for (Corner corner : this.zone.getCorners()) {
      if (corner.ocean || corner.coast) {
        corner.moisture = 1.0;
      }
    }
  }

  /**
   * Determine whether a given point should be on the island or in the water.
   * 
   * @return true if the point should be on the island.
   */
  private boolean isInside(Point point) {
    return this.islandShape.isInside(new Point(2 * (point.x / this.size - 0.5), 2 * (point.y / this.size - 0.5)));
  }

  /**
   * Create rivers along edges. Pick a random corner point, then
   * move downslope. Mark the edges and corners as rivers.
   */
  private void createRivers() {
    this.riverCreator.createRivers(this.zone.getCorners(), this.size, this.seed);
  }

  /**
   * Polygon moisture is the average of the moisture at corners
   */
  private void assignMoisture() {
    for (Zone zone : this.zone.getZones()) {
      zone.getCenter().assignMoisture();
    }
  }

  /** Polygon elevations are the average of the elevations of their corners. */
  private void assignElevations() {
    double sumElevation;
    for (Zone zone : this.zone.getZones()) {
      sumElevation = 0.0;
      for (Corner q : zone.getCorners()) {
        sumElevation += q.elevation;
      }
      zone.getCenter().elevation = sumElevation / zone.getCenter().corners.size();
    }
  }

  /**
   * Calculate downslope pointers. At every point, we point to the
   * point downstream from it, or to itself. This is used for
   * generating rivers and watersheds.
   */
  private void calculateDownslopes() {
    Corner r;

    for (Corner q : this.zone.getCorners()) {
      r = q;
      for (Corner s : q.adjacent) {
        if (s.elevation <= r.elevation) {
          r = s;
        }
      }
      q.downslope = r;
    }
  }

  /**
   * Calculate the watershed of every land point. The watershed is
   * the last downstream land point in the downslope graph. TODO:
   * watersheds are currently calculated on corners, but it'd be
   * more useful to compute them on polygon centers so that every
   * polygon can be marked as being in one watershed.
   */
  private void calculateWatersheds() {
    // var q:Corner, r:Corner, i:int, changed:Boolean;
    boolean changed;
    Corner r;
    // Initially the watershed pointer points downslope one step.
    for (Corner q : this.zone.getCorners()) {
      q.watershed = q;
      if (!q.ocean && !q.coast) {
        q.watershed = q.downslope;
      }
    }
    // Follow the downslope pointers to the coast. Limit to 100
    // iterations although most of the time with numPoints==2000 it
    // only takes 20 iterations because most points are not far from
    // a coast. TODO: can run faster by looking at
    // p.watershed.watershed instead of p.downslope.watershed.
    for (int i = 0; i < 100; i++) {
      changed = false;
      for (Corner q : this.zone.getCorners()) {
        if (!q.ocean && !q.coast && !q.watershed.coast) {
          r = q.downslope.watershed;
          if (!r.ocean)
            q.watershed = r;
          changed = true;
        }
      }
      if (!changed)
        break;
    }
    // How big is each watershed?
    for (Corner q : this.zone.getCorners()) {
      r = q.watershed;
      r.watershed_size = 1 + r.watershed_size;
    }
  }
}
