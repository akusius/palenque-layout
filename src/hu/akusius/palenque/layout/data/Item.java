package hu.akusius.palenque.layout.data;

import hu.akusius.palenque.layout.util.SkipWhitespaceReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Objects;

/**
 * Egy elem az elrendezésen belül.
 * @author Bujdosó Ákos
 */
public class Item {

  private final ItemType type;

  private final Cell center;

  private final CellRect rect;

  /**
   * Visszaadja, hogy a megadott elem érvényes-e (belefér-e a koordinátarendszerbe).
   * @param type Az elem típusa.
   * @param center Az elem középső cellája.
   * @return {@code true}, ha az elem érvényes.
   */
  public static final boolean isValid(ItemType type, Cell center) {
    if (type == null || center == null) {
      return false;
    }

    switch (type) {
      case Triplet:
        return Math.abs(center.getY()) < Cell.RANGE;
      case Sun:
      case Star:
        return Math.abs(center.getX()) < Cell.RANGE && Math.abs(center.getY()) < Cell.RANGE;
      default:
        throw new AssertionError();
    }
  }

  /**
   * Egy új elem létrehozása.
   * @param type Az elem típusa.
   * @param center Az elem középső cellája.
   * @throws IllegalArgumentException A megadott elem nem fér bele a koordinátarendszerbe.
   */
  public Item(ItemType type, Cell center) throws IllegalArgumentException {
    if (!isValid(type, center)) {
      throw new IllegalArgumentException();
    }
    this.type = type;
    this.center = center;

    if (type == ItemType.Triplet) {
      this.rect = new CellRect(new Cell(center.getX(), center.getY() - 1), new Cell(center.getX(), center.getY() + 1));
    } else {
      this.rect = new CellRect(new Cell(center.getX() - 1, center.getY() - 1), new Cell(center.getX() + 1, center.getY() + 1));
    }
  }

  /**
   * @return Az elem típusa.
   */
  public ItemType getType() {
    return type;
  }

  /**
   * @return Az elem középső cellája.
   */
  public Cell getCenter() {
    return center;
  }

  /**
   * @return Az elemet tartalmazó téglalap.
   */
  public CellRect getRect() {
    return rect;
  }

  /**
   * Új elem létrehozása az aktuális elem referenciacellához viszonyított elmozgatásával.
   * @param reference A referenciacella, amihez képest történik az elem mozgatása. Nem szükséges az elemen belül lennie.
   * @param target A referenciacella új helye.
   * @return Az új, elmozgatott elem, vagy {@code null}, ha nem érvényes az elmozgatás.
   */
  public Item moveByReference(Cell reference, Cell target) {
    int newX = center.getX() + target.getX() - reference.getX();
    int newY = center.getY() + target.getY() - reference.getY();
    if (!Cell.isValid(newX, newY)) {
      return null;
    }
    Cell newCenter = new Cell(newX, newY);
    return isValid(type, newCenter) ? new Item(type, newCenter) : null;
  }

  /**
   * @return A szerializált elem.
   */
  public String serialize() {
    return type.getShortName() + this.center.serialize();
  }

  /**
   * @param data A korábban szerializált elem.
   * @return A deszerializált elem.
   * @throws IllegalArgumentException Érvénytelen adatformátum.
   */
  public static Item deserialize(String data) throws IllegalArgumentException {
    if (data == null || data.length() == 0) {
      throw new IllegalArgumentException();
    }
    return deserialize(new StringReader(data));
  }

  /**
   * @param source A deszerializálás forrása.
   * @return A deszerializált elem.
   * @throws IllegalArgumentException Érvénytelen adatformátum.
   */
  public static Item deserialize(Reader source) throws IllegalArgumentException {
    if (source == null) {
      throw new IllegalArgumentException();
    }
    if (!(source instanceof SkipWhitespaceReader)) {
      source = new SkipWhitespaceReader(source);
    }
    final ItemType type;
    try {
      int val = source.read();
      if (val < 0) {
        throw new IllegalArgumentException();
      }
      type = ItemType.fromChar((char) val);
    } catch (IOException ex) {
      throw new IllegalArgumentException(ex);
    }
    final Cell center = Cell.deserialize(source);
    return new Item(type, center);
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 37 * hash + Objects.hashCode(this.type);
    hash = 37 * hash + Objects.hashCode(this.center);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Item other = (Item) obj;
    if (this.type != other.type) {
      return false;
    }
    return Objects.equals(this.center, other.center);
  }

  /**
   * Az elem klónozása.
   * @return A klónozott új elem.
   */
  public Item createClone() {
    return new Item(this.type, this.center.createClone());
  }
}
