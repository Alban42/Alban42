package com.alma42.mapgen.grid.shape.implementations;

import com.alma42.mapgen.grid.AGrid;
import com.alma42.mapgen.grid.coordinate.Coordinates;
import com.alma42.mapgen.grid.shape.AShape;
import com.alma42.mapgen.utils.geometry.Point;

public class SquareShape extends AShape {

  public SquareShape(final AGrid grid, final int biomeManagerType, final Coordinates coordinates,
      final Point center) {
    super(grid, biomeManagerType, coordinates, center);
  }

}
