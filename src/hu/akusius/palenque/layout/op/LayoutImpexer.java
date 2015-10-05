package hu.akusius.palenque.layout.op;

import hu.akusius.palenque.layout.data.Layout;

/**
 * Interfész egy elrendezés importálásához és exportálásához.
 * @author Bujdosó Ákos
 */
public interface LayoutImpexer {

  /**
   * Egy elrendezés beimportálása.
   * @return A beimportált elrendezés, illetve {@code null}, ha nem történt importálás.
   */
  Layout importLayout();

  /**
   * A megadott elrendezé exportálása
   * @param layoutToExport Az exportálandó elrendezés.
   * @return {@code true}, ha megtörtént ténylegesen az exportálás.
   */
  boolean exportLayout(Layout layoutToExport);
}
