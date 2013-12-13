package com.alma42.mapgen.factories;

import java.util.Random;

import com.alma42.mapgen.island_shape.IIslandShape;
import com.alma42.mapgen.island_shape.implementations.BlobIslandShape;
import com.alma42.mapgen.island_shape.implementations.RadialIslandShape;
import com.alma42.mapgen.island_shape.implementations.SquareIslandShape;

public class IslandShapeFactory {

  public final static int RADIAL = 1;
  public final static int SQUARE = 2;
  public final static int BLOB   = 3;

  public static IIslandShape createIslandShape(int type, Random seed, int size) {
    IIslandShape islandShape = null;
    switch (type) {
      case SQUARE:
        islandShape = new SquareIslandShape();
        break;
      case RADIAL:
        islandShape = new RadialIslandShape(seed, size);
        break;
      case BLOB:
        islandShape = new BlobIslandShape(size);
        break;
      default:
        break;
    }

    return islandShape;
  }

}
