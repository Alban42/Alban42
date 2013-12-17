package com.alma42.mapgen.utils.geometry;

import com.alma42.mapgen.grid.AGridComponent;

/**
 * 
 * @author Alban
 * 
 */
public class Edge {

  private final AGridComponent parent;
  private final Corner         corner1, corner2;

  private boolean              border;

  /**
   * @param corner1
   * @param corner2
   */
  public Edge(final AGridComponent parent, final Corner corner1, final Corner corner2) {
    this(parent, corner1, corner2, false);
  }

  public Edge(final AGridComponent parent, final Corner corner1, final Corner corner2, final boolean border) {
    this.parent = parent;
    this.corner1 = corner1;
    this.corner2 = corner2;
    this.border = border;
  }

  /**
   * @return the corner1
   */
  public Corner getCorner1() {
    return this.corner1;
  }

  /**
   * @return the corner2
   */
  public Corner getCorner2() {
    return this.corner2;
  }

  /**
   * @return the parent
   */
  public AGridComponent getParent() {
    return this.parent;
  }

  /**
   * @return the border
   */
  public boolean isBorder() {
    return this.border;
  }

  /**
   * @param border
   *          the border to set
   */
  public void setBorder(final boolean border) {
    this.border = border;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Edge [corner1=" + this.corner1 + ", corner2=" + this.corner2 + ", border=" + this.border + "]";
  }

}
