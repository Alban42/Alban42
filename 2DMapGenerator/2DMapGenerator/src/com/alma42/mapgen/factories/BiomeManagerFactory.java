/**
 * 
 */
package com.alma42.mapgen.factories;

import com.alma42.mapgen.biomes.IBiomeManager;
import com.alma42.mapgen.biomes.implementations.island.IslandBiomeManager;

/**
 * @author Alban
 * 
 */
public class BiomeManagerFactory {

  public static final int ISLAND = 0;

  public static IBiomeManager createBiomeManager(int type) {
    IBiomeManager biomeManager = null;
    switch (type) {
      case ISLAND:
        biomeManager = new IslandBiomeManager();
        break;
      default:
        break;
    }

    return biomeManager;
  }

}
