package com.alma42.mapgen.grid.comparator;

import java.util.Comparator;

import com.alma42.mapgen.grid.coordinate.Coordinates;

public class CoordinateNeighborComparator implements Comparator<Coordinates> {

  @Override
  public int compare(final Coordinates o1, final Coordinates o2) {
    int result = 0;
    if (o1.getX() < o2.getX()) {
      result = -1;
    } else if (o1.getX() > o2.getX()) {
      result = 1;
    } else {
      if (o1.getY() < o2.getY()) {
        result = -1;
      } else if (o1.getY() > o2.getY()) {
        result = 1;
      }
    }
    return result;
  }

}
