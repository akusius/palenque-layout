package hu.akusius.palenque.layout.op;

import hu.akusius.palenque.layout.data.Layout;
import hu.akusius.palenque.layout.data.LayoutContainer;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Bujdosó Ákos
 */
public class HistoryManagerTest {

  public HistoryManagerTest() {
  }

  @Test
  public void test1() {
    Layout l1 = Layout.createDefault();
    Layout l2 = l1.randomize();
    assertThat(l2, not(equalTo(l1)));

    LayoutContainer lc = new LayoutContainer(l1);
    HistoryManager hm = new HistoryManager(lc);

    PropAction undoAction = hm.getUndoAction();
    PropAction redoAction = hm.getRedoAction();

    assertTrue(hm.isEnabled());
    assertFalse(undoAction.isEnabled());
    assertFalse(redoAction.isEnabled());

    assertThat(lc.getLayout(), equalTo(l1));

    lc.setLayout(l2);
    assertThat(lc.getLayout(), equalTo(l2));
    assertTrue(undoAction.isEnabled());
    assertFalse(redoAction.isEnabled());

    undoAction.performAction();
    assertThat(lc.getLayout(), equalTo(l1));
    assertFalse(undoAction.isEnabled());
    assertTrue(redoAction.isEnabled());

    redoAction.performAction();
    assertThat(lc.getLayout(), equalTo(l2));
    assertTrue(undoAction.isEnabled());
    assertFalse(redoAction.isEnabled());

    hm.setEnabled(false);
    assertFalse(hm.isEnabled());
    assertFalse(undoAction.isEnabled());
    assertFalse(redoAction.isEnabled());

    hm.setEnabled(true);
    assertTrue(hm.isEnabled());
    assertTrue(undoAction.isEnabled());
    assertFalse(redoAction.isEnabled());

    undoAction.performAction();
    assertThat(lc.getLayout(), equalTo(l1));
    assertFalse(undoAction.isEnabled());
    assertTrue(redoAction.isEnabled());

    lc.setLayout(l2);
    assertThat(lc.getLayout(), equalTo(l2));
    assertTrue(undoAction.isEnabled());
    assertFalse(redoAction.isEnabled());

    Layout l3 = l2.randomize();
    assertThat(l3, not(equalTo(l2)));
    assertThat(l3, not(equalTo(l1)));

    lc.setLayout(l3);
    assertThat(lc.getLayout(), equalTo(l3));
    assertTrue(undoAction.isEnabled());
    assertFalse(redoAction.isEnabled());

    undoAction.performAction();
    assertThat(lc.getLayout(), equalTo(l2));
    assertTrue(undoAction.isEnabled());
    assertTrue(redoAction.isEnabled());

    undoAction.performAction();
    assertThat(lc.getLayout(), equalTo(l1));
    assertFalse(undoAction.isEnabled());
    assertTrue(redoAction.isEnabled());

    redoAction.performAction();
    assertThat(lc.getLayout(), equalTo(l2));
    assertTrue(undoAction.isEnabled());
    assertTrue(redoAction.isEnabled());

    redoAction.performAction();
    assertThat(lc.getLayout(), equalTo(l3));
    assertTrue(undoAction.isEnabled());
    assertFalse(redoAction.isEnabled());

    undoAction.performAction();
    assertThat(lc.getLayout(), equalTo(l2));
    assertTrue(undoAction.isEnabled());
    assertTrue(redoAction.isEnabled());

    hm.setEnabled(false);
    assertFalse(hm.isEnabled());
    assertFalse(undoAction.isEnabled());
    assertFalse(redoAction.isEnabled());

    hm.setEnabled(true);
    assertTrue(hm.isEnabled());
    assertTrue(undoAction.isEnabled());
    assertTrue(redoAction.isEnabled());

    undoAction.performAction();
    assertThat(lc.getLayout(), equalTo(l1));
    assertFalse(undoAction.isEnabled());
    assertTrue(redoAction.isEnabled());

    lc.setLayout(l2);
    assertThat(lc.getLayout(), equalTo(l2));
    assertTrue(undoAction.isEnabled());
    assertFalse(redoAction.isEnabled());
  }

  @Test
  public void test2() {
    Layout l1 = Layout.createDefault();
    Layout l2 = l1.randomize();
    assertThat(l2, not(equalTo(l1)));

    LayoutContainer lc = new LayoutContainer(l1);
    HistoryManager hm = new HistoryManager(lc);

    PropAction undoAction = hm.getUndoAction();
    PropAction redoAction = hm.getRedoAction();

    assertThat(lc.getLayout(), equalTo(l1));

    lc.setLayout(l2);
    assertThat(lc.getLayout(), equalTo(l2));
    assertTrue(undoAction.isEnabled());
    assertFalse(redoAction.isEnabled());

    Layout l3 = l2.randomize();
    assertThat(l3, not(equalTo(l2)));
    assertThat(l3, not(equalTo(l1)));

    lc.setLayout(l3);
    assertThat(lc.getLayout(), equalTo(l3));
    assertTrue(undoAction.isEnabled());
    assertFalse(redoAction.isEnabled());

    undoAction.performAction();
    assertThat(lc.getLayout(), equalTo(l2));
    assertTrue(undoAction.isEnabled());
    assertTrue(redoAction.isEnabled());

    undoAction.performAction();
    assertThat(lc.getLayout(), equalTo(l1));
    assertFalse(undoAction.isEnabled());
    assertTrue(redoAction.isEnabled());

    redoAction.performAction();
    assertThat(lc.getLayout(), equalTo(l2));
    assertTrue(undoAction.isEnabled());
    assertTrue(redoAction.isEnabled());

    redoAction.performAction();
    assertThat(lc.getLayout(), equalTo(l3));
    assertTrue(undoAction.isEnabled());
    assertFalse(redoAction.isEnabled());

    try {
      redoAction.performAction();
      fail();
    } catch (IllegalStateException ex) {
    }
  }

  @Test
  public void test3() {
    LayoutContainer lc = new LayoutContainer(Layout.createDefault());
    HistoryManager hm = new HistoryManager(lc);

    Layout refLayout = null;
    for (int i = 1; i <= HistoryManager.MAX_UNDO + 50; i++) {
      Layout l = Layout.createDefault().randomize();
      lc.setLayout(l);
      if (i == 50) {
        refLayout = l;
      }
    }

    PropAction undoAction = hm.getUndoAction();
    PropAction redoAction = hm.getRedoAction();
    for (int i = 0; i < HistoryManager.MAX_UNDO; i++) {
      assertTrue(undoAction.isEnabled());
      undoAction.performAction();
    }

    assertFalse(undoAction.isEnabled());
    assertTrue(redoAction.isEnabled());
    assertThat(lc.getLayout(), equalTo(refLayout));

    lc.setLayout(Layout.createDefault());
    assertTrue(undoAction.isEnabled());
    assertFalse(redoAction.isEnabled());
  }

  @Test
  public void test4() {
    Layout l1 = Layout.createDefault();
    Layout l2 = l1.randomize();
    assertThat(l2, not(equalTo(l1)));

    LayoutContainer lc = new LayoutContainer(l1);
    HistoryManager hm = new HistoryManager(lc);

    PropAction undoAction = hm.getUndoAction();
    PropAction redoAction = hm.getRedoAction();

    assertThat(lc.getLayout(), equalTo(l1));

    lc.setLayout(l2);
    assertThat(lc.getLayout(), equalTo(l2));
    assertTrue(undoAction.isEnabled());
    assertFalse(redoAction.isEnabled());

    hm.setEnabled(false);
    assertFalse(hm.isEnabled());
    assertThat(lc.getLayout(), equalTo(l2));
    assertFalse(undoAction.isEnabled());
    assertFalse(redoAction.isEnabled());

    Layout l3 = l2.randomize();
    assertThat(l3, not(equalTo(l2)));
    assertThat(l3, not(equalTo(l1)));

    lc.setLayout(l3);
    assertThat(lc.getLayout(), equalTo(l3));
    assertFalse(undoAction.isEnabled());
    assertFalse(redoAction.isEnabled());

    hm.setEnabled(true);
    assertTrue(hm.isEnabled());
    assertThat(lc.getLayout(), equalTo(l3));
    assertTrue(undoAction.isEnabled());
    assertFalse(redoAction.isEnabled());
  }

  @Test
  public void test5() {
    Layout l1 = Layout.createDefault();
    Layout l2 = l1.randomize();
    assertThat(l2, not(equalTo(l1)));

    LayoutContainer lc = new LayoutContainer(l1);
    HistoryManager hm = new HistoryManager(lc);

    PropAction undoAction = hm.getUndoAction();
    PropAction redoAction = hm.getRedoAction();
    PropAction switchAction = hm.getSwitchAction();

    assertThat(lc.getLayout(), equalTo(l1));
    assertFalse(undoAction.isEnabled());
    assertFalse(redoAction.isEnabled());
    assertFalse(switchAction.isEnabled());

    lc.setLayout(l2);
    assertThat(lc.getLayout(), equalTo(l2));
    assertTrue(undoAction.isEnabled());
    assertFalse(redoAction.isEnabled());
    assertTrue(switchAction.isEnabled());

    switchAction.performAction();
    assertThat(lc.getLayout(), equalTo(l1));
    assertFalse(undoAction.isEnabled());
    assertTrue(redoAction.isEnabled());
    assertTrue(switchAction.isEnabled());

    switchAction.performAction();
    assertThat(lc.getLayout(), equalTo(l2));
    assertTrue(undoAction.isEnabled());
    assertFalse(redoAction.isEnabled());
    assertTrue(switchAction.isEnabled());

    Layout l3 = l1.randomize();
    lc.setLayout(l3);

    assertThat(lc.getLayout(), equalTo(l3));
    assertTrue(undoAction.isEnabled());
    assertFalse(redoAction.isEnabled());
    assertTrue(switchAction.isEnabled());

    switchAction.performAction();
    assertThat(lc.getLayout(), equalTo(l2));
    assertTrue(undoAction.isEnabled());
    assertTrue(redoAction.isEnabled());
    assertTrue(switchAction.isEnabled());

    switchAction.performAction();
    assertThat(lc.getLayout(), equalTo(l3));
    assertTrue(undoAction.isEnabled());
    assertFalse(redoAction.isEnabled());
    assertTrue(switchAction.isEnabled());

    undoAction.performAction();
    assertThat(lc.getLayout(), equalTo(l2));
    assertTrue(undoAction.isEnabled());
    assertTrue(redoAction.isEnabled());
    assertTrue(switchAction.isEnabled());

    undoAction.performAction();
    assertThat(lc.getLayout(), equalTo(l1));
    assertFalse(undoAction.isEnabled());
    assertTrue(redoAction.isEnabled());
    assertFalse(switchAction.isEnabled());

    redoAction.performAction();
    assertThat(lc.getLayout(), equalTo(l2));
    assertTrue(undoAction.isEnabled());
    assertTrue(redoAction.isEnabled());
    assertTrue(switchAction.isEnabled());

    switchAction.performAction();
    assertThat(lc.getLayout(), equalTo(l3));
    assertTrue(undoAction.isEnabled());
    assertFalse(redoAction.isEnabled());
    assertTrue(switchAction.isEnabled());

    switchAction.performAction();
    assertThat(lc.getLayout(), equalTo(l2));
    assertTrue(undoAction.isEnabled());
    assertTrue(redoAction.isEnabled());
    assertTrue(switchAction.isEnabled());

    hm.setEnabled(false);
    assertFalse(hm.isEnabled());
    assertThat(lc.getLayout(), equalTo(l2));
    assertFalse(undoAction.isEnabled());
    assertFalse(redoAction.isEnabled());
    assertFalse(switchAction.isEnabled());

    hm.setEnabled(true);
    assertTrue(hm.isEnabled());
    assertThat(lc.getLayout(), equalTo(l2));
    assertTrue(undoAction.isEnabled());
    assertTrue(redoAction.isEnabled());
    assertTrue(switchAction.isEnabled());

    switchAction.performAction();
    assertThat(lc.getLayout(), equalTo(l3));
    assertTrue(undoAction.isEnabled());
    assertFalse(redoAction.isEnabled());
    assertTrue(switchAction.isEnabled());
  }
}
