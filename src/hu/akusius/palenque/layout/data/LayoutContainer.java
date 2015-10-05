package hu.akusius.palenque.layout.data;

import java.beans.*;

/**
 * Egy elrendezést tároló osztály.
 * @author Bujdosó Ákos
 */
public class LayoutContainer {

  private Layout layout;

  /**
   * Létrehozás.
   * @param layout A tartalmazott elrendezés. Nem lehet {@code null}.
   */
  public LayoutContainer(Layout layout) {
    if (layout == null) {
      throw new IllegalArgumentException();
    }
    this.layout = layout;
  }

  public static final String PROP_LAYOUT = "layout";

  /**
   * @return A tartalmazott elrendezés.
   */
  public Layout getLayout() {
    return layout;
  }

  /**
   * Új elrendezés megadása.
   * @param layout Az új elrendezés. Nem lehet {@code null}. Ha tartalmában megegyezik a jelenlegivel, akkor nem történik változtatás.
   */
  public void setLayout(Layout layout) {
    if (layout == null) {
      throw new IllegalArgumentException();
    }
    if (this.layout.equals(layout)) {
      // Nincs szükség változtatásra
      return;
    }

    Layout oldLayout = this.layout;
    this.layout = layout;
    propertyChangeSupport.firePropertyChange(PROP_LAYOUT, oldLayout, layout);
  }

  private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.removePropertyChangeListener(listener);
  }

}
