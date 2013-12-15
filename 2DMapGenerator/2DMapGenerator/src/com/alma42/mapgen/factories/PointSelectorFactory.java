package com.alma42.mapgen.factories;

import java.util.Random;

import com.alma42.mapgen.biomes.IBiomeManager;
import com.alma42.mapgen.grid.AGrid;
import com.alma42.mapgen.point.APointSelector;
import com.alma42.mapgen.point.implemtations.HexagonZoneSelector;
import com.alma42.mapgen.point.implemtations.SquareZoneSelector;

public class PointSelectorFactory {

  public final static int HEXAGON = 0;
  public final static int SQUARE  = 1;

  public static APointSelector createPointSelector(final int type, final Random random, final int size,
      final int shapeNumber, final AGrid grid, final IBiomeManager biomeManager) {
    APointSelector pointSelector = null;
    switch (type) {
      case HEXAGON:
        pointSelector = new HexagonZoneSelector(size, shapeNumber, grid, biomeManager);
        break;
      case SQUARE:
        pointSelector = new SquareZoneSelector(size, shapeNumber, grid, biomeManager);
        break;
      default:
        break;
    }

    return pointSelector;
  }

  private PointSelectorFactory() {

  }
}
