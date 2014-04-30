/**
 * 
 */
package com.alma42.mapgen.biomes.factory;

import java.util.Random;

import com.alma42.mapgen.biomes.ABiomeManager;
import com.alma42.mapgen.biomes.implementations.island.IslandBiomeManager;

/**
 * @author Alban
 * 
 */
public class BiomeManagerFactory {

  public static final int ISLAND = 0;

  public static ABiomeManager createBiomeManager(final int type, final int size, final Random random) {
    ABiomeManager biomeManager = null;
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
