package hu.akusius.palenque.layout.ui;

import hu.akusius.palenque.layout.data.Cell;
import hu.akusius.palenque.layout.data.CellRect;
import hu.akusius.palenque.layout.ui.rendering.Transformer;
import java.awt.Dimension;
import java.awt.Rectangle;

/**
 * Egy cella koordináták alapján való megkeresését végző osztály.
 * @author Bujdosó Ákos
 */
final class CellFinder {

  private Dimension dim;

  private double lastFL;

  private Rectangle rect;

  private double cellWidth;

  private double cellHeight;

  /**
   * A cella megkeresése a megadott koordináták alapján.
   * @param x Az X koordináta.
   * @param y Az Y koordináta.
   * @param d A megjelenítés dimenziói.
   * @return A koordinátához tartozó cella, vagy {@code null}, ha nincsen ilyen.
   */
  Cell findCell(int x, int y, Dimension d) {
    if (!d.equals(dim) || lastFL != Transformer.getCurrentFL()) {
      recalculate(d);
    }

    if (!rect.contains(x, y)) {
      return null;
    }

    x -= rect.x;
    y -= rect.y;

    x = (int) (x / cellWidth) - Cell.RANGE;
    y = Cell.RANGE - (int) (y / cellHeight);

    return Cell.isValid(x, y) ? new Cell(x, y) : null;
  }

  private void recalculate(Dimension d) {
    this.dim = d;
    this.lastFL = Transformer.getCurrentFL();

    CellRect cr = new CellRect(new Cell(-Cell.RANGE, -Cell.RANGE), new Cell(Cell.RANGE, Cell.RANGE));
    int[][] ps = Transformer.project(cr, null, dim);
    this.rect = new Rectangle(ps[0][0], ps[0][1], ps[1][0] - ps[0][0] + 1, ps[3][1] - ps[0][1] + 1);

    double cellNum = Cell.RANGE * 2 + 1;
    this.cellWidth = (double) this.rect.width / cellNum;
    this.cellHeight = (double) this.rect.height / cellNum;
  }
}
