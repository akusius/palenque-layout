package hu.akusius.palenque.layout.op;

import hu.akusius.palenque.layout.data.Layout;
import hu.akusius.palenque.layout.data.LayoutContainer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Az elrendezéssel kapcsolatos műveleteket végző osztály.
 * Az exportáláshoz és importáláshoz a {@link #getImpexer}-t használja, ami alapesetben nem végez semmilyen műveletet.
 * @author Bujdosó Ákos
 */
public class LayoutManager {

  private final LayoutContainer layoutContainer;

  private final PropAction randomizeAction;

  private final PropAction resetAction;

  private final PropAction exportAction;

  private final PropAction importAction;

  private final Layout defaultLayout = Layout.createDefault();

  public LayoutManager(LayoutContainer layoutContainer) {
    this.layoutContainer = layoutContainer;
    this.layoutContainer.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        assert LayoutContainer.PROP_LAYOUT.equals(evt.getPropertyName());
        refreshActionEnableStates();
      }
    });
    this.randomizeAction = new PropAction() {
      @Override
      protected void action(Object param) {
        LayoutContainer lc = LayoutManager.this.layoutContainer;
        lc.setLayout(lc.getLayout().randomize());
      }
    };
    this.resetAction = new PropAction() {
      @Override
      protected void action(Object param) {
        LayoutContainer lc = LayoutManager.this.layoutContainer;
        lc.setLayout(defaultLayout);
      }
    };
    this.exportAction = new PropAction() {
      @Override
      protected void action(Object param) {
        impexer.exportLayout(LayoutManager.this.layoutContainer.getLayout());
      }
    };
    this.importAction = new PropAction() {
      @Override
      protected void action(Object param) {
        Layout layout = impexer.importLayout();
        if (layout != null) {
          LayoutManager.this.layoutContainer.setLayout(layout);
        }
      }
    };
    refreshActionEnableStates();
  }

  /**
   * @return Az elrendezést véletlenszerűen megváltozató művelet.
   */
  public PropAction getRandomizeAction() {
    return randomizeAction;
  }

  /**
   * @return Az alapelrendezést visszaállító művelet.
   */
  public PropAction getResetAction() {
    return resetAction;
  }

  /**
   * @return Az exportálást végző művelet.
   */
  public PropAction getExportAction() {
    return exportAction;
  }

  /**
   * @return Az importálást végző művelet.
   */
  public PropAction getImportAction() {
    return importAction;
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
    this.randomizeAction.setEnabledInternal(this.enabled);
    this.resetAction.setEnabledInternal(this.enabled && !this.layoutContainer.getLayout().equals(defaultLayout));
    this.exportAction.setEnabledInternal(this.enabled);
    this.importAction.setEnabledInternal(this.enabled);
  }

  private LayoutImpexer impexer = new LayoutImpexer() {

    @Override
    public Layout importLayout() {
      return null;
    }

    @Override
    public boolean exportLayout(hu.akusius.palenque.layout.data.Layout layoutToExport) {
      return false;
    }
  };

  /**
   * Az importáláshoz és exportáláshoz használt {@link LayoutImpexer}. Alapesetben nem végez semmilyen műveletet.
   * @return Az aktuális importáló/exportáló.
   */
  public LayoutImpexer getImpexer() {
    return impexer;
  }

  /**
   * Új importáló/exportáló beállítása.
   * @param impexer Az új importáló/exportáló.
   */
  public void setImpexer(LayoutImpexer impexer) {
    if (impexer == null) {
      throw new IllegalArgumentException();
    }
    this.impexer = impexer;
  }

}
