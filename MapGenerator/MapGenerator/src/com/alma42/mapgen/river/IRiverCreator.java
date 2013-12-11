/**
 * 
 */
package com.alma42.mapgen.river;

import java.util.ArrayList;
import java.util.Random;

import com.alma42.mapgen.utils.geometry.Corner;

/**
 * @author Alban
 * 
 */
public interface IRiverCreator {

  public void createRivers(ArrayList<Corner> corners, int size, Random seed);

}
