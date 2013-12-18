package com.alma42.mapgen.utils.geometry;

import java.util.Map;
import java.util.TreeMap;

import com.alma42.mapgen.biomes.IProperties;
import com.alma42.mapgen.biomes.factory.PropertiesFactory;
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

  private static final double              GRID_UNIT = AGridComponent.GRID_UNIT;

  private Map<Coordinates, Corner>         adjacents;
  private boolean                          border;
  private final Coordinates                coordinates;

  private final AGridComponent             parent;
  private final Point                      point;

  private IProperties                      properties;
  private int                              propertiesType;
  private Map<Coordinates, AGridComponent> touches;

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

  private void addTouche(final Grid gridParent, final Coordinates coordinates) {
    AGridComponent component = null;
    for (final AGridComponent tmpComponent : gridParent.getChilds().values()) {
      if (tmpComponent.getCoordinates().equals(coordinates)) {
        component = tmpComponent;
        break;
      }
    }
    this.touches.put(coordinates, component);
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

  public int getAdjacentsSize() {
    int total = 0;
    for (final Corner corner : getAdjacents().values()) {
      if (corner != null) {
        total++;
      }
    }
    return total;
  }

  /**
   * @return the coordinates
   */
  public Coordinates getCoordinates() {
    return this.coordinates;
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

  public IProperties getProperties() {
    if (this.properties == null) {
      this.properties = PropertiesFactory.createProperties(this.propertiesType);
    }

    return this.properties;
  }

  public Map<Coordinates, AGridComponent> getTouches() {
    if (this.touches == null) {
      this.touches = new TreeMap<Coordinates, AGridComponent>(this.parent.getCoordinateComparator());
      final double px = this.coordinates.getX();
      final double py = this.coordinates.getY();
      Coordinates coordinates;
      final Grid gridParent = getGridParent(this.parent);
      final double gridUnit = GRID_UNIT / 2;

      // North-East component
      coordinates = new Coordinates(px + gridUnit, py - gridUnit, Position.NE);
      addTouche(gridParent, coordinates);
      // South-East component
      coordinates = new Coordinates(px + gridUnit, py + gridUnit, Position.SE);
      addTouche(gridParent, coordinates);
      // South-West component
      coordinates = new Coordinates(px - gridUnit, py + gridUnit, Position.SW);
      addTouche(gridParent, coordinates);
      // North-West component
      coordinates = new Coordinates(px - gridUnit, py - gridUnit, Position.NW);
      addTouche(gridParent, coordinates);
    }
    return this.touches;
  }

  public int getTouchesSize() {
    int total = 0;
    for (final AGridComponent component : getTouches().values()) {
      if (component != null) {
        total++;
      }
    }
    return total;
  }

  /**
   * @return the border
   */
  public boolean isBorder() {
    return this.border;
  }

  /**
   * @param border
   *          the border to set
   */
  public void setBorder(final boolean border) {
    this.border = border;
  }

  public void setProperties(final int propertiesType) {
    this.propertiesType = propertiesType;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Corner [coordinates=" + this.coordinates + ", point=" + this.point + ", border="
        + this.border + "]";
  }

}
