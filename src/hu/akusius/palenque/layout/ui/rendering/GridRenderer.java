package hu.akusius.palenque.layout.ui.rendering;

import hu.akusius.palenque.layout.data.Cell;
import hu.akusius.palenque.layout.data.CellRect;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 * A négyzetrács kirajzolását végző osztály.
 * @author Bujdosó Ákos
 */
public final class GridRenderer {

  private static final int STRONG_LINE_STEP = 5;

  private static final Color normalColor = new Color(170, 170, 170);

  private static final Color strongColor = new Color(100, 100, 100);

  private static List<Line> normalLines;

  private static List<Line> strongLines;

  /**
   * A négyzetrács kirajzolása.
   * @param graphics A kirajzolás célja.
   * @param dim A megjelenítés dimenziói.
   */
  public static void render(Graphics2D graphics, Dimension dim) {
    Graphics2D g = (Graphics2D) graphics.create();

    g.setColor(strongColor);

    Cell center = new Cell(0, 0);
    int[][] ps = Transformer.project(center, null, dim);
    int diff = (ps[1][0] - ps[0][0]) / 3;
    g.drawLine(ps[0][0] + diff, ps[0][1] + diff, ps[2][0] - diff, ps[2][1] - diff);
    g.drawLine(ps[2][0] - diff, ps[0][1] + diff, ps[0][0] + diff, ps[2][1] - diff);

    if (normalLines == null || strongLines == null) {
      createLines();
    }

    for (Line line : strongLines) {
      line.draw(g, dim);
    }

    g.setColor(normalColor);

    for (Line line : normalLines) {
      line.draw(g, dim);
    }
  }

  private static void createLines() {
    int num = Cell.RANGE * 2 + 1;
    normalLines = new ArrayList<>(num);
    strongLines = new ArrayList<>(num);

    for (int i = 0; i <= Cell.RANGE; i++) {
      addLine(new CellRect(new Cell(i, -Cell.RANGE), new Cell(i, Cell.RANGE)), 1, 2, i);
      addLine(new CellRect(new Cell(-Cell.RANGE, i), new Cell(Cell.RANGE, i)), 0, 1, i);
    }

    for (int i = 0; i >= -Cell.RANGE; i--) {
      addLine(new CellRect(new Cell(i, -Cell.RANGE), new Cell(i, Cell.RANGE)), 0, 3, i);
      addLine(new CellRect(new Cell(-Cell.RANGE, i), new Cell(Cell.RANGE, i)), 3, 2, i);
    }
  }

  private static void addLine(CellRect rect, int p1, int p2, int num) {
    double[][] ps = Transformer.getPoints(rect);
    Line line = new Line(ps[p1][0], ps[p1][1], ps[p2][0], ps[p2][1]);
    if (num % STRONG_LINE_STEP == 0) {
      strongLines.add(line);
    } else {
      normalLines.add(line);
    }
  }

  private static void drawRing(int size, Graphics2D g, Dimension dim) {
    double radius = Math.sqrt(size);
    int[][] ps = Transformer.project(new double[][]{
      {-radius - .5, +radius + .5, 0.0},
      {+radius + .5, -radius - .5, 0.0}
    }, dim);

    g.drawOval(ps[0][0], ps[0][1], ps[1][0] - ps[0][0], ps[1][1] - ps[0][1]);
  }

  private GridRenderer() {
  }

  private static final class Line {

    double x1, y1, x2, y2;

    Line() {
    }

    Line(double x1, double y1, double x2, double y2) {
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;
    }

    void draw(Graphics2D g, Dimension d) {
      int[][] ps = Transformer.project(new double[][]{{x1, y1, 0.0d}, {x2, y2, 0.0d}}, d);
      g.drawLine(ps[0][0], ps[0][1], ps[1][0], ps[1][1]);
    }
  }

}
