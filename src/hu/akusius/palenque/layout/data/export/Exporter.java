package hu.akusius.palenque.layout.data.export;

import hu.akusius.palenque.layout.data.Layout;

/**
 * Az exportálók által implementálandó interfész.
 * @author Bujdosó Ákos
 */
public interface Exporter {

  /**
   * @return Az exportáló által kezelt formátum megnevezése.
   */
  String getFormat();

  /**
   * @return A kezelt formátumhoz tartozó fájlkiterjesztés.
   */
  String getFileExtension();

  /**
   * A megadott elrendezés exportálása az exportáló által kezelt formátumban.
   * @param layout Az exportálandó elrendezés.
   * @return Az elrendezés exportálva a megfelelő formátumban.
   */
  String export(Layout layout);
}
