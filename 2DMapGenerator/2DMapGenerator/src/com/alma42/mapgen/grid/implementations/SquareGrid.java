package com.alma42.mapgen.grid.implementations;

import java.util.ArrayList;

import com.alma42.mapgen.grid.AGrid;
import com.alma42.mapgen.grid.coordinate.Coordinates;
import com.alma42.mapgen.grid.shape.AShape;

public class SquareGrid extends AGrid {

  public SquareGrid(final int size, final int shapeNumber) {
    super(size, shapeNumber);
  }

  @Override
  public ArrayList<AShape> getNeighbors(final AShape shape) {
    final ArrayList<AShape> neighbors = new ArrayList<AShape>();
    final Coordinates shapeCoordinates = shape.getCoordinates();
    int x, y;
    Coordinates coordinates = null;

    // North neighbor
    x = shapeCoordinates.getX();
    y = shapeCoordinates.getY() + 1;
    coordinates = new Coordinates(x, y, Coordinates.Position.N);
    neighbors.add(getShape(coordinates));
    // East neighbor
    x = shapeCoordinates.getX() + 1;
    y = shapeCoordinates.getY();
    coordinates = new Coordinates(x, y, Coordinates.Position.E);
    neighbors.add(getShape(coordinates));
    // South neighbor
    x = shapeCoordinates.getX();
    y = shapeCoordinates.getY() - 1;
    coordinates = new Coordinates(x, y, Coordinates.Position.S);
    neighbors.add(getShape(coordinates));
    // West neighbor
    x = shapeCoordinates.getX() - 1;
    y = shapeCoordinates.getY();
    coordinates = new Coordinates(x, y, Coordinates.Position.W);
    neighbors.add(getShape(coordinates));

    return neighbors;
  }
}
