package com.alma42.mapgen;

import java.util.Random;

import com.alma42.mapgen.factories.BiomeManagerFactory;
import com.alma42.mapgen.factories.IslandShapeFactory;
import com.alma42.mapgen.grid.AGridComponent;
import com.alma42.mapgen.grid.Grid;
import com.alma42.mapgen.island_shape.IIslandShape;
import com.alma42.mapgen.utils.geometry.Point;

public class Test2 {

  private static boolean isInside(final IIslandShape islandShape, final Point point) {

    return islandShape.isInside(point);
  }

  public static void main(final String[] args) {
    final int size = 600;
    final int shapeNumber = 10000;
    final int seed = (int) System.nanoTime();
    final int islandShapeType = IslandShapeFactory.RADIAL;
    final int biomeManagerType = BiomeManagerFactory.ISLAND;

    final Random random = new Random(seed);
    final IIslandShape islandShape = IslandShapeFactory.createIslandShape(islandShapeType, random, size);

    final Grid grid = new Grid(size, shapeNumber);
    grid.populate();
    System.out.println("NUMBER : " + grid.getChilds().size());
    int x = -1;
    String result = "";
    for (final AGridComponent child : grid.getChilds().values()) {
      if (child.getCoordinates().getX() != x) {
        result += "\n";
        x = child.getCoordinates().getX();
      }

      if (isInside(islandShape, child.getCenter())) {
        result += "L";
      } else {
        result += "W";
      }
    }

    System.out.println(result);
  }
}
