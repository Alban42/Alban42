/**
 * 
 */
package com.alma42.mapgen.biomes.factory;

import java.util.Random;

import com.alma42.mapgen.biomes.IBiomeManager;
import com.alma42.mapgen.biomes.implementations.island.IslandBiomeManager;

/**
 * @author Alban
 * 
 */
public class BiomeManagerFactory {

  public static final int ISLAND = 0;

  public static IBiomeManager createBiomeManager(final int type, final int size, final Random random) {
    IBiomeManager biomeManager = null;
    switch (type) {
      case ISLAND:
        biomeManager = new IslandBiomeManager(size, random);
        break;
      default:
        break;
    }

    return biomeManager;
  }

}
