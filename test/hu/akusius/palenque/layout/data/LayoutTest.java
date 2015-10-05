package hu.akusius.palenque.layout.data;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.junit.Test;
import util.StringUtils;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Bujdosó Ákos
 */
public class LayoutTest {

  private static final class ChangeListenerTester implements ChangeListener {

    boolean hasChanged = false;

    @Override
    public void stateChanged(ChangeEvent e) {
      this.hasChanged = true;
    }

    public void reset() {
      this.hasChanged = false;
    }

  }

  public LayoutTest() {
  }

  @Test
  public void test1() {
    Layout l = Layout.createDefault();

    List<Item> items = l.getItems();
    assertThat(items.size(), equalTo(l.getNumberOfItems()));
    assertThat(l.getNumberOfItems(), not(equalTo(0)));
    for (int i = 0; i < items.size(); i++) {
      Item item = items.get(i);
      assertThat(l.hasItem(item), is(true));
      assertThat(l.getIndex(item), equalTo(i));
    }

    Layout l2 = new Layout(items);
    assertThat(l2, not(sameInstance(l)));
    assertThat(l2.getNumberOfItems(), equalTo(l.getNumberOfItems()));
    assertThat(l2, equalTo(l));
    assertThat(l, equalTo(l2));
    assertThat(l2.hashCode(), equalTo(l.hashCode()));

    Layout l3 = l2.createClone();
    assertThat(l3, not(sameInstance(l2)));
    assertThat(l3, not(sameInstance(l)));
    assertThat(l3, equalTo(l2));
    assertThat(l3, equalTo(l));
    assertThat(l3.hashCode(), equalTo(l2.hashCode()));
    assertThat(l3.hashCode(), equalTo(l.hashCode()));

    Layout l4 = Layout.deserialize(l3.serialize());
    assertThat(l4, not(sameInstance(l3)));
    assertThat(l4, equalTo(l3));
    assertThat(l4, equalTo(l2));
    assertThat(l4, equalTo(l));

    Layout l5 = Layout.deserialize(new StringReader(l4.serialize()));
    assertThat(l5, not(sameInstance(l3)));
    assertThat(l5, equalTo(l3));
    assertThat(l5, equalTo(l2));
    assertThat(l5, equalTo(l));
    assertThat(l5, equalTo(Layout.createDefault()));
  }

  @Test
  public void test2() {
    Layout l = Layout.createDefault();

    l = l.randomize();
    assertThat(l, not(equalTo(Layout.createDefault())));
    assertThat(l.getNumberOfItems(), equalTo(Layout.createDefault().getNumberOfItems()));
  }

  @Test
  public void test3() {
    Layout l = new Layout();
    assertThat(l.getNumberOfItems(), equalTo(0));
    assertThat(l.getItems().size(), equalTo(0));

    Item item = new Item(ItemType.Triplet, new Cell(0, 0));
    for (int i = 0; i < Layout.MAX_ITEM_NUM; i++) {
      assertTrue(l.canAddMore());
      l = l.addItem(item);
      assertTrue(l.hasItem(item));
      assertThat(l.getNumberOfItems(), equalTo(i + 1));
    }

    assertFalse(l.canAddMore());
    try {
      l.addItem(item);
      fail();
    } catch (IllegalStateException e) {
    }

    assertThat(l, not(equalTo(Layout.createDefault())));
    List<Item> items = new ArrayList<>(l.getItems());
    for (Item i : items) {
      assertThat(i, not(sameInstance(item)));
      assertThat(i, equalTo(item));
      l = l.removeItem(item.createClone());
    }

    assertThat(l.getNumberOfItems(), equalTo(0));
    assertThat(l, equalTo(new Layout()));

    try {
      l.removeItem(item);
      fail();
    } catch (IllegalArgumentException e) {
    }
  }

  @Test
  public void test4() {
    Layout l = new Layout();
    assertThat(l.getNumberOfItems(), equalTo(0));
    assertThat(l.getItems().size(), equalTo(0));

    Item item = new Item(ItemType.Triplet, new Cell(3, 2));
    l = l.addItem(item);

    assertThat(l.getNumberOfItems(), equalTo(1));
    assertThat(l.getIndex(item), equalTo(0));

    assertThat(l.getItem(new Cell(3, 1)), equalTo(item));
    assertThat(l.getItem(new Cell(3, 2)), equalTo(item));
    assertThat(l.getItem(new Cell(3, 3)), equalTo(item));
    assertThat(l.getItem(new Cell(3, 4)), nullValue());
    assertThat(l.getItem(new Cell(3, 0)), nullValue());

    l = l.moveItem(item, new Cell(6, 8));

    assertThat(l.getIndex(item), nullValue());
    item = l.getItems().get(0);
    assertThat(l.getItem(new Cell(6, 6)), nullValue());
    assertThat(l.getItem(new Cell(6, 7)), equalTo(item));
    assertThat(l.getItem(new Cell(6, 8)), equalTo(item));
    assertThat(l.getItem(new Cell(6, 9)), equalTo(item));
    assertThat(l.getItem(new Cell(6, 10)), nullValue());

    l = l.removeItem(new Item(ItemType.Triplet, new Cell(6, 8)));
    assertThat(l.getNumberOfItems(), equalTo(0));

    item = new Item(ItemType.Sun, new Cell(-4, -7));
    l = l.addItem(item);

    for (int row = -5; row <= -3; row++) {
      for (int col = -8; col <= -6; col++) {
        assertThat(l.getItem(new Cell(row, col)), equalTo(item));
      }
    }
    assertThat(l.getItem(new Cell(-2, -2)), nullValue());
  }

  @Test
  public void test5() {
    Layout l = Layout.createDefault();

    for (Item item : l.getItems()) {
      l = l.moveItem(item, item.getCenter());
    }

    assertThat(l, equalTo(Layout.createDefault()));
  }

  @Test
  public void test6() {
    try {
      Layout.deserialize((String) null);
      fail();
    } catch (IllegalArgumentException ex) {
    }

    try {
      Layout.deserialize((Reader) null);
      fail();
    } catch (IllegalArgumentException ex) {
    }

    try {
      Layout.deserialize("");
      fail();
    } catch (IllegalArgumentException ex) {
    }

    try {
      Layout.deserialize("dummy");
      fail();
    } catch (IllegalArgumentException ex) {
    }

    try {
      Layout.deserialize("abcd");
      fail();
    } catch (IllegalArgumentException ex) {
    }
  }

  @Test
  public void test7() {
    Layout l = Layout.createDefault();
    String s = StringUtils.insertWhitespaces(l.serialize(), 1.0f, 3);
    Layout ls = Layout.deserialize(s);
    assertThat(ls, not(sameInstance(l)));
    assertThat(ls, equalTo(l));
    assertThat(ls.equals(l), is(true));
    assertThat(ls.hashCode(), equalTo(l.hashCode()));
  }

  @Test
  public void test8() {
    Layout l = Layout.createDefault();
    List<Item> defItems = Arrays.asList(Layout.getDefaultItems());
    assertThat(l.getItems(), equalTo(defItems));
  }

  @Test
  public void test9() {
    Layout l = Layout.createDefault();
    List<Item> defItems = Arrays.asList(Layout.getDefaultItems());
    assertThat(l.getItems(), equalTo(defItems));

    Item item = l.getItem(new Cell(20, 5));
    assertThat(item, notNullValue());
    assertThat(l.getIndex(item), equalTo(8));

    l = l.moveItem(item, new Cell(20, 6));
    assertThat(l.getItems(), not(equalTo(defItems)));

    item = l.getItem(new Cell(20, 6));
    assertThat(item, notNullValue());
    l = l.moveItem(item, new Cell(20, 5));
    assertThat(l.getItems(), equalTo(defItems));
  }
}
