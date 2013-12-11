package com.alma42.mapgen.factories;

import com.alma42.mapgen.river.IRiverCreator;
import com.alma42.mapgen.river.implementations.River;

public class RiverCreatorFactory {

  public static final int RIVER = 0;

  public IRiverCreator createRiverCreator(int type) {
    IRiverCreator riverCreator = null;

    switch (type) {
      case RIVER:
        riverCreator = new River();
        break;
      default:
        break;
    }

    return riverCreator;
  }

}
