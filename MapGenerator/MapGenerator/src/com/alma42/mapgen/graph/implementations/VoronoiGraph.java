package com.alma42.mapgen.graph.implementations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.alma42.mapgen.graph.IGraph;
import com.alma42.mapgen.utils.geometry.Center;
import com.alma42.mapgen.utils.geometry.Corner;
import com.alma42.mapgen.utils.geometry.Edge;
import com.alma42.mapgen.utils.geometry.Point;
import com.alma42.mapgen.utils.voronoi.LineSegment;
import com.alma42.mapgen.utils.voronoi.Voronoi;

public class VoronoiGraph implements IGraph {

  private int               size;

  private ArrayList<Center> centers;
  private ArrayList<Corner> corners;
  private ArrayList<Edge>   edges;
  private Voronoi           voronoi;

  public VoronoiGraph(int size, Random seed, int pointNumber) {
    this.centers = new ArrayList<Center>();
    this.corners = new ArrayList<Corner>();
    this.edges = new ArrayList<Edge>();
    this.voronoi = new Voronoi(pointNumber, size, size, seed, null);
  }

  @Override
  public void buildGraph(ArrayList<Point> points) {
    points = this.voronoi.siteCoords();
    this.voronoi = new Voronoi(points, null, this.voronoi.get_plotBounds());
    final HashMap<Point, Center> pointCenterMap = new HashMap<Point, Center>();
    points = this.voronoi.siteCoords();
    Center newCenter;

    // Build Center objects for each of the points, and a lookup map
    // to find those Center objects again as we build the graph
    for (Point point : points) {
      newCenter = new Center();
      newCenter.index = this.centers.size();
      newCenter.point = point;
      this.centers.add(newCenter);
      pointCenterMap.put(point, newCenter);
    }

    // Workaround for Voronoi lib bug: we need to call region()
    // before Edges or neighboringSites are available
    for (Center center : this.centers) {
      this.voronoi.region(center.point);
    }

    final ArrayList<com.alma42.mapgen.utils.voronoi.Edge> libedges = this.voronoi.edges();
    final HashMap<Integer, Corner> pointCornerMap = new HashMap<Integer, Corner>();

    for (final com.alma42.mapgen.utils.voronoi.Edge libedge : libedges) {
      final LineSegment vEdge = libedge.voronoiEdge();
      final LineSegment dEdge = libedge.delaunayLine();

      // Fill the graph data. Make an Edge object corresponding to
      // the edge from the voronoi library.
      Edge edge = new Edge();
      edge.index = this.edges.size();
      edge.river = 0;
      this.edges.add(edge);

      // Edges point to corners. Edges point to centers.
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
      if (edge.d0 != null && edge.d1 != null) {
        addToCenterList(edge.d0.neighbors, edge.d1);
        addToCenterList(edge.d1.neighbors, edge.d0);
      }

      // Corners point to corners
      if (edge.v0 != null && edge.v1 != null) {
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
      final Point point) {
    Corner corner;
    int index;

    if (point == null) {
      return null;
    }
    // for (bucket = (int) ((point.x) - 1); bucket <= (point.x) + 1; bucket++) {
    // q = pointCornerMap.get(bucket);
    // double dx = point.x - q.point.x;
    // double dy = point.y - q.point.y;
    // if (dx * dx + dy * dy < 1e-6) {
    // return q;
    // }
    // }
    index = (int) point.x + ((int) (point.y) * this.size * 2);
    corner = pointCornerMap.get(index);
    if (corner == null) {
      corner = new Corner();
      corner.index = this.corners.size();
      corner.point = point;
      corner.border = (point.x == 0 || point.x == this.size
          || point.y == 0 || point.y == this.size);
      this.corners.add(corner);
      pointCornerMap.put(index, corner);
    }
    return corner;
  }

  /**
   * @return the size
   */
  public int getSize() {
    return this.size;
  }

  /**
   * @return the centers
   */
  @Override
  public ArrayList<Center> getCenters() {
    return this.centers;
  }

  /**
   * @return the corners
   */
  @Override
  public ArrayList<Corner> getCorners() {
    return this.corners;
  }

  /**
   * @return the edges
   */
  @Override
  public ArrayList<Edge> getEdges() {
    return this.edges;
  }

}
