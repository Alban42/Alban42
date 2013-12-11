/**
 * 
 */
package com.alma42.mapgen.island_shape.implementations;

import com.alma42.mapgen.island_shape.IIslandShape;
import com.alma42.mapgen.utils.geometry.Point;
import com.alma42.mapgen.utils.perlin.PerlinNoiseGenerator;

/**
 * @author Alban
 * 
 */
public class PerlinIslandShape implements IIslandShape {

  private PerlinNoiseGenerator perlin;

  public PerlinIslandShape(int seed) {
    this.perlin = new PerlinNoiseGenerator(seed);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.alma42.mapgen.islandShape.IIslandShape#isInside(com.alma42.mapgen.utils.geometry.Point)
   */
  @Override
  public boolean isInside(Point point) {
    final double c = (this.perlin.noise2(
        Float.valueOf(String.valueOf((point.x + 1) * 128)),
        Float.valueOf(String.valueOf((point.y + 1) * 128)))) / 255.0;
    return c > (0.3 + 0.3 * point.length * point.length);
  }
}
