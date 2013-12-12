package com.alma42.mapgen.factories;

import java.util.Random;

import com.alma42.mapgen.graph.IGraph;
import com.alma42.mapgen.graph.implementations.VoronoiGraph;

public class GraphFactory {

  public static final int VORONOI = 0;

  public static IGraph creatGraph(int type, int size, Random seed, int pointNumber) {
    IGraph graph = null;
    switch (type) {
      case VORONOI:
        graph = new VoronoiGraph(size, seed, pointNumber);
        break;
      default:
        break;
    }
    return graph;
  }

}
