package com.alma42.mapgen.pointSelectors.implemtations;

import java.util.ArrayList;

import com.alma42.mapgen.pointSelectors.IPointSelector;
import com.alma42.mapgen.utils.geometry.Point;

public class HexagonPointSelector implements IPointSelector {

  @Override
  public ArrayList<Point> generatePoints(int size) {
    ArrayList<Point> points = new ArrayList<Point>();
    double number = Math.sqrt(size);
    for (int x = 0; x < number; x++) {
      for (int y = 0; y < number; y++) {
        points.add(new Point((0.5 + x) / number * size, (0.25 + 0.5 * x % 2 + y) / number * size));
      }
    }
    return points;
  }
}
