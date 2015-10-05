package hu.akusius.palenque.layout.ui;

import hu.akusius.palenque.layout.data.Layout;
import hu.akusius.palenque.layout.op.LayoutImpexer;
import javax.swing.JOptionPane;

/**
 * Olyan {@link LayoutImpexer}, amelyik dialógust ({@link TextImpexerDialog}) használ az exportáláshoz / importáláshoz.
 * @author Bujdosó Ákos
 */
public class DialogLayoutImpexer implements LayoutImpexer {

  private final DialogParent dialogParent;

  public DialogLayoutImpexer(DialogParent dialogParent) {
    this.dialogParent = dialogParent;
  }

  @Override
  public Layout importLayout() {
    String data;
    while (true) {
      data = TextImpexerDialog.importText(dialogParent.getWindow(), "pql");
      if (data == null) {
        // Megszakították az importálást
        return null;
      }
      try {
        return Layout.deserialize(data);
      } catch (Exception e) {
        JOptionPane.showMessageDialog(dialogParent.getComponent(), "Invalid data!", "Error", JOptionPane.WARNING_MESSAGE);
      }
    }
  }

  @Override
  public boolean exportLayout(Layout layoutToExport) {
    return TextImpexerDialog.exportText(dialogParent.getWindow(), layoutToExport.serialize(), "pql");
  }

}
