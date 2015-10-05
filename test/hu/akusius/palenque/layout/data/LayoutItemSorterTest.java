package hu.akusius.palenque.layout.data;

import java.util.*;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Bujdosó Ákos
 */
public class LayoutItemSorterTest {

  public LayoutItemSorterTest() {
  }

  private static class EachItem implements Iterable<Item> {

    @Override
    public Iterator<Item> iterator() {
      return new IteratorImpl();
    }

    private static class IteratorImpl implements Iterator<Item> {

      private Item nextItem = null;

      {
        findNext();
      }

      private void findNext() {
        final Item item = nextItem;
        final Item nItem;

        ItemType t;
        Cell c;

        if (item == null) {
          t = null;
          c = nextCell(null);
        } else {
          t = item.getType();
          c = item.getCenter();
        }

        while (true) {
          t = nextType(t);
          if (t == null) {
            t = nextType(t);
            c = nextCell(c);
            if (c == null) {
              nItem = null;
              break;
            }
          }
          if (Item.isValid(t, c)) {
            nItem = new Item(t, c);
            break;
          }
        }
        this.nextItem = nItem;
      }

      private static Cell nextCell(Cell cell) {
        if (cell == null) {
          return new Cell(-Cell.RANGE, -Cell.RANGE);
        }

        int x = cell.getX();
        int y = cell.getY();

        x++;
        if (x > Cell.RANGE) {
          x = -Cell.RANGE;
          y++;
          if (y > Cell.RANGE) {
            return null;
          }
        }
        return new Cell(x, y);
      }

      private static ItemType nextType(ItemType type) {
        if (type == null) {
          return ItemType.Triplet;
        }
        switch (type) {
          case Triplet:
            return ItemType.Sun;
          case Sun:
            return ItemType.Star;
          case Star:
            return null;
          default:
            throw new AssertionError();
        }
      }

      @Override
      public boolean hasNext() {
        return nextItem != null;
      }

      @Override
      public Item next() {
        Item ni = nextItem;
        findNext();
        return ni;
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException("Not supported.");
      }
    }
  }

  /**
   * Minden elemkombináció letesztelése.
   */
  //@Test
  public void test1() {
    LayoutItemSorter sorter = new LayoutItemSorter();

    for (Item it1 : new EachItem()) {
      for (Item it2 : new EachItem()) {
        assertNotSame(it1, it2);
        assertTrue(it1.equals(it1));
        assertTrue(it2.equals(it2));
        assertTrue(sorter.compare(it1, it1) == 0);
        assertTrue(sorter.compare(it2, it2) == 0);

        if (it1.equals(it2)) {
          assertTrue(it2.equals(it1));
          assertTrue(sorter.compare(it1, it2) == 0);
          assertTrue(sorter.compare(it2, it1) == 0);
        } else {
          int c1 = sorter.compare(it1, it2);
          int c2 = sorter.compare(it2, it1);
          assertFalse(c1 == 0);
          assertTrue(c1 == -1 || c1 == +1);
          assertTrue(c1 == -c2);
        }
      }
    }
  }

  /**
   * Alapértelmezett elrendezés tesztelése.
   */
  @Test
  public void test2() {
    LayoutItemSorter sorter = new LayoutItemSorter();

    Item[] defItems = Layout.getDefaultItems();
    Item[] items = Layout.getDefaultItems();

    assertThat(items, not(sameInstance(defItems)));
    assertThat(items, equalTo(defItems));

    List<Item> itemList = Arrays.asList(items);
    Collections.reverse(itemList);
    assertThat(items, not(equalTo(defItems)));
    Arrays.sort(items, sorter);
    assertThat(items, equalTo(defItems));

    long seed = System.nanoTime();
    Random random = new Random(seed);

    for (int i = 0; i < 100; i++) {
      do {
        Collections.shuffle(itemList, random);
      } while (Arrays.equals(items, defItems));
      assertThat(items, not(equalTo(defItems)));
      Arrays.sort(items, sorter);
      assertThat(items, equalTo(defItems));
      Arrays.sort(items, sorter);
      assertThat(items, equalTo(defItems));
    }

    itemList = new ArrayList<>(itemList);
    List<Item> defList = Arrays.asList(defItems);

    for (int i = 0; i < defItems.length - 1; i++) {
      Item item = itemList.remove(i);
      assertThat(itemList, not(equalTo(defList)));
      itemList.add(item);
      assertThat(itemList, not(equalTo(defList)));
      Collections.sort(itemList, sorter);
      assertThat(itemList, equalTo(defList));
    }
  }

  private static Item getRandomItem(Random random) {
    while (true) {
      int x = random.nextInt(2 * Cell.RANGE + 1) - Cell.RANGE;
      int y = random.nextInt(2 * Cell.RANGE + 1) - Cell.RANGE;
      if (Cell.isValid(x, y)) {
        Cell c = new Cell(x, y);
        ItemType[] types = ItemType.values();
        ItemType type = types[random.nextInt(types.length)];
        if (Item.isValid(type, c)) {
          return new Item(type, c);
        }
      }
    }
  }

  /**
   * Véletlen elrendezés tesztelése.
   */
  @Test
  public void test3() {
    LayoutItemSorter sorter = new LayoutItemSorter();

    long seed = System.nanoTime();
    Random random = new Random(seed);

    int length = random.nextInt(20) + 5;
    Item[] refItems = new Item[length];

    for (int i = 0; i < refItems.length; i++) {
      Item item = getRandomItem(random);
      refItems[i] = item;
    }

    Arrays.sort(refItems, sorter);

    Item[] items = refItems.clone();

    assertThat(items, not(sameInstance(refItems)));
    assertThat(items, equalTo(refItems));

    List<Item> itemList = Arrays.asList(items);
    Collections.reverse(itemList);
    assertThat(items, not(equalTo(refItems)));
    Arrays.sort(items, sorter);
    assertThat(items, equalTo(refItems));

    for (int i = 0; i < 100; i++) {
      do {
        Collections.shuffle(itemList, random);
      } while (Arrays.equals(items, refItems));
      assertThat(items, not(equalTo(refItems)));
      Arrays.sort(items, sorter);
      assertThat(items, equalTo(refItems));
      Arrays.sort(items, sorter);
      assertThat(items, equalTo(refItems));
    }

    itemList = new ArrayList<>(itemList);
    List<Item> refList = Arrays.asList(refItems);

    for (int i = 0; i < refItems.length - 1; i++) {
      Item item = itemList.remove(i);
      assertThat(itemList, not(equalTo(refList)));
      itemList.add(item);
      assertThat(itemList, not(equalTo(refList)));
      Collections.sort(itemList, sorter);
      assertThat(itemList, equalTo(refList));
    }
  }
}
