package com.alma42.mapgen.point.implemtations;

import java.util.ArrayList;

import com.alma42.mapgen.biomes.IBiomeManager;
import com.alma42.mapgen.grid.AGrid;
import com.alma42.mapgen.point.APointSelector;
import com.alma42.mapgen.utils.geometry.Point;

public class HexagonZoneSelector extends APointSelector {

  public HexagonZoneSelector(final int size, final int shapeNumebr, final AGrid grid, final IBiomeManager biomeManager) {
    super(size, shapeNumebr, grid, biomeManager);
  }

  @Override
  public ArrayList<Point> generatePoints(final int size, final int zoneNumber) {
    ArrayList<Point> points = new ArrayList<Point>();
    double number = Math.sqrt(zoneNumber);
    for (int x = 0; x < number; x++) {
      for (int y = 0; y < number; y++) {
        points.add(new Point(((0.5 + x) / number) * size, ((0.25 + ((0.5 * x) % 2) + y) / number) * size));
      }
    }
    return points;
  }

  @Override
  public void generatePoints(final int size, final int shapeNumber, final AGrid aGrid) {

  }

  @Override
  public boolean needMoreRandomness() {
    return true;
  }
}
