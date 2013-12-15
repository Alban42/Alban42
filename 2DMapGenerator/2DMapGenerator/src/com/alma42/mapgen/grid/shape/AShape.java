package com.alma42.mapgen.grid.shape;

import com.alma42.mapgen.biomes.IBiome;
import com.alma42.mapgen.biomes.IBiomeManager;
import com.alma42.mapgen.factories.BiomeManagerFactory;
import com.alma42.mapgen.grid.AGrid;
import com.alma42.mapgen.utils.geometry.Point;

public abstract class AShape {

  private final AGrid         parent;
  private AGrid               subgrid;
  private final IBiomeManager biomeManager;
  private IBiome<?>           biome;

  private final Point         point;


  public AShape(final AGrid parent, final int biomeManagerType, final Point point) {
    super();
    this.parent = parent;
    this.biomeManager = BiomeManagerFactory.createBiomeManager(biomeManagerType);
    this.point = point;
  }

  public IBiome<?> getBiome() {
    if (this.biome == null) {
      this.biome = this.biomeManager.getBiome(this);
    }

    return this.biome;
  }

  /**
   * @return the grid
   */
  public AGrid getSubgrid() {
    return this.subgrid;
  }

}
