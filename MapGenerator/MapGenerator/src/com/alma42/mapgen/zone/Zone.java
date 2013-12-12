package com.alma42.mapgen.zone;

import com.alma42.mapgen.utils.geometry.Center;
import com.alma42.mapgen.utils.geometry.Point;
import com.alma42.mapgen.zone.types.IZoneType;

public class Zone {

  private Center    center;
  private IZoneType zoneType;

  public Zone(int size, IZoneType zoneType) {
    this.zoneType = zoneType;
    this.center = new Center(new Point(size / 2, size / 2));
  }

  public void createZone() {
    this.zoneType.createZone();
  }

  /**
   * @return the center
   */
  public Center getCenter() {
    return this.center;
  }
}
