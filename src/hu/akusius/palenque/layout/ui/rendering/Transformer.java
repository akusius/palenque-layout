package hu.akusius.palenque.layout.ui.rendering;

import hu.akusius.palenque.layout.data.Cell;
import hu.akusius.palenque.layout.data.CellRect;
import java.awt.Dimension;
import org.other.Matrix;

/**
 * Osztály a transzformációk és a perspektivikus projekció kezeléséhez.
 * @author Bujdosó Ákos
 */
public class Transformer {

  /**
   * Focal length of view
   * Minden 60-nal (59 + 1) osztható méret esetén egyenlő nagyságúak a négyzetek.
   */
  public static final double defaultFL = 59;

  private static double FL = defaultFL;

  /**
   * Az FL igazítása, hogy egyenlő nagyságúak legyenek a négyzetek.
   * @param dim Az új méret. {@code null} esetén visszaállítja az eredeti FL-t.
   */
  public static void adjustFL(Dimension dim) {
    if (dim == null) {
      FL = defaultFL;
      return;
    }
    int size = Math.min(dim.width, dim.height);
    double fact = Math.round(size / (defaultFL + 1.0));
    FL = size / fact - 1.0;
  }

  public static double getCurrentFL() {
    return FL;
  }

  /**
   * A megadott cellát ábrázoló téglalap (négyzet) pontjai BF, JF, JA, BA sorrendben.
   * @param cell A cella.
   * @return A cellát ábrázoló téglalap pontjainak tömbje BF, JF, JA, BA sorrendben.
   */
  public static final double[][] getPoints(Cell cell) {
    double l = cell.getX() - .5;
    double r = cell.getX() + .5;
    double t = cell.getY() + .5;
    double b = cell.getY() - .5;
    return new double[][]{{l, t, 0.0d}, {r, t, 0.0d}, {r, b, 0.0d}, {l, b, 0.0d}};
  }

  /**
   * A megadott cellát ábrázoló téglalap (négyzet) pontjai BF, JF, JA, BA sorrendben.
   * @param x A cella X koordinátája.
   * @param y A cella Y koordinátája.
   * @return A cellát ábrázoló téglalap pontjainak tömbje BF, JF, JA, BA sorrendben.
   */
  public static double[][] getPoints(int x, int y) {
    double l = x - .5;
    double r = x + .5;
    double t = y + .5;
    double b = y - .5;
    return new double[][]{{l, t, 0.0d}, {r, t, 0.0d}, {r, b, 0.0d}, {l, b, 0.0d}};
  }

  /**
   * A megadott cellatéglalapot ábrázoló téglalap pontjai BF, JF, JA, BA sorrendben.
   * @param cellRect A cellatéglalap.
   * @return A cellatéglalapot ábrázoló téglalap pontjainak tömbje BF, JF, JA, BA sorrendben.
   */
  public static final double[][] getPoints(CellRect cellRect) {
    Cell lb = cellRect.getLeftBottom();
    Cell rt = cellRect.getRightTop();
    double l = lb.getX() - .5;
    double r = rt.getX() + .5;
    double t = rt.getY() + .5;
    double b = lb.getY() - .5;
    return new double[][]{{l, t, 0.0d}, {r, t, 0.0d}, {r, b, 0.0d}, {l, b, 0.0d}};
  }

  /**
   * A megadott cellatéglalapot ábrázoló téglalap pontjai BF, JF, JA, BA sorrendben.
   * @param x1 A cella egyik sarkának X koordinátája.
   * @param y1 A cella egyik sarkának Y koordinátája.
   * @param x2 A cella másik sarkának X koordinátája.
   * @param y2 A cella másik sarkának Y koordinátája.
   * @return A cellatéglalapot ábrázoló téglalap pontjainak tömbje BF, JF, JA, BA sorrendben.
   */
  public static double[][] getPoints(int x1, int y1, int x2, int y2) {
    double l = Math.min(x1, x2) - .5;
    double r = Math.max(x1, x2) + .5;
    double t = Math.max(y1, y2) + .5;
    double b = Math.min(y1, y2) - .5;
    return new double[][]{{l, t, 0.0d}, {r, t, 0.0d}, {r, b, 0.0d}, {l, b, 0.0d}};
  }

  /**
   * A megadott pont transzformálása 3D-ban.
   * @param p A transzformálandó pont.
   * @param transMatrix A transzformációs mátrix.
   * @return A transzformált pont.
   */
  public static final double[] transform(double[] p, Matrix transMatrix) {
    assert p.length == 3;
    double[] v = new double[3];
    transMatrix.transformPoint(p[0], p[1], p[2], v);
    return v;
  }

  /**
   * A megadott pontok transzformálása 3D-ban.
   * @param points A transzformálandó pontok koordinátáinak tömbje.
   * @param transMatrix A transzformációs mátrix.
   * @return A transzformált pontok koordinátáinak tömbje.
   */
  public static final double[][] transform(double[][] points, Matrix transMatrix) {
    double[][] r = new double[points.length][3];
    for (int i = 0; i < points.length; i++) {
      double[] p = points[i];
      assert p.length == 3;
      transMatrix.transformPoint(p[0], p[1], p[2], r[i]);
    }
    return r;
  }

  /**
   * A megadott cella transzformálása 3D-ban.
   * @param cell A projektálandó cella.
   * @param transMatrix A transzformációs mátrix.
   * @return A cellát alkotó 4 pont tömbje (BF, JF, JA, BA sorrendben).
   */
  public static final double[][] transform(Cell cell, Matrix transMatrix) {
    return transform(getPoints(cell), transMatrix);
  }

  /**
   * A megadott cellatéglalap transzformálása 3D-ban.
   * @param cellRect A projektálandó cellatéglalap.
   * @param transMatrix A transzformációs mátrix.
   * @return A cellát alkotó 4 pont tömbje (BF, JF, JA, BA sorrendben).
   */
  public static final double[][] transform(CellRect cellRect, Matrix transMatrix) {
    return transform(getPoints(cellRect), transMatrix);
  }

  /**
   * A megadott pont (vektor) perspektivikus projekciója 3D-ban.
   * @param point A projektálandó pont (vektor).
   * @param dim A megjelenítés dimenziói.
   * @return A perspektivikus projekció eredménye, vagy {@code null}, ha nem képezhető le az adott pont.
   */
  public static final int[] project(double[] point, Dimension dim) {
    assert point.length == 3;

    double near = FL;
    if (point[2] > near) {
      return null;
    }
    double fact = 1.0 / (near - point[2] + 1.0);  // kamera 1.0 távolságra van near-től
    fact *= Math.min(dim.width, dim.height);

    double x = dim.width / 2.0 + fact * point[0];
    double y = dim.height / 2.0 - fact * point[1];
    return new int[]{(int) (x + .5), (int) (y + .5)};
  }

  /**
   * A megadott pontok perspektivikus projekciója 3D-ban.
   * @param points A projektálandó pontok.
   * @param dim A megjelenítés dimenziói.
   * @return A perspektivikus projekció eredménye, vagy {@code null}, ha nem képezhető le valamelyik pont.
   */
  public static final int[][] project(double[][] points, Dimension dim) {
    int[][] r = new int[points.length][2];
    for (int i = 0; i < points.length; i++) {
      double[] p = points[i];
      assert p.length == 3;
      int[] c = project(p, dim);
      if (c == null) {
        return null;
      }
      r[i][0] = c[0];
      r[i][1] = c[1];
    }
    return r;
  }

  /**
   * A megadott cella transzformálása és projektálása 3D-ban.
   * @param cell A cella.
   * @param transMatrix A transzformációs mátrix (vagy {@code null}, ha nincs szükség transzformálásra).
   * @param dim A megjelenítés dimenziói.
   * @return A cellát alkotó 4 pont tömbje (BF, JF, JA, BA sorrendben) vagy {@code null}, ha a cella nem (teljesen) látható.
   */
  public static final int[][] project(Cell cell, Matrix transMatrix, Dimension dim) {
    double[][] points = getPoints(cell);
    if (transMatrix != null) {
      points = transform(points, transMatrix);
    }
    return project(points, dim);
  }

  /**
   * A megadott cella transzformálása és projektálása.
   * @param x A cella X koordinátája.
   * @param y A cella Y koordinátája.
   * @param transMatrix A transzformációs mátrix (vagy {@code null}, ha nincs szükség transzformálásra).
   * @param dim A megjelenítés dimenziói.
   * @return A cellát alkotó 4 pont tömbje (BF, JF, JA, BA sorrendben) vagy {@code null}, ha a cella nem (teljesen) látható.
   */
  public static int[][] project(int x, int y, Matrix transMatrix, Dimension dim) {
    double[][] points = getPoints(x, y);
    if (transMatrix != null) {
      points = transform(points, transMatrix);
    }
    return project(points, dim);
  }

  /**
   * A megadott cellatéglalap transzformálása és projektálása 3D-ban.
   * @param cellRect A cellatéglalap.
   * @param transMatrix A transzformációs mátrix (vagy {@code null}, ha nincs szükség transzformálásra).
   * @param dim A megjelenítés dimenziói.
   * @return A cellatéglalapot alkotó 4 pont tömbje (BF, JF, JA, BA sorrendben) vagy {@code null}, ha a cella nem látható.
   */
  public static final int[][] project(CellRect cellRect, Matrix transMatrix, Dimension dim) {
    double[][] points = getPoints(cellRect);
    if (transMatrix != null) {
      points = transform(points, transMatrix);
    }
    return project(points, dim);
  }

  /**
   * A megadott cellatéglalap transzformálása és projektálása 3D-ban.
   * @param x1 A cella egyik sarkának X koordinátája.
   * @param y1 A cella egyik sarkának Y koordinátája.
   * @param x2 A cella másik sarkának X koordinátája.
   * @param y2 A cella másik sarkának Y koordinátája.
   * @param transMatrix A transzformációs mátrix (vagy {@code null}, ha nincs szükség transzformálásra).
   * @param dim A megjelenítés dimenziói.
   * @return A cellatéglalapot alkotó 4 pont tömbje (BF, JF, JA, BA sorrendben) vagy {@code null}, ha a cella nem látható.
   */
  public static int[][] project(int x1, int y1, int x2, int y2, Matrix transMatrix, Dimension dim) {
    double[][] points = getPoints(x1, y1, x2, y2);
    if (transMatrix != null) {
      points = transform(points, transMatrix);
    }
    return project(points, dim);
  }

  private Transformer() {
  }
}
