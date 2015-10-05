package util;

import java.util.Random;

/**
 *
 * @author Bujdosó Ákos
 */
public class StringUtils {

  /**
   * Üres karakterek beszúrása a sztringbe.
   * @param s Az eredeti sztring.
   * @param probability A beszúrás valószínűsége karakterenként (0-1).
   * @param num Maximum ennyi karaktert szúr be alkalmanként.
   * @return Az üres karakterekkel megtűzdelt új sztring.
   */
  public static final String insertWhitespaces(String s, float probability, int num) {
    Random rnd = new Random();
    char[] wchars = new char[]{' ', '\t', '\n', '\r'};
    StringBuilder sb = new StringBuilder(s.length() * 2);
    for (int i = rnd.nextInt(num); i >= 0; i--) {
      sb.append(wchars[rnd.nextInt(wchars.length)]);
    }
    for (char c : s.toCharArray()) {
      sb.append(c);
      if (rnd.nextFloat() <= probability) {
        for (int i = rnd.nextInt(num); i >= 0; i--) {
          sb.append(wchars[rnd.nextInt(wchars.length)]);
        }
      }
    }
    return sb.toString();
  }

  private StringUtils() {
  }

}
