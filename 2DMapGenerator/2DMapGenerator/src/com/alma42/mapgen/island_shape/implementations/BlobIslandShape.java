package com.alma42.mapgen.island_shape.implementations;

import com.alma42.mapgen.island_shape.IIslandShape;
import com.alma42.mapgen.utils.geometry.Point;

public class BlobIslandShape implements IIslandShape {

  private int size;

  public BlobIslandShape(int size) {
    this.size = size;
  }

  @Override
  public boolean isInside(Point point) {
    point = new Point(2 * (point.x / this.size - 0.5), 2 * (point.y / this.size - 0.5));

    boolean eye1, eye2, body;
    eye1 = new Point(point.x - 0.2, point.y / 2 + 0.2).length() < 0.05;
    eye2 = new Point(point.x + 0.2, point.y / 2 + 0.2).length() < 0.05;
    body = point.length() < 0.8 - 0.18 * Math.sin(5 * Math.atan2(point.y, point.x));
    return body && !eye1 && !eye2;
  }
}
