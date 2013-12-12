package com.alma42.mapgen.factories;

import java.util.Random;

import com.alma42.mapgen.island_shape.IIslandShape;
import com.alma42.mapgen.island_shape.implementations.PerlinIslandShape;
import com.alma42.mapgen.island_shape.implementations.RadialIslandShape;
import com.alma42.mapgen.island_shape.implementations.SquareIslandShape;

public class IslandShapeFactory {

  public final static int PERLIN = 0;
  public final static int RADIAL = 1;
  public final static int SQUARE = 2;

  private IslandShapeFactory() {

  }

  public static IIslandShape createIslandShape(int type, Random seed) {
    IIslandShape islandShape = null;
    switch (type) {
      case PERLIN:
        islandShape = new PerlinIslandShape(seed);
        break;
      case SQUARE:
        islandShape = new SquareIslandShape();
        break;
      case RADIAL:
        islandShape = new RadialIslandShape(seed);
        break;
      default:
        break;
    }

    return islandShape;
  }

}
