package hu.akusius.palenque.layout.ui;

import hu.akusius.palenque.layout.util.IoUtils;
import hu.akusius.palenque.layout.util.UIUtils;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Dialógusdoboz szöveg importálásához/exportálásához.
 * @author Bujdosó Ákos
 */
public class TextImpexerDialog extends JDialog {

  private final ClipboardHandler clipboardHandler = new ClipboardHandler();

  private final FileOperationHandler fileOperationHandler = new FileOperationHandler();

  private final boolean exportMode;

  private final String extension;

  private boolean closedWithOK = false;

  private JTextArea tbText;

  private JButton btnOK;

  private JButton btnCancel;

  private JButton btnFile;

  private JButton btnClipboard;

  private TextImpexerDialog(Window owner, boolean exportMode, String initialText, String extension) {
    super(owner, Dialog.ModalityType.APPLICATION_MODAL);

    this.exportMode = exportMode;
    this.extension = extension;

    initComponents();

    if (exportMode) {
      setText(initialText);
    } else {
      setText("");
      btnOK.setEnabled(false);
    }

    this.pack();
    // this.setResizable(false);
    this.setLocationRelativeTo(owner);
  }

  private void initComponents() {
    String title = exportMode ? "Export" : "Import";

    this.setTitle(title);
    this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    getContentPane().setLayout(new BorderLayout(5, 10));

    tbText = new JTextArea();
    tbText.setFont(new Font("Monospaced", Font.PLAIN, tbText.getFont().getSize()));
    tbText.setName("tbText");
    tbText.setEditable(!exportMode);
    tbText.setColumns(70);
    tbText.setLineWrap(true);
    tbText.setRows(10);
    tbText.setTabSize(2);
    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setViewportView(tbText);
    getContentPane().add(scrollPane, BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel();

    btnClipboard = new JButton(exportMode ? "To Clipboard" : "From Clipboard", IconFactory.readIcon(exportMode ? "copy.png" : "paste.png"));
    FontUtility.scaleFont(btnClipboard);
    btnClipboard.setMnemonic(KeyEvent.VK_C);
    btnClipboard.setName("btnClipboard");
    buttonPanel.add(btnClipboard);

    btnFile = new JButton(exportMode ? "To File" : "From File", IconFactory.readIcon(exportMode ? "save.png" : "load.png"));
    FontUtility.scaleFont(btnFile);
    btnFile.setMnemonic(KeyEvent.VK_F);
    btnFile.setName("btnFile");
    buttonPanel.add(btnFile);

    buttonPanel.add(Box.createHorizontalStrut(25));

    btnOK = new JButton(exportMode ? "OK" : "Import");
    FontUtility.scaleFont(btnOK);
    btnOK.setMnemonic(exportMode ? KeyEvent.VK_O : KeyEvent.VK_I);
    btnOK.setName("btnOK");
    buttonPanel.add(btnOK);

    btnCancel = new JButton("Cancel");
    FontUtility.scaleFont(btnCancel);
    //btnCancel.setMnemonic(KeyEvent.VK_C);
    btnCancel.setName("btnCancel");

    if (!exportMode) {
      // Csak importálásnál lehet megszakítani
      buttonPanel.add(btnCancel);
    }

    getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    getRootPane().setDefaultButton(btnOK);

    hookEvents();

    UIUtils.installDialogEscapeCloseOperation(this);

    closedWithOK = false;
  }

  private void hookEvents() {
    btnClipboard.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        if (exportMode) {
          clipboardHandler.setClipboardContents(tbText.getText());
        } else {
          String text = clipboardHandler.getClipboardContents();
          if (text != null) {
            setText(text);
          }
        }
      }
    });

    btnFile.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        if (exportMode) {
          fileOperationHandler.writeToFile(tbText.getText());
        } else {
          String text = fileOperationHandler.readFromFile();
          if (text != null) {
            setText(text);
          }
        }
      }
    });

    btnOK.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        closedWithOK = true;
        setVisible(false);
      }
    });

    btnCancel.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        closedWithOK = false;
        setVisible(false);
      }
    });

    tbText.getDocument().addDocumentListener(new DocumentListener() {

      @Override
      public void insertUpdate(DocumentEvent e) {
        sync(e);
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        sync(e);
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        sync(e);
      }

      private void sync(DocumentEvent e) {
        btnOK.setEnabled(e.getDocument().getLength() > 0);
      }
    });
  }

  private void setText(String text) {
    tbText.setText(text);
    tbText.setCaretPosition(0);
    tbText.setRows(Math.max(Math.min(tbText.getLineCount(), 25), 10));
    //tbText.requestFocus();

    this.pack();
    this.setLocationRelativeTo(getOwner());
  }

  private boolean exportText() {
    closedWithOK = false;
    setVisible(true);
    return closedWithOK;
  }

  private String importText() {
    closedWithOK = false;
    setVisible(true);
    return closedWithOK ? tbText.getText() : null;
  }

  /**
   * A dialógus megjelenítése exportáláshoz, a megadott szöveggel.
   * @param parent A dialógusdoboz szülője.
   * @param text Az exportálandó szöveg.
   * @param extension A fájlkiterjesztés a mentéshez.
   * @return {@code true}, ha megtörtént az exportálás.
   */
  public static boolean exportText(Window parent, String text, String extension) {
    if (text == null) {
      throw new IllegalArgumentException();
    }
    TextImpexerDialog dialog = new TextImpexerDialog(parent, true, text, extension);
    try {
      return dialog.exportText();
    } finally {
      dialog.dispose();
    }
  }

  /**
   * A dialógus megjelenítése importáláshoz.
   * @param parent A dialógusdoboz szülője.
   * @param extension A fájlkiterjesztés a betöltéshez.
   * @return Az importált szöveg, vagy {@code null}, ha nem történt importálás.
   */
  public static String importText(Window parent, String extension) {
    TextImpexerDialog dialog = new TextImpexerDialog(parent, false, null, extension);
    try {
      return dialog.importText();
    } finally {
      dialog.dispose();
    }
  }

  private static class ClipboardHandler {

    private Clipboard clipboard;

    private void ensureClipboard() {
      if (clipboard != null) {
        return;
      }

      clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    public String getClipboardContents() {
      ensureClipboard();

      final Transferable tr;
      if (clipboard != null) {
        tr = clipboard.getContents(null);
      } else {
        tr = null;
      }

      if (tr != null && tr.isDataFlavorSupported(DataFlavor.stringFlavor)) {
        try {
          return (String) tr.getTransferData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
          throw new RuntimeException(e);
        }
      }

      return null;
    }

    public void setClipboardContents(String value) {
      ensureClipboard();

      StringSelection stringSelection = new StringSelection(value);
      if (clipboard != null) {
        clipboard.setContents(stringSelection, null);
      }
    }
  }

  private class FileOperationHandler {

    private static final String ENCODING = "UTF-8";

    private FileChooser fileChooser;

    private void ensureFileChooser() {
      if (fileChooser == null) {
        fileChooser = new FileChooser(extension);
      }
    }

    public void writeToFile(String content) {
      ensureFileChooser();

      try {
        if (fileChooser.showSaveDialog(TextImpexerDialog.this) == JFileChooser.APPROVE_OPTION) {
          File file = fileChooser.getSelectedFile();
          if (file != null) {
            try (FileOutputStream fos = new FileOutputStream(file);
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos, ENCODING))) {
              writer.write(content);
            }
          }
        }
      } catch (HeadlessException | IOException e) {
        throw new RuntimeException(e);
      }
    }

    public String readFromFile() {
      ensureFileChooser();

      try {
        if (fileChooser.showOpenDialog(TextImpexerDialog.this) == JFileChooser.APPROVE_OPTION) {
          File file = fileChooser.getSelectedFile();
          if (file != null) {
            try (InputStream is = new FileInputStream(file)) {
              return IoUtils.readStringFromInputStream(is, ENCODING);
            }
          }
        }
      } catch (HeadlessException | IOException e) {
        throw new RuntimeException(e);
      }

      return null;
    }
  }
}
