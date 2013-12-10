/**
 * 
 */
package com.alma42.mapgen.islandShape.implementations;

import com.alma42.mapgen.islandShape.IIslandShape;
import com.alma42.mapgen.utils.geometry.Point;

/**
 * @author Alban
 * 
 */
public class SquareIslandShape implements IIslandShape {

  /*
   * (non-Javadoc)
   * 
   * @see com.alma42.mapgen.islandShape.IIslandShape#isInside(com.alma42.mapgen.utils.geometry.Point)
   */
  @Override
  public boolean isInside(Point point) {
    return true;
  }

}
