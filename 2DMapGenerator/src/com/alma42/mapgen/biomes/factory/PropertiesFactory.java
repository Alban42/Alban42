package com.alma42.mapgen.biomes.factory;

import com.alma42.mapgen.biomes.IProperties;
import com.alma42.mapgen.biomes.implementations.island.IslandProperties;

public class PropertiesFactory {

  public static final int ISLAND = 0;

  public static IProperties createProperties(final int type) {
    IProperties properties = null;
    switch (type) {
      case ISLAND:
        properties = new IslandProperties();
        break;
      default:
        break;
    }

    return properties;
  }

}
