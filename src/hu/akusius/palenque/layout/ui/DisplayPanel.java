package hu.akusius.palenque.layout.ui;

import hu.akusius.palenque.layout.op.OperationManager;
import hu.akusius.palenque.layout.op.PropToggle;
import hu.akusius.palenque.layout.ui.rendering.Transformer;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BoxLayout;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

/**
 * A megjelenítést végző panel.
 * @author Bujdosó Ákos
 */
public class DisplayPanel extends JPanel {

  private final OperationManager om;

  private final BackgroundPanel backgroundPanel;

  private final LayoutPanel layoutPanel;

  private final EditPanel editPanel;

  private final PropToggle normalizedView;

  private final ImageSource imageSource;

  public DisplayPanel(OperationManager operationManager) {
    this.om = operationManager;
    this.backgroundPanel = new BackgroundPanel(this.om);
    this.layoutPanel = new LayoutPanel(this.om);
    this.editPanel = new EditPanel(this.om);
    this.normalizedView = om.getNormalizedViewToggle();
    this.initComponents();

    this.normalizedView.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (PropToggle.PROP_SELECTED.equals(evt.getPropertyName())) {
          Transformer.adjustFL((boolean) evt.getNewValue() ? getSize() : null);
        }
      }
    });

    this.imageSource = new ImageSource() {

      @Override
      public int getMinSize() {
        return 60;
      }

      @Override
      public int getMaxSize() {
        return 1200;
      }

      @Override
      public int getSuggestedSize() {
        Dimension dim = getSize();
        int size = Math.min(dim.width, dim.height);
        return Math.max(Math.min(size, getMaxSize()), getMinSize());
      }

      @Override
      public BufferedImage generateImage(int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        Dimension dim = new Dimension(size, size);
        boolean normalized = om.getNormalizedViewToggle().isSelected();
        if (normalized) {
          Transformer.adjustFL(dim);
        }
        backgroundPanel.redraw((Graphics2D) graphics.create(), dim);
        layoutPanel.redraw((Graphics2D) graphics.create(), dim);
        if (normalized) {
          Transformer.adjustFL(getSize());
        }
        return image;
      }
    };
  }

  private void initComponents() {
    this.setBorder(new BevelBorder(BevelBorder.LOWERED));
    this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

    JLayeredPane layeredPane = new JLayeredPane();
    layeredPane.add(backgroundPanel, new Integer(1));
    layeredPane.add(layoutPanel, new Integer(2));
    layeredPane.add(editPanel, new Integer(3));

    layeredPane.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        // System.out.println("resize: layeredPane");
        Dimension size = getSize();
        backgroundPanel.setBounds(0, 0, size.width, size.height);
        layoutPanel.setBounds(0, 0, size.width, size.height);
        editPanel.setBounds(0, 0, size.width, size.height);
        if (normalizedView.isSelected()) {
          Transformer.adjustFL(size);
        }
      }
    });

    this.add(layeredPane);
  }

  ImageSource asImageSource() {
    return imageSource;
  }
}
