package com.alma42.mapgen.grid.shape;

import com.alma42.mapgen.biomes.IBiome;
import com.alma42.mapgen.biomes.IBiomeManager;
import com.alma42.mapgen.factories.BiomeManagerFactory;
import com.alma42.mapgen.factories.GridFactory;
import com.alma42.mapgen.grid.AGrid;
import com.alma42.mapgen.grid.coordinate.Coordinates;
import com.alma42.mapgen.utils.geometry.Point;

public abstract class AShape {

  private final AGrid         parent;
  private AGrid               subgrid;

  private final IBiomeManager biomeManager;
  private IBiome<?>           biome;

  private final Coordinates   coordinates;
  private final Point         center;

  public AShape(final AGrid parent, final int biomeManagerType, final Coordinates coordinates, final Point center) {
    this.parent = parent;
    this.biomeManager = BiomeManagerFactory.createBiomeManager(biomeManagerType);
    this.coordinates = coordinates;
    this.center = center;
  }

  public AGrid createSubGrid(final int gridType, final int size, final int shapeNumber) {
    if (this.subgrid == null) {
      this.subgrid = GridFactory.createGrid(gridType, size, shapeNumber);
    }
    return this.subgrid;
  }

  public IBiome<?> getBiome() {
    if (this.biome == null) {
      this.biome = this.biomeManager.getBiome(this);
    }

    return this.biome;
  }

  /**
   * @return the center
   */
  public Point getCenter() {
    return this.center;
  }

  /**
   * @return the coordinates
   */
  public Coordinates getCoordinates() {
    return this.coordinates;
  }

  /**
   * @return the parent
   */
  public AGrid getParent() {
    return this.parent;
  }

  /**
   * @return the grid
   */
  public AGrid getSubgrid() {
    return this.subgrid;
  }

  @Override
  public String toString() {
    return this.coordinates.toString();
  }

}
