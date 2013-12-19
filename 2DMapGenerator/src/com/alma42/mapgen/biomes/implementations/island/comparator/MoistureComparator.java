package com.alma42.mapgen.biomes.implementations.island.comparator;

import java.util.Comparator;

import com.alma42.mapgen.biomes.implementations.island.IslandBiomeManager;
import com.alma42.mapgen.utils.geometry.Corner;

public class MoistureComparator implements Comparator<Corner> {

  @Override
  public int compare(final Corner arg0, final Corner arg1) {
    int result = 0;
    if (IslandBiomeManager.getProperties(arg0).getMoisture() < IslandBiomeManager.getProperties(arg1).getMoisture()) {
      result = -1;
    } else if (IslandBiomeManager.getProperties(arg0).getMoisture() > IslandBiomeManager.getProperties(arg1)
        .getMoisture()) {
      result = 1;
    }
    return result;
  }

}
