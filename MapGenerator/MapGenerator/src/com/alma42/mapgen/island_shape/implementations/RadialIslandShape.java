/**
 * 
 */
package com.alma42.mapgen.island_shape.implementations;

import java.util.Random;

import com.alma42.mapgen.island_shape.IIslandShape;
import com.alma42.mapgen.utils.geometry.Point;

/**
 * @author Alban
 * 
 */
public class RadialIslandShape implements IIslandShape {

  // The radial island radius is based on overlapping sine waves
  static public double ISLAND_FACTOR = 1.07; // 1.0 means no small islands; 2.0 leads to a lot
  private double       startAngle, dipAngle, dipWidth;
  private int          bumps;

  public RadialIslandShape(Random seed) {
    this.bumps = seed.nextInt(5) + 1;
    this.startAngle = seed.nextDouble() * 2 * Math.PI;
    this.dipAngle = seed.nextDouble() * 2 * Math.PI;
    this.dipWidth = (seed.nextDouble() * .5) + .2;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.alma42.mapgen.islandShape.IIslandShape#isInside(com.alma42.mapgen.utils.geometry.Point)
   */
  @Override
  public boolean isInside(Point point) {
    double angle = Math.atan2(point.y, point.x);
    double length = 0.5 * (Math.max(Math.abs(point.x), Math.abs(point.y)) + point.length());

    double r1 = 0.5 + 0.40 * Math.sin(this.startAngle + this.bumps * angle + Math.cos((this.bumps + 3) * angle));
    double r2 = 0.7 - 0.20 * Math.sin(this.startAngle + this.bumps * angle - Math.sin((this.bumps + 2) * angle));
    if (Math.abs(angle - this.dipAngle) < this.dipWidth
        || Math.abs(angle - this.dipAngle + 2 * Math.PI) < this.dipWidth
        || Math.abs(angle - this.dipAngle - 2 * Math.PI) < this.dipWidth) {
      r1 = r2 = 0.2;
    }
    return (length < r1 || (length > r1 * ISLAND_FACTOR && length < r2));
  }

}
