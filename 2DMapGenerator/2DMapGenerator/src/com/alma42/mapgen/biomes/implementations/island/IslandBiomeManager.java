package com.alma42.mapgen.biomes.implementations.island;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.alma42.mapgen.biomes.ABiomeManager;
import com.alma42.mapgen.biomes.IBiome;
import com.alma42.mapgen.biomes.factory.PropertiesFactory;
import com.alma42.mapgen.biomes.implementations.island.IslandStringBiome.StringData;
import com.alma42.mapgen.biomes.implementations.island.comparator.LocationComparator;
import com.alma42.mapgen.biomes.implementations.island.shape.IIslandShape;
import com.alma42.mapgen.biomes.implementations.island.shape.factory.IslandShapeFactory;
import com.alma42.mapgen.grid.AGridComponent;
import com.alma42.mapgen.grid.Grid;
import com.alma42.mapgen.utils.geometry.Corner;
import com.alma42.mapgen.utils.geometry.Point;

public class IslandBiomeManager extends ABiomeManager {

  /**
   * 0 to 1, fraction of water corners for water polygon
   */
  public static final double LAKE_THRESHOLD = 0.3;
  private static final int   PROPERTIES     = PropertiesFactory.ISLAND;
  private static final int   SHAPE          = IslandShapeFactory.RADIAL;

  /** Polygon elevations are the average of the elevations of their corners. */
  private static void assignElevations(final Grid grid) {
    double sumElevation;
    int sumCorners;
    for (final AGridComponent child : grid.getChilds().values()) {
      sumElevation = 0.0;
      sumCorners = 0;
      for (final Corner corner : child.getCorners().values()) {
        if (corner != null) {
          sumCorners++;
          sumElevation += getProperties(corner).getElevation();
        }
      }
      getProperties(child).setElevation(sumElevation / sumCorners);
    }
  }

  /** Determine polygon and corner types: ocean, coast, land. */
  private static void assignOceanCoastAndLand(final Grid grid) {
    // Compute polygon attributes 'ocean' and 'water' based on the
    // corner attributes. Count the water corners per
    // polygon. Oceans are all polygons connected to the edge of the
    // map. In the first pass, mark the edges of the map as ocean;
    // in the second pass, mark any water-containing polygon
    // connected an ocean as ocean.
    final LinkedList<AGridComponent> queue = new LinkedList<AGridComponent>();
    int numCorner, numWater, numOcean, numLand;
    for (final AGridComponent component : grid.getChilds().values()) {
      if (component != null) {
        numWater = 0;
        numCorner = 0;
        for (final Corner corner : component.getCorners().values()) {
          if (corner != null) {
            numCorner++;
            if (corner.isBorder()) {
              getProperties(component).setBorder(true);
              getProperties(component).setOcean(true);
              getProperties(component).setWater(true);
              getProperties(corner).setWater(true);
              queue.add(component);
            }
            if (getProperties(corner).isWater()) {
              numWater++;
            }
          }
        }
        getProperties(component).setWater(((numWater / numCorner) >= LAKE_THRESHOLD));
      }
    }

    while (!queue.isEmpty()) {
      final AGridComponent component = queue.pop();
      for (final AGridComponent componentNeighbor : component.getNeighbors().values()) {
        if (componentNeighbor != null) {
          if (getProperties(componentNeighbor).isWater() && !getProperties(componentNeighbor).isOcean()) {
            getProperties(componentNeighbor).setOcean(true);
            queue.add(componentNeighbor);
          }
        }
      }
    }

    // Set the polygon attribute 'coast' based on its neighbors. If
    // it has at least one ocean and at least one land neighbor,
    // then this is a coastal polygon.
    for (final AGridComponent component : grid.getChilds().values()) {
      if (component != null) {
        numOcean = 0;
        numLand = 0;
        for (final AGridComponent componentNeighbor : component.getNeighbors().values()) {
          if (componentNeighbor != null) {
            if (getProperties(componentNeighbor).isOcean()) {
              numOcean++;
            }
            if (!getProperties(componentNeighbor).isWater()) {
              numLand++;
            }
          }
        }
        getProperties(component).setCoast((numOcean > 0) && (numLand > 0));
      }
    }

    // Set the corner attributes based on the computed polygon
    // attributes. If all polygons connected to this corner are
    // ocean, then it's ocean; if all are land, then it's land;
    // otherwise it's coast.
    for (final Corner corner : grid.getAllCorners()) {
      numOcean = 0;
      numLand = 0;
      for (final AGridComponent touche : corner.getTouches().values()) {
        if (touche != null) {
          if (getProperties(touche).isOcean()) {
            numOcean++;
          }
          if (!getProperties(touche).isWater()) {
            numLand++;
          }
        }
      }
      getProperties(corner).setOcean((numOcean == corner.getTouchesSize()));
      getProperties(corner).setCoast((numOcean > 0) && (numLand > 0));
      getProperties(corner).setWater(
          corner.isBorder()
              || ((numLand != corner.getTouchesSize()) && !getProperties(corner).isCoast()));
    }
  }

  /**
   * Calculate downslope pointers. At every point, we point to the point downstream from it, or to itself. This is used
   * for generating rivers and watersheds.
   */
  private static void calculateDownslopes(final Grid grid) {
    Corner cornerTMP;

    for (final Corner corner : grid.getAllCorners()) {
      if (corner != null) {
        cornerTMP = corner;
        for (final Corner cornerAdjacent : corner.getAdjacents().values()) {
          if (cornerAdjacent != null) {
            if (getProperties(cornerAdjacent).getElevation() <= getProperties(cornerTMP).getElevation()) {
              cornerTMP = cornerAdjacent;
            }
          }
        }
        getProperties(corner).setDownslope(cornerTMP);
      }
    }
  }

  public static IslandProperties getProperties(final AGridComponent component) {
    return ((IslandProperties) component.getProperties());
  }

  public static IslandProperties getProperties(final Corner corner) {
    return ((IslandProperties) corner.getProperties());
  }

  /**
   * Create an array of corners that are on land only, for use by algorithms that work only on land. We return an array
   * instead of a vector because the redistribution algorithms want to sort this array using Array.sortOn.
   */
  private static ArrayList<Corner> landCorners(final List<Corner> corners) {
    final ArrayList<Corner> locations = new ArrayList<Corner>();
    for (final Corner corner : corners) {
      if (corner != null) {
        if (!getProperties(corner).isOcean() && !getProperties(corner).isCoast()) {
          locations.add(corner);
        }
      }
    }
    return locations;
  }

  /**
   * Change the overall distribution of elevations so that lower elevations are more common than higher elevations.
   * Specifically, we want elevation X to have frequency (1-X). To do this we will sort the corners, then set each
   * corner to its desired elevation.
   */
  private static void redistributeElevations(final List<Corner> corners) {
    // SCALE_FACTOR increases the mountain area. At 1.0 the maximum
    // elevation barely shows up on the map, so we set it to 1.1.
    final double SCALE_FACTOR = 1.1;
    double x, y;
    final List<Corner> locations = landCorners(corners);

    Collections.sort(locations, new LocationComparator());

    for (int i = 0; i < locations.size(); i++) {
      // Let y(x) be the total area that we want at elevation <= x.
      // We want the higher elevations to occur less than lower
      // ones, and set the area to be y(x) = 1 - (1-x)^2.
      y = i / (locations.size());
      // Now we have to solve for x, given the known y.
      // * y = 1 - (1-x)^2
      // * y = 1 - (1 - 2x + x^2)
      // * y = 2x - x^2
      // * x^2 - 2x + y = 0
      // From this we can use the quadratic equation to get:
      x = Math.sqrt(SCALE_FACTOR) - Math.sqrt(SCALE_FACTOR * (1 - y));
      x = Math.min(x, 1);
      getProperties(locations.get(i)).setElevation(x);
    }

    for (final Corner corner : corners) {
      if (getProperties(corner).isOcean() || getProperties(corner).isCoast()) {
        getProperties(corner).setElevation(0.0);
      }
    }
  }

  private final IIslandShape islandShape;

  private final Random       random;

  public IslandBiomeManager(final int size, final Random random) {
    super(PROPERTIES);
    this.random = random;
    this.islandShape = IslandShapeFactory.createIslandShape(SHAPE, random, size);
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
      getProperties(corner).setWater(!isInside(corner.getPoint()));
      // The edges of the map are elevation 0
      if (corner.isBorder()) {
        getProperties(corner).setElevation(0.0);
        queue.add(corner);
      } else {
        getProperties(corner).setElevation(Double.MAX_VALUE);
      }
    }

    // Traverse the graph and assign elevations to each point. As we
    // move away from the map border, increase the elevations. This
    // guarantees that rivers always have a way down to the coast by
    // going downhill (no local minima).
    while (!queue.isEmpty()) {
      final Corner corner = queue.pop();
      for (final Corner cornerAdjacent : corner.getAdjacents().values()) {
        if (cornerAdjacent != null) {
          // Every step up is epsilon over water or 1 over land. The
          // number doesn't matter because we'll rescale the
          // elevations later.
          newElevation = 0.01 + getProperties(corner).getElevation();
          if (!getProperties(corner).isWater() && !getProperties(cornerAdjacent).isWater()) {
            newElevation += 1;
            // HACK: the map looks nice because of randomness of
            // points, randomness of rivers, and randomness of
            // edges. Without random point selection, I needed to
            // inject some more randomness to make maps look
            // nicer. I'm doing it here, with elevations, but I
            // think there must be a better way. This hack is only
            // used with square/hexagon grids.
            newElevation += this.random.nextDouble();
          }
          // If this point changed, we'll add it to the queue so
          // that we can process its neighbors too.
          if (newElevation < getProperties(cornerAdjacent).getElevation()) {
            getProperties(cornerAdjacent).setElevation(newElevation);
            queue.add(cornerAdjacent);
          }
        }
      }
    }
  }

  @Override
  protected void execAssignBiome() {
    if (!(this.gridComponent instanceof Grid)) {
      throw new ClassCastException("The gridComponent must be an instance of " + Grid.class.getName());
    }
    final Grid grid = (Grid) this.gridComponent;
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
    redistributeElevations(grid.getAllCorners());

    // Polygon elevations are the average of their corners
    assignElevations(grid);

    // System.out.println("Assign downslopes ...");
    // Determine downslope paths.
    calculateDownslopes(grid);

    // Determine watersheds: for every corner, where does it flow
    // out into the ocean?
    // calculateWatersheds();

    // Create rivers.
    // System.out.println("Create River ...");
    // createRivers();

    // Determine moisture at corners, starting at rivers
    // and lakes, but not oceans. Then redistribute
    // moisture to cover the entire range evenly from 0.0
    // to 1.0. Then assign polygon moisture as the average
    // of the corner moisture.
    // assignCornerMoisture();
    // redistributeMoisture(landCorners(this.graph.getCorners()));

    // System.out.println("Assign moisture ...");
    // assignMoisture();
    // System.out.println("Assign biomes ...");
    // assignBiomes();
  }

  @Override
  public IBiome<String> getBiome(final AGridComponent gridComponent) {
    IBiome<String> biome = null;
    if (getProperties(gridComponent).isOcean()) {
      biome = new IslandStringBiome(StringData.OCEAN);
    } else if (getProperties(gridComponent).isWater()) {
      if (getProperties(gridComponent).getElevation() < 0.1) {
        biome = new IslandStringBiome(StringData.MARSH);
      }
      if (getProperties(gridComponent).getElevation() > 0.8) {
        biome = new IslandStringBiome(StringData.ICE);
      }
      biome = new IslandStringBiome(StringData.LAKE);
    } else if (getProperties(gridComponent).isCoast()) {
      biome = new IslandStringBiome(StringData.BEACH);
    } else if (getProperties(gridComponent).getElevation() > 0.8) {
      if (getProperties(gridComponent).getMoisture() > 0.50) {
        biome = new IslandStringBiome(StringData.SNOW);
      } else if (getProperties(gridComponent).getMoisture() > 0.33) {
        biome = new IslandStringBiome(StringData.TUNDRA);
      } else if (getProperties(gridComponent).getMoisture() > 0.16) {
        biome = new IslandStringBiome(StringData.BARE);
      } else {
        biome = new IslandStringBiome(StringData.SCORCHED);
      }
    } else if (getProperties(gridComponent).getElevation() > 0.6) {
      if (getProperties(gridComponent).getMoisture() > 0.66) {
        biome = new IslandStringBiome(StringData.TAIGA);
      } else if (getProperties(gridComponent).getMoisture() > 0.33) {
        biome = new IslandStringBiome(StringData.SHRUBLAND);
      } else {
        biome = new IslandStringBiome(StringData.TEMPERATE_DESERT);
      }
    } else if (getProperties(gridComponent).getElevation() > 0.3) {
      if (getProperties(gridComponent).getMoisture() > 0.83) {
        biome = new IslandStringBiome(StringData.TEMPERATE_RAIN_FOREST);
      } else if (getProperties(gridComponent).getMoisture() > 0.50) {
        biome = new IslandStringBiome(StringData.TEMPERATE_DECIDUOUS_FOREST);
      } else if (getProperties(gridComponent).getMoisture() > 0.16) {
        biome = new IslandStringBiome(StringData.GRASSLAND);
      } else {
        biome = new IslandStringBiome(StringData.TEMPERATE_DESERT);
      }
    } else {
      if (getProperties(gridComponent).getMoisture() > 0.66) {
        biome = new IslandStringBiome(StringData.TROPICAL_RAIN_FOREST);
      } else if (getProperties(gridComponent).getMoisture() > 0.33) {
        biome = new IslandStringBiome(StringData.TROPICAL_SEASONAL_FOREST);
      } else if (getProperties(gridComponent).getMoisture() > 0.16) {
        biome = new IslandStringBiome(StringData.GRASSLAND);
      } else {
        biome = new IslandStringBiome(StringData.SUBTROPICAL_DESERT);
      }
    }
    return biome;
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
