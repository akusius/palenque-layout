package hu.akusius.palenque.layout.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Bujdosó Ákos
 */
public class SkipWhitespaceReaderTest {

  public SkipWhitespaceReaderTest() {
  }

  @Test
  public void test1() throws IOException {
    String s = " T\t E \nS\rT\t \n\r";
    Reader reader = new SkipWhitespaceReader(new StringReader(s));
    assertThat(reader.read(), equalTo((int) 'T'));
    assertThat(reader.read(), equalTo((int) 'E'));
    assertThat(reader.read(), equalTo((int) 'S'));
    assertThat(reader.read(), equalTo((int) 'T'));
    assertThat(reader.read(), equalTo(-1));
  }

  @Test
  public void test2() throws IOException {
    String s = "TEST";
    Reader reader = new SkipWhitespaceReader(new StringReader(s));
    assertThat(reader.read(), equalTo((int) 'T'));
    assertThat(reader.read(), equalTo((int) 'E'));
    assertThat(reader.read(), equalTo((int) 'S'));
    assertThat(reader.read(), equalTo((int) 'T'));
    assertThat(reader.read(), equalTo(-1));
  }

  @Test
  public void test3() throws IOException {
    String s = " T\t E \nS\rT\t \n\r";
    Reader reader = new SkipWhitespaceReader(new StringReader(s));
    char[] buf = new char[10];
    assertThat(reader.read(buf, 3, 5), equalTo(4));
    assertThat(new String(buf, 3, 4), equalTo("TEST"));
  }

  @Test
  public void test4() throws IOException {
    String s = "TEST";
    Reader reader = new SkipWhitespaceReader(new StringReader(s));
    char[] buf = new char[10];
    assertThat(reader.read(buf, 3, 5), equalTo(4));
    assertThat(new String(buf, 3, 4), equalTo("TEST"));
  }

  @Test
  public void test5() throws IOException {
    String s = " T\t E \nS\rT\t \n\r";
    Reader reader = new SkipWhitespaceReader(new StringReader(s));
    assertThat(reader.skip(3), equalTo(3L));
    assertThat(reader.read(), equalTo((int) 'T'));
    assertThat(reader.read(), equalTo(-1));
  }

}
