package com.alma42.mapgen.factories;

import com.alma42.mapgen.pointSelectors.IPointSelector;
import com.alma42.mapgen.pointSelectors.implemtations.HexagonPointSelector;
import com.alma42.mapgen.pointSelectors.implemtations.SquarePointSelector;

public class PointSelectorFactory {

  public final static int HEXAGON = 0;
  public final static int SQUARE  = 1;

  private PointSelectorFactory() {

  }

  public static IPointSelector createPointSelector(int type) {
    IPointSelector pointSelector = null;
    switch (type) {
      case HEXAGON:
        pointSelector = new HexagonPointSelector();
        break;
      case SQUARE:
        pointSelector = new SquarePointSelector();
        break;
      default:
        break;
    }

    return pointSelector;
  }
}
