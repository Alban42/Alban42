package com.alma42.mapgen.factories;

import java.util.Random;

import com.alma42.mapgen.islandShape.IIslandShape;
import com.alma42.mapgen.islandShape.implementations.PerlinIslandShape;
import com.alma42.mapgen.islandShape.implementations.RadialIslandShape;
import com.alma42.mapgen.islandShape.implementations.SquareIslandShape;

public class IslandShapeFactory {

  public final static int PERLIN = 0;
  public final static int RADIAL = 1;
  public final static int SQUARE = 2;

  private IslandShapeFactory() {

  }

  public static IIslandShape createIslandShape(int type, int seed) {
    IIslandShape islandShape = null;
    switch (type) {
      case PERLIN:
        islandShape = new PerlinIslandShape(seed);
        break;
      case SQUARE:
        islandShape = new SquareIslandShape();
        break;
      case RADIAL:
        islandShape = new RadialIslandShape(new Random(seed));
        break;
      default:
        break;
    }

    return islandShape;
  }

}
