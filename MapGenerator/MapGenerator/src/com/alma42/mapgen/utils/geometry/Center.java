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
  public double            area;

  public Center() {
  }

  public Center(Point loc) {
    this.loc = loc;
  }

  public Edge lookupEdgeFromCenter(Center center) {
    Edge result = null;
    for (Edge edge : this.borders) {
      if (edge.d0.equals(center) || edge.d1.equals(center))
        result = edge;
    }
    return result;
  }

  // Moisture is the average of the moisture at corners
  public void assignMoisture() {
    double sumMoisture;
    sumMoisture = 0.0;
    for (Corner corner : this.corners) {
      if (corner.moisture > 1.0) {
        corner.moisture = 1.0;
      }
      sumMoisture += corner.moisture;
    }
    this.moisture = sumMoisture / this.corners.size();
  }

  public double getX() {
    return this.loc.x;
  }

  public double getY() {
    return this.loc.y;
  }
}
