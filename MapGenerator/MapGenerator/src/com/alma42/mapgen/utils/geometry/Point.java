package com.alma42.mapgen.utils.geometry;

public class Point {

  public double  x, y;
  private double length;

  public Point(double x, double y) {
    this.x = x;
    this.y = y;
    // this.length = Trigonometry.distance(new Point(0, 0), this);
  }

  @Override
  public String toString() {
    return this.x + ", " + this.y;
  }

  public double l2() {
    return this.x * this.x + this.y * this.y;
  }

  public double length() {
    return Math.sqrt(this.x * this.x + this.y * this.y);
  }

  public static Point interpolate(final Point pt1, final Point pt2, final double f) {
    final double d = pt1.x - pt2.x;
    final double x = pt2.x + f * d;
    final double y = pt2.y + (x - pt2.x) * ((pt1.y - pt2.y) / d);
    return new Point(x, y);
  }
}
