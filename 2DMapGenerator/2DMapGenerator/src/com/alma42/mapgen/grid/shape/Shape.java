package com.alma42.mapgen.grid.shape;

import com.alma42.mapgen.biomes.IBiome;
import com.alma42.mapgen.biomes.IBiomeManager;
import com.alma42.mapgen.factories.BiomeManagerFactory;
import com.alma42.mapgen.grid.AGridComponent;
import com.alma42.mapgen.grid.Grid;
import com.alma42.mapgen.grid.coordinate.Coordinates;
import com.alma42.mapgen.utils.geometry.Point;

public class Shape extends AGridComponent {

  private IBiomeManager biomeManager;
  private IBiome<?>     biome;

  public Shape(final Grid parent, final Coordinates coordinates, final Point center, final double size) {
    super(parent, coordinates, center, size);
  }

  public Shape(final Grid parent, final Coordinates coordinates, final Point center, final double size,
      final int biomeManagerType) {
    this(parent, coordinates, center, size);
    this.biomeManager = BiomeManagerFactory.createBiomeManager(biomeManagerType);
  }

  public IBiome<?> getBiome() {
    if (this.biome == null) {
      this.biome = this.biomeManager.getBiome(this);
    }

    return this.biome;
  }

}
