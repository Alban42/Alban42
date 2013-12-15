package com.alma42.mapgen.biomes;

import com.alma42.mapgen.grid.shape.AShape;
import com.alma42.mapgen.utils.geometry.Center;

public interface IBiomeManager {

  public void assignBiome(Center center);

  public IBiome getBiome(AShape aShape);

}
