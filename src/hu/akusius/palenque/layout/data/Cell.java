package hu.akusius.palenque.layout.data;

import hu.akusius.palenque.layout.util.SkipWhitespaceReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * Egy cella a koordinátarendszerben.
 * @author Bujdosó Ákos
 */
public class Cell {

  /**
   * A koordinátarendszer határa (+/- és X/Y)
   */
  public final static int RANGE = 25;

  private final int x;

  private final int y;

  /**
   * Visszaadja, hogy a megadott koordináták érvényesek-e (belefér-e a cella a koordinátarendszerbe).
   * @param x Az X koordináta.
   * @param y Az Y koordináta.
   * @return {@code true}, ha a koordináták érvényesek.
   */
  public static final boolean isValid(int x, int y) {
    return x >= -RANGE && x <= RANGE
            && y >= -RANGE && y <= RANGE;
  }

  /**
   * A cella létrehozása.
   * @param x Az X koordináta.
   * @param y Az Y koordináta.
   * @throws IllegalArgumentException A cella nem fér bele a koordinátarendszerbe.
   */
  public Cell(int x, int y) throws IllegalArgumentException {
    if (!isValid(x, y)) {
      throw new IllegalArgumentException();
    }
    this.x = x;
    this.y = y;
  }

  /**
   * @return A cella X koordinátája.
   */
  public int getX() {
    return x;
  }

  /**
   * @return A cella Y koordinátája.
   */
  public int getY() {
    return y;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 29 * hash + this.x;
    hash = 29 * hash + this.y;
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
    final Cell other = (Cell) obj;
    if (this.x != other.x) {
      return false;
    }
    return this.y == other.y;
  }

  /**
   * A cella klónozása.
   * @return A klónozott új cella.
   */
  public Cell createClone() {
    return new Cell(this.x, this.y);
  }

  private static final int SERIALIZE_LENGTH = 4;

  /**
   * @return A szerializált cella.
   */
  public String serialize() {
    assert RANGE < 50;
    return String.format("%02d%02d",
            this.x >= 0 ? this.x : 100 + this.x,
            this.y >= 0 ? this.y : 100 + this.y);
  }

  /**
   * @param data A korábban szerializált cella.
   * @return A deszerializált cella.
   * @throws IllegalArgumentException Érvénytelen adat lett megadva.
   */
  public static Cell deserialize(String data) throws IllegalArgumentException {
    if (data == null || data.length() == 0) {
      throw new IllegalArgumentException();
    }
    return deserialize(new StringReader(data));
  }

  /**
   * @param source A deszerializálás forrása.
   * @return A deszerializált cella.
   * @throws IllegalArgumentException Érvénytelen adatformátum.
   */
  public static Cell deserialize(Reader source) throws IllegalArgumentException {
    if (source == null) {
      throw new IllegalArgumentException();
    }
    if (!(source instanceof SkipWhitespaceReader)) {
      source = new SkipWhitespaceReader(source);
    }
    try {
      char[] buf = new char[SERIALIZE_LENGTH];
      int read = source.read(buf, 0, SERIALIZE_LENGTH);
      if (read < SERIALIZE_LENGTH) {
        throw new IllegalArgumentException();
      }
      int x = Integer.valueOf(new String(buf, 0, 2));
      x = x < 50 ? x : x - 100;
      int y = Integer.valueOf(new String(buf, 2, 2));
      y = y < 50 ? y : y - 100;
      return new Cell(x, y);
    } catch (IOException ex) {
      throw new IllegalArgumentException(ex);
    }
  }
}
