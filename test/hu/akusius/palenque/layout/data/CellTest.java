package hu.akusius.palenque.layout.data;

import java.io.Reader;
import java.io.StringReader;
import org.junit.Test;
import util.StringUtils;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Bujdosó Ákos
 */
public class CellTest {

  public CellTest() {
  }

  @Test
  public void test1() {
    assertThat(Cell.isValid(0, 0), is(true));
    assertThat(Cell.isValid(Cell.RANGE, Cell.RANGE), is(true));
    assertThat(Cell.isValid(Cell.RANGE, -Cell.RANGE), is(true));
    assertThat(Cell.isValid(-Cell.RANGE, Cell.RANGE), is(true));
    assertThat(Cell.isValid(-Cell.RANGE, -Cell.RANGE), is(true));
    assertThat(Cell.isValid(-Cell.RANGE + 1, -Cell.RANGE + 1), is(true));
    assertThat(Cell.isValid(Cell.RANGE - 1, Cell.RANGE - 1), is(true));

    assertThat(Cell.isValid(Cell.RANGE + 1, 0), is(false));
    assertThat(Cell.isValid(0, Cell.RANGE + 1), is(false));
    assertThat(Cell.isValid(0, -Cell.RANGE - 1), is(false));
    assertThat(Cell.isValid(-Cell.RANGE - 1, 0), is(false));
  }

  @Test(expected = IllegalArgumentException.class)
  public void test2() {
    Cell cell = new Cell(Cell.RANGE + 1, 0);
  }

  @Test
  public void test3() {
    for (int i = -Cell.RANGE; i <= Cell.RANGE; i++) {
      Cell c1 = new Cell(i, 0);
      Cell c2 = new Cell(0, i);
      Cell c3 = new Cell(i, i);

      assertThat(c1.getX(), equalTo(i));
      assertThat(c1.getY(), equalTo(0));
      assertThat(c2.getX(), equalTo(0));
      assertThat(c2.getY(), equalTo(i));
      assertThat(c3.getX(), equalTo(i));
      assertThat(c3.getY(), equalTo(i));
      assertThat(c1.equals(c1), is(true));
      assertThat(c1.equals(c2), is(i == 0));
      assertThat(c1.equals(c3), is(i == 0));
      assertThat(c2.equals(c2), is(true));
      assertThat(c2.equals(c3), is(i == 0));
      assertThat(c3.equals(c3), is(true));

      Cell c1s = Cell.deserialize(c1.serialize());
      assertThat(c1, not(sameInstance(c1s)));
      assertThat(c1, equalTo(c1s));
      assertThat(c1.equals(c1s), is(true));
      assertThat(c1s.equals(c1), is(true));
      assertThat(c1s.equals(c2), is(i == 0));

      Cell c1c = c1.createClone();
      assertThat(c1, not(sameInstance(c1c)));
      assertThat(c1, equalTo(c1c));
      assertThat(c1.equals(c1c), is(true));
      assertThat(c1c.equals(c1), is(true));
      assertThat(c1c.equals(c2), is(i == 0));

      Cell c2s = Cell.deserialize(new StringReader(c2.serialize()));
      assertThat(c2, not(sameInstance(c2s)));
      assertThat(c2, equalTo(c2s));
      assertThat(c2.equals(c2s), is(true));
      assertThat(c2s.equals(c2), is(true));
      assertThat(c2s.equals(c1s), is(i == 0));

      Cell c2c = c2.createClone();
      assertThat(c2, not(sameInstance(c2c)));
      assertThat(c2, equalTo(c2c));
      assertThat(c2.equals(c2c), is(true));
      assertThat(c2c.equals(c2), is(true));
      assertThat(c2c.equals(c1c), is(i == 0));
    }
  }

  @Test
  public void test4() {
    try {
      Cell.deserialize((String) null);
      fail();
    } catch (IllegalArgumentException ex) {
    }

    try {
      Cell.deserialize((Reader) null);
      fail();
    } catch (IllegalArgumentException ex) {
    }

    try {
      Cell.deserialize("");
      fail();
    } catch (IllegalArgumentException ex) {
    }

    try {
      Cell.deserialize("dummy");
      fail();
    } catch (IllegalArgumentException ex) {
    }

    try {
      Cell.deserialize("abcd");
      fail();
    } catch (IllegalArgumentException ex) {
    }
  }

  @Test
  public void test5() {
    Cell c = new Cell(Cell.RANGE, -Cell.RANGE);
    String s = StringUtils.insertWhitespaces(c.serialize(), 1.0f, 3);
    Cell cs = Cell.deserialize(s);
    assertThat(cs, not(sameInstance(c)));
    assertThat(cs, equalTo(c));
    assertThat(cs.equals(c), is(true));
    assertThat(cs.hashCode(), equalTo(c.hashCode()));
  }
}
