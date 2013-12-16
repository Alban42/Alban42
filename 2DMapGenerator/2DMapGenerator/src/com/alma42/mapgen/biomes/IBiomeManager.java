package com.alma42.mapgen.biomes;

import com.alma42.mapgen.grid.shape.Shape;
import com.alma42.mapgen.utils.geometry.Center;

public interface IBiomeManager {

  public void assignBiome(Center center);

  public IBiome getBiome(Shape aShape);

}
