package com.alma42.mapgen;

import java.util.ArrayList;
import java.util.Random;

import com.alma42.mapgen.factories.IslandShapeFactory;
import com.alma42.mapgen.factories.PointSelectorFactory;
import com.alma42.mapgen.island_shape.IIslandShape;
import com.alma42.mapgen.point.IPointSelector;
import com.alma42.mapgen.utils.geometry.Point;

public class Test2 {

  public static void main(String[] args) {
    int size = 600;
    int pointNumber = 10000;
    int seed = (int) System.nanoTime();
    int pointSelectorType = PointSelectorFactory.SQUARE;
    int islandShapeType = IslandShapeFactory.RADIAL;

    Random random = new Random(seed);
    IPointSelector pointSelector = PointSelectorFactory.createPointSelector(pointSelectorType, random);
    IIslandShape islandShape = IslandShapeFactory.createIslandShape(islandShapeType, random, size);

    ArrayList<Point> points = pointSelector.generatePoints(size, pointNumber);
    System.out.println("Points size : " + points.size());
    double x = -1;
    String result = "";
    for (Point point : points) {
      if (point.x != x) {
        result += "\n";
        x = point.x;
      }

      if (isInside(islandShape, point)) {
        result += "L";
      } else {
        result += "W";
      }

    }

    System.out.println(result);
  }

  private static boolean isInside(IIslandShape islandShape, Point point) {
    return islandShape.isInside(point);
  }
}
