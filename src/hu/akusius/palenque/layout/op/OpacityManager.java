package hu.akusius.palenque.layout.op;

import java.util.EnumMap;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Az egyes rétegek átlátszatlanságát nyilvántartó és kezelő osztály.
 * @author Bujdosó Ákos
 */
public class OpacityManager {

  /**
   * Az átlátszatlanság minimális értéke (teljesen átlátszó).
   */
  public static final int MIN_VALUE = 0;

  /**
   * Az átlátszatlanság maximális értéke (teljesen átlátszatlan).
   */
  public static final int MAX_VALUE = 100;

  private static Map<Layer, Integer> getDefaultValues() {
    assert MIN_VALUE == 0 && MAX_VALUE == 100;

    Map<Layer, Integer> values = new EnumMap<>(Layer.class);
    values.put(Layer.Lid, 35);
    values.put(Layer.Grid, 35);
    values.put(Layer.Reference, 100);
    values.put(Layer.Layout, 100);

    return values;
  }

  private final Persister persister;

  private final Map<Layer, PropSlider> sliders;

  private final Map<Layer, Integer> defaultValues;

  private Map<Layer, Integer> savepoint;

  public OpacityManager() {
    this(new PreferencePersister(), getDefaultValues());
  }

  /**
   * Létrehozás egyedi {@link Persister}-rel és kézi alapértékekkel.
   * Főleg tesztelési célokra.
   * @param persister Az egyedi {@link Persister}.
   * @param defaultValues Az alapértékek.
   */
  OpacityManager(Persister persister, Map<Layer, Integer> defaultValues) {
    if (persister == null) {
      throw new IllegalArgumentException();
    }
    this.persister = persister;

    EnumMap<Layer, Integer> def = new EnumMap<>(Layer.class);
    for (Layer layer : Layer.values()) {
      final int value;
      if (defaultValues != null && defaultValues.get(layer) != null) {
        value = defaultValues.get(layer);
      } else {
        value = MAX_VALUE;
      }
      def.put(layer, value);
    }
    this.defaultValues = def;

    Map<Layer, Integer> values = persister.load(this.defaultValues);

    EnumMap<Layer, PropSlider> sls = new EnumMap<>(Layer.class);
    for (Layer layer : Layer.values()) {
      final int value;
      if (values.get(layer) != null) {
        value = values.get(layer);
      } else {
        value = this.defaultValues.get(layer);
      }
      PropSlider slider = new PropSlider(MIN_VALUE, MAX_VALUE, value);
      sls.put(layer, slider);
    }
    this.sliders = sls;
  }

  /**
   * @return Az egyes rétegekhez tartozó {@link PropSlider}-ek.
   */
  public Map<Layer, PropSlider> getSliders() {
    return new EnumMap<>(sliders);
  }

  /**
   * @param layer A réteg.
   * @return A megadott réteghez tartozó {@link PropSlider}.
   */
  public PropSlider getSlider(Layer layer) {
    if (layer == null) {
      throw new IllegalArgumentException();
    }
    return sliders.get(layer);
  }

  /**
   * Az aktuális állapot elmentése (tranzakció indítása).
   * Ha már létezik korábbi, ami nem lett lezárva, akkor kivételt dob.
   * @throws IllegalStateException Már létezik korábbi (nem lezárt) állapot.
   */
  public void savepoint() throws IllegalStateException {
    if (savepoint != null) {
      throw new IllegalStateException();
    }
    savepoint = getSliderValues();
  }

  /**
   * A legutóbbi elmentett állapot visszaállítása.
   * Ha nincs ilyen, akkor kivételt dob.
   * @throws IllegalStateException Nincsen elmentett korábbi állapot.
   */
  public void rollback() throws IllegalStateException {
    if (savepoint == null) {
      throw new IllegalStateException();
    }
    setSliderValues(savepoint);
    savepoint = null;
  }

  /**
   * Az aktuális állapot véglegesítése és az elmentett állapot törlése.
   * @throws IllegalStateException Nincsen elmentett korábbi állapot.
   */
  public void commit() throws IllegalStateException {
    if (savepoint == null) {
      throw new IllegalStateException();
    }
    persister.save(getSliderValues());
    savepoint = null;
  }

  /**
   * Az alapértékek visszaállítása.
   */
  public void reset() {
    setSliderValues(defaultValues);
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
    for (PropSlider slider : sliders.values()) {
      slider.setEnabledInternal(this.enabled);
    }
  }

  private Map<Layer, Integer> getSliderValues() {
    Map<Layer, Integer> values = new EnumMap<>(Layer.class);
    for (Map.Entry<Layer, PropSlider> entrySet : sliders.entrySet()) {
      Layer layer = entrySet.getKey();
      PropSlider slider = entrySet.getValue();
      values.put(layer, slider.getValue());
    }
    return values;
  }

  private void setSliderValues(Map<Layer, Integer> values) {
    for (Map.Entry<Layer, Integer> entrySet : values.entrySet()) {
      Layer layer = entrySet.getKey();
      Integer value = entrySet.getValue();
      if (value == null) {
        throw new IllegalArgumentException();
      }
      sliders.get(layer).setValueInternal(value);
    }
  }

  private static class DummyPersister implements Persister {

    private Map<Layer, Integer> saved;

    @Override
    public Map<Layer, Integer> load(Map<Layer, Integer> defaultValues) {
      return saved != null ? new EnumMap<>(saved) : defaultValues;
    }

    @Override
    public void save(Map<Layer, Integer> values) {
      saved = new EnumMap<>(values);
    }
  }

  private static class PreferencePersister implements Persister {

    private final Preferences prefs = Preferences.userNodeForPackage(PreferencePersister.class).node("opacity");

    @Override
    public Map<Layer, Integer> load(Map<Layer, Integer> defaultValues) {
      Map<Layer, Integer> result = new EnumMap<>(Layer.class);
      for (Layer layer : Layer.values()) {
        result.put(layer, prefs.getInt(layer.name(), defaultValues.get(layer)));
      }
      return result;
    }

    @Override
    public void save(Map<Layer, Integer> values) {
      for (Layer layer : Layer.values()) {
        final Integer value = values.get(layer);
        if (value != null) {
          prefs.putInt(layer.name(), value);
        } else {
          prefs.remove(layer.name());
        }
      }

      try {
        prefs.flush();
      } catch (BackingStoreException ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  /**
   * Az egyes rétegek.
   */
  @SuppressWarnings("PublicInnerClass")
  public enum Layer {

    Lid,
    Grid,
    Reference,
    Layout
  }

  /**
   * Interfész az átlátszatlansági értékek elmentéséhez és visszatöltéséhez.
   */
  @SuppressWarnings("PackageVisibleInnerClass")
  interface Persister {

    /**
     * A korábban elmentett értékek visszatöltése.
     * Ha nem történt korábban mentés, akkor a megadott alapértékeket kell visszaadni.
     * @param defaultValues Az alapértékek.
     * @return A visszatöltött értékek.
     */
    Map<Layer, Integer> load(Map<Layer, Integer> defaultValues);

    /**
     * A megadott értékek elmentése.
     * @param values Az elmentendő értékek.
     */
    void save(Map<Layer, Integer> values);
  }
}
