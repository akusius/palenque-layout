package hu.akusius.palenque.layout.data.export;

import hu.akusius.palenque.layout.data.Item;
import hu.akusius.palenque.layout.data.Layout;

/**
 * Elrendezés adatainak exportálása TXT formátumban.
 * @author Bujdosó Ákos
 */
public class TXTExporter extends TemplatedExporter {

  public TXTExporter() {
    super("TXT");
  }

  @Override
  protected String formatValue(Object value, String var, Item item, Layout layout) {
    return item != null ? center(value.toString(), 5) : value.toString();
  }

  public static String center(String s, int size) {
    String out = String.format("%" + size + "s%s%" + size + "s", "", s, "");
    float mid = (float) out.length() / 2;
    float start = mid - (size / 2);
    float end = start + size;
    return out.substring((int) start, (int) end);
  }
}
