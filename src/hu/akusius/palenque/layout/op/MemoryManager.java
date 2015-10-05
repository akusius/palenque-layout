package hu.akusius.palenque.layout.op;

import hu.akusius.palenque.layout.data.Layout;
import hu.akusius.palenque.layout.data.LayoutContainer;
import hu.akusius.palenque.layout.util.Event1;
import hu.akusius.palenque.layout.util.EventListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A memóriaregisztereket kezelő osztály.
 * @author Bujdosó Ákos
 */
public class MemoryManager {

  /**
   * A regiszterek maximális száma.
   */
  public final static int MAX_REG_NUM = 5;

  private final LayoutContainer lc;

  private final int regNum;

  private final List<Register> registers;

  private final PropAction clearAction;

  /**
   * @param layoutContainer Az elrendezést tartalmazó osztály.
   * @param regNum A regiszterek száma. Legfeljebb {@link #MAX_REG_NUM} lehet.
   */
  public MemoryManager(LayoutContainer layoutContainer, int regNum) {
    if (layoutContainer == null || regNum < 1 || regNum > MAX_REG_NUM) {
      throw new IllegalArgumentException();
    }
    this.regNum = regNum;

    List<Register> regs = new ArrayList<>(regNum);
    for (int i = 0; i < regNum; i++) {
      Register reg = new Register();
      regs.add(reg);
    }
    this.registers = Collections.unmodifiableList(regs);

    this.lc = layoutContainer;
    this.lc.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (LayoutContainer.PROP_LAYOUT.equals(evt.getPropertyName())) {
          String layout = ((Layout) evt.getNewValue()).serialize();
          for (Register reg : registers) {
            reg.refresh(layout);
          }
        }
      }
    });

    clearAction = new PropAction();
    clearAction.setEnabled(false);
    clearAction.addActionPerformedListener(new EventListener<Event1<Object>>() {
      @Override
      public void notify(Event1<Object> e) {
        assert MemoryManager.this.enabled;

        for (Register reg : registers) {
          reg.clear();
        }
        refreshClear();
      }
    });
  }

  /**
   * @return A kezelt regiszterek száma.
   */
  public int getRegNum() {
    return regNum;
  }

  /**
   * @return A regiszterekhez tartozó nyomógombok listája.
   */
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  public List<? extends PropToggle> getRegToggles() {
    return registers;
  }

  /**
   * @param reg A regiszter száma.
   * @return Az adott számú regiszterhez tartozó nyomógomb.
   */
  public PropToggle getRegToggle(int reg) {
    return registers.get(reg);
  }

  /**
   * @return Az összes regiszter törlését végző művelet.
   */
  public PropAction getClearAction() {
    return clearAction;
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

    if (this.enabled) {
      String layout = lc.getLayout().serialize();
      for (Register register : registers) {
        register.refresh(layout);
      }
      refreshClear();
    } else {
      for (Register register : registers) {
        register.setEnabled(false);
      }
      clearAction.setEnabled(false);
    }
  }

  private void refreshClear() {
    for (Register register : registers) {
      if (!register.isEmpty()) {
        clearAction.setEnabled(true);
        return;
      }
    }
    clearAction.setEnabled(false);
  }

  private class Register extends PropToggle {

    private String layout;

    Register() {
      super(false);
    }

    boolean isEmpty() {
      return layout == null;
    }

    void refresh(String curLayout) {
      setEnabled(MemoryManager.this.enabled
              && (layout == null || !layout.equals(curLayout)));
    }

    void clear() {
      layout = null;
      setSelectedInternal(false);
      refresh(null);
    }

    @Override
    void setSelectedInternal(boolean selected) {
      assert MemoryManager.this.enabled;

      boolean oldSelected = this.isSelected();
      super.setSelectedInternal(selected);
      if (selected != oldSelected) {
        if (selected) {
          layout = lc.getLayout().serialize();
          setEnabled(false);
          refreshClear();
        } else {
          if (layout != null) {
            Layout newLayout = Layout.deserialize(layout);
            layout = null;
            lc.setLayout(newLayout);
            refreshClear();
          }
        }
      }
    }
  }
}
