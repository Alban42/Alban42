/**
 * 
 */
package com.alma42.mapgen;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import com.alma42.mapgen.factories.ZoneTypeFactory;
import com.alma42.mapgen.utils.geometry.Center;
import com.alma42.mapgen.utils.geometry.Corner;
import com.alma42.mapgen.utils.geometry.Edge;
import com.alma42.mapgen.utils.geometry.Rectangle;
import com.alma42.mapgen.zone.Zone;
import com.alma42.mapgen.zone.types.IZoneType;

/**
 * @author Alban
 * 
 */
public class Map {

  // Passed in by the caller:
  public int                size;

  // These store the graph data
  public Zone               zone;

  private Random            r;

  public Rectangle          bounds;
  private ArrayList<Center> centers;
  private ArrayList<Edge>   edges;
  private ArrayList<Corner> corners;

  private Color             RIVER;
  private Graphics2D        graphic;

  public Map(int size, int pointNumber, int seed, int pointSelectorType, int graphType, int islandShapeType,
      int riverCreatorType, int biomeManagerType) {
    this.size = size;

    IZoneType zoneType = ZoneTypeFactory.createIsland(size, pointNumber, seed, pointSelectorType, graphType,
        islandShapeType, riverCreatorType, biomeManagerType);
    zoneType.createZone();
    this.bounds = zoneType.getBounds();
    this.centers = zoneType.getGraph().getCenters();
    this.edges = zoneType.getGraph().getEdges();
    this.corners = zoneType.getGraph().getCorners();

    this.RIVER = new Color(0x225588);
  }

  public void paint() {
    final BufferedImage img = new BufferedImage(this.size, this.size, BufferedImage.TYPE_4BYTE_ABGR);
    this.graphic = img.createGraphics();
    this.graphic.setColor(Color.BLACK);
    this.graphic.fillRect(0, 0, this.size, this.size);

    paint(this.graphic, true, true, false, false, false, true);

    final JFrame frame = new JFrame() {
      private static final long serialVersionUID = -1131483500708731880L;

      @Override
      public void paint(final Graphics g) {
        g.drawImage(img, 25, 35, null);
      }
    };
    frame.setTitle("java fortune");
    frame.setVisible(true);
    frame.setSize(this.size + 50, this.size + 50);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }

  // also records the area of each voronoi cell
  private void paint(final Graphics2D g, final boolean drawBiomes,
      final boolean drawRivers, final boolean drawSites,
      final boolean drawCorners, final boolean drawDelaunay,
      final boolean drawVoronoi) {
    final int numSites = this.centers.size();

    Color[] defaultColors = null;
    if (!drawBiomes) {
      defaultColors = new Color[numSites];
      for (int i = 0; i < defaultColors.length; i++) {
        defaultColors[i] = new Color(this.r.nextInt(255),
            this.r.nextInt(255), this.r.nextInt(255));
      }
    }

    // draw via triangles
    for (final Center center : this.centers) {
      if (drawBiomes) {
        g.setColor((Color) center.biome.getValue());
      } else {
        g.setColor(defaultColors[center.index]);
      }

      // only used if Center c is on the edge of the graph. allows for
      // completely filling in the outer polygons
      Corner edgeCorner1 = null;
      Corner edgeCorner2 = null;
      center.area = 0;
      for (final Center n : center.neighbors) {
        final Edge e = edgeWithCenters(center, n);

        if (e.v0 == null) {
          // outermost voronoi edges aren't stored in the graph
          continue;
        }

        // find a corner on the exterior of the graph
        // if this Edge e has one, then it must have two,
        // finding these two corners will give us the missing
        // triangle to render. this special triangle is handled
        // outside this for loop
        final Corner cornerWithOneAdjacent = e.v0.border ? e.v0 : e.v1;
        if (cornerWithOneAdjacent.border) {
          if (edgeCorner1 == null) {
            edgeCorner1 = cornerWithOneAdjacent;
          } else {
            edgeCorner2 = cornerWithOneAdjacent;
          }
        }

        drawTriangle(g, e.v0, e.v1, center);
        center.area += Math.abs((center.point.x * (e.v0.point.y - e.v1.point.y))
            + (e.v0.point.x * (e.v1.point.y - center.point.y))
            + (e.v1.point.x * (center.point.y - e.v0.point.y))) / 2;
      }

      // handle the missing triangle
      if (edgeCorner2 != null) {
        /*
         * if these two outer corners are NOT on the same exterior edge of the graph, then we actually must render a
         * polygon (w/ 4 points) and take into consideration one of the four corners (either 0,0 or 0,height or width,0
         * or width,height) note: the 'missing polygon' may have more than just 4 points. this is common when the number
         * of sites are quite low (less than 5), but not a problem with a more useful number of sites. TODO: find a way
         * to fix this
         */

        if (closeEnough(edgeCorner1.point.x, edgeCorner2.point.x, 1)) {
          drawTriangle(g, edgeCorner1, edgeCorner2, center);
        } else {
          final int[] x = new int[4];
          final int[] y = new int[4];
          x[0] = (int) center.point.x;
          y[0] = (int) center.point.y;
          x[1] = (int) edgeCorner1.point.x;
          y[1] = (int) edgeCorner1.point.y;

          // determine which corner this is
          x[2] = (int) ((closeEnough(edgeCorner1.point.x,
              this.bounds.x, 1) || closeEnough(edgeCorner2.point.x,
              this.bounds.x, .5))
              ? this.bounds.x
              : this.bounds.right);
          y[2] = (int) ((closeEnough(edgeCorner1.point.y,
              this.bounds.y, 1) || closeEnough(edgeCorner2.point.y,
              this.bounds.y, .5))
              ? this.bounds.y
              : this.bounds.bottom);

          x[3] = (int) edgeCorner2.point.x;
          y[3] = (int) edgeCorner2.point.y;

          g.fillPolygon(x, y, 4);
          center.area += 0; // TODO: area of polygon given vertices
        }
      }
    }

    for (final Edge e : this.edges) {
      if (drawDelaunay) {
        g.setStroke(new BasicStroke(1));
        g.setColor(Color.YELLOW);
        g.drawLine((int) e.d0.point.x, (int) e.d0.point.y,
            (int) e.d1.point.x, (int) e.d1.point.y);
      }
      if (drawRivers && (e.river > 0)) {
        g.setStroke(new BasicStroke(1 + (int) Math.sqrt(e.river * 2)));
        g.setColor(this.RIVER);
        g.drawLine((int) e.v0.point.x, (int) e.v0.point.y,
            (int) e.v1.point.x, (int) e.v1.point.y);
      }
    }

    if (drawSites) {
      g.setColor(Color.BLACK);
      for (final Center s : this.centers) {
        g.fillOval((int) (s.point.x - 2), (int) (s.point.y - 2), 4, 4);
      }
    }

    if (drawCorners) {
      g.setColor(Color.WHITE);
      for (final Corner c : this.corners) {
        g.fillOval((int) (c.point.x - 2), (int) (c.point.y - 2), 4, 4);
      }
    }
    g.setColor(Color.WHITE);
    g.drawRect((int) this.bounds.x, (int) this.bounds.y,
        (int) this.bounds.width, (int) this.bounds.height);
  }

  private static void drawTriangle(final Graphics2D g, final Corner c1,
      final Corner c2, final Center center) {
    final int[] x = new int[3];
    final int[] y = new int[3];
    x[0] = (int) center.point.x;
    y[0] = (int) center.point.y;
    x[1] = (int) c1.point.x;
    y[1] = (int) c1.point.y;
    x[2] = (int) c2.point.x;
    y[2] = (int) c2.point.y;
    g.fillPolygon(x, y, 3);
  }

  private static boolean closeEnough(final double d1, final double d2,
      final double diff) {
    return Math.abs(d1 - d2) <= diff;
  }

  private static Edge edgeWithCenters(final Center c1, final Center c2) {
    for (final Edge e : c1.borders) {
      if ((e.d0 == c2) || (e.d1 == c2)) {
        return e;
      }
    }
    return null;
  }
}
