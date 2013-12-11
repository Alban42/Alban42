package com.alma42.mapgen.zone_selectors.implemtations;

import java.util.ArrayList;

import com.alma42.mapgen.utils.Zone;
import com.alma42.mapgen.utils.geometry.Center;
import com.alma42.mapgen.utils.geometry.Point;
import com.alma42.mapgen.zone_selectors.IZoneSelector;

public class SquareZoneSelector implements IZoneSelector {

  @Override
  public ArrayList<Zone> generateZones(int size, int zoneNumber) {
    ArrayList<Zone> zones = new ArrayList<Zone>();
    double number = Math.sqrt(zoneNumber);
    for (int x = 0; x < number; x++) {
      for (int y = 0; y < number; y++) {
        zones.add(new Zone(new Center(new Point((0.5 + x) / number * size, (0.5 + y) / number * size)), size
            / zoneNumber));
      }
    }
    return zones;
  }
}
