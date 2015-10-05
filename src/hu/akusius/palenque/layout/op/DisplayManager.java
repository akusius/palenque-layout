package hu.akusius.palenque.layout.op;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * A megjelenítéssel kapcsolatos tulajdonságokat kezelő osztály.
 * @author Bujdosó Ákos
 */
public class DisplayManager {

  private final PropSliderFrame frameSlider;

  private final PropToggle showLidToggle;

  private final PropToggle showGridToggle;

  private final PropToggle showReferenceToggle;

  private final PropToggle showNumbersToggle;

  public DisplayManager(PropSliderFrame frameSlider) {
    this.frameSlider = frameSlider;
    this.frameSlider.addPropertyChangeListener(new PropertyChangeListener() {

      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (PropSlider.PROP_VALUE.equals(evt.getPropertyName())) {
          refreshStates();
        }
      }
    });
    this.showLidToggle = new PropToggle(false);
    this.showGridToggle = new PropToggle(false);
    this.showReferenceToggle = new PropToggle(false);
    this.showNumbersToggle = new PropToggle(false);
    this.refreshStates();
  }

  /**
   * @return A fedélrajz megjelenítését vezérlő tulajdonság.
   */
  public PropToggle getShowLidToggle() {
    return showLidToggle;
  }

  /**
   * @return A négyzetrács megjelenítését vezérlő tulajdonság.
   */
  public PropToggle getShowGridToggle() {
    return showGridToggle;
  }

  /**
   * @return A referencia megjelenítését vezérlő tulajdonság.
   */
  public PropToggle getShowReferenceToggle() {
    return showReferenceToggle;
  }

  /**
   * @return A számok megjelenítését vezérlő tulajdonság.
   */
  public PropToggle getShowNumbersToggle() {
    return showNumbersToggle;
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
    refreshStates();
  }

  private void refreshStates() {
    this.showLidToggle.setEnabledInternal(this.enabled);
    this.showGridToggle.setEnabledInternal(this.enabled);
    this.showNumbersToggle.setEnabledInternal(this.enabled && frameSlider.isFirstFrame());
    this.showReferenceToggle.setEnabledInternal(this.enabled);
  }

}
