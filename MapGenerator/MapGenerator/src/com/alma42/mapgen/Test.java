package com.alma42.mapgen;

import com.alma42.mapgen.factories.BiomeManagerFactory;
import com.alma42.mapgen.factories.GraphFactory;
import com.alma42.mapgen.factories.IslandShapeFactory;
import com.alma42.mapgen.factories.PointSelectorFactory;
import com.alma42.mapgen.factories.RiverCreatorFactory;

public class Test {

  /**
   * @param args
   */
  public static void main(String[] args) {
    int size = 800;
    int pointNumber = 10000;
    int seed = (int) System.nanoTime();
    int pointSelectorType = PointSelectorFactory.RANDOM;
    int graphType = GraphFactory.VORONOI;
    int islandShapeType = IslandShapeFactory.RADIAL;
    int riverCreatorType = RiverCreatorFactory.RIVER;
    int biomeManagerType = BiomeManagerFactory.ISLAND;
    Map map = new Map(size, pointNumber, seed, pointSelectorType, graphType, islandShapeType, riverCreatorType,
        biomeManagerType);
    System.out.println("Paint ...");
    map.paint();
  }
}
