package com.alma42.mapgen;

import java.util.Random;

import com.alma42.mapgen.biomes.factory.BiomeManagerFactory;
import com.alma42.mapgen.biomes.implementations.island.shape.IIslandShape;
import com.alma42.mapgen.biomes.implementations.island.shape.factory.IslandShapeFactory;
import com.alma42.mapgen.grid.AGridComponent;
import com.alma42.mapgen.grid.Grid;
import com.alma42.mapgen.utils.geometry.Corner;
import com.alma42.mapgen.utils.geometry.Point;

public class Test2 {

  private static boolean isInside(final IIslandShape islandShape, final Point point) {

    return islandShape.isInside(point);
  }

  public static void main(final String[] args) {
    final int size = 1000;
    final int shapeNumber = 10000;
    final int biomeManagerType = BiomeManagerFactory.ISLAND;

    // testMapGen();
    final com.alma42.mapgen.Map map = new com.alma42.mapgen.Map(size, shapeNumber, biomeManagerType);
    map.createMap();
    // for (final AGridComponent child : map.getGrid().getChilds().values()) {
    // System.out.println(child.getCoordinates().toString());
    // for (final Corner corner : child.getCorners().values()) {
    // System.out.println("\t - " + corner.toString());
    // }
    // System.out.println(child.getProperties().toString());
    // }

    double x = -1;
    String result = "";
    for (final AGridComponent child : map.getGrid().getChilds().values()) {
      if (child.getCoordinates().getX() != x) {
        result += "\n";
        x = child.getCoordinates().getX();
      }

      result += map.getBiomeManager().getBiome(child).getValue();
    }

    System.out.println(result);
  }

  private static void testMapGen() {
    final int size = 100;
    final int shapeNumber = 100;
    final int seed = (int) System.nanoTime();
    final int islandShapeType = IslandShapeFactory.RADIAL;
    final int biomeManagerType = BiomeManagerFactory.ISLAND;

    final Random random = new Random(seed);
    final IIslandShape islandShape = IslandShapeFactory.createIslandShape(islandShapeType, random, size);

    final Grid grid = new Grid(size, shapeNumber);
    System.out.println("NUMBER : " + grid.getChilds().size());
    double x = -1;
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

    for (final Corner child : grid.getAllCorners()) {
      if (child.getCoordinates().getX() != x) {
        result += "\n";
        x = child.getCoordinates().getX();
      }

      if (isInside(islandShape, child.getPoint())) {
        result += "L";
      } else {
        result += "W";
      }
    }

    System.out.println(result);
    // final AGridComponent child = grid.getChild(new Coordinates(1, 1));
    // System.out.println("CHILD : " + child.toString());
    // final Map<Coordinates, Corner> corners = child.getCorners();
    // System.out.println("CORNERS : " + corners.values().toString());
    // for (final Coordinates coord : corners.keySet()) {
    // System.out.print("Coordinates : " + coord.toString());
    // System.out.println(corners.get(coord).getAdjacents().toString());
    // }
    // System.out.println("CORNERS : " + corners.toString());
    // final Map<Coordinates, AGridComponent> neighbors = child.getNeighbors();
    // System.out.println("NEIGHBORS : " + neighbors.toString());
    // final Map<Coordinates, Edge> borders = child.getBorders();
    // for (final Coordinates coord : borders.keySet()) {
    // System.out.print("Coordinates : " + coord.toString());
    // System.out.println(borders.get(coord).toString());
    // }
  }
}
