package hu.akusius.palenque.layout.data;

import org.junit.Test;
import util.EventTester;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.*;

/**
 *
 * @author Bujdosó Ákos
 */
public class LayoutContainerTest {

  public LayoutContainerTest() {
  }

  @Test(expected = IllegalArgumentException.class)
  public void test1() {
    LayoutContainer lc = new LayoutContainer(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void test2() {
    LayoutContainer lc = new LayoutContainer(Layout.createDefault());
    lc.setLayout(null);
  }

  @Test
  public void test3() {
    Layout l = Layout.createDefault();
    LayoutContainer lc = new LayoutContainer(l);

    assertThat(lc.getLayout(), sameInstance(l));
    assertThat(lc.getLayout(), equalTo(l));

    EventTester et = new EventTester();
    lc.addPropertyChangeListener(et.propertyChangeListener);

    lc.setLayout(Layout.createDefault());
    assertFalse(et.hadAnyEvent());

    lc.setLayout(new Layout());
    assertTrue(et.hadPropertyChange(LayoutContainer.PROP_LAYOUT));
    assertFalse(et.hadOtherPropertyChange(LayoutContainer.PROP_LAYOUT));
  }

  @Test
  public void test4() {
    Layout l = Layout.createDefault();
    LayoutContainer lc = new LayoutContainer(l);

    EventTester et = new EventTester();
    lc.addPropertyChangeListener(et.propertyChangeListener);

    lc.setLayout(new Layout());
    assertTrue(et.hadPropertyChange(LayoutContainer.PROP_LAYOUT));
    assertFalse(et.hadOtherPropertyChange(LayoutContainer.PROP_LAYOUT));

    et.clear();
    assertFalse(et.hadAnyEvent());

    lc.removePropertyChangeListener(et.propertyChangeListener);

    lc.setLayout(Layout.createDefault());
    assertFalse(et.hadAnyEvent());
  }
}
