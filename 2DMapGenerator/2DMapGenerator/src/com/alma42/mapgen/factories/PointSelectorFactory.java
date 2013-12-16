package com.alma42.mapgen.factories;

import java.util.Random;

import com.alma42.mapgen.grid.AGrid;
import com.alma42.mapgen.point.APointSelector;
import com.alma42.mapgen.point.implemtations.HexagonZoneSelector;
import com.alma42.mapgen.point.implemtations.SquareZoneSelector;

public class PointSelectorFactory {

  public final static int HEXAGON = 0;
  public final static int SQUARE  = 1;

  public static APointSelector createPointSelector(final int type, final Random random, final AGrid grid,
      final int biomeManagerType) {
    APointSelector pointSelector = null;
    switch (type) {
      case HEXAGON:
        pointSelector = new HexagonZoneSelector(grid, biomeManagerType);
        break;
      case SQUARE:
        pointSelector = new SquareZoneSelector(grid, biomeManagerType);
        break;
      default:
        break;
    }

    return pointSelector;
  }

  private PointSelectorFactory() {

  }
}
