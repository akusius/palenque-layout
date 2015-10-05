package hu.akusius.palenque.layout.data;

import java.util.Comparator;
import java.util.Objects;

/**
 * Az elrendezés elemeit sorrendező osztály.
 * Azért lett ennyire körülményes, hogy szimulálni tudjuk vele az eredeti (papíros) sorrendet.
 * @author Bujdosó Ákos
 */
final class LayoutItemSorter implements Comparator<Item> {

  @Override
  public int compare(Item i1, Item i2) {
    if (Objects.equals(i1, i2)) {
      return 0;
    }

    int x1 = i1.getCenter().getX();
    int y1 = i1.getCenter().getY();
    int x2 = i2.getCenter().getX();
    int y2 = i2.getCenter().getY();

    // 1. Gyűrű (belülről kifelé)
    int r1 = getRing(x1, y1);
    int r2 = getRing(x2, y2);
    if (r1 != r2) {
      return r1 < r2 ? -1 : +1;
    }

    // 2. Vízszintes fél (fentről lefelé)
    if (y1 >= 0 != y2 >= 0) {
      return y1 > y2 ? -1 : +1;
    }

    // 3. Függőleges fél (balról jobbra)
    if (x1 >= 0 != x2 >= 0) {
      return x1 < x2 ? -1 : +1;
    }

    // 4. X koordináta (balról jobbra)
    if (x1 != x2) {
      return x1 < x2 ? -1 : +1;
    }

    // 5. Y koordináta (fentről lefelé)
    if (y1 != y2) {
      return y1 > y2 ? -1 : +1;
    }

    // 6. Típus
    return Integer.compare(i1.getType().ordinal(), i2.getType().ordinal());
  }

  private static int getRing(int x, int y) {
    int d = x * x + y * y;
    if (d < 65) {
      return 1;
    }
    if (d < 150) {
      return 2;
    }
    return 3;
  }

}
