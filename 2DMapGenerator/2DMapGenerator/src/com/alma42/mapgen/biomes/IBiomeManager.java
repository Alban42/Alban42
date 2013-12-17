package com.alma42.mapgen.biomes;

import com.alma42.mapgen.grid.AGridComponent;

public interface IBiomeManager {

  public void assignBiome(AGridComponent gridComponent);

  public IBiome getBiome(AGridComponent gridComponent);

}
