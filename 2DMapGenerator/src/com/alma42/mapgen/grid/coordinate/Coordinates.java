package com.alma42.mapgen.grid.coordinate;

public class Coordinates {

  public enum Position {
    A(-1), N(0), NE(0.5), E(1), SE(1.5), S(2), SW(2.5), W(3), NW(3.5);
    double value;

    Position(final double value) {
      this.value = value;
    }

    public double getValue() {
      return this.value;
    }

  }

  private final double   x;
  private final double   y;
  private final Position position;

  /**
   * @param x
   * @param y
   */
  public Coordinates(final double x, final double y) {
    this(x, y, Position.A);
  }

  /**
   * @param x
   * @param y
   */
  public Coordinates(final double x, final double y, final Position position) {
    this.x = x;
    this.y = y;
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
    // if (this.position != other.position) {
    // return false;
    // }
    if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
      return false;
    }
    if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
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
  public double getX() {
    return this.x;
  }

  /**
   * @return the y
   */
  public double getY() {
    return this.y;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + ((this.position == null) ? 0 : this.position.hashCode());
    long temp;
    temp = Double.doubleToLongBits(this.x);
    result = (prime * result) + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(this.y);
    result = (prime * result) + (int) (temp ^ (temp >>> 32));
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Coordinates [x=" + this.x + ", y=" + this.y + ", position=" + this.position + "]";
  }

}
