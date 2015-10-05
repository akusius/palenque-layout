package hu.akusius.palenque.layout.ui.rendering;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * A fedélkép kirajzolását végző osztály.
 * @author Bujdosó Ákos
 */
public final class LidRenderer {

  private static final double size = 57d;

  private static final double[] center = {-.5d, -1d};

  private static final double[][] corners;

  private static BufferedImage image;

  static {
    double hs = size / 2d;
    corners = new double[][]{
      {center[0] - hs, center[1] + hs, 0d},
      {center[0] + hs, center[1] - hs, 0d}
    };
  }

  /**
   * A fedélkép kirajzolása.
   * @param g A kirajzolás célja.
   * @param dim A megjelenítés dimenziói.
   */
  public static void render(Graphics2D g, Dimension dim) {
    try {
      if (image == null) {
        image = ImageIO.read(LidRenderer.class.getResource("lid.png"));
      }

      int[][] ps = Transformer.project(corners, dim);
      int width = ps[1][0] - ps[0][0] + 1;
      int height = width;

      g = (Graphics2D) g.create();

      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      g.drawImage(image, ps[0][0], ps[0][1], width, height, null);
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  private LidRenderer() {
  }

}
