package hu.akusius.palenque.layout.data;

import hu.akusius.palenque.layout.util.SkipWhitespaceReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;

/**
 * Egy elrendezést kezelő osztály.
 * Az elemeket definiált sorrendben tárolja, a változtatásokhoz új elrendezést hoz létre (immutable).
 * @author Bujdosó Ákos
 */
public class Layout {

  /**
   * Az elrendezésen belül az elemek maximális száma.
   */
  public final static int MAX_ITEM_NUM = 25;

  private final List<Item> items;

  /**
   * @return Az alapértelmezett elrendezés elemei, a megfelelő sorrendben.
   */
  static Item[] getDefaultItems() {
    return new Item[]{
      new Item(ItemType.Triplet, new Cell(-5, 6)),
      new Item(ItemType.Triplet, new Cell(-5, -4)),
      new Item(ItemType.Triplet, new Cell(4, -4)),
      new Item(ItemType.Triplet, new Cell(-10, 1)),
      new Item(ItemType.Triplet, new Cell(9, 1)),
      new Item(ItemType.Triplet, new Cell(-16, 17)),
      new Item(ItemType.Triplet, new Cell(6, 20)),
      new Item(ItemType.Triplet, new Cell(20, 20)),
      new Item(ItemType.Triplet, new Cell(20, 5)),
      new Item(ItemType.Triplet, new Cell(-16, -23)),
      new Item(ItemType.Sun, new Cell(-11, -20)),
      new Item(ItemType.Star, new Cell(20, -2))
    };
  }

  /**
   * @return Az alapértelmezett elrendezés.
   */
  public static Layout createDefault() {
    return new Layout(Arrays.asList(getDefaultItems()));
  }

  /**
   * Üres elrendezés létrehozása.
   */
  public Layout() {
    this(null);
  }

  /**
   * Elrendezés létrehozása a megadott elemekből.
   * @param items Az elrendezés elemei.
   */
  public Layout(Iterable<Item> items) {
    List<Item> list = new ArrayList<>(MAX_ITEM_NUM);
    if (items != null) {
      for (Item item : items) {
        list.add(item.createClone());
      }
      Collections.sort(list, new LayoutItemSorter());
    }
    this.items = Collections.unmodifiableList(list);
  }

  /**
   * @return Az elrendezés elemeinek listája egyértelműen definiált sorrendben. A lista nem módosítható.
   */
  @SuppressWarnings("ReturnOfCollectionOrArrayField")
  public List<Item> getItems() {
    return this.items;
  }

  /**
   * Visszaadja, hogy létezik-e az adott elem az elrendezésen belül.
   * @param item A vizsgálandó elem.
   * @return {@code true}, ha létezik.
   */
  public boolean hasItem(Item item) {
    return this.items.contains(item);
  }

  /**
   * Visszaadja a megadott elemhez tartozó indexet (sorszámot).
   * @param item A vizsgálandó elem.
   * @return Az elemhez tartozó index, vagy {@code null}, ha nem létezik.
   */
  public Integer getIndex(Item item) {
    int index = this.items.indexOf(item);
    return index == -1 ? null : index;
  }

  /**
   * Adott cellához az elem megkeresése (hit test).
   * Figyelembe veszi az elemek méretét is.
   * @param cell A vizsgált cella.
   * @return A cellához tartozó első elem, vagy {@code null}, ha nincs ilyen.
   */
  public Item getItem(Cell cell) {
    if (cell == null) {
      throw new IllegalArgumentException();
    }
    for (Item item : items) {
      if (item.getRect().contains(cell)) {
        return item;
      }
    }
    return null;
  }

  /**
   * @return Az elrendezés elemeinek a száma.
   */
  public int getNumberOfItems() {
    return this.items.size();
  }

  /**
   * @return {@code true}, ha lehet további elemeket adni az elrendezéshez.
   * @see #MAX_ITEM_NUM
   */
  public boolean canAddMore() {
    return this.items.size() < MAX_ITEM_NUM;
  }

  /**
   * Új elem hozzáadása az elrendezéshez.
   * @param item A hozzáadandó elem.
   * @return Az új elrendezés a hozzáadott elemmel.
   * @throws IllegalStateException Nem adható több elem az elrendezéshez.
   */
  public Layout addItem(Item item) throws IllegalStateException {
    if (item == null) {
      throw new IllegalArgumentException();
    }
    if (!canAddMore()) {
      throw new IllegalStateException();
    }
    List<Item> newItems = new ArrayList<>(this.items);
    newItems.add(item);
    return new Layout(newItems);
  }

  /**
   * A megadott elem eltávolítása az elrendezésből.
   * @param item Az eltávolítandó elem. Ha többször is szerepel, akkor csak egyszer távolítja el.
   * @return Az új elrendezés az eltávolítás után.
   * @throws IllegalArgumentException Az elem nem szerepel az elrendezésben.
   */
  public Layout removeItem(Item item) throws IllegalArgumentException {
    if (item == null) {
      throw new IllegalArgumentException();
    }
    if (!this.items.contains(item)) {
      throw new IllegalArgumentException();
    }
    List<Item> newItems = new ArrayList<>(this.items);
    newItems.remove(item);
    return new Layout(newItems);
  }

  /**
   * A megadott elem elmozgatása az elrendezésen belül.
   * @param item A mozgatandó elem.
   * @param newCenter Az új középcella.
   * @return Az új elrendezés a mozgatást követően, vagy a jelenlegi, ha nincs szükség mozgatásra.
   * @throws IllegalArgumentException Az elem nem szerepel az elrendezésben, vagy érvénytelen az új elem.
   */
  public Layout moveItem(Item item, Cell newCenter) throws IllegalArgumentException {
    if (item == null || !this.items.contains(item)
            || !Item.isValid(item.getType(), newCenter)) {
      throw new IllegalArgumentException();
    }
    if (item.getCenter().equals(newCenter)) {
      // Nincs szükség mozgatásra
      return this;
    }
    List<Item> newItems = new ArrayList<>(this.items);
    newItems.remove(item);
    newItems.add(new Item(item.getType(), newCenter));
    return new Layout(newItems);
  }

  /**
   * Az elemek elmozgatása véletlenszerűen.
   * @return Az új elrendezés az elmozgatásokat követően.
   */
  public Layout randomize() {
    Random rnd = new Random();
    List<Item> newItems = new ArrayList<>(items.size());
    for (Item item : this.items) {
      int x = item.getCenter().getX();
      int y = item.getCenter().getY();
      x += rnd.nextInt(3) - 1;
      y += rnd.nextInt(3) - 1;
      x = Math.max(Math.min(x, Cell.RANGE), -Cell.RANGE);
      y = Math.max(Math.min(y, Cell.RANGE), -Cell.RANGE);
      Cell newCenter = new Cell(x, y);
      if (Item.isValid(item.getType(), newCenter)) {
        newItems.add(new Item(item.getType(), newCenter));
      } else {
        newItems.add(item);
      }
    }
    return new Layout(newItems);
  }

  /**
   * Az elrendezés klónozása.
   * @return A klónozott új elrendezés.
   */
  public Layout createClone() {
    return new Layout(this.items);
  }

  @Override
  public int hashCode() {
    return this.items.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Layout other = (Layout) obj;
    return Objects.equals(this.items, other.items);
  }

  private static final String SERIALIZE_MAGIC = "PQL";

  private String _cachedSerialization;

  /**
   * @return Az elrendezés szerializált formában.
   */
  public String serialize() {
    if (_cachedSerialization == null) {
      StringBuilder sb = new StringBuilder(200);
      sb.append(SERIALIZE_MAGIC);
      for (Item item : items) {
        sb.append(item.serialize());
      }
      _cachedSerialization = sb.toString();
    }
    return _cachedSerialization;
  }

  /**
   * @param data A szerializált elrendezés.
   * @return A deszerializált elrendezés.
   */
  public static Layout deserialize(String data) {
    if (data == null || data.length() == 0) {
      throw new IllegalArgumentException();
    }
    return deserialize(new StringReader(data));
  }

  /**
   * @param source A deszerializálás forrása.
   * @return A deszerializált elrendezés.
   * @throws IllegalArgumentException Érvénytelen adatformátum.
   */
  public static Layout deserialize(Reader source) throws IllegalArgumentException {
    if (source == null) {
      throw new IllegalArgumentException();
    }
    if (!(source instanceof SkipWhitespaceReader)) {
      source = new SkipWhitespaceReader(source);
    }
    try {
      for (int i = 0; i < SERIALIZE_MAGIC.length(); i++) {
        int ch = source.read();
        if (ch == -1 || ch != SERIALIZE_MAGIC.charAt(i)) {
          throw new IllegalArgumentException();
        }
      }
      List<Item> items = new ArrayList<>(MAX_ITEM_NUM);
      PushbackReader pbr = new PushbackReader(source);
      while (true) {
        int ch = pbr.read();
        if (ch == -1) {
          break;
        }
        pbr.unread(ch);
        Item item = Item.deserialize(pbr);
        items.add(item);
      }
      return new Layout(items);
    } catch (IOException ex) {
      throw new IllegalArgumentException(ex);
    }
  }

}
