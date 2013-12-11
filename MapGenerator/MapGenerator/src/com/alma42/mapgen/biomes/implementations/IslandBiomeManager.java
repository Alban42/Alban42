package com.alma42.mapgen.biomes.implementations;

import com.alma42.mapgen.biomes.IBiome;
import com.alma42.mapgen.biomes.IBiomeManager;
import com.alma42.mapgen.utils.Zone;
import com.alma42.mapgen.utils.geometry.Center;

public class IslandBiomeManager implements IBiomeManager {

  @Override
  public IBiome assignBiome(Zone zone) {
    IBiome biome = null;
    Center p = zone.getCenter();
    if (p.ocean) {
      biome = new IslandBiome("OCEAN");
    } else if (p.water) {
      if (p.elevation < 0.1)
        biome = new IslandBiome("MARSH");
      if (p.elevation > 0.8)
        biome = new IslandBiome("ICE");
      biome = new IslandBiome("LAKE");
    } else if (p.coast) {
      biome = new IslandBiome("BEACH");
    } else if (p.elevation > 0.8) {
      if (p.moisture > 0.50)
        biome = new IslandBiome("SNOW");
      else if (p.moisture > 0.33)
        biome = new IslandBiome("TUNDRA");
      else if (p.moisture > 0.16)
        biome = new IslandBiome("BARE");
      else
        biome = new IslandBiome("SCORCHED");
    } else if (p.elevation > 0.6) {
      if (p.moisture > 0.66)
        biome = new IslandBiome("TAIGA");
      else if (p.moisture > 0.33)
        biome = new IslandBiome("SHRUBLAND");
      else
        biome = new IslandBiome("TEMPERATE_DESERT");
    } else if (p.elevation > 0.3) {
      if (p.moisture > 0.83)
        biome = new IslandBiome("TEMPERATE_RAIN_FOREST");
      else if (p.moisture > 0.50)
        biome = new IslandBiome("TEMPERATE_DECIDUOUS_FOREST");
      else if (p.moisture > 0.16)
        biome = new IslandBiome("GRASSLAND");
      else
        biome = new IslandBiome("TEMPERATE_DESERT");
    } else {
      if (p.moisture > 0.66)
        biome = new IslandBiome("TROPICAL_RAIN_FOREST");
      else if (p.moisture > 0.33)
        biome = new IslandBiome("TROPICAL_SEASONAL_FOREST");
      else if (p.moisture > 0.16)
        biome = new IslandBiome("GRASSLAND");
      else
        biome = new IslandBiome("SUBTROPICAL_DESERT");
    }

    return biome;
  }
}
