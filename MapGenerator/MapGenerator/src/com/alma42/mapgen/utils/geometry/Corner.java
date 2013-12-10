package com.alma42.mapgen.utils.geometry;

import java.util.ArrayList;

/**
 * 
 * @author Alban
 * 
 */
public class Corner {

  public ArrayList<Center> touches   = new ArrayList<Center>(); // set of polygons touching this corner
  public ArrayList<Corner> adjacent  = new ArrayList<Corner>(); // set of corners connected to this one
  public ArrayList<Edge>   protrudes = new ArrayList<Edge>();  // set of edges touching the corner
  public Point             loc;
  public int               index;
  public boolean           border;
  public double            elevation;
  public boolean           water, ocean, coast;
  public Corner            downslope;
  public int               river;
  public double            moisture;
}
