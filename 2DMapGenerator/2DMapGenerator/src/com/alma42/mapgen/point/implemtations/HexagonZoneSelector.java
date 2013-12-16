package com.alma42.mapgen.point.implemtations;

import java.util.ArrayList;

import com.alma42.mapgen.grid.AGrid;
import com.alma42.mapgen.point.APointSelector;
import com.alma42.mapgen.utils.geometry.Point;

public class HexagonZoneSelector extends APointSelector {

  public HexagonZoneSelector(final AGrid grid, final int biomeManagerType) {
    super(grid, biomeManagerType);
  }

  @Override
  public ArrayList<Point> generatePoints() {
    final ArrayList<Point> points = new ArrayList<Point>();
    final double number = Math.sqrt(this.shapeNumber);
    for (int x = 0; x < number; x++) {
      for (int y = 0; y < number; y++) {
        points.add(new Point(((0.5 + x) / number) * this.size, ((0.25 + ((0.5 * x) % 2) + y) / number) * this.size));
      }
    }
    return points;
  }

  @Override
  public void generateShapes() {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean needMoreRandomness() {
    return true;
  }
}
