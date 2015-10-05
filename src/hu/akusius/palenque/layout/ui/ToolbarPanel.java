package hu.akusius.palenque.layout.ui;

import hu.akusius.palenque.layout.op.*;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Panel a vezérlőgombok és -elemek megjelenítéséhez.
 * @author Bujdosó Ákos
 */
public class ToolbarPanel extends JPanel {

  private final OperationManager om;

  public ToolbarPanel(OperationManager operationManager) {
    this.om = operationManager;
    this.initComponents();
  }

  private void initComponents() {
    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    this.setBorder(new BevelBorder(BevelBorder.LOWERED));

    this.initUpperPanel();
    this.initLowerPanel();
  }

  private void initUpperPanel() {
    JPanel p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
    p.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));

    p.add(Box.createHorizontalStrut(10));

    EditManager em = om.getEditManager();
    JToggleButton btnSelect = newToggleButton(em.getSelectToggle(), "select", "Select and move (1)");
    p.add(btnSelect);
    JToggleButton btnAdd = newToggleButton(em.getAddToggle(), "add", "Add new item (2)");
    p.add(btnAdd);
    JToggleButton btnRemove = newToggleButton(em.getRemoveToggle(), "remove", "Remove item (3)");
    p.add(btnRemove);
    ButtonGroup editModes = new ButtonGroup();
    editModes.add(btnSelect);
    editModes.add(btnAdd);
    editModes.add(btnRemove);

    p.add(Box.createHorizontalStrut(10));
    p.add(Box.createHorizontalGlue());

    DisplayManager dm = om.getDisplayManager();
    p.add(newToggleButton(dm.getShowLidToggle(), "lid", "Lid picture (5)"));
    p.add(newToggleButton(dm.getShowGridToggle(), "grid", "Grid (6, G)"));
    p.add(newToggleButton(dm.getShowReferenceToggle(), "ref", "Reference (7)"));
    p.add(newToggleButton(dm.getShowNumbersToggle(), "number", "Item numbers (8)"));

    p.add(Box.createHorizontalStrut(10));
    p.add(Box.createHorizontalGlue());

    LayoutManager lm = om.getLayoutManager();
    p.add(newActionButton(lm.getRandomizeAction(), "random", "Randomize (shuffle) layout (R)"));
    p.add(newActionButton(lm.getResetAction(), "reset", "Default layout (D)"));

    p.add(Box.createHorizontalStrut(10));
    p.add(Box.createHorizontalGlue());

    HistoryManager hm = om.getHistoryManager();
    p.add(newActionButton(hm.getUndoAction(), "undo", "Undo (Z, BACKSPACE)"));
    p.add(newActionButton(hm.getRedoAction(), "redo", "Redo (Y, BACKSPACE)"));

    p.add(Box.createHorizontalStrut(10));
    p.add(Box.createHorizontalGlue());

    p.add(newToggleButton(om.getNormalizedViewToggle(), "normalized", "Normalized rendering (N)"));

    MemoryManager mm = om.getMemoryManager();
    p.add(newToggleButton(mm.getRegToggle(0), "memory", "Layout memory (M)"));

    p.add(Box.createHorizontalStrut(10));
    p.add(Box.createHorizontalGlue());

    p.add(newActionButton(lm.getExportAction(), "export", "Export (E)"));
    p.add(newActionButton(lm.getImportAction(), "import", "Import (I)"));

    p.add(Box.createHorizontalStrut(10));

    this.add(p);
  }

  private void initLowerPanel() {
    JPanel p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
    p.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));

    PlayManager pm = om.getPlayManager();
    p.add(Box.createHorizontalStrut(10));
    p.add(newToggleButton(pm.getPlayingToggle(), "play", "Play / stop (SPACE)"));
    p.add(Box.createHorizontalStrut(10));

    p.add(newActionButton(pm.getToStartAction(), "start", "To start (HOME, C)"));
    p.add(Box.createHorizontalStrut(3));
    p.add(newSlider(pm.getFrameSlider()));
    p.add(Box.createHorizontalStrut(3));
    p.add(newActionButton(pm.getToEndAction(), "end", "To end (END, V)"));

    p.add(Box.createHorizontalStrut(10));

    p.add(newToggleButton(pm.getQuickAnimationToggle(), "quick", "Quick playing (Q)"));
    p.add(Box.createHorizontalStrut(3));

    JSlider slSpeed = newSlider(pm.getSpeedSlider());
    slSpeed.setToolTipText("Speed");
    slSpeed.setPreferredSize(new Dimension(60, 30));
    slSpeed.setMaximumSize(new Dimension(60, 30));
    p.add(slSpeed);

    p.add(Box.createHorizontalStrut(10));

    p.add(newActionButton(om.getModifyOpacityAction(), "opacity", "Layer opacities (O)"));
    p.add(newActionButton(om.getShowDataAction(), "data", "Item data (X)"));

    p.add(newActionButton(om.getScreenshotAction(), "scshot", "Screenshot (S)"));
    p.add(newActionButton(om.getInfoAction(), "info", "Information (Alt-I)"));

    p.add(Box.createHorizontalStrut(10));

    this.add(p);
  }

  private static JButton newActionButton(final PropAction prop, String iconName, String tooltipText) {
    final JButton btn = new JButton("");
    setAbstractButton(btn, iconName, tooltipText);
    btn.setEnabled(prop.isEnabled());
    btn.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        prop.performAction();
      }
    });
    prop.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
          case PropAction.PROP_ENABLED:
            btn.setEnabled(prop.isEnabled());
            break;
        }
      }
    });
    return btn;
  }

  private static JToggleButton newToggleButton(final PropToggle prop, String iconName, String tooltipText) {
    final JToggleButton btn = new JToggleButton();
    setAbstractButton(btn, iconName, tooltipText);
    btn.setSelected(prop.isSelected());
    btn.setEnabled(prop.isEnabled());
    btn.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        if (prop.isEnabled()) {
          if (prop.isInGroup()) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
              prop.setSelected(true);
            }
          } else {
            prop.setSelected(e.getStateChange() == ItemEvent.SELECTED);
          }
        }
      }
    });
    prop.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
          case PropToggle.PROP_ENABLED:
            btn.setEnabled(prop.isEnabled());
            break;
          case PropToggle.PROP_SELECTED:
            btn.setSelected(prop.isSelected());
            break;
        }
      }
    });
    return btn;
  }

  private static void setAbstractButton(AbstractButton button, String iconName, String tooltipText) {
    button.setMinimumSize(new Dimension(30, 30));
    button.setPreferredSize(new Dimension(30, 30));
    button.setMaximumSize(new Dimension(30, 30));
    button.setFocusable(false);

    if (iconName != null) {
      ImageIcon icon = IconFactory.readIcon(iconName + ".png");
      if (icon != null) {
        button.setIcon(icon);
      }
    }

    if (tooltipText != null) {
      button.setToolTipText(tooltipText);
    }
  }

  private static JSlider newSlider(final PropSlider prop) {
    final JSlider sl = new JSlider(prop.getMin(), prop.getMax(), prop.getValue());
    sl.setFocusable(false);
    sl.setEnabled(prop.isEnabled());
    sl.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        if (prop.isEnabled()) {
          prop.setValue(sl.getValue());
        }
      }
    });
    prop.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
          case PropSlider.PROP_ENABLED:
            sl.setEnabled(prop.isEnabled());
            break;
          case PropSlider.PROP_VALUE:
            sl.setValue(prop.getValue());
            break;
        }
      }
    });
    return sl;
  }
}
