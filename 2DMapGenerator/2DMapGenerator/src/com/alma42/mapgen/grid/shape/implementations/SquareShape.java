package com.alma42.mapgen.grid.shape.implementations;

import com.alma42.mapgen.biomes.IBiomeManager;
import com.alma42.mapgen.grid.AGrid;
import com.alma42.mapgen.grid.shape.AShape;
import com.alma42.mapgen.utils.geometry.Point;

public class SquareShape extends AShape {

  public SquareShape(final AGrid grid, final IBiomeManager biomeManager, final Point point) {
    super(grid, biomeManager, point);
  }

}
