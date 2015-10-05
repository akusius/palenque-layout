package hu.akusius.palenque.layout.ui;

import hu.akusius.palenque.layout.op.*;
import hu.akusius.palenque.layout.util.Event1;
import hu.akusius.palenque.layout.util.EventListener;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;

/**
 * A fő panel.
 * @author Bujdosó Ákos
 */
public class MainPanel extends JPanel {

  private final OperationManager operationManager;

  private DisplayPanel displayPanel;

  private ToolbarPanel toolbarPanel;

  private final DialogParent dialogParent;

  public MainPanel(OperationManager operationManager, DialogParent dp) {
    this.operationManager = operationManager;
    this.dialogParent = dp;
    this.operationManager.getLayoutManager().setImpexer(new DialogLayoutImpexer(this.dialogParent));
    initComponents();
    initHotkeys();
    hookEvents();

    ToolTipManager.sharedInstance().setInitialDelay(200);
    ToolTipManager.sharedInstance().setReshowDelay(2000);
  }

  private void initComponents() {
    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    displayPanel = new DisplayPanel(this.operationManager);
    displayPanel.setPreferredSize(new Dimension(600, 600));
    add(displayPanel);

    toolbarPanel = new ToolbarPanel(this.operationManager);
    toolbarPanel.setPreferredSize(new Dimension(600, 80));
    toolbarPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 80));
    toolbarPanel.setMinimumSize(new Dimension(Short.MIN_VALUE, 80));
    add(toolbarPanel);
  }

  private void initHotkeys() {
    EditManager em = operationManager.getEditManager();
    addPropHotkey(em.getSelectToggle(), "1");
    addPropHotkey(em.getAddToggle(), "2");
    addPropHotkey(em.getRemoveToggle(), "3");

    DisplayManager dm = operationManager.getDisplayManager();
    addPropHotkey(dm.getShowLidToggle(), "5");
    addPropHotkey(dm.getShowGridToggle(), "6", "G");
    addPropHotkey(dm.getShowReferenceToggle(), "7");
    addPropHotkey(dm.getShowNumbersToggle(), "8");

    LayoutManager lm = operationManager.getLayoutManager();
    addPropHotkey(lm.getRandomizeAction(), "R");
    addPropHotkey(lm.getResetAction(), "D");
    addPropHotkey(lm.getExportAction(), "E");
    addPropHotkey(lm.getImportAction(), "I");

    HistoryManager hm = operationManager.getHistoryManager();
    addPropHotkey(hm.getUndoAction(), "Z");
    addPropHotkey(hm.getRedoAction(), "Y");
    addPropHotkey(hm.getSwitchAction(), "BACK_SPACE");

    addPropHotkey(operationManager.getNormalizedViewToggle(), "N");

    MemoryManager mm = operationManager.getMemoryManager();
    addPropHotkey(mm.getRegToggle(0), "M");
    addPropHotkey(mm.getClearAction(), "shift M");

    PlayManager pm = operationManager.getPlayManager();
    addPropHotkey(pm.getPlayingToggle(), "SPACE");
    addPropHotkey(pm.getToStartAction(), "C", "HOME");
    addPropHotkey(pm.getToEndAction(), "V", "END");
    addPropHotkey(pm.getQuickAnimationToggle(), "Q");

    addPropHotkey(operationManager.getModifyOpacityAction(), "O");
    addPropHotkey(operationManager.getShowDataAction(), "X");
    addPropHotkey(operationManager.getScreenshotAction(), "S");
    addPropHotkey(operationManager.getInfoAction(), "alt I");
  }

  private void addPropHotkey(Prop prop, String... keys) {
    addPropHotkey(prop, null, getKeyStrokes(keys));
  }

  private void addPropHotkey(Prop prop, Object param, String... keys) {
    addPropHotkey(prop, param, getKeyStrokes(keys));
  }

  private void addPropHotkey(final Prop prop, final Object param, KeyStroke[] keys) {
    final Action action;
    if (prop instanceof PropAction) {
      action = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (prop.isEnabled()) {
            ((PropAction) prop).performAction(param);
          }
        }
      };
    } else if (prop instanceof PropToggle) {
      action = new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
          PropToggle toggle = (PropToggle) prop;
          if (toggle.isEnabled()) {
            if (toggle.isInGroup()) {
              toggle.setSelected(true);
            } else {
              toggle.setSelected(!toggle.isSelected());
            }
          }
        }
      };
    } else {
      throw new IllegalArgumentException();
    }
    addActionHotkey(action, keys);
  }

  private final Set<KeyStroke> usedHotkeys = new HashSet<>(50);

  private void addActionHotkey(Action action, KeyStroke[] keys) {
    if (action == null || keys == null || keys.length <= 0) {
      throw new IllegalArgumentException();
    }

    Object actionObj = new Object();
    for (KeyStroke key : keys) {
      if (usedHotkeys.contains(key)) {
        throw new IllegalArgumentException("Key is already attached: " + key);
      }
      usedHotkeys.add(key);
      getInputMap(WHEN_IN_FOCUSED_WINDOW).put(key, actionObj);
    }
    getActionMap().put(actionObj, action);
  }

  private KeyStroke[] getKeyStrokes(String... keys) {
    KeyStroke[] keyStrokes = new KeyStroke[keys.length];
    for (int i = 0; i < keys.length; i++) {
      keyStrokes[i] = KeyStroke.getKeyStroke(keys[i]);
    }
    return keyStrokes;
  }

  private void hookEvents() {
    operationManager.getModifyOpacityAction().addActionPerformedListener(new EventListener<Event1<Object>>() {
      @Override
      public void notify(Event1<Object> e) {
        OpacityDialog.show(operationManager.getOpacityManager(), dialogParent);
      }
    });
    operationManager.getShowDataAction().addActionPerformedListener(new EventListener<Event1<Object>>() {
      @Override
      public void notify(Event1<Object> e) {
        DataDialog.show(operationManager.getLayoutContainer().getLayout(), dialogParent);
      }
    });
    operationManager.getScreenshotAction().addActionPerformedListener(new EventListener<Event1<Object>>() {
      @Override
      public void notify(Event1<Object> e) {
        ImageSaver.saveImage(displayPanel.asImageSource(), dialogParent);
      }
    });
    operationManager.getInfoAction().addActionPerformedListener(new EventListener<Event1<Object>>() {
      @Override
      public void notify(Event1<Object> e) {
        new InfoDialog(dialogParent.getWindow()).setVisible(true);
      }
    });
  }
}
