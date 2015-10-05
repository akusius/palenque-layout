package hu.akusius.palenque.layout.data;

import java.util.Random;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Bujdosó Ákos
 */
public class CellRectTest {

  public CellRectTest() {
  }

  @Test
  public void test1() {
    CellRect cr = new CellRect(new Cell(0, 0), new Cell(5, 2));
    assertThat(cr.getC1(), equalTo(new Cell(0, 0)));
    assertThat(cr.getC2(), equalTo(new Cell(5, 2)));
    assertThat(cr.getLeftBottom(), equalTo(new Cell(0, 0)));
    assertThat(cr.getRightTop(), equalTo(new Cell(5, 2)));
    assertThat(cr.getWidth(), equalTo(6));
    assertThat(cr.getHeight(), equalTo(3));
    assertThat(cr.contains(new Cell(0, 0)), is(true));
    assertThat(cr.contains(new Cell(1, 1)), is(true));
    assertThat(cr.contains(new Cell(2, 2)), is(true));
    assertThat(cr.contains(new Cell(3, 3)), is(false));
    assertThat(cr.contains(new Cell(5, 2)), is(true));
    assertThat(cr.contains(new Cell(5, 3)), is(false));
    assertThat(cr.contains(new Cell(-1, -1)), is(false));
  }

  @Test
  public void test2() {
    CellRect cr = new CellRect(new Cell(0, 0), new Cell(-3, -7));
    assertThat(cr.getC1(), equalTo(new Cell(0, 0)));
    assertThat(cr.getC2(), equalTo(new Cell(-3, -7)));
    assertThat(cr.getLeftBottom(), equalTo(new Cell(-3, -7)));
    assertThat(cr.getRightTop(), equalTo(new Cell(0, 0)));
    assertThat(cr.getWidth(), equalTo(4));
    assertThat(cr.getHeight(), equalTo(8));
    assertThat(cr.contains(new Cell(0, 0)), is(true));
    assertThat(cr.contains(new Cell(-1, -1)), is(true));
    assertThat(cr.contains(new Cell(-2, -2)), is(true));
    assertThat(cr.contains(new Cell(-3, -3)), is(true));
    assertThat(cr.contains(new Cell(-4, -4)), is(false));
    assertThat(cr.contains(new Cell(-3, -7)), is(true));
    assertThat(cr.contains(new Cell(-4, -7)), is(false));
  }

  @Test
  public void test3() {
    CellRect cr = new CellRect(new Cell(4, -3), new Cell(-12, 9));
    assertThat(cr.getC1(), equalTo(new Cell(4, -3)));
    assertThat(cr.getC2(), equalTo(new Cell(-12, 9)));
    assertThat(cr.getLeftBottom(), equalTo(new Cell(-12, -3)));
    assertThat(cr.getRightTop(), equalTo(new Cell(4, 9)));
    assertThat(cr.getWidth(), equalTo(17));
    assertThat(cr.getHeight(), equalTo(13));
    assertThat(cr.contains(new Cell(0, 0)), is(true));
    assertThat(cr.contains(new Cell(4, -3)), is(true));
    assertThat(cr.contains(new Cell(-12, 9)), is(true));
    assertThat(cr.contains(new Cell(4, -4)), is(false));
    assertThat(cr.contains(new Cell(5, -3)), is(false));
    assertThat(cr.contains(new Cell(-13, 0)), is(false));
    assertThat(cr.contains(new Cell(-5, 10)), is(false));
  }

  @Test
  public void test4() {
    CellRect cr = new CellRect(new Cell(-17, -13), new Cell(-17, -13));
    assertThat(cr.getC1(), equalTo(new Cell(-17, -13)));
    assertThat(cr.getC2(), equalTo(new Cell(-17, -13)));
    assertThat(cr.getLeftBottom(), equalTo(new Cell(-17, -13)));
    assertThat(cr.getRightTop(), equalTo(new Cell(-17, -13)));
    assertThat(cr.getWidth(), equalTo(1));
    assertThat(cr.getHeight(), equalTo(1));
    assertThat(cr.contains(new Cell(0, 0)), is(false));
    assertThat(cr.contains(new Cell(-17, -13)), is(true));
  }

  private static Cell getRandomCell(Random r) {
    int x = r.nextInt(Cell.RANGE * 2 + 1) - Cell.RANGE;
    int y = r.nextInt(Cell.RANGE * 2 + 1) - Cell.RANGE;
    return new Cell(x, y);
  }

  @Test
  public void test5() {
    Random rnd = new Random();
    for (int i = 0; i < 100; i++) {
      Cell c1 = getRandomCell(rnd);
      Cell c2 = getRandomCell(rnd);

      CellRect cr = new CellRect(c1, c2);
      Cell lb = cr.getLeftBottom();
      Cell rt = cr.getRightTop();

      assertThat(cr, equalTo(new CellRect(c2, c1)));
      assertThat(cr, equalTo(new CellRect(lb, rt)));
      assertThat(cr, equalTo(new CellRect(rt, lb)));

      assertThat(cr.getC1(), equalTo(c1));
      assertThat(cr.getC2(), equalTo(c2));
      assertThat(lb.getX(), equalTo(Math.min(c1.getX(), c2.getX())));
      assertThat(lb.getY(), equalTo(Math.min(c1.getY(), c2.getY())));
      assertThat(rt.getX(), equalTo(Math.max(c1.getX(), c2.getX())));
      assertThat(rt.getY(), equalTo(Math.max(c1.getY(), c2.getY())));
      assertThat(cr.getWidth(), equalTo(Math.abs(c1.getX() - c2.getX()) + 1));
      assertThat(cr.getHeight(), equalTo(Math.abs(c1.getY() - c2.getY()) + 1));

      for (int j = 0; j < 10; j++) {
        Cell c = getRandomCell(rnd);
        assertThat(cr.contains(c), equalTo(c.getX() >= lb.getX() && c.getX() <= rt.getX()
                && c.getY() >= lb.getY() && c.getY() <= rt.getY()));
      }

      CellRect crc = cr.createClone();
      assertThat(crc, not(sameInstance(cr)));
      assertThat(crc, equalTo(cr));
      assertThat(crc.getC1(), not(sameInstance(cr.getC1())));
      assertThat(crc.getC1(), equalTo(cr.getC1()));
      assertThat(crc.getC2(), not(sameInstance(cr.getC2())));
      assertThat(crc.getC2(), equalTo(cr.getC2()));
    }
  }

}
