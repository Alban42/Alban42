package com.alma42.mapgen.factories;

import com.alma42.mapgen.zone.types.IZoneType;
import com.alma42.mapgen.zone.types.implementations.Island;

public class ZoneTypeFactory {

  public static IZoneType createIsland(int size, int pointNumber, int seed, int pointSelectorType, int graphType,
      int islandShapeType, int riverCreatorType, int biomeManagerType) {
    return new Island(size, pointNumber, seed, pointSelectorType, graphType, islandShapeType, riverCreatorType,
        biomeManagerType);
  }
}
