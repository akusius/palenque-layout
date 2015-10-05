package hu.akusius.palenque.layout.data;

import java.io.Reader;
import java.io.StringReader;
import org.junit.Test;
import util.StringUtils;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Bujdosó Ákos
 */
public class ItemTest {

  public ItemTest() {
  }

  @Test
  public void test1() {
    assertThat(Item.isValid(ItemType.Triplet, new Cell(0, 0)), is(true));
    assertThat(Item.isValid(ItemType.Sun, new Cell(0, 0)), is(true));
    assertThat(Item.isValid(ItemType.Star, new Cell(0, 0)), is(true));
    assertThat(Item.isValid(null, null), is(false));

    assertThat(Item.isValid(ItemType.Triplet, new Cell(0, Cell.RANGE)), is(false));
    assertThat(Item.isValid(ItemType.Triplet, new Cell(0, -Cell.RANGE)), is(false));
    assertThat(Item.isValid(ItemType.Triplet, new Cell(Cell.RANGE, Cell.RANGE - 1)), is(true));
    assertThat(Item.isValid(ItemType.Triplet, new Cell(Cell.RANGE, -Cell.RANGE + 1)), is(true));

    assertThat(Item.isValid(ItemType.Sun, new Cell(0, Cell.RANGE)), is(false));
    assertThat(Item.isValid(ItemType.Sun, new Cell(0, -Cell.RANGE)), is(false));
    assertThat(Item.isValid(ItemType.Sun, new Cell(Cell.RANGE - 1, -Cell.RANGE + 1)), is(true));
    assertThat(Item.isValid(ItemType.Sun, new Cell(-Cell.RANGE + 1, Cell.RANGE - 1)), is(true));

    assertThat(Item.isValid(ItemType.Star, new Cell(0, Cell.RANGE)), is(false));
    assertThat(Item.isValid(ItemType.Star, new Cell(0, -Cell.RANGE)), is(false));
    assertThat(Item.isValid(ItemType.Star, new Cell(Cell.RANGE - 1, -Cell.RANGE + 1)), is(true));
    assertThat(Item.isValid(ItemType.Star, new Cell(-Cell.RANGE + 1, Cell.RANGE - 1)), is(true));
  }

  private static Cell rectLeftTop(CellRect cr) {
    return new Cell(cr.getLeftBottom().getX(), cr.getRightTop().getY());
  }

  private static Cell rectRightBottom(CellRect cr) {
    return new Cell(cr.getRightTop().getX(), cr.getLeftBottom().getY());
  }

  private static CellRect getCoordinateSystem() {
    return new CellRect(new Cell(-Cell.RANGE, -Cell.RANGE), new Cell(Cell.RANGE, Cell.RANGE));
  }

  @Test
  public void test2() {
    Item item = new Item(ItemType.Triplet, new Cell(0, 0));
    assertThat(item.getType(), equalTo(ItemType.Triplet));
    assertThat(item.getCenter(), equalTo(new Cell(0, 0)));
    assertThat(item.getRect(), equalTo(new CellRect(new Cell(0, -1), new Cell(0, 1))));
    assertThat(item.getRect().getHeight(), equalTo(3));
    assertThat(item.getRect().getWidth(), equalTo(1));

    assertThat(item, equalTo(Item.deserialize(item.serialize())));
    assertThat(item.equals(Item.deserialize(new StringReader(item.serialize()))), is(true));

    Item clone = item.createClone();
    assertThat(clone, not(sameInstance(item)));
    assertThat(clone, equalTo(item));
    assertThat(clone.equals(item), is(true));
    assertThat(clone.getCenter(), not(sameInstance(item.getCenter())));

    item = item.moveByReference(new Cell(5, 3), new Cell(-2, -7));
    assertThat(item.getType(), equalTo(ItemType.Triplet));
    assertThat(item.getCenter(), equalTo(new Cell(-7, -10)));
    assertThat(item.getRect(), equalTo(new CellRect(new Cell(-7, -9), new Cell(-7, -11))));
    assertThat(item.getRect().getHeight(), equalTo(3));
    assertThat(item.getRect().getWidth(), equalTo(1));

    assertThat(item.moveByReference(new Cell(0, 0), new Cell(-Cell.RANGE, 0)), is(nullValue()));

    CellRect cs = getCoordinateSystem();
    item = new Item(ItemType.Triplet, new Cell(-Cell.RANGE, -Cell.RANGE + 1));
    assertThat(item.getRect().getLeftBottom(), equalTo(cs.getLeftBottom()));
    item = item.moveByReference(item.getCenter(), new Cell(-Cell.RANGE, Cell.RANGE - 1));
    assertThat(item, notNullValue());
    assertThat(rectLeftTop(item.getRect()), equalTo(rectLeftTop(cs)));
    item = item.moveByReference(item.getCenter(), new Cell(Cell.RANGE, Cell.RANGE - 1));
    assertThat(item, notNullValue());
    assertThat(item.getRect().getRightTop(), equalTo(cs.getRightTop()));
    item = item.moveByReference(item.getCenter(), new Cell(Cell.RANGE, -Cell.RANGE + 1));
    assertThat(item, notNullValue());
    assertThat(rectRightBottom(item.getRect()), equalTo(rectRightBottom(cs)));
  }

  @Test
  public void test3() {
    Item item = new Item(ItemType.Sun, new Cell(8, 5));
    assertThat(item.getType(), equalTo(ItemType.Sun));
    assertThat(item.getCenter(), equalTo(new Cell(8, 5)));
    assertThat(item.getRect(), equalTo(new CellRect(new Cell(9, 6), new Cell(7, 4))));
    assertThat(item.getRect().getHeight(), equalTo(3));
    assertThat(item.getRect().getWidth(), equalTo(3));

    assertThat(item, equalTo(Item.deserialize(item.serialize())));
    assertThat(item.equals(Item.deserialize(new StringReader(item.serialize()))), is(true));

    Item clone = item.createClone();
    assertThat(clone, not(sameInstance(item)));
    assertThat(clone, equalTo(item));
    assertThat(clone.equals(item), is(true));
    assertThat(clone.getCenter(), not(sameInstance(item.getCenter())));

    item = item.moveByReference(new Cell(9, 5), new Cell(1, 0));
    assertThat(item.getType(), equalTo(ItemType.Sun));
    assertThat(item.getCenter(), equalTo(new Cell(0, 0)));
    assertThat(item.getRect(), equalTo(new CellRect(new Cell(-1, -1), new Cell(1, 1))));
    assertThat(item.getRect().getHeight(), equalTo(3));
    assertThat(item.getRect().getWidth(), equalTo(3));

    assertThat(item.moveByReference(new Cell(10, 10), new Cell(-Cell.RANGE, 0)), is(nullValue()));

    CellRect cs = getCoordinateSystem();
    item = new Item(ItemType.Sun, new Cell(-Cell.RANGE + 1, -Cell.RANGE + 1));
    assertThat(item.getRect().getLeftBottom(), equalTo(cs.getLeftBottom()));
    item = item.moveByReference(item.getCenter(), new Cell(-Cell.RANGE + 1, Cell.RANGE - 1));
    assertThat(item, notNullValue());
    assertThat(rectLeftTop(item.getRect()), equalTo(rectLeftTop(cs)));
    item = item.moveByReference(item.getCenter(), new Cell(Cell.RANGE - 1, Cell.RANGE - 1));
    assertThat(item, notNullValue());
    assertThat(item.getRect().getRightTop(), equalTo(cs.getRightTop()));
    item = item.moveByReference(item.getCenter(), new Cell(Cell.RANGE - 1, -Cell.RANGE + 1));
    assertThat(item, notNullValue());
    assertThat(rectRightBottom(item.getRect()), equalTo(rectRightBottom(cs)));
  }

  @Test
  public void test4() {
    Item item = new Item(ItemType.Star, new Cell(-6, -14));
    assertThat(item.getType(), equalTo(ItemType.Star));
    assertThat(item.getCenter(), equalTo(new Cell(-6, -14)));
    assertThat(item.getRect(), equalTo(new CellRect(new Cell(-5, -13), new Cell(-7, -15))));
    assertThat(item.getRect().getHeight(), equalTo(3));
    assertThat(item.getRect().getWidth(), equalTo(3));

    assertThat(item, equalTo(Item.deserialize(item.serialize())));
    assertThat(item.equals(Item.deserialize(new StringReader(item.serialize()))), is(true));

    Item clone = item.createClone();
    assertThat(clone, not(sameInstance(item)));
    assertThat(clone, equalTo(item));
    assertThat(clone.equals(item), is(true));
    assertThat(clone.getCenter(), not(sameInstance(item.getCenter())));

    item = item.moveByReference(new Cell(2, 3), new Cell(8, 17));
    assertThat(item.getType(), equalTo(ItemType.Star));
    assertThat(item.getCenter(), equalTo(new Cell(0, 0)));
    assertThat(item.getRect(), equalTo(new CellRect(new Cell(-1, -1), new Cell(1, 1))));
    assertThat(item.getRect().getHeight(), equalTo(3));
    assertThat(item.getRect().getWidth(), equalTo(3));

    assertThat(item.moveByReference(new Cell(10, 10), new Cell(-Cell.RANGE, 0)), is(nullValue()));

    CellRect cs = getCoordinateSystem();
    item = new Item(ItemType.Star, new Cell(-Cell.RANGE + 1, -Cell.RANGE + 1));
    assertThat(item.getRect().getLeftBottom(), equalTo(cs.getLeftBottom()));
    item = item.moveByReference(item.getCenter(), new Cell(-Cell.RANGE + 1, Cell.RANGE - 1));
    assertThat(item, notNullValue());
    assertThat(rectLeftTop(item.getRect()), equalTo(rectLeftTop(cs)));
    item = item.moveByReference(item.getCenter(), new Cell(Cell.RANGE - 1, Cell.RANGE - 1));
    assertThat(item, notNullValue());
    assertThat(item.getRect().getRightTop(), equalTo(cs.getRightTop()));
    item = item.moveByReference(item.getCenter(), new Cell(Cell.RANGE - 1, -Cell.RANGE + 1));
    assertThat(item, notNullValue());
    assertThat(rectRightBottom(item.getRect()), equalTo(rectRightBottom(cs)));
  }

  @Test
  public void test5() {
    try {
      Item.deserialize((String) null);
      fail();
    } catch (IllegalArgumentException ex) {
    }

    try {
      Item.deserialize((Reader) null);
      fail();
    } catch (IllegalArgumentException ex) {
    }

    try {
      Item.deserialize("");
      fail();
    } catch (IllegalArgumentException ex) {
    }

    try {
      Item.deserialize("dummy");
      fail();
    } catch (IllegalArgumentException ex) {
    }

    try {
      Item.deserialize("abcd");
      fail();
    } catch (IllegalArgumentException ex) {
    }
  }

  @Test
  public void test6() {
    Item i = new Item(ItemType.Sun, new Cell(Cell.RANGE - 1, -Cell.RANGE + 1));
    String s = StringUtils.insertWhitespaces(i.serialize(), 1.0f, 3);
    Item is = Item.deserialize(s);
    assertThat(is, not(sameInstance(i)));
    assertThat(is, equalTo(i));
    assertThat(is.equals(i), is(true));
    assertThat(is.hashCode(), equalTo(i.hashCode()));
  }
}
