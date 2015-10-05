package hu.akusius.palenque.layout.op;

import hu.akusius.palenque.layout.data.Layout;
import hu.akusius.palenque.layout.data.LayoutContainer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;

/**
 * Az elrendezéstörténetet kezelő osztály.
 * @author Bujdosó Ákos
 */
public class HistoryManager {

  /**
   * A visszavonási lépések maximális száma.
   */
  public final static int MAX_UNDO = 100;

  private final UndoAction undoAction;

  private final RedoAction redoAction;

  private final SwitchAction switchAction;

  private final LayoutContainer layoutContainer;

  private final Deque<String> undo = new ArrayDeque<>(MAX_UNDO);

  private final Stack<String> redo = new Stack<>();

  private boolean inChange = false;

  /**
   * Létrehozás.
   * @param layoutContainer Az elrendezést tartalmazó objektum.
   */
  public HistoryManager(LayoutContainer layoutContainer) {
    this.layoutContainer = layoutContainer;
    this.layoutContainer.addPropertyChangeListener(new PropertyChangeListener() {

      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        assert LayoutContainer.PROP_LAYOUT.equals(evt.getPropertyName());
        if (inChange) {
          return;
        }
        Layout oldLayout = (Layout) evt.getOldValue();
        while (undo.size() >= MAX_UNDO) {
          undo.removeLast();
        }
        undo.push(oldLayout.serialize());
        redo.clear();
        refreshActionEnableStates();
      }
    });

    this.undoAction = new UndoAction();
    this.redoAction = new RedoAction();
    this.switchAction = new SwitchAction();
    refreshActionEnableStates();
  }

  /**
   * @return Az Undo műveletet kezelő osztály.
   */
  public PropAction getUndoAction() {
    return undoAction;
  }

  /**
   * @return A Redo műveletet kezelő osztály.
   */
  public PropAction getRedoAction() {
    return redoAction;
  }

  /**
   * @return A váltóműveletet kezelő osztály.
   */
  public PropAction getSwitchAction() {
    return switchAction;
  }

  private boolean enabled = true;

  /**
   * @return A jelenlegi engedélyezettségi állapot.
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Az engedélyezettségi állapot beállítása.
   * @param enabled Az új engedélyezettségi állapot.
   */
  public void setEnabled(boolean enabled) {
    if (this.enabled == enabled) {
      return;
    }
    this.enabled = enabled;
    refreshActionEnableStates();
  }

  private void refreshActionEnableStates() {
    this.undoAction.enable(this.enabled && !undo.isEmpty());
    this.redoAction.enable(this.enabled && !redo.isEmpty());
    this.switchAction.enable(this.enabled && this.switchAction.canSwitch());
  }

  private class UndoAction extends PropAction {

    @Override
    protected void action(Object param) {
      assert !undo.isEmpty();
      Layout oldLayout = layoutContainer.getLayout();
      Layout newLayout = Layout.deserialize(undo.peek());
      try {
        inChange = true;
        HistoryManager.this.layoutContainer.setLayout(newLayout);
        undo.pop();
        redo.push(oldLayout.serialize());
      } finally {
        inChange = false;
      }
      refreshActionEnableStates();
    }

    public void enable(boolean enabled) {
      super.setEnabledInternal(enabled);
    }

  }

  private class RedoAction extends PropAction {

    @Override
    protected void action(Object param) {
      assert !redo.isEmpty();
      Layout oldLayout = layoutContainer.getLayout();
      Layout newLayout = Layout.deserialize(redo.peek());
      try {
        inChange = true;
        HistoryManager.this.layoutContainer.setLayout(newLayout);
        undo.push(oldLayout.serialize());  // Itt nem léphetjük túl a méretet
        redo.pop();
      } finally {
        inChange = false;
      }
      refreshActionEnableStates();
    }

    public void enable(boolean enabled) {
      super.setEnabledInternal(enabled);
    }
  }

  private class SwitchAction extends PropAction {

    @Override
    protected void action(Object param) {
      assert canSwitch();

      if (redoAction.isEnabled()) {
        assert redo.size() == 1;
        redoAction.performAction();
      } else if (undoAction.isEnabled()) {
        undoAction.performAction();
      }
    }

    boolean canSwitch() {
      if (redoAction.isEnabled()) {
        return redo.size() == 1;
      }
      return undoAction.isEnabled();
    }

    public void enable(boolean enabled) {
      super.setEnabledInternal(enabled);
    }
  }
}
