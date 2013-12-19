package com.alma42.mapgen.biomes.implementations.island;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.alma42.mapgen.biomes.ABiomeManager;
import com.alma42.mapgen.biomes.IBiome;
import com.alma42.mapgen.biomes.factory.PropertiesFactory;
import com.alma42.mapgen.biomes.implementations.island.IslandBiome.ColorData;
import com.alma42.mapgen.biomes.implementations.island.comparator.LocationComparator;
import com.alma42.mapgen.biomes.implementations.island.comparator.MoistureComparator;
import com.alma42.mapgen.biomes.implementations.island.shape.IIslandShape;
import com.alma42.mapgen.biomes.implementations.island.shape.factory.IslandShapeFactory;
import com.alma42.mapgen.grid.AGridComponent;
import com.alma42.mapgen.grid.Grid;
import com.alma42.mapgen.grid.shape.Shape;
import com.alma42.mapgen.utils.geometry.Corner;
import com.alma42.mapgen.utils.geometry.Point;

public class IslandBiomeManager extends ABiomeManager {

  /**
   * 0 to 1, fraction of water corners for water polygon
   */
  public static final double LAKE_THRESHOLD = 0.3;
  private static final int   PROPERTIES     = PropertiesFactory.ISLAND;
  private static final int   SHAPE          = IslandShapeFactory.RADIAL;

  /**
   * Calculate moisture. Freshwater sources spread moisture: rivers and lakes (not oceans). Saltwater sources have
   * moisture but do not spread it (we set it at the end, after propagation).
   */
  private static void assignCornerMoisture(final Grid grid) {
    double newMoisture;
    final LinkedList<Corner> queue = new LinkedList<Corner>();
    // Fresh water
    for (final Corner corner : grid.getAllCorners()) {
      if (corner != null) {
        if ((getProperties(corner).isWater() || (getProperties(corner).getRiver() > 0))
            && !getProperties(corner).isOcean()) {
          getProperties(corner).setMoisture(
              getProperties(corner).getRiver() > 0 ? Math.min(3.0, (0.2 * getProperties(corner).getRiver())) : 1.0);
          queue.add(corner);
        } else {
          getProperties(corner).setMoisture(0.0);
        }
      }
    }

    while (!queue.isEmpty()) {
      final Corner corner = queue.pop();
      for (final Corner adjacentCorner : corner.getAdjacents().values()) {
        if (adjacentCorner != null) {
          newMoisture = getProperties(corner).getMoisture() * 0.9;
          if (newMoisture > getProperties(adjacentCorner).getMoisture()) {
            getProperties(adjacentCorner).setMoisture(newMoisture);
            queue.add(adjacentCorner);
          }
        }
      }
    }

    // Salt water
    for (final Corner corner : grid.getAllCorners()) {
      if (corner != null) {
        if (getProperties(corner).isOcean() || getProperties(corner).isCoast()) {
          getProperties(corner).setMoisture(1.0);
        }
      }
    }
  }

  /** Polygon elevations are the average of the elevations of their corners. */
  private static void assignElevations(final Grid grid) {
    double sumElevation;
    int sumCorners;
    for (final AGridComponent child : grid.getChilds().values()) {
      if (child != null) {
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
  }

  /**
   * Polygon moisture is the average of the moisture at corners
   */
  private static void assignMoisture(final Grid grid) {
    double sumMoisture, cornerSize;
    for (final AGridComponent component : grid.getChilds().values()) {
      if (component != null) {
        sumMoisture = 0.0;
        cornerSize = 0.0;
        for (final Corner q : component.getCorners().values()) {
          if (q != null) {
            if (getProperties(q).getMoisture() > 1.0) {
              getProperties(q).setMoisture(1.0);
            }
            sumMoisture += getProperties(q).getMoisture();
            cornerSize++;
          }

        }
        getProperties(component).setMoisture(sumMoisture / cornerSize);
      }
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

  /**
   * Calculate the watershed of every land point. The watershed is the last downstream land point in the downslope
   * graph. TODO: watersheds are currently calculated on corners, but it'd be more useful to compute them on polygon
   * centers so that every polygon can be marked as being in one watershed.
   */
  private static void calculateWatersheds(final Grid grid) {
    boolean changed;
    Corner cornerTMP;
    // Initially the watershed pointer points downslope one step.
    for (final Corner corner : grid.getAllCorners()) {
      if (corner != null) {
        getProperties(corner).setWatershed(corner);
        if (!getProperties(corner).isOcean() && !getProperties(corner).isCoast()) {
          getProperties(corner).setWatershed(getProperties(corner).getDownslope());
        }
      }
    }
    // Follow the downslope pointers to the coast. Limit to 100
    // iterations although most of the time with numPoints==2000 it
    // only takes 20 iterations because most points are not far from
    // a coast. TODO: can run faster by looking at
    // p.watershed.watershed instead of p.downslope.watershed.
    for (int i = 0; i < 100; i++) {
      changed = false;
      for (final Corner corner : grid.getAllCorners()) {
        if (corner != null) {
          if (!getProperties(corner).isOcean() && !getProperties(corner).isCoast()
              && !getProperties(getProperties(corner).getWatershed()).isCoast()) {
            cornerTMP = getProperties(getProperties(corner).getDownslope()).getWatershed();
            if (!getProperties(cornerTMP).isOcean()) {
              getProperties(corner).setWatershed(cornerTMP);
            }
            changed = true;
          }
        }
      }
      if (!changed) {
        break;
      }
    }
    // How big is each watershed?
    for (final Corner corner : grid.getAllCorners()) {
      if (corner != null) {
        cornerTMP = getProperties(corner).getWatershed();
        getProperties(cornerTMP).setWatershedSize(1 + getProperties(cornerTMP).getWatershedSize());
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
      y = (double) i / (locations.size());
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
      if (corner != null) {
        if (getProperties(corner).isOcean() || getProperties(corner).isCoast()) {
          getProperties(corner).setElevation(0.0);
        }
      }
    }
  }

  // Change the overall distribution of moisture to be evenly distributed.
  private static void redistributeMoisture(final ArrayList<Corner> locations) {
    Collections.sort(locations, new MoistureComparator());
    for (int i = 0; i < locations.size(); i++) {
      getProperties(locations.get(i)).setMoisture((double) i / (locations.size()));
    }
  }

  private final IIslandShape islandShape;

  private final Random       random;

  public IslandBiomeManager(final int size, final Random random) {
    super(PROPERTIES);
    this.random = random;
    this.islandShape = IslandShapeFactory.createIslandShape(SHAPE, random, size);
  }

  private void assignBiomes(final Grid grid) {
    for (final AGridComponent child : grid.getChilds().values()) {
      if (child instanceof Shape) {
        ((Shape) child).setBiome(getBiome(child));
      }
    }
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
      if (corner != null) {
        getProperties(corner).setWater(!isInside(corner.getPoint()));
        // The edges of the map are elevation 0
        if (corner.isBorder()) {
          getProperties(corner).setElevation(0.0);
          queue.add(corner);
        } else {
          getProperties(corner).setElevation(Double.MAX_VALUE);
        }
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
    long startedTime = System.currentTimeMillis();
    final Grid grid = (Grid) this.gridComponent;
    // Determine the elevations and water at corners.
    System.out.println("Assign Corner Elevations ...");
    startedTime = System.currentTimeMillis();
    assignCornerElevations(grid);
    System.out.println("\t - Time : " + ((System.currentTimeMillis() - startedTime) / 1000));

    // Determine polygon and corner type: ocean, coast, land.
    System.out.println("Assign Ocean and Coast... ");
    startedTime = System.currentTimeMillis();
    assignOceanCoastAndLand(grid);
    System.out.println("\t - Time : " + ((System.currentTimeMillis() - startedTime) / 1000));

    // Rescale elevations so that the highest is 1.0, and they're
    // distributed well. We want lower elevations to be more common
    // than higher elevations, in proportions approximately matching
    // concentric rings. That is, the lowest elevation is the
    // largest ring around the island, and therefore should more
    // land area than the highest elevation, which is the very
    // center of a perfectly circular island.
    System.out.println("Redistribute Elevations ... ");
    startedTime = System.currentTimeMillis();
    redistributeElevations(grid.getAllCorners());
    System.out.println("\t - Time : " + ((System.currentTimeMillis() - startedTime) / 1000));

    // Polygon elevations are the average of their corners
    System.out.println("Assign Elevations ... ");
    startedTime = System.currentTimeMillis();
    assignElevations(grid);
    System.out.println("\t - Time : " + ((System.currentTimeMillis() - startedTime) / 1000));

    System.out.println("Assign downslopes ...");
    startedTime = System.currentTimeMillis();
    // Determine downslope paths.
    calculateDownslopes(grid);
    System.out.println("\t - Time : " + ((System.currentTimeMillis() - startedTime) / 1000));

    System.out.println("Assign Watersheds ...");
    startedTime = System.currentTimeMillis();
    // Determine watersheds: for every corner, where does it flow
    // out into the ocean?
    calculateWatersheds(grid);
    System.out.println("\t - Time : " + ((System.currentTimeMillis() - startedTime) / 1000));

    // Create rivers.
    // System.out.println("Create River ...");
    // createRivers();

    // Determine moisture at corners, starting at rivers
    // and lakes, but not oceans. Then redistribute
    // moisture to cover the entire range evenly from 0.0
    // to 1.0. Then assign polygon moisture as the average
    // of the corner moisture.
    System.out.println("Assign Corner Moisture ...");
    startedTime = System.currentTimeMillis();
    assignCornerMoisture(grid);
    redistributeMoisture(landCorners(grid.getAllCorners()));
    System.out.println("\t - Time : " + ((System.currentTimeMillis() - startedTime) / 1000));

    System.out.println("Assign moisture ...");
    startedTime = System.currentTimeMillis();
    assignMoisture(grid);
    System.out.println("\t - Time : " + ((System.currentTimeMillis() - startedTime) / 1000));
    // System.out.println("Assign biomes ...");
    assignBiomes(grid);
  }

  @Override
  public IBiome<Integer> getBiome(final AGridComponent gridComponent) {
    IBiome<Integer> biome = null;
    if (getProperties(gridComponent).isOcean()) {
      biome = new IslandBiome(ColorData.OCEAN);
    } else if (getProperties(gridComponent).isWater()) {
      if (getProperties(gridComponent).getElevation() < 0.1) {
        biome = new IslandBiome(ColorData.MARSH);
      }
      if (getProperties(gridComponent).getElevation() > 0.8) {
        biome = new IslandBiome(ColorData.ICE);
      }
      biome = new IslandBiome(ColorData.LAKE);
    } else if (getProperties(gridComponent).isCoast()) {
      biome = new IslandBiome(ColorData.BEACH);
    } else if (getProperties(gridComponent).getElevation() > 0.8) {
      if (getProperties(gridComponent).getMoisture() > 0.50) {
        biome = new IslandBiome(ColorData.SNOW);
      } else if (getProperties(gridComponent).getMoisture() > 0.33) {
        biome = new IslandBiome(ColorData.TUNDRA);
      } else if (getProperties(gridComponent).getMoisture() > 0.16) {
        biome = new IslandBiome(ColorData.BARE);
      } else {
        biome = new IslandBiome(ColorData.SCORCHED);
      }
    } else if (getProperties(gridComponent).getElevation() > 0.6) {
      if (getProperties(gridComponent).getMoisture() > 0.66) {
        biome = new IslandBiome(ColorData.TAIGA);
      } else if (getProperties(gridComponent).getMoisture() > 0.33) {
        biome = new IslandBiome(ColorData.SHRUBLAND);
      } else {
        biome = new IslandBiome(ColorData.TEMPERATE_DESERT);
      }
    } else if (getProperties(gridComponent).getElevation() > 0.3) {
      if (getProperties(gridComponent).getMoisture() > 0.83) {
        biome = new IslandBiome(ColorData.TEMPERATE_RAIN_FOREST);
      } else if (getProperties(gridComponent).getMoisture() > 0.50) {
        biome = new IslandBiome(ColorData.TEMPERATE_DECIDUOUS_FOREST);
      } else if (getProperties(gridComponent).getMoisture() > 0.16) {
        biome = new IslandBiome(ColorData.GRASSLAND);
      } else {
        biome = new IslandBiome(ColorData.TEMPERATE_DESERT);
      }
    } else {
      if (getProperties(gridComponent).getMoisture() > 0.66) {
        biome = new IslandBiome(ColorData.TROPICAL_RAIN_FOREST);
      } else if (getProperties(gridComponent).getMoisture() > 0.33) {
        biome = new IslandBiome(ColorData.TROPICAL_SEASONAL_FOREST);
      } else if (getProperties(gridComponent).getMoisture() > 0.16) {
        biome = new IslandBiome(ColorData.GRASSLAND);
      } else {
        biome = new IslandBiome(ColorData.SUBTROPICAL_DESERT);
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
