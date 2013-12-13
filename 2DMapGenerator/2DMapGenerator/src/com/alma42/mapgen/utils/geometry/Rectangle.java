package com.alma42.mapgen.utils.geometry;

/**
 * Rectangle.java
 */
public class Rectangle {

  final public double x, y, width, height, right, bottom, left, top;

  public Rectangle(double x, double y, double width, double height) {
    this.left = this.x = x;
    this.top = this.y = y;
    this.width = width;
    this.height = height;
    this.right = x + width;
    this.bottom = y + height;
  }

  public boolean liesOnAxes(Point p) {
    return Trigonometry.closeEnough(p.x, this.x, 1) || Trigonometry.closeEnough(p.y, this.y, 1)
        || Trigonometry.closeEnough(p.x, this.right, 1)
        || Trigonometry.closeEnough(p.y, this.bottom, 1);
  }

  public boolean inBounds(Point p) {
    return inBounds(p.x, p.y);
  }

  public boolean inBounds(double x0, double y0) {
    if (x0 < this.x || x0 > this.right || y0 < this.y || y0 > this.bottom) {
      return false;
    }
    return true;
  }
}
