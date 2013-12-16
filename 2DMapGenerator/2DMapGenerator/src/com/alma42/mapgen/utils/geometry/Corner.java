package com.alma42.mapgen.utils.geometry;

import java.util.ArrayList;

import com.alma42.mapgen.grid.AGridComponent;

/**
 * 
 * @author Alban
 * 
 */
public class Corner {

  public Point             point;
  public AGridComponent    parent;

  public ArrayList<Center> touches   = new ArrayList<Center>(); // set of polygons touching this corner
  public ArrayList<Corner> adjacent  = new ArrayList<Corner>(); // set of corners connected to this one
  public ArrayList<Edge>   protrudes = new ArrayList<Edge>();  // set of edges touching the corner

  public int               index, watershed_size;
  public boolean           border;
  public double            elevation;
  public boolean           water, ocean, coast;
  public Corner            downslope, watershed;
  public int               river;
  public double            moisture;

  public Corner(final AGridComponent parent, final Point point) {
    this.parent = parent;
    this.point = point;
  }

  public Edge lookupEdgeFromCorner(final Corner corner) {
    Edge result = null;

    for (final Edge edge : this.protrudes) {
      if (edge.v0.equals(corner) || edge.v1.equals(corner)) {
        result = edge;
        break;
      }
    }
    return result;
  }
}
