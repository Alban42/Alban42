package com.alma42.mapgen.grid.implementations;

import java.util.Random;

import com.alma42.mapgen.factories.PointSelectorFactory;
import com.alma42.mapgen.grid.AGrid;

public class SquareGrid extends AGrid {

  public SquareGrid(final Random seed, final int size, final int shapeNumber) {
    super(PointSelectorFactory.createPointSelector(PointSelectorFactory.SQUARE, seed), size, shapeNumber);
  }


}
