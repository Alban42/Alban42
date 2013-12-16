package com.alma42.mapgen.grid;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import com.alma42.mapgen.grid.coordinate.Coordinates;
import com.alma42.mapgen.grid.shape.AShape;

public abstract class AGrid {

  private final Map<Coordinates, AShape> grid;
  private final int                      size;
  private final int                      shapeNumber;

  public AGrid(final int size, final int shapeNumber) {
    this.grid = new TreeMap<Coordinates, AShape>(new Comparator<Coordinates>() {

      @Override
      public int compare(final Coordinates o1, final Coordinates o2) {
        int result = 0;
        if (o1.getX() < o2.getX()) {
          result = -1;
        } else if (o1.getX() > o2.getX()) {
          result = 1;
        } else {
          if (o1.getY() < o2.getY()) {
            result = -1;
          } else if (o1.getY() > o2.getY()) {
            result = 1;
          }
        }
        if (result == 0) {
        }
        return result;
      }
    });
    this.size = size;
    this.shapeNumber = shapeNumber;
  }

  public void addShape(final AShape shape) {
    this.grid.put(shape.getCoordinates(), shape);
  }

  /**
   * @return the grid
   */
  public Map<Coordinates, AShape> getGrid() {
    return this.grid;
  }

  public abstract ArrayList<AShape> getNeighbors(AShape shape);

  public AShape getShape(final Coordinates coordinates) {
    return getGrid().get(coordinates);
  }

  /**
   * @return the shapeNumber
   */
  public int getShapeNumber() {
    return this.shapeNumber;
  }

  /**
   * @return the size
   */
  public int getSize() {
    return this.size;
  }

}