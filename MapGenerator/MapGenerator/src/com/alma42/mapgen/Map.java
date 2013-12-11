/**
 * 
 */
package com.alma42.mapgen;

import java.util.ArrayList;
import java.util.Random;

import com.alma42.mapgen.biomes.IBiomeManager;
import com.alma42.mapgen.island_shape.IIslandShape;
import com.alma42.mapgen.river.IRiverCreator;
import com.alma42.mapgen.utils.Zone;
import com.alma42.mapgen.utils.geometry.Corner;
import com.alma42.mapgen.utils.geometry.Edge;
import com.alma42.mapgen.utils.geometry.Point;

/**
 * @author Alban
 * 
 */
public class Map {

  /**
   * 0 to 1, fraction of water corners for water polygon
   */
  public static final double LAKE_THRESHOLD = 0.3;

  // Passed in by the caller:
  public int                 SIZE;

  public int                 numPoints;

  public Random              seed;

  // These store the graph data
  public ArrayList<Zone>     zones;
  public ArrayList<Corner>   corners;
  public ArrayList<Edge>     edges;

  private IIslandShape       islandShape;
  private IBiomeManager      biomeManager;
  private IRiverCreator      riverCreator;

  /**
   * Create rivers along edges. Pick a random corner point, then
   * move downslope. Mark the edges and corners as rivers.
   */
  public void createRivers() {
    this.riverCreator.createRivers(this.corners, this.SIZE, this.seed);
  }

  /**
   * Calculate moisture. Freshwater sources spread moisture: rivers
   * and lakes (not oceans). Saltwater sources have moisture but do
   * not spread it (we set it at the end, after propagation).
   */
  public void assignCornerMoisture() {
    double newMoisture;
    ArrayList<Corner> queue = new ArrayList<Corner>();
    // Fresh water
    for (Corner corner : this.corners) {
      if ((corner.water || corner.river > 0) && !corner.ocean) {
        corner.moisture = corner.river > 0 ? Math.min(3.0, (0.2 * corner.river)) : 1.0;
        queue.add(corner);
      } else {
        corner.moisture = 0.0;
      }
    }

    for (Corner corner : queue) {
      for (Corner adjacentCorner : corner.adjacent) {
        newMoisture = corner.moisture * 0.9;
        if (newMoisture > adjacentCorner.moisture) {
          adjacentCorner.moisture = newMoisture;
          queue.add(adjacentCorner);
        }
      }
    }
    // Salt water
    for (Corner corner : this.corners) {
      if (corner.ocean || corner.coast) {
        corner.moisture = 1.0;
      }
    }
  }

  /**
   * Polygon moisture is the average of the moisture at corners
   */
  public void assignMoisture() {
    for (Zone zone : this.zones) {
      zone.getCenter().assignMoisture();
    }
  }

  /**
   * Assign biomes to all zones.
   */
  public void assignBiomes() {
    for (Zone zone : this.zones) {
      zone.setBiome(this.biomeManager.assignBiome(zone));
    }
  }

  /**
   * Determine whether a given zone should be on the island or in the water.
   * 
   * @param zone
   *          the zone to determine.
   * @return true if the zone should be on the island.
   */
  public boolean isInside(Zone zone) {
    return this.islandShape.isInside(new Point(2 * (zone.getCenter().getX() / this.SIZE - 0.5), 2 * (zone
        .getCenter().getY() / this.SIZE - 0.5)));
  }
}
