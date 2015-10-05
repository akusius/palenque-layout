package hu.akusius.palenque.layout.data;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Bujdosó Ákos
 */
public class ItemTypeTest {

  public ItemTypeTest() {
  }

  @Test
  public void test1() {
    ItemType[] types = ItemType.values();
    for (ItemType type : types) {
      char ch = type.getChar();
      String sn = type.getShortName();
      assertThat(sn.length(), equalTo(1));
      assertThat(sn, equalTo(String.valueOf(ch)));

      ItemType t2 = ItemType.fromChar(ch);
      assertThat(t2, equalTo(type));
      t2 = ItemType.fromShortName(sn);
      assertThat(t2, equalTo(type));
    }
  }
}
