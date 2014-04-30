package com.alma42.mapgen.zone.types;

import com.alma42.mapgen.graph.IGraph;
import com.alma42.mapgen.utils.geometry.Rectangle;

public interface IZoneType {

  public void createZone();

  public IGraph getGraph();

  public Rectangle getBounds();
}
