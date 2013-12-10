package com.alma42.mapgen.utils.geometry;

import java.util.ArrayList;

/**
 * @author Alban
 * 
 *         Represent the center of something.
 * 
 */
public class Center {

  public int               index;
  public Point             loc;
  public ArrayList<Corner> corners   = new ArrayList<Corner>(); // set of polygon corners
  public ArrayList<Center> neighbors = new ArrayList<Center>(); // set of adjacent polygons
  public ArrayList<Edge>   borders   = new ArrayList<Edge>();  // set of bordering edges
  public boolean           border, ocean, water, coast;
  public double            elevation;
  public double            moisture;
  public Enum<?>           biome;
  public double            area;

  public Center() {
  }

  public Center(Point loc) {
    this.loc = loc;
  }
}
