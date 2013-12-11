package com.alma42.mapgen.utils.voronoi;

public final class Winding {

  final public static Winding CLOCKWISE        = new Winding("clockwise");
  final public static Winding COUNTERCLOCKWISE = new Winding("counterclockwise");
  final public static Winding NONE             = new Winding("none");
  private String              _name;

  private Winding(String name) {
    super();
    this._name = name;
  }

  @Override
  public String toString() {
    return this._name;
  }
}