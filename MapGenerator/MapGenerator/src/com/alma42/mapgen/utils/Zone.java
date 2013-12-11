package com.alma42.mapgen.utils;

import java.util.ArrayList;

import com.alma42.mapgen.biomes.IBiome;
import com.alma42.mapgen.biomes.IBiomeManager;
import com.alma42.mapgen.utils.geometry.Center;
import com.alma42.mapgen.utils.geometry.Point;

public class Zone {

  private int             size;
  private Center          center;
  private ArrayList<Zone> zones;
  private IBiome          biome;
  private IBiomeManager   biomeManager;

  public Zone(int size) {
    this.size = size;
    this.center = new Center(new Point(size / 2, size / 2));
    this.zones = new ArrayList<Zone>();
  }

  public Zone(Center center, int size) {
    this(size);
    this.center = center;
  }

  public Zone(Center center, int size, IBiome biome) {
    this(center, size);
    this.biome = biome;
  }

  public Zone(Center center, int size, IBiomeManager biomeManager) {
    this(center, size);
    this.biomeManager = biomeManager;
  }

  /**
   * @return the center
   */
  public Center getCenter() {
    return this.center;
  }

  /**
   * @param center
   *          the center to set
   */
  public void setCenter(Center center) {
    this.center = center;
  }

  /**
   * @return the zones
   */
  public ArrayList<Zone> getZones() {
    return this.zones;
  }

  /**
   * @param zones
   *          the zones to set
   */
  public void addZone(Zone zone) {
    this.zones.add(zone);
  }

  /**
   * @return the biome
   */
  public IBiome getBiome() {
    if (this.biome == null) {
      this.biome = this.biomeManager.assignBiome(this);
    }
    return this.biome;
  }

  /**
   * @param biome
   *          the biome to set
   */
  public void setBiome(IBiome biome) {
    this.biome = biome;
  }

  /**
   * @return the size
   */
  public int getSize() {
    return this.size;
  }

  /**
   * @param size
   *          the size to set
   */
  public void setSize(int size) {
    this.size = size;
  }

}
