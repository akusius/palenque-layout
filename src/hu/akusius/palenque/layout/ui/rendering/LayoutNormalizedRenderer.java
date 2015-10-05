package hu.akusius.palenque.layout.ui.rendering;

import hu.akusius.palenque.layout.data.*;
import java.awt.Dimension;
import java.awt.Graphics;
import org.other.Matrix;

/**
 * Egy teljes elrendezés normalizált kirajzolását végző osztály.
 * Csak 3D-s kirajzolásra használható (szerkesztési módban nem).
 * A kód nagy része a PalenqueAnimation-ből származik.
 * @author Bujdosó Ákos
 */
public class LayoutNormalizedRenderer {

  public static void render(Layout layout, FrameInfo frameInfo, Graphics graphics, Dimension dim) {
    int step = frameInfo.getStepNum();
    double percent = frameInfo.getPercent() / 100.0;

    Grid grid = new Grid();
    grid.addLayout(layout);

    if (step == 0) {
      GridRenderer.render(grid, null, graphics, dim);
    } else if (step < 4) {
      Grid grRot = grid.cloneGrid();

      if (step >= 2) {
        grid.rotate(step - 1, true);
        grRot.rotate(step - 1, false);
      }
      GridRenderer.render(grid, null, graphics, dim);

      if (percent >= 0.2) {
        Matrix m = new Matrix();
        m.identity();
        m.rotateZ(-Math.PI / 2.0 * (percent - 0.2) * 1.25);
        GridRenderer.render(grRot, m, graphics, dim);
      }
    } else if (step == 4) {
      // Várakozás
      grid.rotate(3, true);
      GridRenderer.render(grid, null, graphics, dim);
    } else if (step == 5) {
      grid.rotate(3, true);
      GridRenderer.render(grid, null, graphics, dim);
      Matrix m = new Matrix();
      Matrix.identity(m);
      m.rotateY(Math.PI * percent);
      GridRenderer.render(grid, m, graphics, dim);
    } else if (step == 6) {
      grid.octuple();
      GridRenderer.render(grid, null, graphics, dim);
    }
  }

  private LayoutNormalizedRenderer() {
  }

  /**
   * Egy négyzetrácsot reprezentáló osztály.
   * A koordináták jobbra és felfelé nőnek.
   * Az elemeket a tömbben soronként tároljuk, lentről felfelé.
   */
  private static final class Grid {

    public static final byte ITEM_EMPTY = 0;

    public static final byte ITEM_SQUARE = 1;

    public static final byte ITEM_SUN = 2;

    public static final byte ITEM_STAR = 4;

    private final int size;

    private final byte[][] items;

    private final int ci;

    Grid() {
      this(2 * Cell.RANGE + 1);
    }

    private Grid(int size) {
      assert size > 1 && size % 2 == 1;
      this.size = size;
      this.items = new byte[size][size];
      this.ci = (size - 1) / 2;
    }

    public int getSize() {
      return size;
    }

    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    byte[][] getItems() {
      return items;
    }

    public void addItem(Item item) {
      Cell center = item.getCenter();
      int ix = center.getX() + ci;
      int iy = center.getY() + ci;
      if (item.getType() == ItemType.Triplet) {
        items[iy - 1][ix] |= ITEM_SQUARE;
        items[iy][ix] |= ITEM_SQUARE;
        items[iy + 1][ix] |= ITEM_SQUARE;
      } else {
        final byte type;
        switch (item.getType()) {
          case Sun:
            type = ITEM_SUN;
            break;
          case Star:
            type = ITEM_STAR;
            break;
          default:
            throw new AssertionError();
        }
        items[iy][ix] |= type;
      }
    }

    public void addLayout(Layout layout) {
      for (Item item : layout.getItems()) {
        addItem(item);
      }
    }

    /**
     * A négyzetrács elemeinek elforgatása 90 fokkal óramutató járása szerinti irányban a megadott számban.
     * @param num A forgatások száma.
     * @param keep {@code true} esetén megtartja az eredeti elemeket, egyébként azokat törli.
     */
    public void rotate(int num, boolean keep) {
      assert num >= 1 && num <= 3;
      for (int iy = 0; iy < size; iy++) {
        for (int ix = 0; ix < size; ix++) {
          byte it = (byte) (items[iy][ix] & 0x0F);
          if (it != ITEM_EMPTY) {
            if (ix == ci && iy == ci) {
              // Középső elem mindig marad a helyén
              continue;
            }
            for (int n = 1; n <= num; n++) {
              if (!keep && n < num) {
                continue;
              }
              final int niy;
              final int nix;
              switch (n) {
                case 1:
                  // y, -x
                  nix = iy;
                  niy = (ci << 1) - ix;
                  break;
                case 2:
                  // -x, -y
                  nix = (ci << 1) - ix;
                  niy = (ci << 1) - iy;
                  break;
                case 3:
                  // -y, x
                  nix = (ci << 1) - iy;
                  niy = ix;
                  break;
                default:
                  throw new AssertionError();
              }
              if (!keep) {
                items[iy][ix] &= 0xF0;
              }
              items[niy][nix] |= (byte) (it << 4);
            }
          }
        }
      }

      // A felső nibble-be tettük bele az új elemeket
      // Végigmegyünk másodjára és beletesszük alulra
      for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
          byte b = items[i][j];
          if (b > 0x0F) {
            byte nit = (byte) (b >> 4);
            items[i][j] |= nit;
          }
        }
      }
    }

    /**
     * A négyzetrács elemeinek nyolcszorozása három elforgatással és tükrözéssel.
     * Az eredeti elemeket a helyükön hagyja.
     */
    public void octuple() {
      rotate(3, true);
      for (int iy = 0; iy < size; iy++) {
        for (int ix = 0; ix < size; ix++) {
          byte it = (byte) (items[iy][ix] & 0x0F);
          if (it != ITEM_EMPTY) {
            int nix = (ci << 1) - ix;
            int niy = iy;
            items[niy][nix] |= it;
          }
        }
      }
    }

    /**
     * A négyzetháló klónozása.
     * @return Az új klónozott négyzetrács.
     */
    public Grid cloneGrid() {
      Grid g = new Grid(this.size);
      for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
          byte b = items[i][j];
          if (b != ITEM_EMPTY) {
            g.items[i][j] = b;
          }
        }
      }
      return g;
    }
  }

  /**
   * Egy {@code Grid} kirajzolása.
   * @author Bujdosó Ákos
   */
  private static final class GridRenderer {

    private static final byte HORZ = 1;

    private static final byte VERT = 2;

    private static final byte BOTH = HORZ | VERT;

    /**
     * A megadott négyzetrács kirajzolása.
     * @param grid A kirajzolandó négyzetrács
     * @param transformMatrix Az alkalmazandó transzformációs mátrix.
     * @param g A kirajzolás célja.
     * @param dim A kirajzolás dimenziói.
     */
    public static void render(Grid grid, final Matrix transformMatrix, final Graphics g, final Dimension dim) {
      int size = grid.getSize();
      final int ci = (size - 1) / 2;
      byte[][] items = grid.getItems();
      byte[][] segs = null;
      for (int iy = 0; iy < size; iy++) {
        for (int ix = 0; ix < size; ix++) {
          byte b = items[iy][ix];
          if (b == Grid.ITEM_EMPTY) {
            continue;
          }

          int x = ix - ci;
          int y = iy - ci;

          if ((b & Grid.ITEM_SQUARE) == Grid.ITEM_SQUARE) {
            // Itt csak szegmensekre bontjuk a négyzetet, és eltároljuk ezeket
            if (segs == null) {
              segs = new byte[size + 1][size + 1];
            }
            segs[iy][ix] = BOTH;   // bal és alsó
            segs[iy][ix + 1] |= VERT;   // jobb
            segs[iy + 1][ix] |= HORZ;   // felső
          }

          if ((b & Grid.ITEM_SUN) == Grid.ITEM_SUN) {
            int[][] ps = Transformer.project(x - 1, y - 1, x + 1, y + 1, transformMatrix, dim);
            if (ps != null) {
              g.drawLine(ps[0][0], ps[0][1], ps[2][0], ps[2][1]);
              g.drawLine(ps[1][0], ps[1][1], ps[3][0], ps[3][1]);
            }
          }

          if ((b & Grid.ITEM_STAR) == Grid.ITEM_STAR) {
            int[][] ps1 = Transformer.project(x - 1, y - 1, x + 1, y + 1, transformMatrix, dim);
            int[][] ps2 = Transformer.project(x, y, transformMatrix, dim);
            if (ps1 != null && ps2 != null) {
              g.drawLine(ps1[0][0], ps1[0][1], ps1[2][0], ps1[2][1]);
              g.drawLine(ps1[1][0], ps1[1][1], ps1[3][0], ps1[3][1]);
              g.drawPolyline(
                      new int[]{ps2[0][0], ps2[1][0], ps2[2][0], ps2[3][0], ps2[0][0]},
                      new int[]{ps2[0][1], ps2[1][1], ps2[2][1], ps2[3][1], ps2[0][1]},
                      5);
            }
          }
        }
      }

      if (segs == null) {
        // Készen vagyunk
        return;
      }

      SegmentRenderer renderer = new SegmentRenderer() {

        @Override
        public void renderSegment(int x1, int y1, int x2, int y2, int p1, int p2) {
          // System.out.println(String.format("x1: %d, y1: %d, x2: %d, y2: %d, p1: %d, p2: %d", x1, y1, x2, y2, p1, p2));
          double[][] points = Transformer.getPoints(x1 - ci, y1 - ci, x2 - ci, y2 - ci);
          double[][] ps = new double[2][3];
          ps[0] = points[p1];
          ps[1] = points[p2];
          if (transformMatrix != null) {
            ps = Transformer.transform(ps, transformMatrix);
          }
          int[][] pps = Transformer.project(ps, dim);
          if (pps != null) {
            g.drawLine(pps[0][0], pps[0][1], pps[1][0], pps[1][1]);
          }
        }
      };

      // Vízszintes szegmensek
      for (int row = 0; row <= size; row++) {
        int start = -1;
        for (int col = 0; col <= size; col++) {
          byte s = segs[row][col];
          if (start == -1 && (s & HORZ) != 0) {
            // Itt kezdődik egy szegmens
            start = col;
          } else if (start != -1 && (s & HORZ) == 0) {
            // Végére értünk egy szegmensnek, meghúzzuk a vonalat
            if (row < size) {
              renderer.renderSegment(start, row, col - 1, row, 3, 2);
            } else {
              renderer.renderSegment(start, row - 1, col - 1, row - 1, 0, 1);
            }
            start = -1;
          }
        }
        if (start != -1) {
          if (row < size) {
            renderer.renderSegment(start, row, size - 1, row, 3, 2);
          } else {
            renderer.renderSegment(start, row - 1, size - 1, row - 1, 0, 1);
          }
        }
      }

      // Föggőleges szegmensek
      for (int col = 0; col <= size; col++) {
        int start = -1;
        for (int row = 0; row <= size; row++) {
          byte s = segs[row][col];
          if (start == -1 && (s & VERT) != 0) {
            // Itt kezdődik egy szegmens
            start = row;
          } else if (start != -1 && (s & VERT) == 0) {
            // Végére értünk egy szegmensnek, meghúzzuk a vonalat
            if (col < size) {
              renderer.renderSegment(col, start, col, row - 1, 0, 3);
            } else {
              renderer.renderSegment(col - 1, start, col - 1, row - 1, 1, 2);
            }
            start = -1;
          }
        }
        if (start != -1) {
          if (col < size) {
            renderer.renderSegment(col, start, col, size - 1, 0, 3);
          } else {
            renderer.renderSegment(col - 1, start, col - 1, size - 1, 1, 2);
          }
        }
      }
    }

    private GridRenderer() {
    }

    private interface SegmentRenderer {

      void renderSegment(int x1, int y1, int x2, int y2, int p1, int p2);
    }
  }
}
