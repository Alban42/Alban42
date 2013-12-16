package com.alma42.mapgen.grid.coordinate;

public class Coordinates {

  public enum Position {
    N, NE, E, SE, S, SW, W, NW, R, L;
  }

  private final int x;
  private final int y;
  private Position  position;

  /**
   * @param x
   * @param y
   */
  public Coordinates(final int x, final int y) {
    this.x = x;
    this.y = y;
    this.position = null;
  }

  /**
   * @param x
   * @param y
   */
  public Coordinates(final int x, final int y, final Position position) {
    this(x, y);
    this.position = position;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Coordinates other = (Coordinates) obj;
    if (this.x != other.x) {
      return false;
    }
    if (this.y != other.y) {
      return false;
    }
    return true;
  }

  /**
   * @return the position
   */
  public Position getPosition() {
    return this.position;
  }

  /**
   * @return the x
   */
  public int getX() {
    return this.x;
  }

  /**
   * @return the y
   */
  public int getY() {
    return this.y;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final String result = String.valueOf(getX()) + String.valueOf(getY());
    return Integer.valueOf(result);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "ACoordinates [x=" + this.x + ", y=" + this.y + "]";
  }

}
