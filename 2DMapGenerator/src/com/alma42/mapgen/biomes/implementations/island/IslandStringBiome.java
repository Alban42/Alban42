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
    BEACH("BB"),
    COAST("CC"),
    GRASSLAND("GG"),
    ICE("II"),
    LAKE("LL"),
    LAKESHORE("LS"),
    MARSH("MM"),
    OCEAN(" "),
    SCORCHED("SC"),
    SHRUBLAND("SL"),
    SNOW("SN"),
    SUBTROPICAL_DESERT("SD"),
    TAIGA("TT"),
    TEMPERATE_DECIDUOUS_FOREST("TF"),
    TEMPERATE_DESERT("TD"),
    TEMPERATE_RAIN_FOREST("RF"),
    TROPICAL_RAIN_FOREST("RF"),
    TROPICAL_SEASONAL_FOREST("SF"),
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
