package hu.akusius.palenque.layout.op;

import hu.akusius.palenque.layout.data.Item;
import hu.akusius.palenque.layout.data.Layout;
import hu.akusius.palenque.layout.data.LayoutContainer;
import java.awt.event.ActionEvent;
import javax.swing.Timer;
import org.junit.Test;
import util.EventTester;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Bujdosó Ákos
 */
public class OperationManagerTest {

  private static final class TestTimer extends Timer {

    TestTimer() {
      super(50, null);
    }

    private boolean running = false;

    private int tickNum = 0;

    @Override
    public void stop() {
      running = false;
    }

    @Override
    public boolean isRunning() {
      return running;
    }

    @Override
    public void start() {
      running = true;
    }

    public void tick() {
      tick(1);
    }

    public void tick(int n) {
      assert isRunning();
      assert isRepeats();
      for (int i = 0; i < n; i++) {
        tickNum++;
        fireActionPerformed(new ActionEvent(this, 0, getActionCommand(), System.currentTimeMillis(), 0));
      }
    }

    public long getElapsedMsec() {
      return tickNum * getDelay();
    }
  }

  public OperationManagerTest() {
  }

  @Test
  public void test1() {
    OperationManager om = new OperationManager();

    LayoutContainer lc = om.getLayoutContainer();
    EditManager em = om.getEditManager();
    DisplayManager dm = om.getDisplayManager();
    LayoutManager lm = om.getLayoutManager();
    PlayManager pm = om.getPlayManager();
    HistoryManager hm = om.getHistoryManager();
    MemoryManager mm = om.getMemoryManager();
    OpacityManager opm = om.getOpacityManager();
    PropToggle nvt = om.getNormalizedViewToggle();
    PropAction mopac = om.getModifyOpacityAction();
    PropAction shd = om.getShowDataAction();
    PropAction info = om.getInfoAction();
    PropAction sshot = om.getScreenshotAction();

    assertThat(lc.getLayout(), equalTo(Layout.createDefault()));
    assertThat(em.getCurrentMode(), equalTo(EditMode.Select));
    assertTrue(em.isEnabled());
    assertTrue(dm.isEnabled());
    assertTrue(lm.isEnabled());
    assertTrue(pm.isEnabled());
    assertFalse(pm.getPlayingToggle().isSelected());
    assertTrue(pm.getFrameSlider().isFirstFrame());
    assertTrue(hm.isEnabled());
    assertTrue(mm.isEnabled());
    assertTrue(opm.isEnabled());
    assertTrue(nvt.isEnabled());
    assertTrue(mopac.isEnabled());
    assertTrue(shd.isEnabled());
    assertTrue(info.isEnabled());
    assertTrue(sshot.isEnabled());
  }

  @Test
  public void test2() {
    TestTimer timer = new TestTimer();
    OperationManager om = new OperationManager(timer);

    EditManager em = om.getEditManager();
    DisplayManager dm = om.getDisplayManager();
    LayoutManager lm = om.getLayoutManager();
    HistoryManager hm = om.getHistoryManager();
    MemoryManager mm = om.getMemoryManager();
    OpacityManager opm = om.getOpacityManager();
    PlayManager pm = om.getPlayManager();
    PropToggle nvt = om.getNormalizedViewToggle();
    PropAction mopac = om.getModifyOpacityAction();
    PropAction shd = om.getShowDataAction();
    PropAction info = om.getInfoAction();
    PropAction sshot = om.getScreenshotAction();

    pm.getPlayingToggle().setSelected(true);
    assertTrue(timer.isRunning());
    assertFalse(em.isEnabled());
    assertFalse(dm.isEnabled());
    assertFalse(lm.isEnabled());
    assertFalse(hm.isEnabled());
    assertFalse(mm.isEnabled());
    assertFalse(opm.isEnabled());
    assertFalse(nvt.isEnabled());
    assertFalse(mopac.isEnabled());
    assertFalse(shd.isEnabled());
    assertFalse(info.isEnabled());
    assertFalse(sshot.isEnabled());

    pm.getPlayingToggle().setSelected(false);
    assertFalse(timer.isRunning());
    assertTrue(em.isEnabled());
    assertTrue(dm.isEnabled());
    assertTrue(lm.isEnabled());
    assertTrue(hm.isEnabled());
    assertTrue(mm.isEnabled());
    assertTrue(opm.isEnabled());
    assertTrue(nvt.isEnabled());
    assertTrue(mopac.isEnabled());
    assertTrue(shd.isEnabled());
    assertTrue(info.isEnabled());
    assertTrue(sshot.isEnabled());
  }

  @Test
  public void test3() {
    OperationManager om = new OperationManager();

    LayoutContainer lc = om.getLayoutContainer();
    EditManager em = om.getEditManager();
    DisplayManager dm = om.getDisplayManager();
    LayoutManager lm = om.getLayoutManager();
    HistoryManager hm = om.getHistoryManager();
    MemoryManager mm = om.getMemoryManager();
    OpacityManager opm = om.getOpacityManager();
    PlayManager pm = om.getPlayManager();
    PropToggle nvt = om.getNormalizedViewToggle();
    PropAction mopac = om.getModifyOpacityAction();
    PropAction shd = om.getShowDataAction();
    PropAction info = om.getInfoAction();
    PropAction sshot = om.getScreenshotAction();

    em.getAddToggle().setSelected(true);
    assertTrue(dm.isEnabled());
    assertFalse(lm.isEnabled());
    assertFalse(hm.isEnabled());
    assertFalse(mm.isEnabled());
    assertFalse(opm.isEnabled());
    assertFalse(pm.isEnabled());
    assertFalse(nvt.isEnabled());
    assertFalse(mopac.isEnabled());
    assertFalse(shd.isEnabled());
    assertFalse(info.isEnabled());
    assertFalse(sshot.isEnabled());

    em.getSelectToggle().setSelected(true);
    assertTrue(dm.isEnabled());
    assertTrue(lm.isEnabled());
    assertTrue(hm.isEnabled());
    assertTrue(mm.isEnabled());
    assertTrue(opm.isEnabled());
    assertTrue(pm.isEnabled());
    assertTrue(nvt.isEnabled());
    assertTrue(mopac.isEnabled());
    assertTrue(shd.isEnabled());
    assertTrue(info.isEnabled());
    assertTrue(sshot.isEnabled());

    em.getRemoveToggle().setSelected(true);
    assertTrue(dm.isEnabled());
    assertFalse(lm.isEnabled());
    assertFalse(hm.isEnabled());
    assertFalse(mm.isEnabled());
    assertFalse(opm.isEnabled());
    assertFalse(pm.isEnabled());
    assertFalse(nvt.isEnabled());
    assertFalse(mopac.isEnabled());
    assertFalse(shd.isEnabled());
    assertFalse(info.isEnabled());
    assertFalse(sshot.isEnabled());

    em.getSelectToggle().setSelected(true);
    assertTrue(dm.isEnabled());
    assertTrue(lm.isEnabled());
    assertTrue(hm.isEnabled());
    assertTrue(mm.isEnabled());
    assertTrue(opm.isEnabled());
    assertTrue(pm.isEnabled());
    assertTrue(nvt.isEnabled());
    assertTrue(mopac.isEnabled());
    assertTrue(shd.isEnabled());
    assertTrue(info.isEnabled());
    assertTrue(sshot.isEnabled());

    Layout layout = lc.getLayout();
    Item item = layout.getItems().get(0);
    em.setActiveItem(item);
    em.startMoving(item.getCenter());
    assertTrue(dm.isEnabled());
    assertFalse(lm.isEnabled());
    assertFalse(hm.isEnabled());
    assertFalse(mm.isEnabled());
    assertFalse(opm.isEnabled());
    assertFalse(pm.isEnabled());
    assertFalse(nvt.isEnabled());
    assertFalse(mopac.isEnabled());
    assertFalse(shd.isEnabled());
    assertFalse(info.isEnabled());
    assertFalse(sshot.isEnabled());

    em.cancelMoving();
    assertTrue(dm.isEnabled());
    assertTrue(lm.isEnabled());
    assertTrue(hm.isEnabled());
    assertTrue(mm.isEnabled());
    assertTrue(opm.isEnabled());
    assertTrue(pm.isEnabled());
    assertTrue(nvt.isEnabled());
    assertTrue(mopac.isEnabled());
    assertTrue(shd.isEnabled());
    assertTrue(info.isEnabled());
    assertTrue(sshot.isEnabled());
  }

  @Test
  public void test4() {
    OperationManager om = new OperationManager();

    LayoutContainer lc = om.getLayoutContainer();
    EditManager em = om.getEditManager();
    DisplayManager dm = om.getDisplayManager();
    LayoutManager lm = om.getLayoutManager();
    HistoryManager hm = om.getHistoryManager();
    MemoryManager mm = om.getMemoryManager();
    OpacityManager opm = om.getOpacityManager();
    PlayManager pm = om.getPlayManager();

    Layout layout = lc.getLayout();
    assertThat(layout, equalTo(Layout.createDefault()));

    PropAction undo = hm.getUndoAction();
    PropAction redo = hm.getRedoAction();
    assertFalse(undo.isEnabled());
    assertFalse(redo.isEnabled());

    EventTester et = new EventTester();
    lc.addPropertyChangeListener(et.propertyChangeListener);

    lm.getRandomizeAction().performAction();
    assertThat(layout, not(equalTo(lc.getLayout())));
    assertTrue(et.hadPropertyChange(LayoutContainer.PROP_LAYOUT, layout, lc.getLayout()));
    layout = lc.getLayout();
    assertThat(layout, not(equalTo(Layout.createDefault())));
    assertTrue(undo.isEnabled());
    assertFalse(redo.isEnabled());

    et.clear();
    undo.performAction();
    assertTrue(et.hadPropertyChange(LayoutContainer.PROP_LAYOUT, layout, lc.getLayout()));
    assertThat(lc.getLayout(), equalTo(Layout.createDefault()));
    assertFalse(undo.isEnabled());
    assertTrue(redo.isEnabled());

    pm.getFrameSlider().setValue(10);
    assertTrue(pm.getFrameSlider().isInternalFrame());
    assertFalse(em.isEnabled());
    assertTrue(dm.isEnabled());
    assertTrue(lm.isEnabled());
    assertTrue(hm.isEnabled());
    assertTrue(mm.isEnabled());
    assertTrue(opm.isEnabled());
    assertTrue(redo.isEnabled());

    et.clear();
    redo.performAction();
    assertTrue(et.hadPropertyChange(LayoutContainer.PROP_LAYOUT));
    assertThat(lc.getLayout(), equalTo(layout));
    assertThat(pm.getFrameSlider().getValue(), equalTo(10));
  }
}
