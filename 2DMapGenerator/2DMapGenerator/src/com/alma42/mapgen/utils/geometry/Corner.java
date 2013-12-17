package com.alma42.mapgen.utils.geometry;

import java.util.Map;
import java.util.TreeMap;

import com.alma42.mapgen.grid.AGridComponent;
import com.alma42.mapgen.grid.Grid;
import com.alma42.mapgen.grid.coordinate.Coordinates;
import com.alma42.mapgen.grid.coordinate.Coordinates.Position;

/**
 * 
 * @author Alban
 * 
 */
public class Corner {

  private static final double      GRID_UNIT = AGridComponent.GRID_UNIT;

  private final AGridComponent     parent;
  private final Point              point;
  private final Coordinates        coordinates;

  private Map<Coordinates, Corner> adjacents;

  private boolean                  border, water;

  private double                   elevation;

  public Corner(final AGridComponent parent, final Point point, final Coordinates coordinates) {
    this.parent = parent;
    this.point = point;
    this.coordinates = coordinates;
  }

  private void addAdjacent(final Grid gridParent, final Coordinates coordinates) {
    Corner corner = null;
    for (final Corner tmpCorner : gridParent.getAllCorners()) {
      if (tmpCorner.getCoordinates().equals(coordinates)) {
        corner = tmpCorner;
        break;
      }
    }
    this.adjacents.put(coordinates, corner);
  }

  public Map<Coordinates, Corner> getAdjacents() {
    if (this.adjacents == null) {
      this.adjacents = new TreeMap<Coordinates, Corner>(this.parent.getCoordinateComparator());
      final double px = this.coordinates.getX();
      final double py = this.coordinates.getY();
      Coordinates coordinates;
      final Grid gridParent = getGridParent(this.parent);

      // North corner
      coordinates = new Coordinates(px, py - GRID_UNIT, Position.N);
      addAdjacent(gridParent, coordinates);
      // East corner
      coordinates = new Coordinates(px + GRID_UNIT, py, Position.E);
      addAdjacent(gridParent, coordinates);
      // South corner
      coordinates = new Coordinates(px, py + GRID_UNIT, Position.S);
      addAdjacent(gridParent, coordinates);
      // West corner
      coordinates = new Coordinates(px - GRID_UNIT, py, Position.W);
      addAdjacent(gridParent, coordinates);
    }
    return this.adjacents;
  }

  /**
   * @return the coordinates
   */
  public Coordinates getCoordinates() {
    return this.coordinates;
  }

  /**
   * @return the elevation
   */
  public double getElevation() {
    return this.elevation;
  }

  private Grid getGridParent(final AGridComponent parent) {
    Grid result = null;
    if (!(parent instanceof Grid)) {
      result = getGridParent(parent.getParent());
    } else {
      result = (Grid) parent;
    }

    return result;
  }

  /**
   * @return the parent
   */
  public AGridComponent getParent() {
    return this.parent;
  }

  /**
   * @return the point
   */
  public Point getPoint() {
    return this.point;
  }

  /**
   * @return the border
   */
  public boolean isBorder() {
    return this.border;
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
   * @param elevation
   *          the elevation to set
   */
  public void setElevation(final double elevation) {
    this.elevation = elevation;
  }

  /**
   * @param water
   *          the water to set
   */
  public void setWater(final boolean water) {
    this.water = water;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Corner [coordinates=" + this.coordinates + ", point=" + this.point + "]";
  }

}
