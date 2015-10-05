package hu.akusius.palenque.layout.data;

/**
 * Egy elem lehetséges típusai
 * @author Bujdosó Ákos
 */
public enum ItemType {

  /**
   * Hármas
   */
  Triplet,
  /**
   * Nap
   */
  Sun,
  /**
   * Csillag (másik nap)
   */
  Star;

  public char getChar() {
    switch (this) {
      case Triplet:
        return 'T';
      case Sun:
        return 'S';
      case Star:
        return 'R';
      default:
        throw new AssertionError();
    }
  }

  public String getShortName() {
    return String.valueOf(getChar());
  }

  public static ItemType fromChar(char ch) {
    switch (ch) {
      case 'T':
        return Triplet;
      case 'S':
        return Sun;
      case 'R':
        return Star;
      default:
        throw new IllegalArgumentException();
    }
  }

  public static ItemType fromShortName(String shortName) {
    if (shortName.length() != 1) {
      throw new IllegalArgumentException();
    }
    return fromChar(shortName.charAt(0));
  }
}
