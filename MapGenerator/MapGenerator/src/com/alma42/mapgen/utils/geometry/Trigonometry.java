package com.alma42.mapgen.utils.geometry;

public class Trigonometry {

  public static double distance(Point _coord, Point _coord0) {
    return Math.sqrt((_coord.x - _coord0.x) * (_coord.x - _coord0.x) + (_coord.y - _coord0.y) * (_coord.y - _coord0.y));
  }

}
