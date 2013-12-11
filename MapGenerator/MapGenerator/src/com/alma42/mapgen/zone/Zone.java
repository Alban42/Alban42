package com.alma42.mapgen.zone;

import java.util.ArrayList;
import java.util.Random;

import com.alma42.mapgen.biomes.IBiome;
import com.alma42.mapgen.biomes.IBiomeManager;
import com.alma42.mapgen.utils.geometry.Center;
import com.alma42.mapgen.utils.geometry.Corner;
import com.alma42.mapgen.utils.geometry.Edge;
import com.alma42.mapgen.utils.geometry.Point;
import com.alma42.mapgen.zone.types.IZoneType;

public class Zone {

  private int               size;

  public int                numPoints;
  public Random             seed;
  private Center            center;

  private ArrayList<Zone>   zones;
  private ArrayList<Corner> corners;
  private ArrayList<Edge>   edges;

  private IBiome            biome;
  private IBiomeManager     biomeManager;
  private IZoneType         zoneType;

  public Zone(int size) {
    this.size = size;
    this.center = new Center(new Point(size / 2, size / 2));
    this.zones = new ArrayList<Zone>();
    this.corners = new ArrayList<Corner>();
    this.edges = new ArrayList<Edge>();
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

  public void createZone() {
    zoneType.createZone();
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

  /**
   * @return the edges
   */
  public ArrayList<Edge> getEdges() {
    return this.edges;
  }

  /**
   * @param edge
   *          the edge to add
   */
  public void addEdges(Edge edge) {
    this.edges.add(edge);
  }

  /**
   * @return the corners
   */
  public ArrayList<Corner> getCorners() {
    return this.corners;
  }

  /**
   * @param corner
   *          the corner to add
   */
  public void addCorners(Corner corner) {
    this.corners.add(corner);
  }

  public boolean needMoreRandomness() {
    // TODO call the needMoreRandomness from IZoneSelector;
    return true;
  }
}
