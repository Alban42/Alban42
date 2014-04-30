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

package com.alma42.mapgen.utils;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class CameraHelper {

  private static final String TAG          = CameraHelper.class.getName();

  private final float         MAX_ZOOM_IN  = 0.25f;
  private final float         MAX_ZOOM_OUT = 1000.0f;

  private final Vector2       position;
  private Sprite              target;
  private float               zoom;

  public CameraHelper() {
    this.position = new Vector2();
    this.zoom = 1.0f;
  }

  public void addZoom(final float amount) {
    setZoom(this.zoom + amount);
  }

  public void applyTo(final OrthographicCamera camera) {
    camera.position.x = this.position.x;
    camera.position.y = this.position.y;
    camera.zoom = this.zoom;
    camera.update();
  }

  public Vector2 getPosition() {
    return this.position;
  }

  public Sprite getTarget() {
    return this.target;
  }

  public float getZoom() {
    return this.zoom;
  }

  public boolean hasTarget() {
    return this.target != null;
  }

  public boolean hasTarget(final Sprite target) {
    return hasTarget() && this.target.equals(target);
  }

  public void setPosition(final float x, final float y) {
    this.position.set(x, y);
  }

  public void setTarget(final Sprite target) {
    this.target = target;
  }

  public void setZoom(final float zoom) {
    this.zoom = MathUtils.clamp(zoom, this.MAX_ZOOM_IN, this.MAX_ZOOM_OUT);
  }

  public void update(final float deltaTime) {
    if (!hasTarget()) {
      return;
    }

    this.position.x = this.target.getX() + this.target.getOriginX();
    this.position.y = this.target.getY() + this.target.getOriginY();
  }

}
