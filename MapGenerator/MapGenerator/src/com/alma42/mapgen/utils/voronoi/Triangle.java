package com.alma42.mapgen.utils.voronoi;

import java.util.ArrayList;

public final class Triangle {

  private ArrayList<Site> _sites;

  public ArrayList<Site> get_sites() {
    return this._sites;
  }

  public Triangle(Site a, Site b, Site c) {
    this._sites = new ArrayList<Site>();
    this._sites.add(a);
    this._sites.add(b);
    this._sites.add(c);
  }

  public void dispose() {
    this._sites.clear();
    this._sites = null;
  }
}