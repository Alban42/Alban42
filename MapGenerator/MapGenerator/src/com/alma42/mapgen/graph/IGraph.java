package com.alma42.mapgen.graph;

import java.util.ArrayList;

import com.alma42.mapgen.utils.geometry.Center;
import com.alma42.mapgen.utils.geometry.Corner;
import com.alma42.mapgen.utils.geometry.Edge;
import com.alma42.mapgen.utils.geometry.Point;
import com.alma42.mapgen.utils.geometry.Rectangle;

public interface IGraph {

  public void buildGraph(ArrayList<Point> point);

  public ArrayList<Center> getCenters();

  public ArrayList<Corner> getCorners();

  public ArrayList<Edge> getEdges();

  public Rectangle getBounds();
}
