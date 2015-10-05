package hu.akusius.palenque.layout.op;

import hu.akusius.palenque.layout.data.*;
import java.util.List;
import org.junit.Test;
import util.EventTester;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Bujdosó Ákos
 */
public class EditManagerTest {

  public EditManagerTest() {
  }

  @Test
  public void test1() {
    LayoutContainer lc = new LayoutContainer(Layout.createDefault());
    EditManager em = new EditManager(lc);
    assertTrue(em.isEnabled());
    assertThat(em.getLayoutContainer(), sameInstance(lc));
    assertThat(em.getCurrentMode(), equalTo(EditMode.Select));
    assertThat(em.getActiveItem(), nullValue());
    assertThat(em.getAddItemType(), nullValue());
    assertThat(em.getMovingReferenceCell(), nullValue());

    PropToggle selectToggle = em.getSelectToggle();
    PropToggle addToggle = em.getAddToggle();
    PropToggle removeToggle = em.getRemoveToggle();

    assertTrue(selectToggle.isEnabled());
    assertTrue(addToggle.isEnabled());
    assertTrue(removeToggle.isEnabled());
    assertTrue(selectToggle.isSelected());
    assertFalse(addToggle.isSelected());
    assertFalse(removeToggle.isSelected());
    assertTrue(selectToggle.isInGroup());
    assertTrue(addToggle.isInGroup());
    assertTrue(removeToggle.isInGroup());

    em.setEnabled(false);
    assertFalse(em.isEnabled());
    assertThat(em.getCurrentMode(), equalTo(EditMode.Disabled));
    assertFalse(selectToggle.isEnabled());
    assertFalse(addToggle.isEnabled());
    assertFalse(removeToggle.isEnabled());
    assertTrue(selectToggle.isSelected());
    assertFalse(addToggle.isSelected());
    assertFalse(removeToggle.isSelected());

    em.setEnabled(true);
    assertTrue(em.isEnabled());
    assertThat(em.getCurrentMode(), equalTo(EditMode.Select));
    assertTrue(selectToggle.isEnabled());
    assertTrue(addToggle.isEnabled());
    assertTrue(removeToggle.isEnabled());
    assertTrue(selectToggle.isSelected());
    assertFalse(addToggle.isSelected());
    assertFalse(removeToggle.isSelected());
  }

  @Test
  public void test2() {
    Layout l = Layout.createDefault();
    List<Item> items = l.getItems();
    LayoutContainer lc = new LayoutContainer(l);
    EditManager em = new EditManager(lc);
    assertTrue(em.isEnabled());
    assertThat(em.getLayoutContainer(), sameInstance(lc));
    assertThat(em.getCurrentMode(), equalTo(EditMode.Select));

    PropToggle selectToggle = em.getSelectToggle();
    PropToggle addToggle = em.getAddToggle();
    PropToggle removeToggle = em.getRemoveToggle();
    assertTrue(selectToggle.isEnabled());
    assertTrue(selectToggle.isSelected());
    assertTrue(removeToggle.isEnabled());
    assertTrue(selectToggle.isSelected());

    EventTester et = new EventTester();
    em.addPropertyChangeListener(et.propertyChangeListener);
    lc.addPropertyChangeListener(et.propertyChangeListener);

    Item item = items.get(0);
    em.setActiveItem(item);
    assertThat(em.getActiveItem(), equalTo(item));
    assertTrue(et.hadPropertyChange(EditManager.PROP_ACTIVEITEM, null, item));
    assertFalse(et.hadOtherPropertyChange(EditManager.PROP_ACTIVEITEM));
    et.clear();

    em.startMoving(item.getCenter());
    assertThat(em.getActiveItem(), equalTo(item));
    assertThat(em.getCurrentMode(), equalTo(EditMode.Moving));
    assertThat(em.getMovingReferenceCell(), equalTo(item.getCenter()));
    assertTrue(et.hadPropertyChange(EditManager.PROP_CURRENTMODE, EditMode.Select, EditMode.Moving));
    assertFalse(et.hadOtherPropertyChange(EditManager.PROP_CURRENTMODE));
    et.clear();
    assertFalse(addToggle.isEnabled());
    assertFalse(removeToggle.isEnabled());

    em.cancelMoving();
    assertThat(em.getActiveItem(), nullValue());
    assertThat(em.getCurrentMode(), equalTo(EditMode.Select));
    assertThat(em.getMovingReferenceCell(), nullValue());
    assertTrue(et.hadPropertyChange(EditManager.PROP_CURRENTMODE, EditMode.Moving, EditMode.Select));
    assertFalse(et.hadOtherPropertyChange(EditManager.PROP_CURRENTMODE));
    et.clear();
    assertTrue(addToggle.isEnabled());
    assertTrue(removeToggle.isEnabled());

    em.setActiveItem(item);
    et.clear();
    em.startMoving(item.getCenter());
    assertThat(em.getActiveItem(), equalTo(item));
    assertThat(em.getCurrentMode(), equalTo(EditMode.Moving));
    assertThat(em.getMovingReferenceCell(), equalTo(item.getCenter()));
    assertTrue(et.hadPropertyChange(EditManager.PROP_CURRENTMODE, EditMode.Select, EditMode.Moving));
    assertFalse(et.hadOtherPropertyChange(EditManager.PROP_CURRENTMODE));
    et.clear();
    assertFalse(addToggle.isEnabled());
    assertFalse(removeToggle.isEnabled());

    assertThat(lc.getLayout(), equalTo(l));
    em.finishMoving(new Cell(0, 0));
    assertThat(em.getActiveItem(), nullValue());
    assertThat(em.getCurrentMode(), equalTo(EditMode.Select));
    assertThat(em.getMovingReferenceCell(), nullValue());
    assertThat(lc.getLayout(), not(equalTo(l)));
    assertTrue(et.hadPropertyChange(EditManager.PROP_CURRENTMODE, EditMode.Moving, EditMode.Select));
    assertTrue(et.hadPropertyChange(LayoutContainer.PROP_LAYOUT, l, lc.getLayout()));
    et.clear();
    assertTrue(addToggle.isEnabled());
    assertTrue(removeToggle.isEnabled());
  }

  @Test
  public void test3() {
    Layout l = Layout.createDefault();
    LayoutContainer lc = new LayoutContainer(l);
    EditManager em = new EditManager(lc);

    Item item = l.getItems().get(0);

    try {
      // Nincs aktív elem kiválasztva, ezért hibát kell dobnia
      em.startMoving(item.getCenter());
      fail();
    } catch (Exception e) {
    }

    em.setActiveItem(item);
    assertThat(em.getActiveItem(), equalTo(item));

    em.startMoving(item.getCenter());
    assertThat(em.getActiveItem(), equalTo(item));
    assertThat(em.getCurrentMode(), equalTo(EditMode.Moving));

    lc.setLayout(Layout.createDefault().randomize());

    assertThat(lc.getLayout(), not(equalTo(l)));
    assertThat(em.getLayoutContainer(), sameInstance(lc));
    assertThat(em.getCurrentMode(), equalTo(EditMode.Select));
    assertThat(em.getActiveItem(), nullValue());
    assertThat(em.getAddItemType(), nullValue());
    assertThat(em.getMovingReferenceCell(), nullValue());

    PropToggle selectToggle = em.getSelectToggle();
    PropToggle addToggle = em.getAddToggle();
    PropToggle removeToggle = em.getRemoveToggle();

    assertTrue(selectToggle.isEnabled());
    assertTrue(addToggle.isEnabled());
    assertTrue(removeToggle.isEnabled());
    assertTrue(selectToggle.isSelected());
    assertFalse(addToggle.isSelected());
    assertFalse(removeToggle.isSelected());
  }

  @Test
  public void test4() {
    Layout l = Layout.createDefault();
    LayoutContainer lc = new LayoutContainer(l);
    EditManager em = new EditManager(lc);

    Item item = l.getItems().get(0);
    em.setActiveItem(item);
    assertThat(em.getActiveItem(), equalTo(item));

    em.startMoving(item.getCenter());
    assertThat(em.getActiveItem(), equalTo(item));
    assertThat(em.getCurrentMode(), equalTo(EditMode.Moving));

    em.setEnabled(false);
    assertThat(em.getCurrentMode(), equalTo(EditMode.Disabled));
    assertThat(em.getActiveItem(), nullValue());
    assertThat(em.getAddItemType(), nullValue());
    assertThat(em.getMovingReferenceCell(), nullValue());

    PropToggle selectToggle = em.getSelectToggle();
    PropToggle addToggle = em.getAddToggle();
    PropToggle removeToggle = em.getRemoveToggle();

    assertFalse(selectToggle.isEnabled());
    assertFalse(addToggle.isEnabled());
    assertFalse(removeToggle.isEnabled());
    assertTrue(selectToggle.isSelected());
    assertFalse(addToggle.isSelected());
    assertFalse(removeToggle.isSelected());
  }

  @Test
  public void test5() {
    Layout l = Layout.createDefault();
    LayoutContainer lc = new LayoutContainer(l);
    EditManager em = new EditManager(lc);
    assertTrue(em.isEnabled());
    assertThat(em.getLayoutContainer(), sameInstance(lc));
    assertThat(em.getCurrentMode(), equalTo(EditMode.Select));
    assertThat(em.getAddItemType(), nullValue());

    PropToggle selectToggle = em.getSelectToggle();
    PropToggle addToggle = em.getAddToggle();
    PropToggle removeToggle = em.getRemoveToggle();

    assertTrue(selectToggle.isEnabled());
    assertTrue(addToggle.isEnabled());
    assertTrue(removeToggle.isEnabled());
    assertTrue(selectToggle.isSelected());
    assertFalse(addToggle.isSelected());
    assertFalse(removeToggle.isSelected());

    EventTester et = new EventTester();
    em.addPropertyChangeListener(et.propertyChangeListener);
    lc.addPropertyChangeListener(et.propertyChangeListener);

    addToggle.setSelected(true);
    assertThat(em.getCurrentMode(), equalTo(EditMode.Add));
    assertThat(em.getAddItemType(), equalTo(ItemType.Triplet));
    assertThat(em.getActiveItem(), nullValue());
    assertThat(em.getMovingReferenceCell(), nullValue());
    assertTrue(et.hadPropertyChange(EditManager.PROP_CURRENTMODE, EditMode.Select, EditMode.Add));
    et.clear();
    assertFalse(selectToggle.isSelected());
    assertTrue(addToggle.isSelected());
    assertFalse(removeToggle.isSelected());

    em.switchAddItemType();
    assertThat(em.getAddItemType(), equalTo(ItemType.Sun));
    assertTrue(et.hadPropertyChange(EditManager.PROP_ADDITEMTYPE, ItemType.Triplet, ItemType.Sun));
    assertFalse(et.hadOtherPropertyChange(EditManager.PROP_ADDITEMTYPE));
    et.clear();

    em.switchAddItemType();
    assertThat(em.getAddItemType(), equalTo(ItemType.Star));
    em.switchAddItemType();
    assertThat(em.getAddItemType(), equalTo(ItemType.Triplet));
    et.clear();

    Item item = new Item(em.getAddItemType(), new Cell(3, 10));
    em.addItem(item);
    assertThat(em.getCurrentMode(), equalTo(EditMode.Select));
    assertThat(em.getAddItemType(), nullValue());
    assertThat(em.getActiveItem(), nullValue());
    assertThat(em.getMovingReferenceCell(), nullValue());
    assertThat(lc.getLayout(), not(equalTo(l)));
    assertTrue(et.hadPropertyChange(EditManager.PROP_CURRENTMODE, EditMode.Add, EditMode.Select));
    assertTrue(et.hadPropertyChange(LayoutContainer.PROP_LAYOUT, l, lc.getLayout()));
    assertTrue(lc.getLayout().hasItem(item));
    et.clear();
  }

  @Test
  public void test6() {
    Layout l = Layout.createDefault();
    LayoutContainer lc = new LayoutContainer(l);
    EditManager em = new EditManager(lc);
    assertTrue(em.isEnabled());
    assertThat(em.getLayoutContainer(), sameInstance(lc));
    assertThat(em.getCurrentMode(), equalTo(EditMode.Select));
    assertThat(em.getAddItemType(), nullValue());

    PropToggle selectToggle = em.getSelectToggle();
    PropToggle addToggle = em.getAddToggle();
    PropToggle removeToggle = em.getRemoveToggle();

    assertTrue(selectToggle.isEnabled());
    assertTrue(addToggle.isEnabled());
    assertTrue(removeToggle.isEnabled());
    assertTrue(selectToggle.isSelected());
    assertFalse(addToggle.isSelected());
    assertFalse(removeToggle.isSelected());

    EventTester et = new EventTester();
    em.addPropertyChangeListener(et.propertyChangeListener);

    removeToggle.setSelected(true);
    assertThat(em.getCurrentMode(), equalTo(EditMode.Remove));
    assertThat(em.getAddItemType(), nullValue());
    assertThat(em.getActiveItem(), nullValue());
    assertThat(em.getMovingReferenceCell(), nullValue());
    assertTrue(et.hadPropertyChange(EditManager.PROP_CURRENTMODE, EditMode.Select, EditMode.Remove));
    et.clear();
    assertFalse(selectToggle.isSelected());
    assertFalse(addToggle.isSelected());
    assertTrue(removeToggle.isSelected());

    Item item = l.getItems().get(0);
    em.removeItem(item);
    assertThat(em.getCurrentMode(), equalTo(EditMode.Select));
    assertThat(em.getAddItemType(), nullValue());
    assertThat(em.getActiveItem(), nullValue());
    assertThat(em.getMovingReferenceCell(), nullValue());
    assertThat(lc.getLayout(), not(equalTo(l)));
    assertTrue(et.hadPropertyChange(EditManager.PROP_CURRENTMODE, EditMode.Remove, EditMode.Select));
    assertFalse(lc.getLayout().hasItem(item));
    et.clear();
  }

  @Test
  public void test7() {
    Layout l = Layout.createDefault();
    LayoutContainer lc = new LayoutContainer(l);
    EditManager em = new EditManager(lc);
    assertTrue(em.isEnabled());
    assertThat(em.getLayoutContainer(), sameInstance(lc));
    assertThat(em.getCurrentMode(), equalTo(EditMode.Select));
    assertThat(em.getAddItemType(), nullValue());

    Item item = l.getItems().get(0);
    em.setActiveItem(item);
    em.startMoving(item.getCenter());
    assertThat(em.getCurrentMode(), equalTo(EditMode.Moving));
    assertThat(em.getAddItemType(), nullValue());
    assertThat(em.getActiveItem(), equalTo(item));
    assertThat(em.getMovingReferenceCell(), equalTo(item.getCenter()));

    em.setEnabled(false);
    assertFalse(em.isEnabled());
    assertThat(em.getCurrentMode(), equalTo(EditMode.Disabled));
    assertThat(em.getAddItemType(), nullValue());
    assertThat(em.getActiveItem(), nullValue());
    assertThat(em.getMovingReferenceCell(), nullValue());

    em.setEnabled(true);
    assertTrue(em.isEnabled());
    assertThat(em.getCurrentMode(), equalTo(EditMode.Select));
    assertThat(em.getAddItemType(), nullValue());
    assertThat(em.getActiveItem(), nullValue());
    assertThat(em.getMovingReferenceCell(), nullValue());
  }

  @Test
  public void test8() {
    Layout l = Layout.createDefault();
    LayoutContainer lc = new LayoutContainer(l);
    EditManager em = new EditManager(lc);
    assertTrue(em.isEnabled());
    assertThat(em.getLayoutContainer(), sameInstance(lc));
    assertThat(em.getCurrentMode(), equalTo(EditMode.Select));
    assertThat(em.getAddItemType(), nullValue());

    PropToggle addToggle = em.getAddToggle();
    PropToggle removeToggle = em.getRemoveToggle();

    Item item = l.getItems().get(0);
    while (lc.getLayout().canAddMore()) {
      addToggle.setSelected(true);
      em.addItem(item);
    }

    assertFalse(addToggle.isEnabled());
    em.setEnabled(false);

    em.setEnabled(true);
    assertTrue(em.isEnabled());
    assertThat(em.getCurrentMode(), equalTo(EditMode.Select));
    assertThat(em.getAddItemType(), nullValue());
    assertThat(em.getActiveItem(), nullValue());
    assertThat(em.getMovingReferenceCell(), nullValue());
    assertFalse(addToggle.isEnabled());

    removeToggle.setSelected(true);
    em.removeItem(item);

    assertTrue(lc.getLayout().canAddMore());
    assertTrue(addToggle.isEnabled());
  }
}
