package com.alma42.mapgen;

import java.util.Random;

import com.alma42.mapgen.factories.BiomeManagerFactory;
import com.alma42.mapgen.factories.GridFactory;
import com.alma42.mapgen.factories.IslandShapeFactory;
import com.alma42.mapgen.factories.PointSelectorFactory;
import com.alma42.mapgen.grid.AGrid;
import com.alma42.mapgen.grid.shape.AShape;
import com.alma42.mapgen.island_shape.IIslandShape;
import com.alma42.mapgen.point.APointSelector;
import com.alma42.mapgen.utils.geometry.Point;

public class Test2 {

  private static boolean isInside(final IIslandShape islandShape, final Point point) {
    return islandShape.isInside(point);
  }

  public static void main(final String[] args) {
    final int size = 600;
    final int shapeNumber = 10000;
    final int seed = (int) System.nanoTime();
    final int pointSelectorType = PointSelectorFactory.SQUARE;
    final int islandShapeType = IslandShapeFactory.RADIAL;
    final int biomeManagerType = BiomeManagerFactory.ISLAND;
    final int GridType = GridFactory.SQUARE;

    final Random random = new Random(seed);
    final AGrid grid = GridFactory.createGrid(GridType, size, shapeNumber);
    final APointSelector pointSelector = PointSelectorFactory.createPointSelector(pointSelectorType, random,
        grid, biomeManagerType);
    final IIslandShape islandShape = IslandShapeFactory.createIslandShape(islandShapeType, random, size);

    pointSelector.generateShapes();
    int x = -1;
    String result = "";
    System.out.println("TOTAL : " + grid.getGrid().size());
    for (final AShape shape : grid.getGrid().values()) {
      // System.out.println(shape.toString());
      // System.out.println(shape.getCenter().toString());
      if (shape.getCoordinates().getX() != x) {
        result += "\n";
        x = shape.getCoordinates().getX();
      }

      if (isInside(islandShape, shape.getCenter())) {
        result += "L";
      } else {
        result += "W";
      }
    }

    // System.out.println(result);
  }
}
