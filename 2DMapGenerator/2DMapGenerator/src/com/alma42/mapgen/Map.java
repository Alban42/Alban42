package com.alma42.mapgen;

import java.util.Random;

import com.alma42.mapgen.biomes.ABiomeManager;
import com.alma42.mapgen.biomes.factory.BiomeManagerFactory;
import com.alma42.mapgen.grid.Grid;

public class Map {

  private final ABiomeManager biomeManager;
  private Grid                grid;

  private final Random        random;
  private final int           shapeNumber;
  private final int           size;

  public Map(final int size, final int shapeNumber, final int biomeManagerType) {
    this(size, shapeNumber, biomeManagerType, (int) System.nanoTime());
  }

  public Map(final int size, final int shapeNumber, final int biomeManagerType, final int seed) {
    this.size = size;
    this.shapeNumber = shapeNumber;
    this.random = new Random(seed);
    this.biomeManager = BiomeManagerFactory.createBiomeManager(biomeManagerType, this.size, this.random);

  }

  public void createMap() {
    // Create the grid
    this.grid = new Grid(this.size, this.shapeNumber);

    // Assign biomes to the grid
    this.biomeManager.assignBiome(this.grid);
  }

  /**
   * @return the biomeManager
   */
  public ABiomeManager getBiomeManager() {
    return this.biomeManager;
  }

  /**
   * @return the grid
   */
  public Grid getGrid() {
    return this.grid;
  }
}
