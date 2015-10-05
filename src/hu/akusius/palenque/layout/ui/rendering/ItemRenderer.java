package hu.akusius.palenque.layout.ui.rendering;

import hu.akusius.palenque.layout.data.Item;
import hu.akusius.palenque.layout.data.ItemType;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import org.other.Matrix;

/**
 * Egy adott elem kirajzolását végző osztály.
 * @author Bujdosó Ákos
 */
public final class ItemRenderer {

  /**
   * A megadott elem kirajzolása 2D-ban.
   * @param item A kirajzolandó elem.
   * @param graphics A kirajzolás célja, beállított színekkel.
   * @param dim A megjelenítés dimenziói.
   */
  public static void render2D(Item item, Graphics graphics, Dimension dim) {
    render3D(item, null, graphics, dim);
  }

  /**
   * Visszaadja a megadott elemet 2D-ban befoglaló téglalapot.
   * @param item A vizsgálandó elem.
   * @param dim A megjelenítés dimenziói.
   * @return A megadott elemet 2D-ban befoglaló téglalap.
   */
  public static Rectangle get2DRect(Item item, Dimension dim) {
    int l = Integer.MAX_VALUE;
    int t = Integer.MAX_VALUE;
    int r = -1;
    int b = -1;
    int[][] points = Transformer.project(item.getRect(), null, dim);
    for (int[] p : points) {
      if (p[0] < l) {
        l = p[0];
      }
      if (p[0] > r) {
        r = p[0];
      }
      if (p[1] < t) {
        t = p[0];
      }
      if (p[1] > b) {
        b = p[0];
      }
    }
    return new Rectangle(l, t, r - l, b - t);
  }

  /**
   * A megadott elem kirajzolása 3D-ban.
   * @param item A kirajzolandó elem.
   * @param transformMatrix Az alkalmazandó transzformációs mátrix.
   * @param g A kirajzolás célja, beállított színekkel.
   * @param dim A megjelenítés dimenziói.
   */
  public static void render3D(Item item, Matrix transformMatrix, Graphics g, Dimension dim) {
    int[][] ps = Transformer.project(item.getRect(), transformMatrix, dim);
    ItemType type = item.getType();
    if (type == ItemType.Triplet) {
      g.drawPolyline(
              new int[]{ps[0][0], ps[1][0], ps[2][0], ps[3][0], ps[0][0]},
              new int[]{ps[0][1], ps[1][1], ps[2][1], ps[3][1], ps[0][1]},
              5);
      int[][] cps = Transformer.project(item.getCenter(), transformMatrix, dim);
      g.drawLine(cps[0][0], cps[0][1], cps[1][0], cps[1][1]);
      g.drawLine(cps[3][0], cps[3][1], cps[2][0], cps[2][1]);
    } else {
      assert type == ItemType.Sun || type == ItemType.Star;
      g.drawLine(ps[0][0], ps[0][1], ps[2][0], ps[2][1]);
      g.drawLine(ps[1][0], ps[1][1], ps[3][0], ps[3][1]);
      if (type == ItemType.Star) {
        int[][] cps = Transformer.project(item.getCenter(), transformMatrix, dim);
        g.drawPolyline(
                new int[]{cps[0][0], cps[1][0], cps[2][0], cps[3][0], cps[0][0]},
                new int[]{cps[0][1], cps[1][1], cps[2][1], cps[3][1], cps[0][1]},
                5);
      }
    }
  }

  private ItemRenderer() {
  }

}
