package com.alma42.mapgen.biomes;

import com.alma42.mapgen.grid.AGridComponent;

public abstract class ABiomeManager {

  protected AGridComponent gridComponent;
  protected int            propertiesType;

  public ABiomeManager(final int propertiesType) {
    this.propertiesType = propertiesType;
  }

  public void assignBiome(final AGridComponent gridComponent) {
    this.gridComponent = gridComponent;
    this.gridComponent.setProperties(this.propertiesType);
    execAssignBiome();
  }

  protected abstract void execAssignBiome();

  public abstract IBiome getBiome(final AGridComponent gridComponent);

}
