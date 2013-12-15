package com.alma42.mapgen.grid;

import java.util.HashMap;

import com.alma42.mapgen.grid.coordinate.ACoordinates;
import com.alma42.mapgen.grid.shape.AShape;
import com.alma42.mapgen.point.APointSelector;

public abstract class AGrid {

  private final HashMap<ACoordinates, AShape> grid;
  private final int                           size;
  private final int                           shapeNumber;
  private final APointSelector                pointSelector;

  public AGrid(final APointSelector pointSelector, final int size, final int shapeNumber) {
    this.grid = new HashMap<ACoordinates, AShape>();
    this.size = size;
    this.shapeNumber = shapeNumber;
    this.pointSelector = pointSelector;
  }

  public void addShape(final AShape shape, final ACoordinates coordinates) {
    this.grid.put(coordinates, shape);
  }

  public void populate() {
    this.pointSelector.generatePoints(this.size, this.shapeNumber, this);
  }
}