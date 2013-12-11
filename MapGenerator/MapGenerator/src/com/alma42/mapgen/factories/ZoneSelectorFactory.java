package com.alma42.mapgen.factories;

import java.util.Random;

import com.alma42.mapgen.zone_selectors.IZoneSelector;
import com.alma42.mapgen.zone_selectors.implemtations.HexagonZoneSelector;
import com.alma42.mapgen.zone_selectors.implemtations.RandomZoneSelector;
import com.alma42.mapgen.zone_selectors.implemtations.SquareZoneSelector;

public class ZoneSelectorFactory {

  public final static int HEXAGON = 0;
  public final static int SQUARE  = 1;
  public final static int RANDOM  = 2;

  private ZoneSelectorFactory() {

  }

  public static IZoneSelector createZoneSelector(int type, int seed) {
    IZoneSelector pointSelector = null;
    switch (type) {
      case HEXAGON:
        pointSelector = new HexagonZoneSelector();
        break;
      case SQUARE:
        pointSelector = new SquareZoneSelector();
        break;
      case RANDOM:
        pointSelector = new RandomZoneSelector(new Random(seed));
        break;
      default:
        break;
    }

    return pointSelector;
  }
}
