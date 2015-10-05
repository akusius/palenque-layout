package hu.akusius.palenque.layout.ui;

import hu.akusius.palenque.layout.op.*;
import hu.akusius.palenque.layout.ui.rendering.GridRenderer;
import hu.akusius.palenque.layout.ui.rendering.LidRenderer;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Panel a háttér megjelenítéséhez.
 * @author Bujdosó Ákos
 */
public class BackgroundPanel extends BufferedPanel {

  public static final String FOOTER_TEXT = null;

  private final OperationManager om;

  private AlphaComposite lidComposite;

  private AlphaComposite gridComposite;

  public BackgroundPanel(OperationManager operationManager) {
    this.om = operationManager;

    hookEvents();
    refreshComposites();
  }

  private void hookEvents() {
    DisplayManager dm = om.getDisplayManager();
    PropertyChangeListener selectedListener = new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (PropToggle.PROP_SELECTED.equals(evt.getPropertyName())) {
          clear();
        }
      }
    };
    dm.getShowLidToggle().addPropertyChangeListener(selectedListener);
    dm.getShowGridToggle().addPropertyChangeListener(selectedListener);
    om.getNormalizedViewToggle().addPropertyChangeListener(selectedListener);

    OpacityManager opm = om.getOpacityManager();
    PropertyChangeListener opacityListener = new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (PropSlider.PROP_VALUE.equals(evt.getPropertyName())) {
          refreshComposites();
          clear();
        }
      }
    };
    opm.getSlider(OpacityManager.Layer.Lid).addPropertyChangeListener(opacityListener);
    opm.getSlider(OpacityManager.Layer.Grid).addPropertyChangeListener(opacityListener);
  }

  @Override
  protected void redraw(Graphics2D g, Dimension d) {
    super.redraw(g, d);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    g.setColor(Color.WHITE);
    g.fillRect(0, 0, d.width, d.height);

    if (FOOTER_TEXT != null) {
      drawFooterText(g, d, FOOTER_TEXT);
    }

    DisplayManager dm = om.getDisplayManager();
    if (dm.getShowLidToggle().isSelected()) {
      g.setComposite(lidComposite);
      LidRenderer.render(g, d);
    }
    if (dm.getShowGridToggle().isSelected()) {
      g.setComposite(gridComposite);
      GridRenderer.render(g, d);
    }
  }

  private void refreshComposites() {
    OpacityManager opm = om.getOpacityManager();
    lidComposite = getComposite(opm.getSlider(OpacityManager.Layer.Lid));
    gridComposite = getComposite(opm.getSlider(OpacityManager.Layer.Grid));
  }

  private static AlphaComposite getComposite(PropSlider opacitySlider) {
    assert opacitySlider.getMin() == 0 && opacitySlider.getMax() == 100;

    float alpha = (float) opacitySlider.getValue() / 100f;
    return AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
  }

  private static final Color footerColor = new Color(100, 100, 255, 65);

  private static Font footerFont;

  private static void drawFooterText(Graphics g, Dimension d, String footerText) {
    g = g.create();
    int fontSize = Math.min(d.height, d.width) / 30;
    if (footerFont == null || footerFont.getSize() != fontSize) {
      footerFont = g.getFont().deriveFont(Font.ITALIC, (float) fontSize);
    }
    g.setFont(footerFont);

    FontMetrics fm = g.getFontMetrics();
    int width = fm.stringWidth(footerText);

    g.setColor(footerColor);
    g.drawString(footerText, d.width - width - 5, d.height - 5);
  }
}
