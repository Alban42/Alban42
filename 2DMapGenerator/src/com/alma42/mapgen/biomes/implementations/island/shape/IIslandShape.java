package com.alma42.mapgen.biomes.implementations.island.shape;

import com.alma42.mapgen.utils.geometry.Point;

/**
 * This class has factory functions for generating islands of
 * different shapes. The factory returns a function that takes a
 * normalized point (x and y are -1 to +1) and returns true if the
 * point should be on the island, and false if it should be water
 * (lake or ocean).
 */
public interface IIslandShape {

  /**
   * The 'inside' function that tells us whether
   * a point should be on the island or in the water.
   * 
   * @param point
   *          the point to check
   * @return true if the point is into the island.
   */
  public boolean isInside(Point point);

}
