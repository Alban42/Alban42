package com.alma42.mapgen.utils.geometry;

public class Point {

  public static double distance(Point _coord, Point _coord0) {
    return Math.sqrt((_coord.x - _coord0.x) * (_coord.x - _coord0.x) + (_coord.y - _coord0.y) * (_coord.y - _coord0.y));
  }

  public double x, y, length;

  public Point(double x, double y) {
    this.x = x;
    this.y = y;
    this.length = distance(new Point(0, 0), this);
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
}
