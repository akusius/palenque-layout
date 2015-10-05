package hu.akusius.palenque.layout.op;

import hu.akusius.palenque.layout.data.FrameInfo;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Bujdosó Ákos
 */
public class DisplayManagerTest {

  public DisplayManagerTest() {
  }

  @Test
  public void test1() {
    PropSliderFrame fs = new PropSliderFrame();
    assertThat(fs.getValue(), equalTo(0));
    assertTrue(fs.isFirstFrame());

    DisplayManager dm = new DisplayManager(fs);
    PropToggle showLid = dm.getShowLidToggle();

    assertTrue(dm.isEnabled());
    assertTrue(showLid.isEnabled());
    assertFalse(showLid.isSelected());
    showLid.setSelected(true);
    assertTrue(showLid.isSelected());

    dm.setEnabled(false);
    assertFalse(dm.isEnabled());
    assertFalse(showLid.isEnabled());
    assertTrue(showLid.isSelected());

    fs.setValue(1);
    assertTrue(fs.isInternalFrame());
    assertFalse(showLid.isEnabled());
    assertTrue(showLid.isSelected());

    dm.setEnabled(true);
    assertTrue(dm.isEnabled());
    assertTrue(showLid.isEnabled());
    assertTrue(showLid.isSelected());

    fs.setValue(0);
    assertTrue(showLid.isEnabled());
    assertTrue(showLid.isSelected());

    fs.setValue(FrameInfo.getMaxFrameNum());
    assertTrue(fs.isLastFrame());
    assertTrue(showLid.isEnabled());
    assertTrue(showLid.isSelected());

    dm.setEnabled(false);
    fs.setValue(0);
    assertFalse(showLid.isEnabled());
    assertTrue(showLid.isSelected());

    dm.setEnabled(true);
    assertTrue(showLid.isEnabled());
    assertTrue(showLid.isSelected());
  }

  @Test
  public void test2() {
    PropSliderFrame fs = new PropSliderFrame();
    assertThat(fs.getValue(), equalTo(0));
    assertTrue(fs.isFirstFrame());

    DisplayManager dm = new DisplayManager(fs);
    PropToggle showGrid = dm.getShowGridToggle();

    assertTrue(showGrid.isEnabled());
    assertFalse(showGrid.isSelected());

    fs.setValue(2);
    assertTrue(fs.isInternalFrame());
    assertTrue(showGrid.isEnabled());
    assertFalse(showGrid.isSelected());

    fs.setValue(FrameInfo.getMaxFrameNum());
    assertTrue(fs.isLastFrame());
    assertTrue(showGrid.isEnabled());

    showGrid.setSelected(true);
    assertTrue(showGrid.isSelected());

    dm.setEnabled(false);
    assertFalse(dm.isEnabled());
    assertFalse(showGrid.isEnabled());
    assertTrue(showGrid.isSelected());

    fs.setValue(1);
    assertFalse(showGrid.isEnabled());
    assertTrue(showGrid.isSelected());

    dm.setEnabled(true);
    assertTrue(showGrid.isEnabled());
    assertTrue(showGrid.isSelected());

    fs.setValue(0);
    assertTrue(showGrid.isEnabled());
    assertTrue(showGrid.isSelected());

    showGrid.setSelected(false);
    assertTrue(showGrid.isEnabled());
    assertFalse(showGrid.isSelected());
  }

  @Test
  public void test3() {
    PropSliderFrame fs = new PropSliderFrame();
    assertThat(fs.getValue(), equalTo(0));
    assertTrue(fs.isFirstFrame());

    DisplayManager dm = new DisplayManager(fs);
    PropToggle showNums = dm.getShowNumbersToggle();

    assertTrue(showNums.isEnabled());
    assertFalse(showNums.isSelected());

    showNums.setSelected(true);
    assertTrue(showNums.isSelected());

    fs.setValue(2);
    assertTrue(fs.isInternalFrame());
    assertFalse(showNums.isEnabled());
    assertTrue(showNums.isSelected());

    fs.setValue(FrameInfo.getMaxFrameNum());
    assertTrue(fs.isLastFrame());
    assertFalse(showNums.isEnabled());
    assertTrue(showNums.isSelected());

    dm.setEnabled(false);
    assertFalse(dm.isEnabled());
    assertFalse(showNums.isEnabled());
    assertTrue(showNums.isSelected());

    fs.setValue(0);
    assertTrue(fs.isFirstFrame());
    assertFalse(showNums.isEnabled());
    assertTrue(showNums.isSelected());

    dm.setEnabled(true);
    assertTrue(fs.isFirstFrame());
    assertTrue(showNums.isEnabled());
    assertTrue(showNums.isSelected());

    showNums.setSelected(false);
    assertTrue(showNums.isEnabled());
    assertFalse(showNums.isSelected());
  }

  @Test
  public void test4() {
    PropSliderFrame fs = new PropSliderFrame();
    assertThat(fs.getValue(), equalTo(0));
    assertTrue(fs.isFirstFrame());

    DisplayManager dm = new DisplayManager(fs);
    PropToggle showRef = dm.getShowReferenceToggle();

    assertTrue(showRef.isEnabled());
    assertFalse(showRef.isSelected());

    dm.setEnabled(false);
    assertFalse(dm.isEnabled());
    fs.setValue(FrameInfo.getMaxFrameNum());
    assertTrue(fs.isLastFrame());
    assertFalse(showRef.isEnabled());
    assertFalse(showRef.isSelected());

    dm.setEnabled(true);
    assertTrue(dm.isEnabled());
    assertTrue(showRef.isEnabled());
    assertFalse(showRef.isSelected());

    showRef.setSelected(true);
    assertTrue(showRef.isSelected());

    fs.setValue(10);
    assertTrue(fs.isInternalFrame());
    assertTrue(showRef.isEnabled());
    assertTrue(showRef.isSelected());

    dm.setEnabled(false);
    assertFalse(showRef.isEnabled());
    assertTrue(showRef.isSelected());

    fs.setValue(0);
    assertFalse(showRef.isEnabled());
    assertTrue(showRef.isSelected());

    try {
      showRef.setSelected(false);
      fail();
    } catch (IllegalStateException ex) {
    }

    dm.setEnabled(true);
    assertTrue(showRef.isEnabled());
    assertTrue(showRef.isSelected());

    showRef.setSelected(false);
    assertTrue(showRef.isEnabled());
    assertFalse(showRef.isSelected());
  }
}
