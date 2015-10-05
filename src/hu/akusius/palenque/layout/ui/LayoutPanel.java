package hu.akusius.palenque.layout.ui;

import hu.akusius.palenque.layout.data.FrameInfo;
import hu.akusius.palenque.layout.data.Layout;
import hu.akusius.palenque.layout.data.LayoutContainer;
import hu.akusius.palenque.layout.op.*;
import hu.akusius.palenque.layout.ui.rendering.LayoutNormalizedRenderer;
import hu.akusius.palenque.layout.ui.rendering.LayoutRenderer;
import hu.akusius.palenque.layout.ui.rendering.NumberRenderer;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Panel az elrendezés megjelenítéséhez.
 * @author Bujdosó Ákos
 */
public class LayoutPanel extends BufferedPanel {

  private final OperationManager om;

  private final DisplayManager dm;

  private final PlayManager pm;

  private final EditManager em;

  private final PropToggle normalizedView;

  private final Layout defLayout;

  private final Color refColor = new Color(0, 170, 170);

  private final Color activeColor = new Color(150, 50, 50, 150);

  private AlphaComposite refComposite;

  private AlphaComposite layoutComposite;

  public LayoutPanel(OperationManager operationManager) {
    this.om = operationManager;
    this.dm = om.getDisplayManager();
    this.pm = om.getPlayManager();
    this.em = om.getEditManager();
    this.normalizedView = om.getNormalizedViewToggle();
    this.defLayout = Layout.createDefault();
    this.setOpaque(false);

    hookEvents();
    refreshComposites();
  }

  private void hookEvents() {
    om.getLayoutContainer().addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (LayoutContainer.PROP_LAYOUT.equals(evt.getPropertyName())) {
          clear();
        }
      }
    });

    pm.getFrameSlider().addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (PropSliderFrame.PROP_VALUE.equals(evt.getPropertyName())) {
          clear();
        }
      }
    });
    pm.getPlayingToggle().addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (PropToggle.PROP_SELECTED.equals(evt.getPropertyName())) {
          setDirectMode((boolean) evt.getNewValue()); // Lejátszáskor nem használjuk a belső buffert
        }
      }
    });

    PropertyChangeListener selectedListener = new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (PropToggle.PROP_SELECTED.equals(evt.getPropertyName())) {
          clear();
        }
      }
    };
    dm.getShowNumbersToggle().addPropertyChangeListener(selectedListener);
    dm.getShowReferenceToggle().addPropertyChangeListener(selectedListener);
    normalizedView.addPropertyChangeListener(selectedListener);

    em.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (EditManager.PROP_ACTIVEITEM.equals(evt.getPropertyName())) {
          clear();
        }
      }
    });

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
    opm.getSlider(OpacityManager.Layer.Reference).addPropertyChangeListener(opacityListener);
    opm.getSlider(OpacityManager.Layer.Layout).addPropertyChangeListener(opacityListener);
  }

  @Override
  protected void redraw(Graphics2D g, Dimension d) {
    super.redraw(g, d);

    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    Layout layout = om.getLayoutContainer().getLayout();

    PropSliderFrame fs = pm.getFrameSlider();
    if (fs.isFirstFrame() && !pm.getPlayingToggle().isSelected()) {
      if (dm.getShowReferenceToggle().isSelected()) {
        g.setComposite(refComposite);
        g.setColor(refColor);
        LayoutRenderer.render(defLayout, null, null, g, d);
      }
      g.setComposite(layoutComposite);
      g.setColor(Color.BLACK);
      LayoutRenderer.render(layout, em.getActiveItem(), activeColor, g, d);
      if (dm.getShowNumbersToggle().isSelected()) {
        g.setColor(Color.BLUE);
        NumberRenderer.render(layout, em.getActiveItem(), activeColor, g, d);
      }
    } else {
      FrameInfo fi = fs.getCurrentFrameInfo();
      if (dm.getShowReferenceToggle().isSelected()) {
        g.setComposite(refComposite);
        g.setColor(refColor);
        if (normalizedView.isSelected()) {
          LayoutNormalizedRenderer.render(defLayout, fi, g, d);
        } else {
          LayoutRenderer.render(defLayout, fi, g, d);
        }
      }
      g.setComposite(layoutComposite);
      g.setColor(Color.BLACK);
      if (normalizedView.isSelected()) {
        LayoutNormalizedRenderer.render(layout, fi, g, d);
      } else {
        LayoutRenderer.render(layout, fi, g, d);
      }
    }
  }

  private void refreshComposites() {
    OpacityManager opm = om.getOpacityManager();
    refComposite = getComposite(opm.getSlider(OpacityManager.Layer.Reference));
    layoutComposite = getComposite(opm.getSlider(OpacityManager.Layer.Layout));
  }

  private static AlphaComposite getComposite(PropSlider opacitySlider) {
    assert opacitySlider.getMin() == 0 && opacitySlider.getMax() == 100;

    float alpha = (float) opacitySlider.getValue() / 100f;
    return AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
  }

}
