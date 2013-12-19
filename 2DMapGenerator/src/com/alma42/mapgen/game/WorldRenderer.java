/*******************************************************************************
 * Copyright 2013 Andreas Oehlke
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/

package com.alma42.mapgen.game;

import com.alma42.mapgen.utils.Constants;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

public class WorldRenderer implements Disposable {

  private static final String   TAG = WorldRenderer.class.getName();

  private SpriteBatch           batch;
  private OrthographicCamera    camera;
  private final WorldController worldController;

  public WorldRenderer(final WorldController worldController) {
    this.worldController = worldController;
    init();
  }

  @Override
  public void dispose() {
    this.batch.dispose();
  }

  private void init() {
    this.batch = new SpriteBatch();
    this.camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
    this.camera.position.set(0, 0, 0);
    this.camera.update();
  }

  public void render() {
    renderTestObjects();
  }

  private void renderTestObjects() {
    this.worldController.cameraHelper.applyTo(this.camera);
    this.batch.setProjectionMatrix(this.camera.combined);
    this.batch.begin();
    for (final Sprite sprite : this.worldController.testSprites) {
      sprite.draw(this.batch);
    }
    this.batch.end();
  }

  public void resize(final int width, final int height) {
    this.camera.viewportWidth = (Constants.VIEWPORT_HEIGHT / height) * width;
    this.camera.update();
  }

}
