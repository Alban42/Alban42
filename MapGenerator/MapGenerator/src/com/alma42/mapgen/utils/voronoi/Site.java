package com.alma42.mapgen.utils.voronoi;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Stack;

import com.alma42.mapgen.utils.geometry.Point;
import com.alma42.mapgen.utils.geometry.Rectangle;
import com.alma42.mapgen.utils.geometry.Trigonometry;

public final class Site implements ICoord {

  private static Stack<Site> _pool = new Stack<Site>();

  public static Site create(Point p, int index, double weight, Color color) {
    if (_pool.size() > 0) {
      return _pool.pop().init(p, index, weight, color);
    }
    return new Site(p, index, weight, color);
  }

  public static void sortSites(ArrayList<Site> sites) {
    // sites.sort(Site.compare);
    Collections.sort(sites, new Comparator<Site>() {
      @Override
      public int compare(Site o1, Site o2) {
        return (int) Site.compare(o1, o2);
      }
    });
  }

  /**
   * sort sites on y, then x, coord also change each site's _siteIndex to
   * match its new position in the list so the _siteIndex can be used to
   * identify the site for nearest-neighbor queries
   * 
   * haha "also" - means more than one responsibility...
   * 
   */
  static double compare(Site s1, Site s2) {
    int returnValue = Voronoi.compareByYThenX(s1, s2);

    // swap _siteIndex values if necessary to match new ordering:
    int tempIndex;
    if (returnValue == -1) {
      if (s1._siteIndex > s2._siteIndex) {
        tempIndex = s1._siteIndex;
        s1._siteIndex = s2._siteIndex;
        s2._siteIndex = tempIndex;
      }
    } else if (returnValue == 1) {
      if (s2._siteIndex > s1._siteIndex) {
        tempIndex = s2._siteIndex;
        s2._siteIndex = s1._siteIndex;
        s1._siteIndex = tempIndex;
      }

    }

    return returnValue;
  }

  final private static double EPSILON = .005;

  private static boolean closeEnough(Point p0, Point p1) {
    return Trigonometry.distance(p0, p1) < EPSILON;
  }

  private Point _coord;

  @Override
  public Point get_coord() {
    return this._coord;
  }

  public Color             color;
  public double            weight;
  private int              _siteIndex;
  // the edges that define this Site's Voronoi region:
  public ArrayList<Edge>   _edges;
  // which end of each edge hooks up with the previous edge in _edges:
  private ArrayList<LR>    _edgeOrientations;
  // ordered list of points that define the region clipped to bounds:
  private ArrayList<Point> _region;

  public Site(Point p, int index, double weight, Color color) {
    init(p, index, weight, color);
  }

  private Site init(Point p, int index, double weight, Color color) {
    this._coord = p;
    this._siteIndex = index;
    this.weight = weight;
    this.color = color;
    this._edges = new ArrayList<Edge>();
    this._region = null;
    return this;
  }

  @Override
  public String toString() {
    return "Site " + this._siteIndex + ": " + get_coord();
  }

  public void dispose() {
    this._coord = null;
    clear();
    _pool.push(this);
  }

  private void clear() {
    if (this._edges != null) {
      this._edges.clear();
      this._edges = null;
    }
    if (this._edgeOrientations != null) {
      this._edgeOrientations.clear();
      this._edgeOrientations = null;
    }
    if (this._region != null) {
      this._region.clear();
      this._region = null;
    }
  }

  void addEdge(Edge edge) {
    this._edges.add(edge);
  }

  public Edge nearestEdge() {
    // _edges.sort(Edge.compareSitesDistances);
    Collections.sort(this._edges, new Comparator<Edge>() {
      @Override
      public int compare(Edge o1, Edge o2) {
        return (int) Edge.compareSitesDistances(o1, o2);
      }
    });
    return this._edges.get(0);
  }

  ArrayList<Site> neighborSites() {
    if (this._edges == null || this._edges.isEmpty()) {
      return new ArrayList<Site>();
    }
    if (this._edgeOrientations == null) {
      reorderEdges();
    }
    ArrayList<Site> list = new ArrayList<Site>();
    for (Edge edge : this._edges) {
      list.add(neighborSite(edge));
    }
    return list;
  }

  private Site neighborSite(Edge edge) {
    if (this == edge.get_leftSite()) {
      return edge.get_rightSite();
    }
    if (this == edge.get_rightSite()) {
      return edge.get_leftSite();
    }
    return null;
  }

  ArrayList<Point> region(Rectangle clippingBounds) {
    if (this._edges == null || this._edges.isEmpty()) {
      return new ArrayList<Point>();
    }
    if (this._edgeOrientations == null) {
      reorderEdges();
      this._region = clipToBounds(clippingBounds);
      if ((new Polygon(this._region)).winding() == Winding.CLOCKWISE) {
        Collections.reverse(this._region);
      }
    }
    return this._region;
  }

  private void reorderEdges() {
    // trace("_edges:", _edges);
    EdgeReorderer reorderer = new EdgeReorderer(this._edges, Vertex.class);
    this._edges = reorderer.get_edges();
    // trace("reordered:", _edges);
    this._edgeOrientations = reorderer.get_edgeOrientations();
    reorderer.dispose();
  }

  private ArrayList<Point> clipToBounds(Rectangle bounds) {
    ArrayList<Point> points = new ArrayList<Point>();
    int n = this._edges.size();
    int i = 0;
    Edge edge;
    while (i < n && (this._edges.get(i).get_visible() == false)) {
      ++i;
    }

    if (i == n) {
      // no edges visible
      return new ArrayList<Point>();
    }
    edge = this._edges.get(i);
    LR orientation = this._edgeOrientations.get(i);
    points.add(edge.get_clippedEnds().get(orientation));
    points.add(edge.get_clippedEnds().get((LR.other(orientation))));

    for (int j = i + 1; j < n; ++j) {
      edge = this._edges.get(j);
      if (edge.get_visible() == false) {
        continue;
      }
      connect(points, j, bounds, false);
    }
    // close up the polygon by adding another corner point of the bounds if needed:
    connect(points, i, bounds, true);

    return points;
  }

  private void connect(ArrayList<Point> points, int j, Rectangle bounds, boolean closingUp) {
    Point rightPoint = points.get(points.size() - 1);
    Edge newEdge = this._edges.get(j);
    LR newOrientation = this._edgeOrientations.get(j);
    // the point that must be connected to rightPoint:
    Point newPoint = newEdge.get_clippedEnds().get(newOrientation);
    if (!closeEnough(rightPoint, newPoint)) {
      // The points do not coincide, so they must have been clipped at the bounds;
      // see if they are on the same border of the bounds:
      if (rightPoint.x != newPoint.x
          && rightPoint.y != newPoint.y) {
        // They are on different borders of the bounds;
        // insert one or two corners of bounds as needed to hook them up:
        // (NOTE this will not be correct if the region should take up more than
        // half of the bounds rect, for then we will have gone the wrong way
        // around the bounds and included the smaller part rather than the larger)
        int rightCheck = BoundsCheck.check(rightPoint, bounds);
        int newCheck = BoundsCheck.check(newPoint, bounds);
        double px, py;
        if ((rightCheck & BoundsCheck.RIGHT) != 0) {
          px = bounds.right;
          if ((newCheck & BoundsCheck.BOTTOM) != 0) {
            py = bounds.bottom;
            points.add(new Point(px, py));
          } else if ((newCheck & BoundsCheck.TOP) != 0) {
            py = bounds.top;
            points.add(new Point(px, py));
          } else if ((newCheck & BoundsCheck.LEFT) != 0) {
            if (rightPoint.y - bounds.y + newPoint.y - bounds.y < bounds.height) {
              py = bounds.top;
            } else {
              py = bounds.bottom;
            }
            points.add(new Point(px, py));
            points.add(new Point(bounds.left, py));
          }
        } else if ((rightCheck & BoundsCheck.LEFT) != 0) {
          px = bounds.left;
          if ((newCheck & BoundsCheck.BOTTOM) != 0) {
            py = bounds.bottom;
            points.add(new Point(px, py));
          } else if ((newCheck & BoundsCheck.TOP) != 0) {
            py = bounds.top;
            points.add(new Point(px, py));
          } else if ((newCheck & BoundsCheck.RIGHT) != 0) {
            if (rightPoint.y - bounds.y + newPoint.y - bounds.y < bounds.height) {
              py = bounds.top;
            } else {
              py = bounds.bottom;
            }
            points.add(new Point(px, py));
            points.add(new Point(bounds.right, py));
          }
        } else if ((rightCheck & BoundsCheck.TOP) != 0) {
          py = bounds.top;
          if ((newCheck & BoundsCheck.RIGHT) != 0) {
            px = bounds.right;
            points.add(new Point(px, py));
          } else if ((newCheck & BoundsCheck.LEFT) != 0) {
            px = bounds.left;
            points.add(new Point(px, py));
          } else if ((newCheck & BoundsCheck.BOTTOM) != 0) {
            if (rightPoint.x - bounds.x + newPoint.x - bounds.x < bounds.width) {
              px = bounds.left;
            } else {
              px = bounds.right;
            }
            points.add(new Point(px, py));
            points.add(new Point(px, bounds.bottom));
          }
        } else if ((rightCheck & BoundsCheck.BOTTOM) != 0) {
          py = bounds.bottom;
          if ((newCheck & BoundsCheck.RIGHT) != 0) {
            px = bounds.right;
            points.add(new Point(px, py));
          } else if ((newCheck & BoundsCheck.LEFT) != 0) {
            px = bounds.left;
            points.add(new Point(px, py));
          } else if ((newCheck & BoundsCheck.TOP) != 0) {
            if (rightPoint.x - bounds.x + newPoint.x - bounds.x < bounds.width) {
              px = bounds.left;
            } else {
              px = bounds.right;
            }
            points.add(new Point(px, py));
            points.add(new Point(px, bounds.top));
          }
        }
      }
      if (closingUp) {
        // newEdge's ends have already been added
        return;
      }
      points.add(newPoint);
    }
    Point newRightPoint = newEdge.get_clippedEnds().get(LR.other(newOrientation));
    if (!closeEnough(points.get(0), newRightPoint)) {
      points.add(newRightPoint);
    }
  }

  public double get_x() {
    return this._coord.x;
  }

  public double get_y() {
    return this._coord.y;
  }

  public double dist(ICoord p) {
    return Trigonometry.distance(p.get_coord(), this._coord);
  }
}

final class BoundsCheck {

  final public static int TOP    = 1;
  final public static int BOTTOM = 2;
  final public static int LEFT   = 4;
  final public static int RIGHT  = 8;

  /**
   * 
   * @param point
   * @param bounds
   * @return an int with the appropriate bits set if the Point lies on the
   *         corresponding bounds lines
   * 
   */
  public static int check(Point point, Rectangle bounds) {
    int value = 0;
    if (point.x == bounds.left) {
      value |= LEFT;
    }
    if (point.x == bounds.right) {
      value |= RIGHT;
    }
    if (point.y == bounds.top) {
      value |= TOP;
    }
    if (point.y == bounds.bottom) {
      value |= BOTTOM;
    }
    return value;
  }

  public BoundsCheck() {
    throw new Error("BoundsCheck constructor unused");
  }
}
