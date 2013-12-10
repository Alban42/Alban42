package com.alma42.mapgen.pointSelectors;

import java.util.ArrayList;

import com.alma42.mapgen.utils.geometry.Point;

public interface IPointSelector {

  /**
   * Generate a grid of point of : {@code size x size}.
   * 
   * @param size
   *          the size of the grid (the grid will {@code size x size}).
   * @return an {@link ArrayList} of {@link Point}.
   */
  public ArrayList<Point> generatePoints(int size);
}