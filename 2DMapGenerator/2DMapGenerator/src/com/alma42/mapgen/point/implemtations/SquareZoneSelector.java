package com.alma42.mapgen.point.implemtations;

import java.util.ArrayList;

import com.alma42.mapgen.biomes.IBiomeManager;
import com.alma42.mapgen.grid.AGrid;
import com.alma42.mapgen.grid.coordinate.implementations.SquareCoordinates;
import com.alma42.mapgen.grid.shape.implementations.SquareShape;
import com.alma42.mapgen.point.APointSelector;
import com.alma42.mapgen.utils.geometry.Point;

public class SquareZoneSelector extends APointSelector {

  public SquareZoneSelector(final int size, final int shapeNumebr, final AGrid grid, final IBiomeManager biomeManager) {
    super(size, shapeNumebr, grid, biomeManager);
  }

  @Override
  public ArrayList<Point> generatePoints(final int size, final int zoneNumber) {
    ArrayList<Point> points = new ArrayList<Point>();
    double number = Math.sqrt(zoneNumber);
    for (int x = 0; x < number; x++) {
      for (int y = 0; y < number; y++) {
        points.add(new Point(((0.5 + x) / number) * size, ((0.5 + y) / number) * size));
      }
    }
    return points;
  }

  @Override
  public void generatePoints(final int size, final int shapeNumber, final AGrid grid) {
    Point point;
    double number = Math.sqrt(shapeNumber);
    for (int x = 0; x < number; x++) {
      for (int y = 0; y < number; y++) {
        point = new Point(((0.5 + x) / number) * size, ((0.5 + y) / number) * size);
        grid.addShape(new SquareShape(this.grid, this.biomeManager, point), new SquareCoordinates(x, y));
      }
    }
  }

  @Override
  public boolean needMoreRandomness() {
    return true;
  }
}
