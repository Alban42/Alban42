package com.alma42.mapgen;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
  public static void main(final String[] args) {
    final LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
    cfg.title = "2DMapGenerator";
    cfg.useGL20 = false;
    cfg.width = 600;
    cfg.height = 600;

    new LwjglApplication(new MapGeneratorUI(), cfg);
  }
}
