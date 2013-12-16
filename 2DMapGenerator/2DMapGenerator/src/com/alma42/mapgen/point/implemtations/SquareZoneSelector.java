package com.alma42.mapgen.point.implemtations;

import java.util.ArrayList;

import com.alma42.mapgen.factories.ShapeFactory;
import com.alma42.mapgen.grid.AGrid;
import com.alma42.mapgen.grid.coordinate.Coordinates;
import com.alma42.mapgen.point.APointSelector;
import com.alma42.mapgen.utils.geometry.Point;

public class SquareZoneSelector extends APointSelector {

  public SquareZoneSelector(final AGrid grid, final int biomeManagerType) {
    super(grid, biomeManagerType);
  }

  @Override
  public ArrayList<Point> generatePoints() {
    final ArrayList<Point> points = new ArrayList<Point>();
    final double number = Math.sqrt(this.shapeNumber);
    for (int x = 0; x < number; x++) {
      for (int y = 0; y < number; y++) {
        points.add(new Point(((0.5 + x) / number) * this.size, ((0.5 + y) / number) * this.size));
      }
    }
    return points;
  }

  @Override
  public void generateShapes() {
    Point point;
    Coordinates coordinates;
    System.out.println("SHAPE NUMBER : " + this.shapeNumber);
    final double number = Math.sqrt(this.shapeNumber);
    System.out.println("NUMBER : " + number);
    for (int x = 0; x < number; x++) {
      for (int y = 0; y < number; y++) {
        coordinates = new Coordinates(x, y);
        point = new Point(((0.5 + x) / number) * this.size, ((0.5 + y) / number) * this.size);
        this.grid.addShape(ShapeFactory.createShape(ShapeFactory.SQUARE, this.grid, this.biomeManagerType, coordinates,
            point));
      }
    }
  }

  @Override
  public boolean needMoreRandomness() {
    return true;
  }
}
