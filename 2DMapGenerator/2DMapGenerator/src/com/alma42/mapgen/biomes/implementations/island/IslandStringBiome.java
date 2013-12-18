/**
 * 
 */
package com.alma42.mapgen.biomes.implementations.island;

import com.alma42.mapgen.biomes.IBiome;

/**
 * @author Alban
 * 
 */
public class IslandStringBiome implements IBiome<String> {

  enum StringData {

    BARE("BA"),
    BEACH("B"),
    COAST("C"),
    GRASSLAND("G"),
    ICE("I"),
    LAKE("L"),
    LAKESHORE("LS"),
    MARSH("M"),
    OCEAN("O"),
    SCORCHED("S"),
    SHRUBLAND("S"),
    SHURBLAND("S"),
    SNOW("S"),
    SUBTROPICAL_DESERT("D"),
    TAIGA("T"),
    TEMPERATE_DECIDUOUS_FOREST("TF"),
    TEMPERATE_DESERT("TD"),
    TEMPERATE_RAIN_FOREST("TRF"),
    TROPICAL_RAIN_FOREST("TRF"),
    TROPICAL_SEASONAL_FOREST("TSF"),
    TUNDRA("TU");
    String string;

    StringData(final String string) {
      this.string = string;
    }

  }

  private final StringData biome;

  public IslandStringBiome(final StringData biome) {
    this.biome = biome;
  }

  @Override
  public String getValue() {
    return this.biome.string;
  }
}
