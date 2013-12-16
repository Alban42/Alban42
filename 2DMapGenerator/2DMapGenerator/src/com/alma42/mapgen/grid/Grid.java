package com.alma42.mapgen.grid;

import java.util.Map;
import java.util.TreeMap;

import com.alma42.mapgen.grid.coordinate.Coordinates;
import com.alma42.mapgen.grid.shape.Shape;
import com.alma42.mapgen.utils.geometry.Point;

public class Grid extends AGridComponent {

  private final Map<Coordinates, AGridComponent> childs;
  private final int                              shapeNumber;

  public Grid(final double size, final int shapeNumber) {
    this(null, new Coordinates(0, 0), new Point((size / 2), (size / 2)), size, shapeNumber);
  }

  public Grid(final Grid parent, final Coordinates coordinates, final Point center, final double size,
      final int shapeNumber) {
    super(parent, coordinates, center, size);
    this.childs = new TreeMap<Coordinates, AGridComponent>(this.coordinateComparator);
    this.shapeNumber = shapeNumber;
  }

  public AGridComponent getChild(final Coordinates coordinates) {
    return getChilds().get(coordinates);
  }

  /**
   * @return the childs
   */
  public Map<Coordinates, AGridComponent> getChilds() {
    return this.childs;
  }

  /**
   * @return the size
   */
  public double getSize() {
    return this.size;
  }

  public void populate() {
    if (this.childs.size() == 0) {
      Point point;
      Coordinates coordinates;
      final double number = Math.sqrt(this.shapeNumber);
      for (int x = 0; x < number; x++) {
        for (int y = 0; y < number; y++) {
          coordinates = new Coordinates(x, y);
          point = new Point(((0.5 + x) / number) * this.size, ((0.5 + y) / number) * this.size);
          this.childs.put(coordinates, new Shape(this, coordinates, point, this.size / number));
        }
      }
    }
  }
}