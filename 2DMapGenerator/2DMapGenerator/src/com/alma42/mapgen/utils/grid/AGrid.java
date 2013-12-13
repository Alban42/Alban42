package com.alma42.mapgen.utils.grid;

import java.util.HashMap;

import com.alma42.mapgen.point.IPointSelector;
import com.alma42.mapgen.utils.grid.coordinate.ACoordinates;
import com.alma42.mapgen.utils.grid.shape.AShape;

public abstract class AGrid<C extends ACoordinates, S extends AShape> {

  private HashMap<C, S>  grid;
  private int            size;
  private int            shapeNumber;
  private IPointSelector pointSelector;

  public AGrid(IPointSelector pointSelector, int size, int shapeNumber) {
    this.grid = new HashMap<C, S>();
    this.size = size;
    this.shapeNumber = shapeNumber;
    this.pointSelector = pointSelector;
  }

  public void populate() {
    pointSelector.generatePoints(size, shapeNumber, this);
  }

  public void addShape(S shape, C coordinates) {
    grid.put(coordinates, shape);
  }
}
