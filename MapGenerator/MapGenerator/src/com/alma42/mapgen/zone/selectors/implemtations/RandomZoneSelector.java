/**
 * 
 */
package com.alma42.mapgen.zone.selectors.implemtations;

import java.util.ArrayList;
import java.util.Random;

import com.alma42.mapgen.utils.geometry.Center;
import com.alma42.mapgen.utils.geometry.Point;
import com.alma42.mapgen.zone.Zone;
import com.alma42.mapgen.zone.selectors.IZoneSelector;

/**
 * @author Alban
 * 
 */
public class RandomZoneSelector implements IZoneSelector {

  private Random seed;

  public RandomZoneSelector(Random seed) {
    this.seed = seed;
  }

  @Override
  public ArrayList<Zone> generateZones(int size, int zoneNumber) {
    ArrayList<Zone> zones = new ArrayList<Zone>();
    for (int i = 0; i < zoneNumber; i++) {
      zones.add(new Zone(new Center(new Point((this.seed.nextDouble() * size - 10) + 10,
          (this.seed.nextDouble() * size - 10) + 10)), size / zoneNumber));
    }

    return zones;
  }

  @Override
  public boolean needMoreRandomness() {
    return false;
  }
}
