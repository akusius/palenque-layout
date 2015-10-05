package hu.akusius.palenque.layout.ui.rendering;

import hu.akusius.palenque.layout.data.FrameInfo;
import hu.akusius.palenque.layout.data.Item;
import hu.akusius.palenque.layout.data.Layout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import org.other.Matrix;

/**
 * Egy teljes elrendezés kirajzolását végző osztály.
 * @author Bujdosó Ákos
 */
public final class LayoutRenderer {

  /**
   * Az elrendezés kirajzolása az első képkockához (szerkesztési mód).
   * @param layout A kirajzolandó elrendezés.
   * @param graphics A kirajzolás célja, beállított színekkel.
   * @param dim A megjelenítés dimenziói.
   */
  public static void render(Layout layout, Graphics graphics, Dimension dim) {
    render(layout, null, null, graphics, dim);
  }

  /**
   * Az elrendezés kirajzolása az első képkockához (szerkesztési mód), aktív elem kijelöléssel.
   * @param layout A kirajzolandó elrendezés.
   * @param activeItem Az aktív elem, ha létezik.
   * @param activeColor Az aktív elem kirajzolási színe.
   * @param g A kirajzolás célja, beállított színekkel.
   * @param dim A megjelenítés dimenziói.
   */
  public static void render(Layout layout, Item activeItem, Color activeColor, Graphics g, Dimension dim) {
    for (Item item : layout.getItems()) {
      Color savedColor = null;
      if (item.equals(activeItem)) {
        savedColor = g.getColor();
        g.setColor(activeColor);
      }
      ItemRenderer.render2D(item, g, dim);
      if (savedColor != null) {
        g.setColor(savedColor);
      }
    }
  }

  /**
   * Az elrendezés kirajzolása egy tetszőleges képkockához.
   * @param layout A kirajzolandó elrendezés.
   * @param frameInfo A képkocka, amihez rajzoljuk az elrendezést.
   * @param graphics A kirajzolás célja, beállított színekkel.
   * @param dim A megjelenítés dimenziói.
   */
  public static void render(Layout layout, FrameInfo frameInfo, Graphics graphics, Dimension dim) {
    int stepNum = frameInfo.getStepNum();
    double percent = frameInfo.getPercent() / 100.0;
    double hmPi = -Math.PI / 2.0;

    Matrix m = new Matrix();
    m.identity();

    if (stepNum == 0) {
      renderItems(layout, m, graphics, dim);
    } else if (stepNum >= 1 && stepNum <= 4) {
      renderItems(layout, m, graphics, dim);
      for (int i = 2; i <= stepNum; i++) {
        m.rotateZ(hmPi);
        renderItems(layout, m, graphics, dim);
      }
      if (stepNum < 4 && percent >= 0.2) {
        m.rotateZ(hmPi * (percent - 0.2) * 1.25);
        renderItems(layout, m, graphics, dim);
      }
    } else if (stepNum >= 5) {
      Matrix m2 = new Matrix();
      for (int i = 1; i <= 4; i++) {
        renderItems(layout, m, graphics, dim);
        m2.identity();
        m2.rotateY(stepNum == 5 ? Math.PI * percent : Math.PI);
        m2.preMultiply(m);
        renderItems(layout, m2, graphics, dim);
        m.rotateZ(hmPi);
      }
    }
  }

  private static void renderItems(Layout l, Matrix m, Graphics g, Dimension d) {
    for (Item item : l.getItems()) {
      ItemRenderer.render3D(item, m, g, d);
    }
  }

  private LayoutRenderer() {
  }

}
