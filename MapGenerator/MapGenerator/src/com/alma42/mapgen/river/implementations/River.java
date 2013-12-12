package com.alma42.mapgen.river.implementations;

import java.util.ArrayList;
import java.util.Random;

import com.alma42.mapgen.river.IRiverCreator;
import com.alma42.mapgen.utils.geometry.Corner;
import com.alma42.mapgen.utils.geometry.Edge;

public class River implements IRiverCreator {

  @Override
  public void createRivers(ArrayList<Corner> corners, int size, Random seed) {
    Corner corner;
    Edge edge;
    for (int i = 0; i < size / 2; i++) {
      corner = corners.get(seed.nextInt(corners.size() - 1));
      if (corner.ocean || corner.elevation < 0.3 || corner.elevation > 0.9)
        continue;
      // Bias rivers to go west: if (q.downslope.x > q.x) continue;
      while (!corner.coast) {
        if (corner == corner.downslope) {
          break;
        }
        edge = corner.lookupEdgeFromCorner(corner.downslope);
        edge.river = edge.river + 1;
        corner.river = corner.river + 1;
        corner.downslope.river = corner.downslope.river + 1; // TODO: fix double count
        corner = corner.downslope;
      }
    }
  }

}
