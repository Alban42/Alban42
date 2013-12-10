/**
 * 
 */
package com.alma42.mapgen;

import com.alma42.mapgen.utils.perlin.PerlinNoiseGenerator;

/**
 * @author Alban
 * 
 */
public class Map {

  public static void main(final String[] args) {

    PerlinNoiseGenerator png = new PerlinNoiseGenerator();
    int size = 100;
    String string = "| ";
    for (int x = 0; x < size; x++) {
      string = "";
      for (int y = 0; y < size; y++) {
        string += png.noise2(((x + 1) * 128), ((y + 1) * 128)) + " | ";
      }
      System.out.println(string);
    }

  }
}
