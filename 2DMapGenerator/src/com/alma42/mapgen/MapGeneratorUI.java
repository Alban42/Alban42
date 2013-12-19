package com.alma42.mapgen;

import com.alma42.mapgen.game.WorldController;
import com.alma42.mapgen.game.WorldRenderer;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;

public class MapGeneratorUI implements ApplicationListener {
  private static final String TAG = MapGeneratorUI.class.getName();

  private boolean             paused;
  private WorldController     worldController;

  private WorldRenderer       worldRenderer;

  @Override
  public void create() {
    // Set Libgdx log level to DEBUG
    Gdx.app.setLogLevel(Application.LOG_DEBUG);
    // Load assets
    // Assets.instance.init(new AssetManager());
    // Initialize controller and renderer
    this.worldController = new WorldController();
    this.worldRenderer = new WorldRenderer(this.worldController);
  }

  @Override
  public void dispose() {
    this.worldRenderer.dispose();
    // Assets.instance.dispose();
  }

  @Override
  public void pause() {
    this.paused = true;
  }

  @Override
  public void render() {
    // Do not update game world when paused.
    if (!this.paused) {
      // Update game world by the time that has passed
      // since last rendered frame.
      this.worldController.update(Gdx.graphics.getDeltaTime());
    }
    // Sets the clear screen color to: Cornflower Blue
    Gdx.gl.glClearColor(0x64 / 255.0f, 0x95 / 255.0f, 0xed / 255.0f, 0xff / 255.0f);
    // Clears the screen
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    // Render game world to screen
    this.worldRenderer.render();
  }

  @Override
  public void resize(final int width, final int height) {
    this.worldRenderer.resize(width, height);
  }

  @Override
  public void resume() {
    // Assets.instance.init(new AssetManager());
    this.paused = false;
  }
}
