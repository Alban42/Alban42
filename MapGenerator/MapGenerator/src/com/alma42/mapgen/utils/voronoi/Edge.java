package com.alma42.mapgen.utils.voronoi;

import java.util.HashMap;
import java.util.Stack;

import com.alma42.mapgen.utils.geometry.Point;
import com.alma42.mapgen.utils.geometry.Rectangle;
import com.alma42.mapgen.utils.geometry.Trigonometry;

/**
 * The line segment connecting the two Sites is part of the Delaunay
 * triangulation; the line segment connecting the two Vertices is part of the
 * Voronoi diagram
 * 
 * @author ashaw
 * 
 */
public final class Edge {

  final private static Stack<Edge> _pool = new Stack<Edge>();

  /**
   * This is the only way to create a new Edge
   * 
   * @param site0
   * @param site1
   * @return
   * 
   */
  public static Edge createBisectingEdge(Site site0, Site site1) {
    double dx, dy, absdx, absdy;
    double a, b, c;

    dx = site1.get_x() - site0.get_x();
    dy = site1.get_y() - site0.get_y();
    absdx = dx > 0 ? dx : -dx;
    absdy = dy > 0 ? dy : -dy;
    c = site0.get_x() * dx + site0.get_y() * dy + (dx * dx + dy * dy) * 0.5;
    if (absdx > absdy) {
      a = 1.0;
      b = dy / dx;
      c /= dx;
    } else {
      b = 1.0;
      a = dx / dy;
      c /= dy;
    }

    Edge edge = Edge.create();

    edge.set_leftSite(site0);
    edge.set_rightSite(site1);
    site0.addEdge(edge);
    site1.addEdge(edge);

    edge._leftVertex = null;
    edge._rightVertex = null;

    edge.a = a;
    edge.b = b;
    edge.c = c;
    // trace("createBisectingEdge: a ", edge.a, "b", edge.b, "c", edge.c);

    return edge;
  }

  private static Edge create() {
    Edge edge;
    if (_pool.size() > 0) {
      edge = _pool.pop();
      edge.init();
    } else {
      edge = new Edge();
    }
    return edge;
  }

  /*
   * final private static LINESPRITE:Sprite = new Sprite();
   * final private static GRAPHICS:Graphics = LINESPRITE.graphics;
   * 
   * private var _delaunayLineBmp:BitmapData;
   * internal function get delaunayLineBmp():BitmapData
   * {
   * if (!_delaunayLineBmp)
   * {
   * _delaunayLineBmp = makeDelaunayLineBmp();
   * }
   * return _delaunayLineBmp;
   * }
   * 
   * // making this available to Voronoi; running out of memory in AIR so I cannot cache the bmp
   * internal function makeDelaunayLineBmp():BitmapData
   * {
   * var p0:Point = leftSite.coord;
   * var p1:Point = rightSite.coord;
   * 
   * GRAPHICS.clear();
   * // clear() resets line style back to undefined!
   * GRAPHICS.lineStyle(0, 0, 1.0, false, LineScaleMode.NONE, CapsStyle.NONE);
   * GRAPHICS.moveTo(p0.x, p0.y);
   * GRAPHICS.lineTo(p1.x, p1.y);
   * 
   * var w:int = int(Math.ceil(Math.max(p0.x, p1.x)));
   * if (w < 1)
   * {
   * w = 1;
   * }
   * var h:int = int(Math.ceil(Math.max(p0.y, p1.y)));
   * if (h < 1)
   * {
   * h = 1;
   * }
   * var bmp:BitmapData = new BitmapData(w, h, true, 0);
   * bmp.draw(LINESPRITE);
   * return bmp;
   * }
   */
  public LineSegment delaunayLine() {
    // draw a line connecting the input Sites for which the edge is a bisector:
    return new LineSegment(get_leftSite().get_coord(), get_rightSite().get_coord());
  }

  public LineSegment voronoiEdge() {
    if (!get_visible()) {
      return new LineSegment(null, null);
    }
    return new LineSegment(this._clippedVertices.get(LR.LEFT),
        this._clippedVertices.get(LR.RIGHT));
  }

  private static int       _nedges = 0;
  final public static Edge DELETED = new Edge();
  // the equation of the edge: ax + by = c
  public double            a, b, c;
  // the two Voronoi vertices that the edge connects
  // (if one of them is null, the edge extends to infinity)
  private Vertex           _leftVertex;

  public Vertex get_leftVertex() {
    return this._leftVertex;
  }

  private Vertex _rightVertex;

  public Vertex get_rightVertex() {
    return this._rightVertex;
  }

  public Vertex vertex(LR leftRight) {
    return (leftRight == LR.LEFT) ? this._leftVertex : this._rightVertex;
  }

  public void setVertex(LR leftRight, Vertex v) {
    if (leftRight == LR.LEFT) {
      this._leftVertex = v;
    } else {
      this._rightVertex = v;
    }
  }

  public boolean isPartOfConvexHull() {
    return (this._leftVertex == null || this._rightVertex == null);
  }

  public double sitesDistance() {
    return Trigonometry.distance(get_leftSite().get_coord(), get_rightSite().get_coord());
  }

  public static double compareSitesDistances_MAX(Edge edge0, Edge edge1) {
    double length0 = edge0.sitesDistance();
    double length1 = edge1.sitesDistance();
    if (length0 < length1) {
      return 1;
    }
    if (length0 > length1) {
      return -1;
    }
    return 0;
  }

  public static double compareSitesDistances(Edge edge0, Edge edge1) {
    return -compareSitesDistances_MAX(edge0, edge1);
  }

  // Once clipVertices() is called, this Dictionary will hold two Points
  // representing the clipped coordinates of the left and right ends...
  private HashMap<LR, Point> _clippedVertices;

  public HashMap<LR, Point> get_clippedEnds() {
    return this._clippedVertices;
  }

  // unless the entire Edge is outside the bounds.
  // In that case visible will be false:

  public boolean get_visible() {
    return this._clippedVertices != null;
  }

  // the two input Sites for which this Edge is a bisector:
  private HashMap<LR, Site> _sites;

  public void set_leftSite(Site s) {
    this._sites.put(LR.LEFT, s);
  }

  public Site get_leftSite() {
    return this._sites.get(LR.LEFT);
  }

  public void set_rightSite(Site s) {
    this._sites.put(LR.RIGHT, s);
  }

  public Site get_rightSite() {
    return this._sites.get(LR.RIGHT);
  }

  public Site site(LR leftRight) {
    return this._sites.get(leftRight);
  }

  private int _edgeIndex;

  public void dispose() {
    /*
     * if (_delaunayLineBmp)
     * {
     * _delaunayLineBmp.dispose();
     * _delaunayLineBmp = null;
     * }
     */
    this._leftVertex = null;
    this._rightVertex = null;
    if (this._clippedVertices != null) {
      this._clippedVertices.clear();
      this._clippedVertices = null;
    }
    this._sites.clear();
    this._sites = null;

    _pool.push(this);
  }

  private Edge() {
    this._edgeIndex = _nedges++;
    init();
  }

  private void init() {
    this._sites = new HashMap<LR, Site>();
  }

  public String toString() {
    return "Edge " + this._edgeIndex + "; sites " + this._sites.get(LR.LEFT) + ", " + this._sites.get(LR.RIGHT)
        + "; endVertices " + (this._leftVertex != null ? this._leftVertex.get_vertexIndex() : "null") + ", "
        + (this._rightVertex != null ? this._rightVertex.get_vertexIndex() : "null") + "::";
  }

  /**
   * Set _clippedVertices to contain the two ends of the portion of the
   * Voronoi edge that is visible within the bounds. If no part of the Edge
   * falls within the bounds, leave _clippedVertices null.
   * 
   * @param bounds
   * 
   */
  public void clipVertices(Rectangle bounds) {
    double xmin = bounds.x;
    double ymin = bounds.y;
    double xmax = bounds.right;
    double ymax = bounds.bottom;

    Vertex vertex0, vertex1;
    double x0, x1, y0, y1;

    if (this.a == 1.0 && this.b >= 0.0) {
      vertex0 = this._rightVertex;
      vertex1 = this._leftVertex;
    } else {
      vertex0 = this._leftVertex;
      vertex1 = this._rightVertex;
    }

    if (this.a == 1.0) {
      y0 = ymin;
      if (vertex0 != null && vertex0.get_y() > ymin) {
        y0 = vertex0.get_y();
      }
      if (y0 > ymax) {
        return;
      }
      x0 = this.c - this.b * y0;

      y1 = ymax;
      if (vertex1 != null && vertex1.get_y() < ymax) {
        y1 = vertex1.get_y();
      }
      if (y1 < ymin) {
        return;
      }
      x1 = this.c - this.b * y1;

      if ((x0 > xmax && x1 > xmax) || (x0 < xmin && x1 < xmin)) {
        return;
      }

      if (x0 > xmax) {
        x0 = xmax;
        y0 = (this.c - x0) / this.b;
      } else if (x0 < xmin) {
        x0 = xmin;
        y0 = (this.c - x0) / this.b;
      }

      if (x1 > xmax) {
        x1 = xmax;
        y1 = (this.c - x1) / this.b;
      } else if (x1 < xmin) {
        x1 = xmin;
        y1 = (this.c - x1) / this.b;
      }
    } else {
      x0 = xmin;
      if (vertex0 != null && vertex0.get_x() > xmin) {
        x0 = vertex0.get_x();
      }
      if (x0 > xmax) {
        return;
      }
      y0 = this.c - this.a * x0;

      x1 = xmax;
      if (vertex1 != null && vertex1.get_x() < xmax) {
        x1 = vertex1.get_x();
      }
      if (x1 < xmin) {
        return;
      }
      y1 = this.c - this.a * x1;

      if ((y0 > ymax && y1 > ymax) || (y0 < ymin && y1 < ymin)) {
        return;
      }

      if (y0 > ymax) {
        y0 = ymax;
        x0 = (this.c - y0) / this.a;
      } else if (y0 < ymin) {
        y0 = ymin;
        x0 = (this.c - y0) / this.a;
      }

      if (y1 > ymax) {
        y1 = ymax;
        x1 = (this.c - y1) / this.a;
      } else if (y1 < ymin) {
        y1 = ymin;
        x1 = (this.c - y1) / this.a;
      }
    }

    this._clippedVertices = new HashMap<LR, Point>();
    if (vertex0 == this._leftVertex) {
      this._clippedVertices.put(LR.LEFT, new Point(x0, y0));
      this._clippedVertices.put(LR.RIGHT, new Point(x1, y1));
    } else {
      this._clippedVertices.put(LR.RIGHT, new Point(x0, y0));
      this._clippedVertices.put(LR.LEFT, new Point(x1, y1));
    }
  }
}