package com.alma42.mapgen.point.implemtations;

import java.util.ArrayList;

import com.alma42.mapgen.point.IPointSelector;
import com.alma42.mapgen.utils.geometry.Point;

public class HexagonZoneSelector implements IPointSelector {

  @Override
  public ArrayList<Point> generatePoints(final int size, final int zoneNumber) {
    final ArrayList<Point> points = new ArrayList<Point>();
    final double number = Math.sqrt(zoneNumber);
    for (int x = 0; x < number; x++) {
      for (int y = 0; y < number; y++) {
        points.add(new Point(((0.5 + x) / number) * size, ((0.25 + ((0.5 * x) % 2) + y) / number) * size));
      }
    }
    return points;
  }

  @Override
  public boolean needMoreRandomness() {
    return true;
  }
}
