package com.alma42.mapgen.zone.types.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

import com.alma42.mapgen.biomes.IBiomeManager;
import com.alma42.mapgen.factories.BiomeManagerFactory;
import com.alma42.mapgen.factories.GraphFactory;
import com.alma42.mapgen.factories.IslandShapeFactory;
import com.alma42.mapgen.factories.PointSelectorFactory;
import com.alma42.mapgen.factories.RiverCreatorFactory;
import com.alma42.mapgen.graph.IGraph;
import com.alma42.mapgen.island_shape.IIslandShape;
import com.alma42.mapgen.river.IRiverCreator;
import com.alma42.mapgen.utils.geometry.Center;
import com.alma42.mapgen.utils.geometry.Corner;
import com.alma42.mapgen.utils.geometry.Edge;
import com.alma42.mapgen.utils.geometry.Point;
import com.alma42.mapgen.zone.selectors.IPointSelector;
import com.alma42.mapgen.zone.types.IZoneType;

public class Island implements IZoneType {

  /**
   * 0 to 1, fraction of water corners for water polygon
   */
  public static final double LAKE_THRESHOLD = 0.3;

  private int                size;
  private int                pointNumber;
  private Random             random;
  private IPointSelector     pointSelector;
  private IGraph             graph;

  private IIslandShape       islandShape;
  private IRiverCreator      riverCreator;
  IBiomeManager              biomeManager;

  public Island(int size, int pointNumber, int seed, int pointSelectorType, int graphType, int islandShapeType,
      int riverCreatorType, int biomeManagerType) {
    this.size = size;
    this.pointNumber = pointNumber;
    this.random = new Random(seed);
    this.pointSelector = PointSelectorFactory.createPointSelector(pointSelectorType, this.random);
    this.graph = GraphFactory.creatGraph(graphType, size, this.random, this.pointNumber);
    this.islandShape = IslandShapeFactory.createIslandShape(islandShapeType, this.random);
    this.riverCreator = RiverCreatorFactory.createRiverCreator(riverCreatorType);
    this.biomeManager = BiomeManagerFactory.createBiomeManager(biomeManagerType);
  }

  @Override
  public void createZone() {
    ArrayList<Point> points;
    // Generate the initial random set of zones
    System.out.println("Generate Zones ...");
    points = this.pointSelector.generatePoints(this.size, this.pointNumber);

    // Create a graph structure from the Voronoi edge list. The
    // methods in the Voronoi object are somewhat inconvenient for
    // my needs, so I transform that data into the data I actually
    // need: edges connected to the Delaunay triangles and the
    // Voronoi polygons, a reverse map from those four points back
    // to the edge, a map from these four points to the points
    // they connect to (both along the edge and crosswise).
    System.out.println("Build Graph ...");
    this.graph.buildGraph(points);
    improveCorners();
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
    redistributeElevations(landCorners(this.graph.getCorners()));

    // Assign elevations to non-land corners
    for (Corner q : this.graph.getCorners()) {
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
    redistributeMoisture(landCorners(this.graph.getCorners()));
    assignMoisture();
    assignBiomes();
  }

  /**
   * Although Lloyd relaxation improves the uniformity of polygon sizes, it doesn't help with the edge lengths. Short
   * edges can be bad for some games, and lead to weird artifacts on rivers. We can easily lengthen short edges by
   * moving the corners, but **we lose the Voronoi property**. The corners are moved to the average of the polygon
   * centers around them. Short edges become longer. Long edges tend to become shorter. The polygons tend to be more
   * uniform after this step.
   */
  private void improveCorners() {
    ArrayList<Point> newCorners = new ArrayList<Point>();
    Point point;

    // First we compute the average of the centers next to each corner.
    for (Corner q : this.graph.getCorners()) {
      if (q.border) {
        newCorners.add(q.index, q.point);
      } else {
        point = new Point(0.0, 0.0);
        for (Center r : q.touches) {
          point.x += r.point.x;
          point.y += r.point.y;
        }
        point.x /= q.touches.size();
        point.y /= q.touches.size();
        newCorners.add(q.index, point);
      }
    }

    // Move the corners to the new locations.
    for (int i = 0; i < this.graph.getCorners().size(); i++) {
      this.graph.getCorners().get(i).point = newCorners.get(i);
    }

    // The edge midpoints were computed for the old corners and need
    // to be recomputed.
    for (Edge edge : this.graph.getEdges()) {
      if (edge.v0 != null && edge.v1 != null) {
        edge.setVornoi(edge.v0, edge.v1);
      }
    }
  }

  /**
   * Create an array of corners that are on land only, for use by algorithms that work only on land. We return an array
   * instead of a vector because the redistribution algorithms want to sort this array using Array.sortOn.
   */
  private ArrayList<Corner> landCorners(ArrayList<Corner> corners) {
    ArrayList<Corner> locations = new ArrayList<Corner>();
    for (Corner q : this.graph.getCorners()) {
      if (!q.ocean && !q.coast) {
        locations.add(q);
      }
    }
    return locations;
  }

  /**
   * Determine elevations and water at Voronoi corners. By construction, we have no local minima. This is important for
   * the downslope vectors later, which are used in the river construction algorithm. Also by construction, inlets/bays
   * push low elevation areas inland, which means many rivers end up flowing out through them. Also by construction,
   * lakes often end up on river paths because they don't raise the elevation as much as other terrain does.
   */
  private void assignCornerElevations() {
    double newElevation;
    LinkedList<Corner> queue = new LinkedList<Corner>();
    for (Corner corner : this.graph.getCorners()) {
      corner.water = !isInside(corner.point);
      // The edges of the map are elevation 0
      if (corner.border) {
        corner.elevation = 0.0;
        queue.add(corner);
      } else {
        corner.elevation = Double.MAX_VALUE;
      }
    }
    // Traverse the graph and assign elevations to each point. As we
    // move away from the map border, increase the elevations. This
    // guarantees that rivers always have a way down to the coast by
    // going downhill (no local minima).
    while (!queue.isEmpty()) {
      Corner corner = queue.pop();
      for (Corner cornerAdjacent : corner.adjacent) {
        // Every step up is epsilon over water or 1 over land. The
        // number doesn't matter because we'll rescale the
        // elevations later.
        newElevation = 0.01 + corner.elevation;
        if (!corner.water && !cornerAdjacent.water) {
          newElevation += 1;
          if (needsMoreRandomness()) {
            // HACK: the map looks nice because of randomness of
            // points, randomness of rivers, and randomness of
            // edges. Without random point selection, I needed to
            // inject some more randomness to make maps look
            // nicer. I'm doing it here, with elevations, but I
            // think there must be a better way. This hack is only
            // used with square/hexagon grids.

            // newElevation += this.random.nextDouble();
          }
        }
        // If this point changed, we'll add it to the queue so
        // that we can process its neighbors too.
        if (newElevation < cornerAdjacent.elevation) {
          cornerAdjacent.elevation = newElevation;
          queue.add(cornerAdjacent);
        }
      }
    }
  }

  private boolean needsMoreRandomness() {
    return this.pointSelector.needMoreRandomness();
  }

  /**
   * Change the overall distribution of elevations so that lower elevations are more common than higher elevations.
   * Specifically, we want elevation X to have frequency (1-X). To do this we will sort the corners, then set each
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
    LinkedList<Center> queue = new LinkedList<Center>();
    int numWater, numOcean, numLand;
    for (Center center : this.graph.getCenters()) {
      numWater = 0;
      for (Corner corner : center.corners) {
        if (corner.border) {
          center.border = true;
          center.ocean = true;
          corner.water = true;
          queue.add(center);
        }
        if (corner.water) {
          numWater += 1;
        }
      }
      center.water = (center.ocean || (numWater / center.corners.size()) >= LAKE_THRESHOLD);
    }

    while (!queue.isEmpty()) {
      Center center = queue.pop();
      for (Center centerNeighbor : center.neighbors) {
        if (centerNeighbor.water && !centerNeighbor.ocean) {
          centerNeighbor.ocean = true;
          queue.add(centerNeighbor);
        }
      }
    }

    // Set the polygon attribute 'coast' based on its neighbors. If
    // it has at least one ocean and at least one land neighbor,
    // then this is a coastal polygon.
    for (Center center : this.graph.getCenters()) {
      numOcean = 0;
      numLand = 0;
      for (Center centerNeighbor : center.neighbors) {
        if (centerNeighbor.ocean) {
          numOcean++;
        }
        if (!centerNeighbor.water) {
          numLand++;
        }
      }
      center.coast = (numOcean > 0) && (numLand > 0);
    }

    // Set the corner attributes based on the computed polygon
    // attributes. If all polygons connected to this corner are
    // ocean, then it's ocean; if all are land, then it's land;
    // otherwise it's coast.
    for (Corner corner : this.graph.getCorners()) {
      numOcean = 0;
      numLand = 0;
      for (Center touche : corner.touches) {

        if (touche.ocean) {
          numOcean++;
        }
        if (!touche.water) {
          numLand++;
        }
      }
      corner.ocean = (numOcean == corner.touches.size());
      corner.coast = (numOcean > 0) && (numLand > 0);
      corner.water = corner.border || ((numLand != corner.touches.size()) && !corner.coast);
    }
  }

  /**
   * Calculate moisture. Freshwater sources spread moisture: rivers and lakes (not oceans). Saltwater sources have
   * moisture but do not spread it (we set it at the end, after propagation).
   */
  private void assignCornerMoisture() {
    double newMoisture;
    LinkedList<Corner> queue = new LinkedList<Corner>();
    // Fresh water
    for (Corner corner : this.graph.getCorners()) {
      if ((corner.water || corner.river > 0) && !corner.ocean) {
        corner.moisture = corner.river > 0 ? Math.min(3.0, (0.2 * corner.river)) : 1.0;
        queue.add(corner);
      } else {
        corner.moisture = 0.0;
      }
    }

    while (!queue.isEmpty()) {
      Corner corner = queue.pop();
      for (Corner adjacentCorner : corner.adjacent) {
        newMoisture = corner.moisture * 0.9;
        if (newMoisture > adjacentCorner.moisture) {
          adjacentCorner.moisture = newMoisture;
          queue.add(adjacentCorner);
        }
      }
    }

    // Salt water
    for (Corner corner : this.graph.getCorners()) {
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
   * Create rivers along edges. Pick a random corner point, then move downslope. Mark the edges and corners as rivers.
   */
  private void createRivers() {
    this.riverCreator.createRivers(this.graph.getCorners(), this.size, this.random);
  }

  /**
   * Polygon moisture is the average of the moisture at corners
   */
  private void assignMoisture() {
    double sumMoisture;
    for (Center p : this.graph.getCenters()) {
      sumMoisture = 0.0;
      for (Corner q : p.corners) {
        if (q.moisture > 1.0)
          q.moisture = 1.0;
        sumMoisture += q.moisture;
      }
      p.moisture = sumMoisture / p.corners.size();
    }
  }

  /** Polygon elevations are the average of the elevations of their corners. */
  private void assignElevations() {
    double sumElevation;
    for (Center p : this.graph.getCenters()) {
      sumElevation = 0.0;
      for (Corner q : p.corners) {
        sumElevation += q.elevation;
      }
      p.elevation = sumElevation / p.corners.size();
    }
  }

  /**
   * Calculate downslope pointers. At every point, we point to the point downstream from it, or to itself. This is used
   * for generating rivers and watersheds.
   */
  private void calculateDownslopes() {
    Corner r;

    for (Corner q : this.graph.getCorners()) {
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
   * Calculate the watershed of every land point. The watershed is the last downstream land point in the downslope
   * graph. TODO: watersheds are currently calculated on corners, but it'd be more useful to compute them on polygon
   * centers so that every polygon can be marked as being in one watershed.
   */
  private void calculateWatersheds() {
    boolean changed;
    Corner r;
    // Initially the watershed pointer points downslope one step.
    for (Corner q : this.graph.getCorners()) {
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
      for (Corner q : this.graph.getCorners()) {
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
    for (Corner q : this.graph.getCorners()) {
      r = q.watershed;
      r.watershed_size = 1 + r.watershed_size;
    }
  }

  @Override
  public IGraph getGraph() {
    return this.graph;
  }

  private void assignBiomes() {
    for (Center center : this.graph.getCenters()) {
      this.biomeManager.assignBiome(center);
    }
  }
}
