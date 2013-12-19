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

import java.util.ArrayList;
import java.util.List;

import com.alma42.mapgen.Map;
import com.alma42.mapgen.biomes.factory.BiomeManagerFactory;
import com.alma42.mapgen.grid.AGridComponent;
import com.alma42.mapgen.grid.shape.Shape;
import com.alma42.mapgen.utils.CameraHelper;
import com.alma42.mapgen.utils.geometry.Point;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class WorldController extends InputAdapter {

  private static final String TAG = WorldController.class.getName();

  public CameraHelper         cameraHelper;
  public Map                  map;

  public int                  selectedSprite;
  public List<Sprite>         testSprites;

  public WorldController() {
    init();
  }

  private Pixmap createProceduralPixmap(final AGridComponent component) {
    final int width = 32;
    final int height = 32;
    final Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);
    final Integer hex = ((Integer) ((Shape) component).getBiome().getValue());
    final int r = (hex >> 16) & 255;
    final int g = (hex >> 8) & 255;
    final int b = (hex & 255);
    pixmap.setColor(new Color(r, g, b, 1));
    pixmap.fill();
    // pixmap.drawRectangle(0, 0, width, height);
    return pixmap;
  }

  private void createUser() {
    final int width = 32;
    final int height = 32;
    final Pixmap pixmap = createUser(width, height);
    // Create a new texture from pixmap data
    final Texture texture = new Texture(pixmap);
    // Create new sprites using the just created texture
    final Sprite spr = new Sprite(texture);
    // Define sprite size to be 1m x 1m in game world
    spr.setSize(1, 1);
    // Set origin to spriteï¿½s center
    spr.setOrigin(spr.getWidth() / 2.0f, spr.getHeight() / 2.0f);
    spr.setPosition(0, 0);
    // Put new sprite into array
    this.testSprites.add(spr);
  }

  private Pixmap createUser(final int width, final int height) {
    final Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);
    // Fill square with red color at 50% opacity
    pixmap.setColor(1, 0, 0, 0.5f);
    pixmap.fill();
    // Draw a yellow-colored X shape on square
    pixmap.setColor(1, 1, 0, 1);
    pixmap.drawLine(0, 0, width, height);
    pixmap.drawLine(width, 0, 0, height);
    // Draw a cyan-colored border around square
    pixmap.setColor(0, 1, 1, 1);
    pixmap.drawRectangle(0, 0, width, height);
    return pixmap;
  }

  private void handleDebugInput(final float deltaTime) {
    if (Gdx.app.getType() != ApplicationType.Desktop) {
      return;
    }

    // Selected Sprite Controls
    final float sprMoveSpeed = 5 * deltaTime;
    if (Gdx.input.isKeyPressed(Keys.Q)) {
      moveSelectedSprite(-sprMoveSpeed, 0);
    }
    if (Gdx.input.isKeyPressed(Keys.D)) {
      moveSelectedSprite(sprMoveSpeed, 0);
    }
    if (Gdx.input.isKeyPressed(Keys.Z)) {
      moveSelectedSprite(0, sprMoveSpeed);
    }
    if (Gdx.input.isKeyPressed(Keys.S)) {
      moveSelectedSprite(0, -sprMoveSpeed);
    }

    // Camera Controls (move)
    float camMoveSpeed = 50 * deltaTime;
    final float camMoveSpeedAccelerationFactor = 5;
    if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
      camMoveSpeed *= camMoveSpeedAccelerationFactor;
    }
    if (Gdx.input.isKeyPressed(Keys.LEFT)) {
      moveCamera(-camMoveSpeed, 0);
    }
    if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
      moveCamera(camMoveSpeed, 0);
    }
    if (Gdx.input.isKeyPressed(Keys.UP)) {
      moveCamera(0, camMoveSpeed);
    }
    if (Gdx.input.isKeyPressed(Keys.DOWN)) {
      moveCamera(0, -camMoveSpeed);
    }
    if (Gdx.input.isKeyPressed(Keys.BACKSPACE)) {
      this.cameraHelper.setPosition(0, 0);
    }

    // Camera Controls (zoom)
    float camZoomSpeed = 50 * deltaTime;
    final float camZoomSpeedAccelerationFactor = 5;
    if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
      camZoomSpeed *= camZoomSpeedAccelerationFactor;
    }
    if (Gdx.input.isKeyPressed(Keys.COMMA)) {
      this.cameraHelper.addZoom(camZoomSpeed);
    }
    if (Gdx.input.isKeyPressed(Keys.PERIOD)) {
      this.cameraHelper.addZoom(-camZoomSpeed);
    }
    if (Gdx.input.isKeyPressed(Keys.SLASH)) {
      this.cameraHelper.setZoom(1);
    }
  }

  private void init() {
    Gdx.input.setInputProcessor(this);
    this.cameraHelper = new CameraHelper();
    initTestObjects();
  }

  private void initTestObjects() {
    this.map = new Map(6000, 10000, BiomeManagerFactory.ISLAND);
    this.map.createMap();
    this.testSprites = new ArrayList<Sprite>();
    createUser();

    // Create new sprites using a random texture region
    for (final AGridComponent component : this.map.getGrid().getChilds().values()) {
      final Pixmap pixmap = createProceduralPixmap(component);
      // Create a new texture from pixmap data
      final Texture texture = new Texture(pixmap);
      final Sprite spr = new Sprite(texture);
      // Define sprite size to be 1m x 1m in game world
      spr.setSize((float) component.getSize(), (float) component.getSize());
      final Point center = component.getCenter();
      // Set origin to sprite�s center
      spr.setOrigin((float) center.getX(), (float) center.getY());
      // Calculate random position for sprite
      spr.setPosition((float) center.getX(), (float) center.getY());
      // spr.setColor(biomeManager.getBiome(component).getValue());
      // Put new sprite into array
      this.testSprites.add(spr);
    }
    // Set first sprite as selected one
    this.selectedSprite = 0;
  }

  @Override
  public boolean keyUp(final int keycode) {
    // Reset game world
    if (keycode == Keys.R) {
      init();
      Gdx.app.debug(TAG, "Game world resetted");
    }
    // Select next sprite
    else if (keycode == Keys.SPACE) {
      this.selectedSprite = (this.selectedSprite + 1) % this.testSprites.size();
      // Update camera's target to follow the currently
      // selected sprite
      if (this.cameraHelper.hasTarget()) {
        this.cameraHelper.setTarget(this.testSprites.get(this.selectedSprite));
      }
      Gdx.app.debug(TAG, "Sprite #" + this.selectedSprite + " selected");
    }
    // Toggle camera follow
    else if (keycode == Keys.ENTER) {
      this.cameraHelper.setTarget(this.cameraHelper.hasTarget() ? null : this.testSprites.get(this.selectedSprite));
      Gdx.app.debug(TAG, "Camera follow enabled: " + this.cameraHelper.hasTarget());
    }
    return false;
  }

  private void moveCamera(float x, float y) {
    x += this.cameraHelper.getPosition().x;
    y += this.cameraHelper.getPosition().y;
    this.cameraHelper.setPosition(x, y);
  }

  private void moveSelectedSprite(final float x, final float y) {
    this.testSprites.get(this.selectedSprite).translate(x, y);
  }

  public void update(final float deltaTime) {
    handleDebugInput(deltaTime);
    // updateTestObjects(deltaTime);
    this.cameraHelper.update(deltaTime);
  }

  private void updateTestObjects(final float deltaTime) {
    // Get current rotation from selected sprite
    float rotation = this.testSprites.get(this.selectedSprite).getRotation();
    // Rotate sprite by 90 degrees per second
    rotation += 90 * deltaTime;
    // Wrap around at 360 degrees
    rotation %= 360;
    // Set new rotation value to selected sprite
    this.testSprites.get(this.selectedSprite).setRotation(rotation);
  }
}
