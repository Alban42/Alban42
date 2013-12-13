package com.alma42.mapgen.factories;

import java.util.Random;

import com.alma42.mapgen.point.IPointSelector;
import com.alma42.mapgen.point.implemtations.HexagonZoneSelector;
import com.alma42.mapgen.point.implemtations.SquareZoneSelector;

public class PointSelectorFactory {

  public final static int HEXAGON = 0;
  public final static int SQUARE  = 1;

  private PointSelectorFactory() {

  }

  public static IPointSelector createPointSelector(int type, Random seed) {
    IPointSelector pointSelector = null;
    switch (type) {
      case HEXAGON:
        pointSelector = new HexagonZoneSelector();
        break;
      case SQUARE:
        pointSelector = new SquareZoneSelector();
        break;
      default:
        break;
    }

    return pointSelector;
  }
}
