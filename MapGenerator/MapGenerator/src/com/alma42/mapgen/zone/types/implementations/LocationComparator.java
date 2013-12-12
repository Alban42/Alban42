package com.alma42.mapgen.zone.types.implementations;

import java.util.Comparator;

import com.alma42.mapgen.utils.geometry.Corner;

public class LocationComparator implements Comparator<Corner> {

  @Override
  public int compare(Corner arg0, Corner arg1) {
    int result = 0;
    if (arg0.elevation < arg1.elevation) {
      result = -1;
    } else if (arg0.elevation > arg1.elevation) {
      result = 1;
    }
    return result;
  }

}
