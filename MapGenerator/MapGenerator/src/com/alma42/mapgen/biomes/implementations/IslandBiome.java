/**
 * 
 */
package com.alma42.mapgen.biomes.implementations;

import com.alma42.mapgen.biomes.IBiome;

/**
 * @author Alban
 * 
 */
public class IslandBiome implements IBiome {

  private String biome;

  public IslandBiome(String biome) {
    super();
    this.biome = biome;
  }

  /**
   * @return the biome
   */
  public String getBiome() {
    return this.biome;
  }

  /**
   * @param biome
   *          the biome to set
   */
  public void setBiome(String biome) {
    this.biome = biome;
  }

}
