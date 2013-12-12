package com.alma42.mapgen.zone.selectors;

import java.util.ArrayList;

import com.alma42.mapgen.utils.geometry.Point;

public interface IPointSelector {

  /**
   * Generate a square grid of point of : {@code size x size}.
   * 
   * @param size
   *          the size of the grid (the grid will {@code size x size}).
   * @return an {@link ArrayList} of {@link Point}.
   */
  public ArrayList<Point> generatePoints(int size, int pointNumber);

  /**
   * The square and hex grid point selection remove randomness from
   * where the points are; we need to inject more randomness elsewhere
   * to make the maps look better. I do this in the corner
   * elevations. However I think more experimentation is needed.
   */
  public boolean needMoreRandomness();
}