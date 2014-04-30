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
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Disposable;

public class Assets implements Disposable, AssetErrorListener {

  public class AssetBunny {
    public final AtlasRegion head;

    public AssetBunny(final TextureAtlas atlas) {
      this.head = atlas.findRegion("bunny_head");
    }
  }

  public class AssetFeather {
    public final AtlasRegion feather;

    public AssetFeather(final TextureAtlas atlas) {
      this.feather = atlas.findRegion("item_feather");
    }
  }

  public class AssetGoldCoin {
    public final AtlasRegion goldCoin;

    public AssetGoldCoin(final TextureAtlas atlas) {
      this.goldCoin = atlas.findRegion("item_gold_coin");
    }
  }

  public class AssetLevelDecoration {
    public final AtlasRegion cloud01;
    public final AtlasRegion cloud02;
    public final AtlasRegion cloud03;
    public final AtlasRegion mountainLeft;
    public final AtlasRegion mountainRight;
    public final AtlasRegion waterOverlay;

    public AssetLevelDecoration(final TextureAtlas atlas) {
      this.cloud01 = atlas.findRegion("cloud01");
      this.cloud02 = atlas.findRegion("cloud02");
      this.cloud03 = atlas.findRegion("cloud03");
      this.mountainLeft = atlas.findRegion("mountain_left");
      this.mountainRight = atlas.findRegion("mountain_right");
      this.waterOverlay = atlas.findRegion("water_overlay");
    }
  }

  public class AssetRock {
    public final AtlasRegion edge;
    public final AtlasRegion middle;

    public AssetRock(final TextureAtlas atlas) {
      this.edge = atlas.findRegion("rock_edge");
      this.middle = atlas.findRegion("rock_middle");
    }
  }

  public static final Assets  instance = new Assets();
  public static final String  TAG      = Assets.class.getName();
  private AssetManager        assetManager;

  public AssetBunny           bunny;

  public AssetFeather         feather;

  public AssetGoldCoin        goldCoin;

  public AssetLevelDecoration levelDecoration;

  public AssetRock            rock;

  // singleton: prevent instantiation from other classes
  private Assets() {
  }

  @Override
  public void dispose() {
    this.assetManager.dispose();
  }

  @Override
  public void error(final AssetDescriptor asset, final Throwable throwable) {
    Gdx.app.error(TAG, "Couldn't load asset '" + asset.fileName + "'", throwable);
  }

  public void init(final AssetManager assetManager) {
    this.assetManager = assetManager;
    // set asset manager error handler
    assetManager.setErrorListener(this);
    // load texture atlas
    assetManager.load(Constants.TEXTURE_ATLAS_OBJECTS, TextureAtlas.class);
    // start loading assets and wait until finished
    assetManager.finishLoading();

    Gdx.app.debug(TAG, "# of assets loaded: " + assetManager.getAssetNames().size);
    for (final String a : assetManager.getAssetNames()) {
      Gdx.app.debug(TAG, "asset: " + a);
    }

    final TextureAtlas atlas = assetManager.get(Constants.TEXTURE_ATLAS_OBJECTS);

    // enable texture filtering for pixel smoothing
    for (final Texture t : atlas.getTextures()) {
      t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
    }

    // create game resource objects
    this.bunny = new AssetBunny(atlas);
    this.rock = new AssetRock(atlas);
    this.goldCoin = new AssetGoldCoin(atlas);
    this.feather = new AssetFeather(atlas);
    this.levelDecoration = new AssetLevelDecoration(atlas);
  }

}
