package com.alma42.mapgen.island_shape.implementations;

import com.alma42.mapgen.island_shape.IIslandShape;
import com.alma42.mapgen.utils.geometry.Point;

public class BlobIslandShape implements IIslandShape {

  @Override
  public boolean isInside(Point point) {
    boolean eye1, eye2, body;
    eye1 = new Point(point.x - 0.2, point.y / 2 + 0.2).length() < 0.05;
    eye2 = new Point(point.x + 0.2, point.y / 2 + 0.2).length() < 0.05;
    body = point.length() < 0.8 - 0.18 * Math.sin(5 * Math.atan2(point.y, point.x));
    return body && !eye1 && !eye2;
  }
}
