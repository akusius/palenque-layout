package hu.akusius.palenque.layout.op;

import hu.akusius.palenque.layout.data.Layout;
import hu.akusius.palenque.layout.data.LayoutContainer;
import java.util.List;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Bujdosó Ákos
 */
public class MemoryManagerTest {

  public MemoryManagerTest() {
  }

  @Test
  public void test1() {
    Layout l1 = Layout.createDefault();
    LayoutContainer lc = new LayoutContainer(l1);
    assertThat(lc.getLayout(), equalTo(l1));

    MemoryManager mm = new MemoryManager(lc, 3);

    assertThat(mm.getRegNum(), equalTo(3));
    List<? extends PropToggle> regs = mm.getRegToggles();
    assertThat(regs.size(), equalTo(3));

    for (int i = 0; i < regs.size(); i++) {
      PropToggle reg = regs.get(i);
      assertThat(reg, sameInstance(mm.getRegToggle(i)));
      assertTrue(reg.isEnabled());
      assertFalse(reg.isSelected());
      assertFalse(reg.isInGroup());
    }

    PropToggle r1 = regs.get(0);
    PropToggle r2 = regs.get(1);
    PropToggle r3 = regs.get(2);
    PropAction clear = mm.getClearAction();

    assertFalse(clear.isEnabled());

    r1.setSelected(true);
    assertFalse(r1.isEnabled());
    assertTrue(r1.isSelected());
    assertTrue(r2.isEnabled());
    assertFalse(r2.isSelected());
    assertTrue(r3.isEnabled());
    assertFalse(r3.isSelected());
    assertTrue(clear.isEnabled());

    r3.setSelected(true);
    assertFalse(r1.isEnabled());
    assertTrue(r1.isSelected());
    assertTrue(r2.isEnabled());
    assertFalse(r2.isSelected());
    assertFalse(r3.isEnabled());
    assertTrue(r3.isSelected());
    assertTrue(clear.isEnabled());

    Layout l2 = l1.randomize();
    lc.setLayout(l2);

    assertTrue(r1.isEnabled());
    assertTrue(r1.isSelected());
    assertTrue(r2.isEnabled());
    assertFalse(r2.isSelected());
    assertTrue(r3.isEnabled());
    assertTrue(r3.isSelected());
    assertTrue(clear.isEnabled());

    r2.setSelected(true);
    assertTrue(r1.isEnabled());
    assertTrue(r1.isSelected());
    assertFalse(r2.isEnabled());
    assertTrue(r2.isSelected());
    assertTrue(r3.isEnabled());
    assertTrue(r3.isSelected());
    assertTrue(clear.isEnabled());

    assertThat(lc.getLayout(), equalTo(l2));

    r3.setSelected(false);
    assertFalse(r1.isEnabled());
    assertTrue(r1.isSelected());
    assertTrue(r2.isEnabled());
    assertTrue(r2.isSelected());
    assertTrue(r3.isEnabled());
    assertFalse(r3.isSelected());
    assertTrue(clear.isEnabled());
    assertThat(lc.getLayout(), equalTo(l1));

    r2.setSelected(false);
    assertTrue(r1.isEnabled());
    assertTrue(r1.isSelected());
    assertTrue(r2.isEnabled());
    assertFalse(r2.isSelected());
    assertTrue(r3.isEnabled());
    assertFalse(r3.isSelected());
    assertTrue(clear.isEnabled());
    assertThat(lc.getLayout(), equalTo(l2));

    r3.setSelected(true);
    assertTrue(r1.isEnabled());
    assertTrue(r1.isSelected());
    assertTrue(r2.isEnabled());
    assertFalse(r2.isSelected());
    assertFalse(r3.isEnabled());
    assertTrue(r3.isSelected());
    assertTrue(clear.isEnabled());

    mm.setEnabled(false);
    assertFalse(r1.isEnabled());
    assertTrue(r1.isSelected());
    assertFalse(r2.isEnabled());
    assertFalse(r2.isSelected());
    assertFalse(r3.isEnabled());
    assertTrue(r3.isSelected());
    assertFalse(clear.isEnabled());

    lc.setLayout(l1.createClone());
    assertThat(lc.getLayout(), equalTo(l1));

    assertFalse(r1.isEnabled());
    assertTrue(r1.isSelected());
    assertFalse(r2.isEnabled());
    assertFalse(r2.isSelected());
    assertFalse(r3.isEnabled());
    assertTrue(r3.isSelected());
    assertFalse(clear.isEnabled());

    mm.setEnabled(true);
    assertFalse(r1.isEnabled());
    assertTrue(r1.isSelected());
    assertTrue(r2.isEnabled());
    assertFalse(r2.isSelected());
    assertTrue(r3.isEnabled());
    assertTrue(r3.isSelected());
    assertTrue(clear.isEnabled());

    r3.setSelected(false);
    assertTrue(r1.isEnabled());
    assertTrue(r1.isSelected());
    assertTrue(r2.isEnabled());
    assertFalse(r2.isSelected());
    assertTrue(r3.isEnabled());
    assertFalse(r3.isSelected());
    assertTrue(clear.isEnabled());
    assertThat(lc.getLayout(), equalTo(l2));

    r2.setSelected(false);
    r3.setSelected(false);
    assertTrue(r1.isEnabled());
    assertTrue(r1.isSelected());
    assertTrue(r2.isEnabled());
    assertFalse(r2.isSelected());
    assertTrue(r3.isEnabled());
    assertFalse(r3.isSelected());
    assertTrue(clear.isEnabled());
    assertThat(lc.getLayout(), equalTo(l2));

    r2.setSelected(true);
    assertTrue(r1.isEnabled());
    assertTrue(r1.isSelected());
    assertFalse(r2.isEnabled());
    assertTrue(r2.isSelected());
    assertTrue(r3.isEnabled());
    assertFalse(r3.isSelected());
    assertTrue(clear.isEnabled());
    assertThat(lc.getLayout(), equalTo(l2));

    clear.performAction();
    assertTrue(r1.isEnabled());
    assertFalse(r1.isSelected());
    assertTrue(r2.isEnabled());
    assertFalse(r2.isSelected());
    assertTrue(r3.isEnabled());
    assertFalse(r3.isSelected());
    assertFalse(clear.isEnabled());
    assertThat(lc.getLayout(), equalTo(l2));
  }
}
