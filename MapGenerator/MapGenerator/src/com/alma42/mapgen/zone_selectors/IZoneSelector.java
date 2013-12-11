package com.alma42.mapgen.zone_selectors;

import java.util.ArrayList;

import com.alma42.mapgen.utils.Zone;

public interface IZoneSelector {

  /**
   * Generate a square grid of point of : {@code size x size}.
   * 
   * @param size
   *          the size of the grid (the grid will {@code size x size}).
   * @return an {@link ArrayList} of {@link Zone}.
   */
  public ArrayList<Zone> generateZones(int size, int zoneNumber);
}