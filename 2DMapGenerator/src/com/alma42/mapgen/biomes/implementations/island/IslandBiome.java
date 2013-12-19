/**
 * 
 */
package com.alma42.mapgen.biomes.implementations.island;

import com.alma42.mapgen.biomes.IBiome;

/**
 * @author Alban
 * 
 */
public class IslandBiome implements IBiome<Integer> {

  enum ColorData {

    BARE(0x888888), BEACH(0xa09077), COAST(0x33335a), GRASSLAND(0x88aa55),
    ICE(0x99ffff), LAKE(0x336699), LAKESHORE(0x225588), MARSH(0x2f6666),
    OCEAN(0x44447a), SCORCHED(0x555555),
    SHRUBLAND(0x889977), SHURBLAND(0x889977),
    SNOW(0xffffff), SUBTROPICAL_DESERT(0xd2b98b), TAIGA(0x99aa77),
    TEMPERATE_DECIDUOUS_FOREST(0x679459), TEMPERATE_DESERT(0xc9d29b), TEMPERATE_RAIN_FOREST(0x448855),
    TROPICAL_RAIN_FOREST(0x337755), TROPICAL_SEASONAL_FOREST(0x559944),
    TUNDRA(0xbbbbaa);
    int color;

    ColorData(final int color) {
      // this.color = new Color(color);
      this.color = color;
    }

  }

  private final ColorData biome;

  public IslandBiome(final ColorData biome) {
    this.biome = biome;
  }

  @Override
  public Integer getValue() {
    return this.biome.color;
  }
}
