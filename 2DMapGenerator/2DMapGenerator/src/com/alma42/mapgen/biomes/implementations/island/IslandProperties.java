/**
 * 
 */
package com.alma42.mapgen.biomes.implementations.island;

import com.alma42.mapgen.biomes.IProperties;
import com.alma42.mapgen.utils.geometry.Corner;

/**
 * @author Alban
 * 
 */
public class IslandProperties implements IProperties {

  private boolean border, ocean, water, corner, coast;
  private Corner  downslope, watershed;
  private double  elevation, moisture;
  private int     watershedSize, river;

  /**
   * @return the downslope
   */
  public Corner getDownslope() {
    return this.downslope;
  }

  /**
   * @return the elevation
   */
  public double getElevation() {
    return this.elevation;
  }

  /**
   * @return the moisture
   */
  public double getMoisture() {
    return this.moisture;
  }

  /**
   * @return the river
   */
  public int getRiver() {
    return this.river;
  }

  /**
   * @return the watershed
   */
  public Corner getWatershed() {
    return this.watershed;
  }

  /**
   * @return the whatershedSize
   */
  public int getWatershedSize() {
    return this.watershedSize;
  }

  /**
   * @return the border
   */
  public boolean isBorder() {
    return this.border;
  }

  /**
   * @return the coast
   */
  public boolean isCoast() {
    return this.coast;
  }

  /**
   * @return the corner
   */
  public boolean isCorner() {
    return this.corner;
  }

  /**
   * @return the ocean
   */
  public boolean isOcean() {
    return this.ocean;
  }

  /**
   * @return the water
   */
  public boolean isWater() {
    return this.water;
  }

  /**
   * @param border
   *          the border to set
   */
  public void setBorder(final boolean border) {
    this.border = border;
  }

  /**
   * @param coast
   *          the coast to set
   */
  public void setCoast(final boolean coast) {
    this.coast = coast;
  }

  /**
   * @param corner
   *          the corner to set
   */
  public void setCorner(final boolean corner) {
    this.corner = corner;
  }

  /**
   * @param downslope
   *          the downslope to set
   */
  public void setDownslope(final Corner downslope) {
    this.downslope = downslope;
  }

  /**
   * @param elevation
   *          the elevation to set
   */
  public void setElevation(final double elevation) {
    this.elevation = elevation;
  }

  /**
   * @param moisture
   *          the moisture to set
   */
  public void setMoisture(final double moisture) {
    this.moisture = moisture;
  }

  /**
   * @param ocean
   *          the ocean to set
   */
  public void setOcean(final boolean ocean) {
    this.ocean = ocean;
  }

  /**
   * @param river
   *          the river to set
   */
  public void setRiver(final int river) {
    this.river = river;
  }

  /**
   * @param water
   *          the water to set
   */
  public void setWater(final boolean water) {
    this.water = water;
  }

  /**
   * @param watershed
   *          the watershed to set
   */
  public void setWatershed(final Corner watershed) {
    this.watershed = watershed;
  }

  /**
   * @param watershedSize
   *          the watershedSize to set
   */
  public void setWatershedSize(final int watershedSize) {
    this.watershedSize = watershedSize;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "IslandProperties [border=" + this.border + ", ocean=" + this.ocean + ", water=" + this.water + ", corner="
        + this.corner
        + ", coast=" + this.coast + "]";
  }

}
