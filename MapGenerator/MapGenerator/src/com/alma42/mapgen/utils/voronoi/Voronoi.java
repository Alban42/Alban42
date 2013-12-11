package com.alma42.mapgen.utils.voronoi;

/*
 * Java implementaition by Connor Clark (www.hotengames.com). Pretty much a 1:1
 * translation of a wonderful map generating algorthim by Amit Patel of Red Blob Games,
 * which can be found here (http://www-cs-students.stanford.edu/~amitp/game-programming/polygon-map-generation/)
 * Hopefully it's of use to someone out there who needed it in Java like I did!
 * Note, the only island mode implemented is Radial. Implementing more is something for another day.
 * 
 * FORTUNE'S ALGORTIHIM
 * 
 * This is a java implementation of an AS3 (Flash) implementation of an algorthim
 * originally created in C++. Pretty much a 1:1 translation from as3 to java, save
 * for some necessary workarounds. Original as3 implementation by Alan Shaw (of nodename)
 * can be found here (https://github.com/nodename/as3delaunay). Original algorthim
 * by Steven Fortune (see lisence for c++ implementation below)
 * 
 * The author of this software is Steven Fortune. Copyright (c) 1994 by AT&T
 * Bell Laboratories.
 * Permission to use, copy, modify, and distribute this software for any
 * purpose without fee is hereby granted, provided that this entire notice
 * is included in all copies of any software which is or includes a copy
 * or modification of this software and in all copies of the supporting
 * documentation for such software.
 * THIS SOFTWARE IS BEING PROVIDED "AS IS", WITHOUT ANY EXPRESS OR IMPLIED
 * WARRANTY. IN PARTICULAR, NEITHER THE AUTHORS NOR AT&T MAKE ANY
 * REPRESENTATION OR WARRANTY OF ANY KIND CONCERNING THE MERCHANTABILITY
 * OF THIS SOFTWARE OR ITS FITNESS FOR ANY PARTICULAR PURPOSE.
 */
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.alma42.mapgen.utils.geometry.Point;
import com.alma42.mapgen.utils.geometry.Rectangle;

public final class Voronoi {

  private SiteList             _sites;
  private HashMap<Point, Site> _sitesIndexedByLocation;
  private ArrayList<Triangle>  _triangles;
  private ArrayList<Edge>      _edges;
  // TODO generalize this so it doesn't have to be a rectangle;
  // then we can make the fractal voronois-within-voronois
  private Rectangle            _plotBounds;

  public Rectangle get_plotBounds() {
    return this._plotBounds;
  }

  public void dispose() {
    int i, n;
    if (this._sites != null) {
      this._sites.dispose();
      this._sites = null;
    }
    if (this._triangles != null) {
      n = this._triangles.size();
      for (i = 0; i < n; ++i) {
        this._triangles.get(i).dispose();
      }
      this._triangles.clear();
      this._triangles = null;
    }
    if (this._edges != null) {
      n = this._edges.size();
      for (i = 0; i < n; ++i) {
        this._edges.get(i).dispose();
      }
      this._edges.clear();
      this._edges = null;
    }
    this._plotBounds = null;
    this._sitesIndexedByLocation = null;
  }

  public Voronoi(ArrayList<Point> points, ArrayList<Color> colors,
      Rectangle plotBounds) {
    init(points, colors, plotBounds);
    fortunesAlgorithm();
  }

  public Voronoi(ArrayList<Point> points, ArrayList<Color> colors) {
    double maxWidth = 0, maxHeight = 0;
    for (Point p : points) {
      maxWidth = Math.max(maxWidth, p.x);
      maxHeight = Math.max(maxHeight, p.y);
    }
    System.out.println(maxWidth + "," + maxHeight);
    init(points, colors, new Rectangle(0, 0, maxWidth, maxHeight));
    fortunesAlgorithm();
  }

  public Voronoi(int numSites, double maxWidth, double maxHeight, Random r,
      ArrayList<Color> colors) {
    ArrayList<Point> points = new ArrayList<Point>();
    for (int i = 0; i < numSites; i++) {
      points.add(new Point(r.nextDouble() * maxWidth, r.nextDouble()
          * maxHeight));
    }
    init(points, colors, new Rectangle(0, 0, maxWidth, maxHeight));
    fortunesAlgorithm();
  }

  private void init(ArrayList<Point> points, ArrayList<Color> colors,
      Rectangle plotBounds) {
    this._sites = new SiteList();
    this._sitesIndexedByLocation = new HashMap<Point, Site>();
    addSites(points, colors);
    this._plotBounds = plotBounds;
    this._triangles = new ArrayList<Triangle>();
    this._edges = new ArrayList<Edge>();
  }

  private void addSites(ArrayList<Point> points, ArrayList<Color> colors) {
    int length = points.size();
    for (int i = 0; i < length; ++i) {
      addSite(points.get(i), colors != null ? colors.get(i) : null, i);
    }
  }

  private void addSite(Point p, Color color, int index) {
    double weight = Math.random() * 100;
    Site site = Site.create(p, index, weight, color);
    this._sites.push(site);
    this._sitesIndexedByLocation.put(p, site);
  }

  public ArrayList<Edge> edges() {
    return this._edges;
  }

  public ArrayList<Point> region(Point p) {
    Site site = this._sitesIndexedByLocation.get(p);
    if (site == null) {
      return new ArrayList<Point>();
    }
    return site.region(this._plotBounds);
  }

  // TODO: bug: if you call this before you call region(), something goes
  // wrong :(
  public ArrayList<Point> neighborSitesForSite(Point coord) {
    ArrayList<Point> points = new ArrayList<Point>();
    Site site = this._sitesIndexedByLocation.get(coord);
    if (site == null) {
      return points;
    }
    ArrayList<Site> sites = site.neighborSites();
    for (Site neighbor : sites) {
      points.add(neighbor.get_coord());
    }
    return points;
  }

  public ArrayList<Circle> circles() {
    return this._sites.circles();
  }

  private static ArrayList<Edge> selectEdgesForSitePoint(Point coord,
      ArrayList<Edge> edgesToTest) {
    ArrayList<Edge> filtered = new ArrayList<Edge>();

    for (Edge e : edgesToTest) {
      if (((e.get_leftSite() != null && e.get_leftSite().get_coord() == coord) || (e
          .get_rightSite() != null && e.get_rightSite().get_coord() == coord))) {
        filtered.add(e);
      }
    }
    return filtered;

    /*
     * function myTest(edge:Edge, index:int, vector:Vector.<Edge>):Boolean {
     * return ((edge.leftSite && edge.leftSite.coord == coord) ||
     * (edge.rightSite && edge.rightSite.coord == coord)); }
     */
  }

  private static ArrayList<LineSegment> visibleLineSegments(ArrayList<Edge> edges) {
    ArrayList<LineSegment> segments = new ArrayList<LineSegment>();

    for (Edge edge : edges) {
      if (edge.get_visible()) {
        Point p1 = edge.get_clippedEnds().get(LR.LEFT);
        Point p2 = edge.get_clippedEnds().get(LR.RIGHT);
        segments.add(new LineSegment(p1, p2));
      }
    }

    return segments;
  }

  private static ArrayList<LineSegment> delaunayLinesForEdges(ArrayList<Edge> edges) {
    ArrayList<LineSegment> segments = new ArrayList<LineSegment>();

    for (Edge edge : edges) {
      segments.add(edge.delaunayLine());
    }

    return segments;
  }

  public ArrayList<LineSegment> voronoiBoundaryForSite(Point coord) {
    return visibleLineSegments(selectEdgesForSitePoint(coord, this._edges));
  }

  public ArrayList<LineSegment> delaunayLinesForSite(Point coord) {
    return delaunayLinesForEdges(selectEdgesForSitePoint(coord, this._edges));
  }

  public ArrayList<LineSegment> voronoiDiagram() {
    return visibleLineSegments(this._edges);
  }

  /*
   * public ArrayList<LineSegment>
   * delaunayTriangulation(keepOutMask:BitmapData = null) { return
   * delaunayLinesForEdges(selectNonIntersectingEdges(keepOutMask, _edges)); }
   */
  public ArrayList<LineSegment> hull() {
    return delaunayLinesForEdges(hullEdges());
  }

  private ArrayList<Edge> hullEdges() {
    ArrayList<Edge> filtered = new ArrayList<Edge>();

    for (Edge e : this._edges) {
      if (e.isPartOfConvexHull()) {
        filtered.add(e);
      }
    }

    return filtered;

    /*
     * function myTest(edge:Edge, index:int, vector:Vector.<Edge>):Boolean {
     * return (edge.isPartOfConvexHull()); }
     */
  }

  public ArrayList<Point> hullPointsInOrder() {
    ArrayList<Edge> hullEdges = hullEdges();

    ArrayList<Point> points = new ArrayList<Point>();
    if (hullEdges.isEmpty()) {
      return points;
    }

    EdgeReorderer reorderer = new EdgeReorderer(hullEdges, Site.class);
    hullEdges = reorderer.get_edges();
    ArrayList<LR> orientations = reorderer.get_edgeOrientations();
    reorderer.dispose();

    LR orientation;

    int n = hullEdges.size();
    for (int i = 0; i < n; ++i) {
      Edge edge = hullEdges.get(i);
      orientation = orientations.get(i);
      points.add(edge.site(orientation).get_coord());
    }
    return points;
  }

  /*
   * public ArrayList<LineSegment> spanningTree(String type,
   * keepOutMask:BitmapData = null) { ArrayList<Edge> edges =
   * selectNonIntersectingEdges(keepOutMask, _edges); ArrayList<LineSegment>
   * segments = delaunayLinesForEdges(edges); return kruskal(segments, type);
   * }
   */
  public ArrayList<ArrayList<Point>> regions() {
    return this._sites.regions(this._plotBounds);
  }

  /*
   * public ArrayList<Integer> siteColors(referenceImage:BitmapData = null) {
   * return _sites.siteColors(referenceImage); }
   */
  /**
   * 
   * @param proximityMap
   *          a BitmapData whose regions are filled with the site index
   *          values; see PlanePointsCanvas::fillRegions()
   * @param x
   * @param y
   * @return coordinates of nearest Site to (x, y)
   * 
   */
  /*
   * public Point nearestSitePoint(proximityMap:BitmapData,double x, double y)
   * { return _sites.nearestSitePoint(proximityMap, x, y); }
   */
  public ArrayList<Point> siteCoords() {
    return this._sites.siteCoords();
  }

  private void fortunesAlgorithm() {
    Site newSite, bottomSite, topSite, tempSite;
    Vertex v, vertex;
    Point newintstar = null;
    LR leftRight;
    Halfedge lbnd, rbnd, llbnd, rrbnd, bisector;
    Edge edge;

    Rectangle dataBounds = this._sites.getSitesBounds();

    int sqrt_nsites = (int) Math.sqrt(this._sites.get_length() + 4);
    HalfedgePriorityQueue heap = new HalfedgePriorityQueue(dataBounds.y,
        dataBounds.height, sqrt_nsites);
    EdgeList edgeList = new EdgeList(dataBounds.x, dataBounds.width,
        sqrt_nsites);
    ArrayList<Halfedge> halfEdges = new ArrayList<Halfedge>();
    ArrayList<Vertex> vertices = new ArrayList<Vertex>();

    Site bottomMostSite = this._sites.next();
    newSite = this._sites.next();

    for (;;) {
      if (heap.empty() == false) {
        newintstar = heap.min();
      }

      if (newSite != null
          && (heap.empty() || compareByYThenX(newSite, newintstar) < 0)) {
        /* new site is smallest */
        // trace("smallest: new site " + newSite);

        // Step 8:
        lbnd = edgeList.edgeListLeftNeighbor(newSite.get_coord()); // the
        // Halfedge
        // just
        // to
        // the
        // left
        // of
        // newSite
        // trace("lbnd: " + lbnd);
        rbnd = lbnd.edgeListRightNeighbor; // the Halfedge just to the
        // right
        // trace("rbnd: " + rbnd);
        bottomSite = rightRegion(lbnd, bottomMostSite); // this is the
        // same as
        // leftRegion(rbnd)
        // this Site determines the region containing the new site
        // trace("new Site is in region of existing site: " +
        // bottomSite);

        // Step 9:
        edge = Edge.createBisectingEdge(bottomSite, newSite);
        // trace("new edge: " + edge);
        this._edges.add(edge);

        bisector = Halfedge.create(edge, LR.LEFT);
        halfEdges.add(bisector);
        // inserting two Halfedges into edgeList constitutes Step 10:
        // insert bisector to the right of lbnd:
        EdgeList.insert(lbnd, bisector);

        // first half of Step 11:
        if ((vertex = Vertex.intersect(lbnd, bisector)) != null) {
          vertices.add(vertex);
          heap.remove(lbnd);
          lbnd.vertex = vertex;
          lbnd.ystar = vertex.get_y() + newSite.dist(vertex);
          heap.insert(lbnd);
        }

        lbnd = bisector;
        bisector = Halfedge.create(edge, LR.RIGHT);
        halfEdges.add(bisector);
        // second Halfedge for Step 10:
        // insert bisector to the right of lbnd:
        EdgeList.insert(lbnd, bisector);

        // second half of Step 11:
        if ((vertex = Vertex.intersect(bisector, rbnd)) != null) {
          vertices.add(vertex);
          bisector.vertex = vertex;
          bisector.ystar = vertex.get_y() + newSite.dist(vertex);
          heap.insert(bisector);
        }

        newSite = this._sites.next();
      } else if (heap.empty() == false) {
        /* intersection is smallest */
        lbnd = heap.extractMin();
        llbnd = lbnd.edgeListLeftNeighbor;
        rbnd = lbnd.edgeListRightNeighbor;
        rrbnd = rbnd.edgeListRightNeighbor;
        bottomSite = leftRegion(lbnd, bottomMostSite);
        topSite = rightRegion(rbnd, bottomMostSite);
        // these three sites define a Delaunay triangle
        // (not actually using these for anything...)
        // _triangles.push(new Triangle(bottomSite, topSite,
        // rightRegion(lbnd)));

        v = lbnd.vertex;
        v.setIndex();
        lbnd.edge.setVertex(lbnd.leftRight, v);
        rbnd.edge.setVertex(rbnd.leftRight, v);
        EdgeList.remove(lbnd);
        heap.remove(rbnd);
        EdgeList.remove(rbnd);
        leftRight = LR.LEFT;
        if (bottomSite.get_y() > topSite.get_y()) {
          tempSite = bottomSite;
          bottomSite = topSite;
          topSite = tempSite;
          leftRight = LR.RIGHT;
        }
        edge = Edge.createBisectingEdge(bottomSite, topSite);
        this._edges.add(edge);
        bisector = Halfedge.create(edge, leftRight);
        halfEdges.add(bisector);
        EdgeList.insert(llbnd, bisector);
        edge.setVertex(LR.other(leftRight), v);
        if ((vertex = Vertex.intersect(llbnd, bisector)) != null) {
          vertices.add(vertex);
          heap.remove(llbnd);
          llbnd.vertex = vertex;
          llbnd.ystar = vertex.get_y() + bottomSite.dist(vertex);
          heap.insert(llbnd);
        }
        if ((vertex = Vertex.intersect(bisector, rrbnd)) != null) {
          vertices.add(vertex);
          bisector.vertex = vertex;
          bisector.ystar = vertex.get_y() + bottomSite.dist(vertex);
          heap.insert(bisector);
        }
      } else {
        break;
      }
    }

    // heap should be empty now
    heap.dispose();
    edgeList.dispose();

    for (Halfedge halfEdge : halfEdges) {
      halfEdge.reallyDispose();
    }
    halfEdges.clear();

    // we need the vertices to clip the edges
    for (Edge e : this._edges) {
      e.clipVertices(this._plotBounds);
    }
    // but we don't actually ever use them again!
    for (Vertex v0 : vertices) {
      v0.dispose();
    }
    vertices.clear();

  }

  static Site leftRegion(Halfedge he, Site bottomMostSite) {
    Edge edge = he.edge;
    if (edge == null) {
      return bottomMostSite;
    }
    return edge.site(he.leftRight);
  }

  static Site rightRegion(Halfedge he, Site bottomMostSite) {
    Edge edge = he.edge;
    if (edge == null) {
      return bottomMostSite;
    }
    return edge.site(LR.other(he.leftRight));
  }

  public static int compareByYThenX(Site s1, Site s2) {
    if (s1.get_y() < s2.get_y()) {
      return -1;
    }
    if (s1.get_y() > s2.get_y()) {
      return 1;
    }
    if (s1.get_x() < s2.get_x()) {
      return -1;
    }
    if (s1.get_x() > s2.get_x()) {
      return 1;
    }
    return 0;
  }

  public static int compareByYThenX(Site s1, Point s2) {
    if (s1.get_y() < s2.y) {
      return -1;
    }
    if (s1.get_y() > s2.y) {
      return 1;
    }
    if (s1.get_x() < s2.x) {
      return -1;
    }
    if (s1.get_x() > s2.x) {
      return 1;
    }
    return 0;
  }
}
