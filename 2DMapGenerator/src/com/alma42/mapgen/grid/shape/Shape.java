package com.alma42.mapgen.grid.shape;

import com.alma42.mapgen.biomes.IBiome;
import com.alma42.mapgen.grid.AGridComponent;
import com.alma42.mapgen.grid.Grid;
import com.alma42.mapgen.grid.coordinate.Coordinates;
import com.alma42.mapgen.utils.geometry.Point;

public class Shape extends AGridComponent {

  private IBiome<?> biome;

  public Shape(final Grid parent, final Coordinates coordinates, final Point center, final double size) {
    super(parent, coordinates, center, size);
  }

  public Shape(final Grid parent, final Coordinates coordinates, final Point center, final double size,
      final IBiome<?> biome) {
    this(parent, coordinates, center, size);
    this.biome = biome;
  }

  public IBiome<?> getBiome() {
    return this.biome;
  }

  /**
   * @param biome
   *          the biome to set
   */
  public void setBiome(final IBiome<?> biome) {
    this.biome = biome;
  }

}
