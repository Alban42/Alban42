/**
 * 
 */
package com.alma42.mapgen.grid;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import com.alma42.mapgen.biomes.IProperties;
import com.alma42.mapgen.biomes.factory.PropertiesFactory;
import com.alma42.mapgen.grid.comparator.CoordinateComparator;
import com.alma42.mapgen.grid.comparator.CoordinateNeighborComparator;
import com.alma42.mapgen.grid.coordinate.Coordinates;
import com.alma42.mapgen.utils.geometry.Corner;
import com.alma42.mapgen.utils.geometry.Edge;
import com.alma42.mapgen.utils.geometry.Point;

/**
 * @author Alban
 * 
 */
public abstract class AGridComponent {

  public final static double                 GRID_UNIT                 = 1;

  protected final Grid                       parent;
  private IProperties                        properties;
  private int                                propertiesType;
  protected Map<Coordinates, AGridComponent> neighbors;

  protected Map<Coordinates, Corner>         corners;

  protected Map<Coordinates, Edge>           borders;

  protected final Coordinates                coordinates;
  protected final Point                      center;
  protected final double                     size;

  protected int                              deep                      = 0;

  protected Comparator<Coordinates>          coordinateComparator      = new CoordinateComparator();
  protected Comparator<Coordinates>          coordinateChildComparator = new CoordinateNeighborComparator();

  /**
   * @param parent
   */
  public AGridComponent(final Grid parent, final Coordinates coordinates, final Point center, final double size) {
    this.parent = parent;
    this.coordinates = coordinates;
    this.center = center;
    this.size = size;
    if (this.parent != null) {
      this.parent.deep++;
    }
  }

  private void addBorder(final Coordinates coordinates, final Coordinates coordinatesCorner1,
      final Coordinates coordinatesCorner2, final Coordinates neighborCoordinate) {
    final Corner corner1 = getCorners().get(coordinatesCorner1);
    final Corner corner2 = getCorners().get(coordinatesCorner2);
    final Edge edge = new Edge(this, corner1, corner2);
    if (getNeighbors().get(neighborCoordinate) == null) {
      edge.setBorder(true);
      corner1.setBorder(true);
      corner2.setBorder(true);
    }
    this.borders.put(coordinates, edge);
  }

  private void addCorner(final double x, final double y, final Coordinates coordinates) {
    Corner corner;
    corner = new Corner(this, new Point(x, y), coordinates);
    this.corners.put(coordinates, corner);
  }

  private void addNeighbor(final double x, final double y, final Coordinates.Position position) {
    Coordinates coordinates;
    coordinates = new Coordinates(x, y, position);
    if (this.parent != null) {
      this.neighbors.put(coordinates, this.parent.getChild(coordinates));
    }
  }

  /**
   * @return the edges
   */
  public Map<Coordinates, Edge> getBorders() {
    if (this.borders == null) {
      this.borders = new TreeMap<Coordinates, Edge>(this.coordinateComparator);
      final double px = this.coordinates.getX();
      final double py = this.coordinates.getY();
      Coordinates coordinates, coordinatesCorner1, coordinatesCorner2 = null;
      final double gridUnit = GRID_UNIT / 2;

      // North border
      coordinates = new Coordinates(px, py - GRID_UNIT, Coordinates.Position.S);
      coordinatesCorner1 = new Coordinates(px - gridUnit, py - gridUnit, Coordinates.Position.NW);
      coordinatesCorner2 = new Coordinates(px + gridUnit, py - gridUnit, Coordinates.Position.NE);
      addBorder(coordinates, coordinatesCorner1, coordinatesCorner2, new Coordinates(px, py - GRID_UNIT));
      // East border
      coordinates = new Coordinates(px + GRID_UNIT, py, Coordinates.Position.W);
      coordinatesCorner1 = new Coordinates(px + gridUnit, py - gridUnit, Coordinates.Position.NE);
      coordinatesCorner2 = new Coordinates(px + gridUnit, py + gridUnit, Coordinates.Position.SE);
      addBorder(coordinates, coordinatesCorner1, coordinatesCorner2, new Coordinates(px + GRID_UNIT, py));
      // South border
      coordinates = new Coordinates(px, py, Coordinates.Position.S);
      coordinatesCorner1 = new Coordinates(px - gridUnit, py + gridUnit, Coordinates.Position.SW);
      coordinatesCorner2 = new Coordinates(px + gridUnit, py + gridUnit, Coordinates.Position.SE);
      addBorder(coordinates, coordinatesCorner1, coordinatesCorner2, new Coordinates(px, py + GRID_UNIT));
      // West border
      coordinates = new Coordinates(px, py, Coordinates.Position.W);
      coordinatesCorner1 = new Coordinates(px - gridUnit, py - gridUnit, Coordinates.Position.NW);
      coordinatesCorner2 = new Coordinates(px - gridUnit, py + gridUnit, Coordinates.Position.SW);
      addBorder(coordinates, coordinatesCorner1, coordinatesCorner2, new Coordinates(px - GRID_UNIT, py));
    }
    return this.borders;
  }

  /**
   * @return the center
   */
  public Point getCenter() {
    return this.center;
  }

  /**
   * @return the coordinateComparator
   */
  public Comparator<Coordinates> getCoordinateComparator() {
    return this.coordinateComparator;
  }

  /**
   * @return the coordinateChildComparator
   */
  public Comparator<Coordinates> getCoordinateChildComparator() {
    return this.coordinateChildComparator;
  }

  /**
   * @return the coordinates
   */
  public Coordinates getCoordinates() {
    return this.coordinates;
  }

  /**
   * @return the corners
   */
  public Map<Coordinates, Corner> getCorners() {
    if (this.corners == null) {
      this.corners = new TreeMap<Coordinates, Corner>(this.coordinateComparator);
      double x;
      double y;
      final double px = this.coordinates.getX();
      final double py = this.coordinates.getY();
      Coordinates coordinates = null;
      final double unit = this.size / 2;
      final double gridUnit = GRID_UNIT / 2;

      // North-West corner
      x = this.center.getX() - unit;
      y = this.center.getY() - unit;
      coordinates = new Coordinates(px - gridUnit, py - gridUnit, Coordinates.Position.NW);
      addCorner(x, y, coordinates);
      // North-East corner
      x = this.center.getX() + unit;
      y = this.center.getY() - unit;
      coordinates = new Coordinates(px + gridUnit, py - gridUnit, Coordinates.Position.NE);
      addCorner(x, y, coordinates);
      // South-West corner
      x = this.center.getX() - unit;
      y = this.center.getY() + unit;
      coordinates = new Coordinates(px - gridUnit, py + gridUnit, Coordinates.Position.SW);
      addCorner(x, y, coordinates);
      // South-East corner
      x = this.center.getX() + unit;
      y = this.center.getY() + unit;
      coordinates = new Coordinates(px + gridUnit, py + gridUnit, Coordinates.Position.SE);
      addCorner(x, y, coordinates);
    }
    return this.corners;
  }

  public Map<Coordinates, AGridComponent> getNeighbors() {
    if (this.neighbors == null) {
      this.neighbors = new TreeMap<Coordinates, AGridComponent>(this.coordinateChildComparator);
      double x, y;
      Coordinates.Position position;

      // North neighbor
      x = this.coordinates.getX();
      y = this.coordinates.getY() - GRID_UNIT;
      position = Coordinates.Position.N;
      addNeighbor(x, y, position);
      // East neighbor
      x = this.coordinates.getX() + GRID_UNIT;
      y = this.coordinates.getY();
      position = Coordinates.Position.E;
      addNeighbor(x, y, position);
      // South neighbor
      x = this.coordinates.getX();
      y = this.coordinates.getY() + GRID_UNIT;
      position = Coordinates.Position.S;
      addNeighbor(x, y, position);
      // West neighbor
      x = this.coordinates.getX() - GRID_UNIT;
      y = this.coordinates.getY();
      position = Coordinates.Position.W;
      addNeighbor(x, y, position);
    }
    return this.neighbors;
  }

  /**
   * @return the parent
   */
  public Grid getParent() {
    return this.parent;
  }

  /**
   * @return the properties
   */
  public IProperties getProperties() {
    if (this.properties == null) {
      this.properties = PropertiesFactory.createProperties(this.propertiesType);
    }
    return this.properties;
  }

  /**
   * @return the size
   */
  public double getSize() {
    return this.size;
  }

  protected void init() {
    getNeighbors();
    getCorners();
    getBorders();
  }

  /**
   * @param properties
   *          the properties to set
   */
  public void setProperties(final int propertiesType) {
    this.propertiesType = propertiesType;
    for (final Corner corner : getCorners().values()) {
      corner.setProperties(propertiesType);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "AGridComponent [coordinates=" + this.coordinates + ", center=" + this.center + ", size=" + this.size
        + "]\n";
  }

}