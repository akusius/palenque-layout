package hu.akusius.palenque.layout.ui.rendering;

import hu.akusius.palenque.layout.data.Item;
import hu.akusius.palenque.layout.data.Layout;
import java.awt.*;
import java.util.List;

/**
 * Az elemekhez tartozó sorszámokat kirajzoló osztály.
 * @author Bujdosó Ákos
 */
public final class NumberRenderer {

  private static Font font;

  /**
   * A megadott elemhez a sorszám kirajzolása 2D-ban.
   * @param item Az elem.
   * @param number Az elem kirajzolandó sorszáma.
   * @param g A kirajzolás célja, beállított színekkel.
   * @param dim A megjelenítés dimenziói.
   */
  public static void render(Item item, int number, Graphics g, Dimension dim) {
    int[][] ps = Transformer.project(item.getRect(), null, dim);
    int[] c = new int[2];
    c[0] = (ps[2][0] + ps[3][0]) / 2;
    c[1] = ps[2][1];

    String str = Integer.toString(number);

    g = g.create();
    int fontSize = Math.min(dim.height, dim.width) / 45;
    if (font == null || font.getSize() != fontSize) {
      font = g.getFont().deriveFont((float) fontSize);
    }
    g.setFont(font);

    // A felső közepet számoltuk ki, ezt kell átváltani a bal alsóra
    FontMetrics fm = g.getFontMetrics();
    int height = fm.getHeight();
    int width = fm.stringWidth(str);
    int lbx = c[0] - width / 2;
    int lby = c[1] + height;

    g.drawString(str, lbx, lby);
  }

  /**
   * A teljes elrendezéshez a sorszámok kirajzolása.
   * @param layout Az elrendezés.
   * @param activeItem Az aktív elem, ha létezik.
   * @param activeColor Az aktív elem sorszámának kirajzolási színe.
   * @param g A kirajzolás célja, beállított színekkel.
   * @param dim A megjelenítés dimenziói.
   */
  public static void render(Layout layout, Item activeItem, Color activeColor, Graphics g, Dimension dim) {
    List<Item> items = layout.getItems();
    for (int i = 0; i < items.size(); i++) {
      Item item = items.get(i);
      Color savedColor = null;
      if (item.equals(activeItem)) {
        savedColor = g.getColor();
        g.setColor(activeColor);
      }
      render(item, i + 1, g, dim);
      if (savedColor != null) {
        g.setColor(savedColor);
      }
    }
  }

  private NumberRenderer() {
  }

}
