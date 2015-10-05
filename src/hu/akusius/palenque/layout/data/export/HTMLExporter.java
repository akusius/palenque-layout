package hu.akusius.palenque.layout.data.export;

import hu.akusius.palenque.layout.data.Item;
import hu.akusius.palenque.layout.data.Layout;

/**
 * Elrendezés adatainak exportálása HTML formátumban.
 * @author Bujdosó Ákos
 */
public class HTMLExporter extends TemplatedExporter {

  public HTMLExporter() {
    super("HTML");
  }

  @Override
  protected String formatValue(Object value, String var, Item item, Layout layout) {
    if (value instanceof Integer) {
      int iv = (Integer) value;
      if (iv < 0) {
        return "&minus;" + Integer.toString(-iv);
      }
    }
    return super.formatValue(value, var, item, layout);
  }
}
