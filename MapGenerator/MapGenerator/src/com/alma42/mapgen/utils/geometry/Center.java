package com.alma42.mapgen.utils.geometry;

import java.util.ArrayList;

import com.alma42.mapgen.biomes.IBiome;

/**
 * @author Alban
 * 
 *         Represent the center of something.
 * 
 */
public class Center {

  public int               index;
  public Point             point;
  public ArrayList<Corner> corners   = new ArrayList<Corner>(); // set of polygon corners
  public ArrayList<Center> neighbors = new ArrayList<Center>(); // set of adjacent polygons
  public ArrayList<Edge>   borders   = new ArrayList<Edge>();  // set of bordering edges
  public boolean           border, ocean, water, coast;
  public double            elevation;
  public double            moisture;
  public double            area;
  public IBiome<?>         biome;

  public Center() {
  }

  public Center(Point point) {
    this.point = point;
  }

  public Edge lookupEdgeFromCenter(Center center) {
    Edge result = null;
    for (Edge edge : this.borders) {
      if (edge.d0.equals(center) || edge.d1.equals(center))
        result = edge;
    }
    return result;
  }

  public double getX() {
    return this.point.x;
  }

  public double getY() {
    return this.point.y;
  }

}
