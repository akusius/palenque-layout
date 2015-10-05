package hu.akusius.palenque.layout.ui;

import hu.akusius.palenque.layout.op.OpacityManager;
import hu.akusius.palenque.layout.op.OpacityManager.Layer;
import hu.akusius.palenque.layout.op.PropSlider;
import hu.akusius.palenque.layout.util.UIUtils;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Dialógus a rétegek átlátszatlanságának beállításához.
 * @author Bujdosó Ákos
 */
public class OpacityDialog extends JDialog {

  private static OpacityDialog dialog;

  private final OpacityManager opacityManager;

  private final Map<PropSlider, PropertyChangeListener> listenersToUnhook = new HashMap<>(10);

  private boolean saved = false;

  public static void show(OpacityManager opm, DialogParent dialogParent) {
    opm.savepoint();

    if (dialog == null) {
      dialog = new OpacityDialog(opm, dialogParent.getWindow());
    }

    dialog.saved = false;
    dialog.setVisible(true);
    if (dialog.saved) {
      opm.commit();
    } else {
      opm.rollback();
    }
  }

  private OpacityDialog(OpacityManager opm, Window owner) {
    super(owner, ModalityType.APPLICATION_MODAL);
    this.opacityManager = opm;

    initComponents();

    this.pack();
    this.setResizable(false);
    this.setLocationRelativeTo(owner);
  }

  private void initComponents() {
    setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

    this.setTitle("Layer opacities");
    setLayout(new BorderLayout(5, 10));

    this.add(createControlPanel(), BorderLayout.CENTER);
    this.add(createButtonPanel(), BorderLayout.SOUTH);

    UIUtils.installDialogEscapeCloseOperation(this);
  }

  private JPanel createControlPanel() {
    JPanel panel = new JPanel();

    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    Map<Layer, String> icons = new EnumMap<>(Layer.class);
    icons.put(Layer.Lid, "lid.png");
    icons.put(Layer.Grid, "grid.png");
    icons.put(Layer.Reference, "ref.png");
    icons.put(Layer.Layout, "layout.png");

    for (Map.Entry<Layer, PropSlider> entrySet : opacityManager.getSliders().entrySet()) {
      Layer layer = entrySet.getKey();
      final PropSlider ps = entrySet.getValue();

      JLabel image = new JLabel(IconFactory.readIcon(icons.get(layer)));

      final JSlider slider = new JSlider(ps.getMin(), ps.getMax(), ps.getValue());
      slider.setEnabled(ps.isEnabled());
      slider.addChangeListener(new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
          if (ps.isEnabled()) {
            ps.setValue(slider.getValue());
          }
        }
      });

      final JSpinner spinner = new JSpinner(new SpinnerNumberModel(ps.getValue(), ps.getMin(), ps.getMax(), 1));
      spinner.setEnabled(ps.isEnabled());
      spinner.addChangeListener(new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
          if (ps.isEnabled()) {
            ps.setValue((int) spinner.getValue());
          }
        }
      });

      PropertyChangeListener pcl = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
          switch (evt.getPropertyName()) {
            case PropSlider.PROP_ENABLED:
              slider.setEnabled(ps.isEnabled());
              spinner.setEnabled(ps.isEnabled());
              break;
            case PropSlider.PROP_VALUE:
              slider.setValue(ps.getValue());
              spinner.setValue(ps.getValue());
              break;
          }
        }
      };
      ps.addPropertyChangeListener(pcl);
      listenersToUnhook.put(ps, pcl);

      JPanel layerPanel = new JPanel();
      layerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 3, 3));

      layerPanel.add(image);
      layerPanel.add(slider);
      layerPanel.add(Box.createHorizontalStrut(5));
      layerPanel.add(spinner);

      panel.add(layerPanel);
    }

    return panel;
  }

  private JPanel createButtonPanel() {
    JPanel panel = new JPanel();

    JButton btnReset = new JButton("Reset");
    FontUtility.scaleFont(btnReset);
    btnReset.setMnemonic(KeyEvent.VK_R);
    btnReset.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        opacityManager.reset();
      }
    });
    panel.add(btnReset);

    panel.add(Box.createHorizontalStrut(25));

    JButton btnCancel = new JButton("Cancel");
    FontUtility.scaleFont(btnCancel);
    btnCancel.setMnemonic(KeyEvent.VK_C);
    btnCancel.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
      }
    });
    panel.add(btnCancel);

    JButton btnOK = new JButton("OK");
    FontUtility.scaleFont(btnOK);
    btnOK.setMnemonic(KeyEvent.VK_O);
    btnOK.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        saved = true;
        setVisible(false);
      }
    });
    panel.add(btnOK);
    getRootPane().setDefaultButton(btnOK);

    return panel;
  }

  private void unhookListeners() {
    for (Map.Entry<PropSlider, PropertyChangeListener> entrySet : listenersToUnhook.entrySet()) {
      PropSlider ps = entrySet.getKey();
      PropertyChangeListener listener = entrySet.getValue();
      ps.removePropertyChangeListener(listener);
    }
    listenersToUnhook.clear();
  }
}
