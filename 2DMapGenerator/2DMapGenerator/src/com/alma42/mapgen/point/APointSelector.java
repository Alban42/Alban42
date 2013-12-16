package com.alma42.mapgen.point;

import java.util.ArrayList;

import com.alma42.mapgen.grid.AGrid;
import com.alma42.mapgen.utils.geometry.Point;

public abstract class APointSelector {

  protected final int   size;
  protected final int   shapeNumber;
  protected final AGrid grid;
  protected final int   biomeManagerType;

  /**
   * @param size
   * @param shapeNumber
   * @param grid
   * @param biomeManagerType
   */
  public APointSelector(final AGrid grid, final int biomeManagerType) {
    this.grid = grid;
    this.size = grid.getSize();
    this.shapeNumber = grid.getShapeNumber();
    this.biomeManagerType = biomeManagerType;
  }

  /**
   * Generate a square grid of point of : {@code size x size}.
   * 
   * @return an {@link ArrayList} of {@link Point}.
   */
  public abstract ArrayList<Point> generatePoints();

  public abstract void generateShapes();

  /**
   * The square and hex grid point selection remove randomness from where the points are; we need to inject more
   * randomness elsewhere to make the maps look better. I do this in the corner elevations. However I think more
   * experimentation is needed.
   */
  public boolean needMoreRandomness() {
    return false;
  }
}