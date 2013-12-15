package com.alma42.mapgen.biomes.implementations;

import java.awt.Color;

import com.alma42.mapgen.biomes.IBiome;
import com.alma42.mapgen.biomes.IBiomeManager;
import com.alma42.mapgen.biomes.implementations.IslandBiome.ColorData;
import com.alma42.mapgen.grid.shape.AShape;
import com.alma42.mapgen.utils.geometry.Center;

public class IslandBiomeManager implements IBiomeManager {

  @Override
  public void assignBiome(final Center center) {
    IBiome<Color> biome = null;
    if (center.ocean) {
      biome = new IslandBiome(ColorData.OCEAN);
    } else if (center.water) {
      if (center.elevation < 0.1) {
        biome = new IslandBiome(ColorData.MARSH);
      }
      if (center.elevation > 0.8) {
        biome = new IslandBiome(ColorData.ICE);
      }
      biome = new IslandBiome(ColorData.LAKE);
    } else if (center.coast) {
      biome = new IslandBiome(ColorData.BEACH);
    } else if (center.elevation > 0.8) {
      if (center.moisture > 0.50) {
        biome = new IslandBiome(ColorData.SNOW);
      } else if (center.moisture > 0.33) {
        biome = new IslandBiome(ColorData.TUNDRA);
      } else if (center.moisture > 0.16) {
        biome = new IslandBiome(ColorData.BARE);
      } else {
        biome = new IslandBiome(ColorData.SCORCHED);
      }
    } else if (center.elevation > 0.6) {
      if (center.moisture > 0.66) {
        biome = new IslandBiome(ColorData.TAIGA);
      } else if (center.moisture > 0.33) {
        biome = new IslandBiome(ColorData.SHRUBLAND);
      } else {
        biome = new IslandBiome(ColorData.TEMPERATE_DESERT);
      }
    } else if (center.elevation > 0.3) {
      if (center.moisture > 0.83) {
        biome = new IslandBiome(ColorData.TEMPERATE_RAIN_FOREST);
      } else if (center.moisture > 0.50) {
        biome = new IslandBiome(ColorData.TEMPERATE_DECIDUOUS_FOREST);
      } else if (center.moisture > 0.16) {
        biome = new IslandBiome(ColorData.GRASSLAND);
      } else {
        biome = new IslandBiome(ColorData.TEMPERATE_DESERT);
      }
    } else {
      if (center.moisture > 0.66) {
        biome = new IslandBiome(ColorData.TROPICAL_RAIN_FOREST);
      } else if (center.moisture > 0.33) {
        biome = new IslandBiome(ColorData.TROPICAL_SEASONAL_FOREST);
      } else if (center.moisture > 0.16) {
        biome = new IslandBiome(ColorData.GRASSLAND);
      } else {
        biome = new IslandBiome(ColorData.SUBTROPICAL_DESERT);
      }
    }

    center.biome = biome;
  }

  @Override
  public IBiome getBiome(final AShape aShape) {

    return null;
  }
}
