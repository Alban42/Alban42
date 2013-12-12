package com.alma42.mapgen.utils.geometry;

/**
 * 
 * @author Alban
 * 
 */
public class Edge {

  public int    index;
  public Center d0, d1;  // Delaunay edge
  public Corner v0, v1;  // Voronoi edge
  public Point  midpoint; // halfway between v0,v1
  public int    river;

  public void setVornoi(Corner v0, Corner v1) {
    this.v0 = v0;
    this.v1 = v1;
    this.midpoint = new Point((v0.point.x + v1.point.x) / 2, (v0.point.y + v1.point.y) / 2);
  }
}
