package com.alma42.mapgen.point;

import java.util.ArrayList;

import com.alma42.mapgen.biomes.IBiomeManager;
import com.alma42.mapgen.grid.AGrid;
import com.alma42.mapgen.utils.geometry.Point;

public abstract class APointSelector {

  protected final int           size;
  protected final int           shapeNumebr;
  protected final AGrid         grid;
  protected final IBiomeManager biomeManager;

  /**
   * @param size
   * @param shapeNumebr
   * @param grid
   * @param biomeManager
   */
  public APointSelector(final int size, final int shapeNumebr, final AGrid grid, final IBiomeManager biomeManager) {
    super();
    this.size = size;
    this.shapeNumebr = shapeNumebr;
    this.grid = grid;
    this.biomeManager = biomeManager;
  }

  /**
   * Generate a square grid of point of : {@code size x size}.
   * 
   * @param size
   *          the size of the grid (the grid will {@code size x size}).
   * @return an {@link ArrayList} of {@link Point}.
   */
  public abstract ArrayList<Point> generatePoints(int size, int pointNumber);

  public abstract void generatePoints(int size, int shapeNumber, AGrid aGrid);

  /**
   * The square and hex grid point selection remove randomness from
   * where the points are; we need to inject more randomness elsewhere
   * to make the maps look better. I do this in the corner
   * elevations. However I think more experimentation is needed.
   */
  public boolean needMoreRandomness() {
    return false;
  }
}