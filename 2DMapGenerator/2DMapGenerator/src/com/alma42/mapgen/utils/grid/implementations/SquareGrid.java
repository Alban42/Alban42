package com.alma42.mapgen.utils.grid.implementations;

import java.util.Random;

import com.alma42.mapgen.factories.PointSelectorFactory;
import com.alma42.mapgen.utils.grid.AGrid;
import com.alma42.mapgen.utils.grid.coordinate.implementations.SquareCoordinates;
import com.alma42.mapgen.utils.grid.shape.implementations.SquareShape;

public class SquareGrid extends AGrid<SquareCoordinates, SquareShape> {

  public SquareGrid(Random seed) {
    super(PointSelectorFactory.createPointSelector(PointSelectorFactory.SQUARE, seed));
  }

}
