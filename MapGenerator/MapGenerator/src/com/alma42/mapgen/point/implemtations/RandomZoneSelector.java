/**
 * 
 */
package com.alma42.mapgen.point.implemtations;

import java.util.ArrayList;
import java.util.Random;

import com.alma42.mapgen.point.IPointSelector;
import com.alma42.mapgen.utils.geometry.Point;

/**
 * @author Alban
 * 
 */
public class RandomZoneSelector implements IPointSelector {

  private Random seed;

  public RandomZoneSelector(Random seed) {
    this.seed = seed;
  }

  @Override
  public ArrayList<Point> generatePoints(int size, int zoneNumber) {
    ArrayList<Point> points = new ArrayList<Point>();
    for (int i = 0; i < zoneNumber; i++) {
      points.add(new Point((this.seed.nextDouble() * size - 10) + 10,
          (this.seed.nextDouble() * size - 10) + 10));
    }

    return points;
  }

  @Override
  public boolean needMoreRandomness() {
    return false;
  }
}
