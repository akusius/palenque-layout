package hu.akusius.palenque.layout.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

/**
 * Panel pufferelt megjelenítéssel.
 * @author Bujdosó Ákos
 */
public class BufferedPanel extends JPanel {

  private BufferedImage buffer;

  private boolean directMode = false;

  public BufferedPanel() {
    super(true);
  }

  public boolean isDirectMode() {
    return directMode;
  }

  protected void setDirectMode(boolean directMode) {
    this.directMode = directMode;
  }

  @Override
  protected void paintComponent(Graphics g) {
    // System.out.println("repaint: " + this.getClass().getSimpleName() + " clip: " + g.getClip());

    Dimension d = getSize();

    if (directMode) {
      redraw((Graphics2D) g, d);
    } else {
      if (buffer == null || buffer.getWidth() != d.width || buffer.getHeight() != d.height) {
        buffer = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D bufG2D = (Graphics2D) buffer.getGraphics();
        redraw(bufG2D, d);
      }
      g.drawImage(buffer, 0, 0, null);
    }
  }

  protected void redraw(Graphics2D g, Dimension d) {
    // System.out.println("redraw: " + this.getClass().getSimpleName());
  }

  protected final void clear() {
    clear(true);
  }

  protected final void clear(boolean repaint) {
    buffer = null;
    if (repaint) {
      repaint();
    }
  }
}
