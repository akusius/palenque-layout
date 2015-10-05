package hu.akusius.palenque.layout.op;

import hu.akusius.palenque.layout.op.OpacityManager.Layer;
import hu.akusius.palenque.layout.op.OpacityManager.Persister;
import java.util.EnumMap;
import java.util.Map;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Bujdosó Ákos
 */
public class OpacityManagerTest {

  private static Map<Layer, Integer> asMap(Integer... values) {
    Map<Layer, Integer> result = new EnumMap<>(Layer.class);
    Layer[] layers = Layer.values();
    for (int i = 0; i < layers.length; i++) {
      if (i < values.length && values[i] != null) {
        result.put(layers[i], values[i]);
      }
    }
    return result;
  }

  private static class TestPersister implements Persister {

    boolean loaded = false;

    @Override
    public Map<Layer, Integer> load(Map<Layer, Integer> defaultValues) {
      loaded = true;
      return defaultValues;
    }

    boolean saved = false;

    @Override
    public void save(Map<Layer, Integer> values) {
      saved = true;
    }
  }

  public OpacityManagerTest() {
  }

  @Test
  public void test1() {
    Map<Layer, Integer> values = asMap(2, 50, 100, 33);
    OpacityManager om = new OpacityManager(new TestPersister(), values);

    Layer[] layers = Layer.values();

    Map<Layer, PropSlider> sliders = om.getSliders();
    assertThat(sliders.size(), equalTo(layers.length));
    for (Layer layer : layers) {
      PropSlider slider = sliders.get(layer);
      assertThat(slider, notNullValue());
      assertThat(slider, sameInstance(om.getSlider(layer)));
      assertTrue(slider.isEnabled());
      assertThat(slider.getMin(), equalTo(OpacityManager.MIN_VALUE));
      assertThat(slider.getMax(), equalTo(OpacityManager.MAX_VALUE));
      assertThat(slider.getValue(), equalTo(values.get(layer)));
    }
  }

  @Test
  public void test2() {
    OpacityManager om = new OpacityManager(new TestPersister(), asMap(null, OpacityManager.MAX_VALUE, null));
    for (Layer layer : Layer.values()) {
      PropSlider slider = om.getSlider(layer);
      assertThat(slider.getValue(), equalTo(OpacityManager.MAX_VALUE));
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void test3() {
    OpacityManager om = new OpacityManager(new TestPersister(), asMap(OpacityManager.MAX_VALUE + 1));
  }

  @Test
  public void test4() {
    final Map<Layer, Integer> values = asMap(11, 22, 33, 44);
    TestPersister persister = new TestPersister() {

      @Override
      public Map<Layer, Integer> load(Map<Layer, Integer> defaultValues) {
        super.load(defaultValues);
        assertThat(defaultValues, notNullValue());
        Layer[] layers = Layer.values();
        assertThat(defaultValues.size(), equalTo(layers.length));
        for (Layer layer : layers) {
          assertThat(defaultValues.get(layer), equalTo(OpacityManager.MAX_VALUE));
        }
        return values;
      }

    };
    OpacityManager om = new OpacityManager(persister, null);
    assertTrue(persister.loaded);
    for (Layer layer : Layer.values()) {
      assertThat(om.getSlider(layer).getValue(), equalTo(values.get(layer)));
    }
    assertFalse(persister.saved);
  }

  @Test(expected = IllegalStateException.class)
  public void test5() {
    OpacityManager om = new OpacityManager(new TestPersister(), null);
    om.savepoint();
    om.savepoint();
  }

  @Test
  public void test6() {
    TestPersister persister = new TestPersister();
    OpacityManager om = new OpacityManager(persister, null);
    assertTrue(persister.loaded);
    assertFalse(persister.saved);

    Map<Layer, PropSlider> sliders = om.getSliders();
    for (PropSlider slider : sliders.values()) {
      assertThat(slider.getValue(), equalTo(OpacityManager.MAX_VALUE));
    }

    om.savepoint();
    for (PropSlider slider : sliders.values()) {
      slider.setValue(OpacityManager.MIN_VALUE);
      assertThat(slider.getValue(), not(equalTo(OpacityManager.MAX_VALUE)));
    }

    om.rollback();
    for (PropSlider slider : sliders.values()) {
      assertThat(slider.getValue(), equalTo(OpacityManager.MAX_VALUE));
    }

    assertFalse(persister.saved);

    try {
      om.rollback();
      fail();
    } catch (IllegalStateException e) {
    }
  }

  @Test
  public void test7() {
    TestPersister persister = new TestPersister() {

      @Override
      public void save(Map<Layer, Integer> values) {
        super.save(values);
        for (Integer value : values.values()) {
          assertThat(value, equalTo(OpacityManager.MIN_VALUE));
        }
      }
    };
    OpacityManager om = new OpacityManager(persister, null);
    assertTrue(persister.loaded);
    assertFalse(persister.saved);

    Map<Layer, PropSlider> sliders = om.getSliders();
    for (PropSlider slider : sliders.values()) {
      assertThat(slider.getValue(), equalTo(OpacityManager.MAX_VALUE));
    }

    om.savepoint();
    for (PropSlider slider : sliders.values()) {
      slider.setValue(OpacityManager.MIN_VALUE);
    }

    om.commit();
    for (PropSlider slider : sliders.values()) {
      assertThat(slider.getValue(), equalTo(OpacityManager.MIN_VALUE));
    }

    assertTrue(persister.saved);

    try {
      om.commit();
      fail();
    } catch (IllegalStateException e) {
    }
  }

  @Test
  public void test8() {
    TestPersister persister = new TestPersister();
    Map<Layer, Integer> values = asMap(11, 22, 33, 44);
    OpacityManager om = new OpacityManager(persister, values);
    assertTrue(persister.loaded);
    assertFalse(persister.saved);

    Map<Layer, PropSlider> sliders = om.getSliders();
    for (Map.Entry<Layer, PropSlider> entrySet : sliders.entrySet()) {
      assertThat(entrySet.getValue().getValue(), equalTo(values.get(entrySet.getKey())));
    }

    for (PropSlider slider : sliders.values()) {
      slider.setValue(OpacityManager.MIN_VALUE);
    }

    om.savepoint();

    om.reset();

    for (Map.Entry<Layer, PropSlider> entrySet : sliders.entrySet()) {
      assertThat(entrySet.getValue().getValue(), equalTo(values.get(entrySet.getKey())));
    }

    om.rollback();

    for (PropSlider slider : sliders.values()) {
      assertThat(slider.getValue(), equalTo(OpacityManager.MIN_VALUE));
    }

    om.reset();

    for (Map.Entry<Layer, PropSlider> entrySet : sliders.entrySet()) {
      assertThat(entrySet.getValue().getValue(), equalTo(values.get(entrySet.getKey())));
    }

    assertFalse(persister.saved);
  }

  @Test
  public void test9() {
    OpacityManager om = new OpacityManager(new TestPersister(), null);

    assertTrue(om.isEnabled());

    Map<Layer, PropSlider> sliders = om.getSliders();

    for (PropSlider slider : sliders.values()) {
      assertTrue(slider.isEnabled());
    }

    om.setEnabled(false);
    assertFalse(om.isEnabled());
    for (PropSlider slider : sliders.values()) {
      assertFalse(slider.isEnabled());
    }

    om.setEnabled(true);
    assertTrue(om.isEnabled());
    for (PropSlider slider : sliders.values()) {
      assertTrue(slider.isEnabled());
    }
  }
}
