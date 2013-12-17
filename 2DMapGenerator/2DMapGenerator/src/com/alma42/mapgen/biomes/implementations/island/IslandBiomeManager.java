package com.alma42.mapgen.biomes.implementations.island;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Random;

import com.alma42.mapgen.biomes.IBiome;
import com.alma42.mapgen.biomes.IBiomeManager;
import com.alma42.mapgen.biomes.implementations.island.IslandBiome.ColorData;
import com.alma42.mapgen.biomes.implementations.island.shape.IIslandShape;
import com.alma42.mapgen.biomes.implementations.island.shape.factory.IslandShapeFactory;
import com.alma42.mapgen.grid.AGridComponent;
import com.alma42.mapgen.grid.Grid;
import com.alma42.mapgen.utils.geometry.Corner;
import com.alma42.mapgen.utils.geometry.Point;

public class IslandBiomeManager implements IBiomeManager {

  private static final int   SHAPE          = IslandShapeFactory.RADIAL;
  /**
   * 0 to 1, fraction of water corners for water polygon
   */
  public static final double LAKE_THRESHOLD = 0.3;

  private final IIslandShape islandShape;
  private final Random       random;

  public IslandBiomeManager(final int size, final Random random) {
    this.random = random;
    this.islandShape = IslandShapeFactory.createIslandShape(SHAPE, random, size);
  }

  @Override
  public void assignBiome(final AGridComponent gridComponent) {
    if (!(gridComponent instanceof Grid)) {
      throw new ClassCastException("The gridComponent must be an instance of " + Grid.class.getName());
    }
    final Grid grid = (Grid) gridComponent;
    // Determine the elevations and water at corners.
    assignCornerElevations(grid);

    // Determine polygon and corner type: ocean, coast, land.
    assignOceanCoastAndLand(grid);

    // Rescale elevations so that the highest is 1.0, and they're
    // distributed well. We want lower elevations to be more common
    // than higher elevations, in proportions approximately matching
    // concentric rings. That is, the lowest elevation is the
    // largest ring around the island, and therefore should more
    // land area than the highest elevation, which is the very
    // center of a perfectly circular island.
    redistributeElevations(landCorners(this.graph.getCorners()));

    // Assign elevations to non-land corners
    for (final Corner q : this.graph.getCorners()) {
      if (q.ocean || q.coast) {
        q.elevation = 0.0;
      }
    }

    // Polygon elevations are the average of their corners
    assignElevations();

    System.out.println("Assign downslopes ...");
    // Determine downslope paths.
    calculateDownslopes();

    // Determine watersheds: for every corner, where does it flow
    // out into the ocean?
    calculateWatersheds();

    // Create rivers.
    System.out.println("Create River ...");
    createRivers();

    // Determine moisture at corners, starting at rivers
    // and lakes, but not oceans. Then redistribute
    // moisture to cover the entire range evenly from 0.0
    // to 1.0. Then assign polygon moisture as the average
    // of the corner moisture.
    assignCornerMoisture();
    redistributeMoisture(landCorners(this.graph.getCorners()));

    System.out.println("Assign moisture ...");
    assignMoisture();
    System.out.println("Assign biomes ...");
    assignBiomes();
  }

  public void assignBiome(final Center center) {
    IBiome<Color> biome = null;
    if (center.ocean) {
      biome = new IslandBiome(ColorData.OCEAN);
    } else if (center.water) {
      if (center.elevation < 0.1) {
        biome = new IslandBiome(ColorData.MARSH);
      }
      if (center.elevation > 0.8) {
        biome = new IslandBiome(ColorData.ICE);
      }
      biome = new IslandBiome(ColorData.LAKE);
    } else if (center.coast) {
      biome = new IslandBiome(ColorData.BEACH);
    } else if (center.elevation > 0.8) {
      if (center.moisture > 0.50) {
        biome = new IslandBiome(ColorData.SNOW);
      } else if (center.moisture > 0.33) {
        biome = new IslandBiome(ColorData.TUNDRA);
      } else if (center.moisture > 0.16) {
        biome = new IslandBiome(ColorData.BARE);
      } else {
        biome = new IslandBiome(ColorData.SCORCHED);
      }
    } else if (center.elevation > 0.6) {
      if (center.moisture > 0.66) {
        biome = new IslandBiome(ColorData.TAIGA);
      } else if (center.moisture > 0.33) {
        biome = new IslandBiome(ColorData.SHRUBLAND);
      } else {
        biome = new IslandBiome(ColorData.TEMPERATE_DESERT);
      }
    } else if (center.elevation > 0.3) {
      if (center.moisture > 0.83) {
        biome = new IslandBiome(ColorData.TEMPERATE_RAIN_FOREST);
      } else if (center.moisture > 0.50) {
        biome = new IslandBiome(ColorData.TEMPERATE_DECIDUOUS_FOREST);
      } else if (center.moisture > 0.16) {
        biome = new IslandBiome(ColorData.GRASSLAND);
      } else {
        biome = new IslandBiome(ColorData.TEMPERATE_DESERT);
      }
    } else {
      if (center.moisture > 0.66) {
        biome = new IslandBiome(ColorData.TROPICAL_RAIN_FOREST);
      } else if (center.moisture > 0.33) {
        biome = new IslandBiome(ColorData.TROPICAL_SEASONAL_FOREST);
      } else if (center.moisture > 0.16) {
        biome = new IslandBiome(ColorData.GRASSLAND);
      } else {
        biome = new IslandBiome(ColorData.SUBTROPICAL_DESERT);
      }
    }

    center.biome = biome;
  }

  /**
   * Determine elevations and water at Voronoi corners. By construction, we have no local minima. This is important for
   * the downslope vectors later, which are used in the river construction algorithm. Also by construction, inlets/bays
   * push low elevation areas inland, which means many rivers end up flowing out through them. Also by construction,
   * lakes often end up on river paths because they don't raise the elevation as much as other terrain does.
   */
  private void assignCornerElevations(final Grid grid) {
    double newElevation;
    final LinkedList<Corner> queue = new LinkedList<Corner>();
    for (final Corner corner : grid.getAllCorners()) {
      corner.setWater(!isInside(corner.getPoint()));
      // The edges of the map are elevation 0
      if (corner.isBorder()) {
        corner.setElevation(0.0);
        queue.add(corner);
      } else {
        corner.setElevation(Double.MAX_VALUE);
      }
    }
    // Traverse the graph and assign elevations to each point. As we
    // move away from the map border, increase the elevations. This
    // guarantees that rivers always have a way down to the coast by
    // going downhill (no local minima).
    while (!queue.isEmpty()) {
      final Corner corner = queue.pop();
      for (final Corner cornerAdjacent : corner.getAdjacents().values()) {
        // Every step up is epsilon over water or 1 over land. The
        // number doesn't matter because we'll rescale the
        // elevations later.
        newElevation = 0.01 + corner.getElevation();
        if (!corner.isWater() && !cornerAdjacent.isWater()) {
          newElevation += 1;
          newElevation += this.random.nextDouble();
        }
        // If this point changed, we'll add it to the queue so
        // that we can process its neighbors too.
        if (newElevation < cornerAdjacent.getElevation()) {
          cornerAdjacent.setElevation(newElevation);
          queue.add(cornerAdjacent);
        }
      }
    }
  }

  /** Determine polygon and corner types: ocean, coast, land. */
  private void assignOceanCoastAndLand(final Grid grid) {
    // Compute polygon attributes 'ocean' and 'water' based on the
    // corner attributes. Count the water corners per
    // polygon. Oceans are all polygons connected to the edge of the
    // map. In the first pass, mark the edges of the map as ocean;
    // in the second pass, mark any water-containing polygon
    // connected an ocean as ocean.
    final LinkedList<AGridComponent> queue = new LinkedList<AGridComponent>();
    int numCorner, numWater, numOcean, numLand;
    for (final AGridComponent component : grid.getChilds().values()) {
      numWater = 0;
      numCorner = 0;
      for (final Corner corner : component.getCorners().values()) {
        if (corner != null) {
          if (corner.isBorder()) {
            component.setBorder(true);
            component.setOcean(true);
            corner.setWater(true);
            queue.add(component);
          }
          if (corner.isWater()) {
            numWater++;
          }
        } else {
          numCorner++;
        }
      }
      component.setWater(component.isOcean() || ((numWater / numCorner) >= LAKE_THRESHOLD));
    }

    while (!queue.isEmpty()) {
      final Center center = queue.pop();
      for (final Center centerNeighbor : center.neighbors) {
        if (centerNeighbor.water && !centerNeighbor.ocean) {
          centerNeighbor.ocean = true;
          queue.add(centerNeighbor);
        }
      }
    }

    // Set the polygon attribute 'coast' based on its neighbors. If
    // it has at least one ocean and at least one land neighbor,
    // then this is a coastal polygon.
    for (final Center center : this.graph.getCenters()) {
      numOcean = 0;
      numLand = 0;
      for (final Center centerNeighbor : center.neighbors) {
        if (centerNeighbor.ocean) {
          numOcean++;
        }
        if (!centerNeighbor.water) {
          numLand++;
        }
      }
      center.coast = (numOcean > 0) && (numLand > 0);
    }

    // Set the corner attributes based on the computed polygon
    // attributes. If all polygons connected to this corner are
    // ocean, then it's ocean; if all are land, then it's land;
    // otherwise it's coast.
    for (final Corner corner : this.graph.getCorners()) {
      numOcean = 0;
      numLand = 0;
      for (final Center touche : corner.touches) {

        if (touche.ocean) {
          numOcean++;
        }
        if (!touche.water) {
          numLand++;
        }
      }
      corner.ocean = (numOcean == corner.touches.size());
      corner.coast = (numOcean > 0) && (numLand > 0);
      corner.water = corner.border || ((numLand != corner.touches.size()) && !corner.coast);
    }
  }

  @Override
  public IBiome getBiome(final AGridComponent gridComponent) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Determine whether a given point should be on the island or in the water.
   * 
   * @return true if the point should be on the island.
   */
  private boolean isInside(final Point point) {
    return this.islandShape.isInside(point);
  }
}
