/**
 * 
 */
package com.alma42.mapgen.pointSelectors.implemtations;

import java.util.ArrayList;
import java.util.Random;

import com.alma42.mapgen.pointSelectors.IPointSelector;
import com.alma42.mapgen.utils.geometry.Point;

/**
 * @author Alban
 * 
 */
public class RandomPointSelector implements IPointSelector {

  private Random seed;

  public RandomPointSelector(Random seed) {
    this.seed = seed;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.alma42.mapgen.pointSelectors.IPointSelector#generatePoints(int)
   */
  @Override
  public ArrayList<Point> generatePoints(int size) {
    Point p;
    ArrayList<Point> points = new ArrayList<Point>();
    for (int i = 0; i < size; i++) {
      p = new Point((seed.nextDouble() * size - 10) + 10,
          (seed.nextDouble() * size - 10) + 10);
      points.add(p);
    }

    return points;
  }
}
