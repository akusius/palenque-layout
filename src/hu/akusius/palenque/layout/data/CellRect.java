package hu.akusius.palenque.layout.data;

import java.util.Objects;

/**
 * Egy cellákból álló téglalap a koordinátarendszerben.
 * @author Bujdosó Ákos
 */
public class CellRect {

  private final Cell c1;

  private final Cell c2;

  private final Cell leftBottom;

  private final Cell rightTop;

  private final int height;

  private final int width;

  /**
   * Új téglalap létrehozása.
   * @param c1 Az egyik sarokcella.
   * @param c2 A másik sarokcella.
   */
  public CellRect(Cell c1, Cell c2) {
    if (c1 == null || c2 == null) {
      throw new IllegalArgumentException();
    }
    this.c1 = c1;
    this.c2 = c2;

    this.leftBottom = new Cell(Math.min(c1.getX(), c2.getX()), Math.min(c1.getY(), c2.getY()));
    this.rightTop = new Cell(Math.max(c1.getX(), c2.getX()), Math.max(c1.getY(), c2.getY()));

    this.width = this.rightTop.getX() - this.leftBottom.getX() + 1;
    this.height = this.rightTop.getY() - this.leftBottom.getY() + 1;
  }

  /**
   * @return A megadott egyik sarokcella.
   */
  public Cell getC1() {
    return c1;
  }

  /**
   * @return A megadott másik sarokcella.
   */
  public Cell getC2() {
    return c2;
  }

  /**
   * @return A téglalap szélessége.
   */
  public int getWidth() {
    return width;
  }

  /**
   * @return A téglalap magassága.
   */
  public int getHeight() {
    return height;
  }

  /**
   * @return A bal alsó cella.
   */
  public Cell getLeftBottom() {
    return leftBottom;
  }

  /**
   * @return A jobb felső cella.
   */
  public Cell getRightTop() {
    return rightTop;
  }

  /**
   * Visszaadja, hogy a megadott cella a téglalapon belül található-e.
   * @param c A vizsgálandó cella.
   * @return {@code true}, ha belül található.
   */
  public boolean contains(Cell c) {
    return c.getX() >= leftBottom.getX() && c.getX() <= rightTop.getX()
            && c.getY() >= leftBottom.getY() && c.getY() <= rightTop.getY();
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 59 * hash + Objects.hashCode(this.leftBottom);
    hash = 59 * hash + Objects.hashCode(this.rightTop);
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
    final CellRect other = (CellRect) obj;
    if (!Objects.equals(this.leftBottom, other.leftBottom)) {
      return false;
    }
    return Objects.equals(this.rightTop, other.rightTop);
  }

  /**
   * A téglalap klónozása.
   * @return A klónozott új téglalap.
   */
  public CellRect createClone() {
    return new CellRect(this.c1.createClone(), this.c2.createClone());
  }

}
