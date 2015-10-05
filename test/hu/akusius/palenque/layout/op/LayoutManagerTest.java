package hu.akusius.palenque.layout.op;

import hu.akusius.palenque.layout.data.Layout;
import hu.akusius.palenque.layout.data.LayoutContainer;
import org.junit.Test;
import util.EventTester;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Bujdosó Ákos
 */
public class LayoutManagerTest {

  public LayoutManagerTest() {
  }

  @Test
  public void test1() {
    Layout l1 = Layout.createDefault();
    Layout l2 = l1.randomize();
    assertThat(l2, not(equalTo(l1)));

    LayoutContainer lc = new LayoutContainer(l1);
    LayoutManager em = new LayoutManager(lc);

    PropAction importAction = em.getImportAction();
    PropAction exportAction = em.getExportAction();

    assertTrue(em.isEnabled());
    assertTrue(importAction.isEnabled());
    assertTrue(exportAction.isEnabled());

    assertThat(lc.getLayout(), equalTo(l1));

    lc.setLayout(l2);
    assertThat(lc.getLayout(), equalTo(l2));
    assertTrue(importAction.isEnabled());
    assertTrue(exportAction.isEnabled());

    EventTester et = new EventTester();
    lc.addPropertyChangeListener(et.propertyChangeListener);
    exportAction.performAction();
    importAction.performAction();
    assertThat(lc.getLayout(), equalTo(l2));
    assertFalse(et.hadAnyEvent());

    em.setEnabled(false);
    assertFalse(em.isEnabled());
    assertFalse(importAction.isEnabled());
    assertFalse(exportAction.isEnabled());

    lc.setLayout(l1);
    assertFalse(em.isEnabled());
    assertFalse(importAction.isEnabled());
    assertFalse(exportAction.isEnabled());

    em.setEnabled(true);
    assertTrue(em.isEnabled());
    assertTrue(importAction.isEnabled());
    assertTrue(exportAction.isEnabled());
  }

  @Test
  public void test2() {
    final Layout l1 = Layout.createDefault();
    LayoutContainer lc = new LayoutContainer(l1);
    LayoutManager em = new LayoutManager(lc);

    PropAction importAction = em.getImportAction();
    PropAction exportAction = em.getExportAction();

    assertTrue(em.isEnabled());
    assertTrue(importAction.isEnabled());
    assertTrue(exportAction.isEnabled());

    assertThat(lc.getLayout(), equalTo(l1));

    final Layout l2 = Layout.createDefault().randomize();

    em.setImpexer(new LayoutImpexer() {

      @Override
      public Layout importLayout() {
        return l2;
      }

      @Override
      public boolean exportLayout(Layout layoutToExport) {
        assertThat(layoutToExport, equalTo(l1));
        return true;
      }
    });

    EventTester et = new EventTester();
    lc.addPropertyChangeListener(et.propertyChangeListener);
    exportAction.performAction();
    importAction.performAction();
    assertThat(lc.getLayout(), equalTo(l2));
    assertTrue(et.hadPropertyChange(LayoutContainer.PROP_LAYOUT, l1, l2));

    lc.setLayout(l1);
    assertThat(lc.getLayout(), equalTo(l1));
  }

  @Test
  public void test3() {
    final Layout l1 = Layout.createDefault();
    LayoutContainer lc = new LayoutContainer(l1);
    LayoutManager em = new LayoutManager(lc);

    PropAction randomizeAction = em.getRandomizeAction();
    PropAction resetAction = em.getResetAction();

    assertTrue(em.isEnabled());
    assertTrue(randomizeAction.isEnabled());
    assertFalse(resetAction.isEnabled());

    assertThat(lc.getLayout(), equalTo(l1));

    EventTester et = new EventTester();
    lc.addPropertyChangeListener(et.propertyChangeListener);
    randomizeAction.performAction();
    Layout l2 = lc.getLayout();
    assertThat(l2, not(equalTo(l1)));
    assertTrue(et.hadPropertyChange(LayoutContainer.PROP_LAYOUT, l1, l2));
    assertTrue(randomizeAction.isEnabled());
    assertTrue(resetAction.isEnabled());
    et.clear();

    resetAction.performAction();
    assertThat(lc.getLayout(), not(equalTo(l2)));
    assertThat(lc.getLayout(), equalTo(l1));
    assertTrue(et.hadPropertyChange(LayoutContainer.PROP_LAYOUT, l2, l1));
    assertTrue(randomizeAction.isEnabled());
    assertFalse(resetAction.isEnabled());
    et.clear();
  }

  @Test
  public void test4() {
    final Layout l1 = Layout.createDefault();
    LayoutContainer lc = new LayoutContainer(l1);
    LayoutManager em = new LayoutManager(lc);

    PropAction randomizeAction = em.getRandomizeAction();
    PropAction resetAction = em.getResetAction();

    assertTrue(em.isEnabled());
    assertTrue(randomizeAction.isEnabled());
    assertFalse(resetAction.isEnabled());

    assertThat(lc.getLayout(), equalTo(l1));

    em.setEnabled(false);
    assertFalse(em.isEnabled());
    assertFalse(randomizeAction.isEnabled());
    assertFalse(resetAction.isEnabled());

    lc.setLayout(l1.randomize());
    assertFalse(em.isEnabled());
    assertFalse(randomizeAction.isEnabled());
    assertFalse(resetAction.isEnabled());

    em.setEnabled(true);
    assertTrue(em.isEnabled());
    assertTrue(randomizeAction.isEnabled());
    assertTrue(resetAction.isEnabled());
  }
}
