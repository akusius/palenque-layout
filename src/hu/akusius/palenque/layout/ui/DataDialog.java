package hu.akusius.palenque.layout.ui;

import hu.akusius.palenque.layout.data.Item;
import hu.akusius.palenque.layout.data.ItemType;
import hu.akusius.palenque.layout.data.Layout;
import hu.akusius.palenque.layout.data.export.*;
import hu.akusius.palenque.layout.util.UIUtils;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

/**
 * Dialógus az elemadatok megjelenítéséhez.
 * @author Bujdosó Ákos
 */
public class DataDialog extends JDialog {

  private static DataDialog dialog;

  /**
   * A dialógus megjelenítése a megadott elrendezés alapján.
   * @param layout A megjelenítendő elrendezés.
   * @param dialogParent A dialógusok szülője.
   */
  public static void show(Layout layout, DialogParent dialogParent) {
    if (dialog == null) {
      dialog = new DataDialog(layout, dialogParent.getWindow());
    } else {
      dialog.refresh(layout);
    }

    dialog.btnOK.requestFocusInWindow();
    dialog.setVisible(true);
  }

  private final DataTableModel tableModel;

  private final Exporter[] exporters
          = new Exporter[]{new HTMLExporter(), new XMLExporter(), new CSVExporter(), new TXTExporter()};

  private JButton btnOK;

  private DataDialog(Layout layout, Window owner) {
    super(owner, ModalityType.APPLICATION_MODAL);

    this.tableModel = new DataTableModel(layout);

    initComponents();

    this.pack();
    //this.setResizable(false);
    this.setLocationRelativeTo(owner);
  }

  private void initComponents() {
    setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

    this.setTitle("Item Data");
    setLayout(new BorderLayout(5, 10));

    JTable table = new JTable(tableModel) {

      @Override
      protected JTableHeader createDefaultTableHeader() {
        return new JTableHeader(columnModel) {
          @Override
          public String getToolTipText(MouseEvent event) {
            java.awt.Point p = event.getPoint();
            int index = columnModel.getColumnIndexAtX(p.x);
            int realIndex = columnModel.getColumn(index).getModelIndex();
            switch (realIndex) {
              case DataTableModel.COL_NUM:
                return "Number";
              case DataTableModel.COL_TYPE:
                return "Type";
              case DataTableModel.COL_X:
                return "X coordinate";
              case DataTableModel.COL_Y:
                return "Y coordinate";
              default:
                throw new AssertionError();
            }
          }
        };
      }
    };
    ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
    table.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
      {
        setHorizontalAlignment(SwingConstants.CENTER);
      }
    });
    table.setDefaultRenderer(TableItemType.class, new DefaultTableCellRenderer() {
      {
        setHorizontalAlignment(SwingConstants.CENTER);
      }

      @Override
      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        l.setToolTipText(((TableItemType) value).getToolTipText());
        return l;
      }
    });

    TableRowSorter<DataTableModel> tableSorter = new TableRowSorter<>(tableModel);
    tableSorter.setSortKeys(Arrays.asList(new SortKey[]{new SortKey(DataTableModel.COL_NUM, SortOrder.ASCENDING)}));
    table.setRowSorter(tableSorter);

    table.setFillsViewportHeight(true);
    JScrollPane scrollPane = new JScrollPane(table);

    Dimension d = table.getPreferredSize();
    d.height = table.getRowHeight() * (table.getRowCount() + 2);
    scrollPane.setPreferredSize(d);

    this.add(scrollPane, BorderLayout.CENTER);
    this.add(createLowerPanel(), BorderLayout.SOUTH);

    UIUtils.installDialogEscapeCloseOperation(this);
  }

  private JPanel createLowerPanel() {
    JPanel panel = new JPanel();

    final JComboBox<Exporter> cb = new JComboBox<>(exporters);
    cb.setRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index,
              boolean isSelected, boolean cellHasFocus) {
        value = ((Exporter) value).getFormat();
        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      }
    });
    FontUtility.scaleFont(cb);
    panel.add(cb);

    JButton btnExport = new JButton("Export", IconFactory.readIcon("export2.png"));
    btnExport.setMnemonic(KeyEvent.VK_E);
    FontUtility.scaleFont(btnExport);
    panel.add(btnExport);

    btnExport.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Object selectedItem = cb.getSelectedItem();
        if (selectedItem instanceof Exporter) {
          Exporter exporter = (Exporter) selectedItem;
          String export = exporter.export(tableModel.getLayout());
          TextImpexerDialog.exportText(DataDialog.this, export, exporter.getFileExtension());
        }
      }
    });

    panel.add(Box.createHorizontalStrut(15));

    btnOK = new JButton("OK");
    FontUtility.scaleFont(btnOK);
    btnOK.setMnemonic(KeyEvent.VK_O);
    btnOK.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
      }
    });
    panel.add(btnOK);
    getRootPane().setDefaultButton(btnOK);

    return panel;
  }

  private void refresh(Layout layout) {
    tableModel.refresh(layout);
  }

  private static final class DataTableModel extends AbstractTableModel {

    static final int COL_NUM = 0;

    static final int COL_TYPE = 1;

    static final int COL_X = 2;

    static final int COL_Y = 3;

    private Item[] items;

    private Layout layout;

    DataTableModel(Layout layout) {
      refresh(layout);
    }

    void refresh(Layout layout) {
      this.layout = layout;
      this.items = layout.getItems().toArray(new Item[0]);
      fireTableDataChanged();
    }

    public Layout getLayout() {
      return layout;
    }

    @Override
    public int getColumnCount() {
      return 4;
    }

    @Override
    public String getColumnName(int column) {
      switch (column) {
        case COL_NUM:
          return "#";
        case COL_TYPE:
          return "T";
        case COL_X:
          return "X";
        case COL_Y:
          return "Y";
        default:
          throw new AssertionError();
      }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
      switch (columnIndex) {
        case COL_NUM:
        case COL_X:
        case COL_Y:
          return Integer.class;
        case COL_TYPE:
          return TableItemType.class;
        default:
          throw new AssertionError();
      }
    }

    @Override
    public int getRowCount() {
      return items.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      Item i = items[rowIndex];
      switch (columnIndex) {
        case COL_NUM:
          return rowIndex + 1;
        case COL_TYPE:
          return TableItemType.getTableItemType(i.getType());
        case COL_X:
          return i.getCenter().getX();
        case COL_Y:
          return i.getCenter().getY();
        default:
          throw new AssertionError();
      }
    }
  }

  private enum TableItemType {

    T,
    S,
    R;

    public static TableItemType getTableItemType(ItemType itemType) {
      switch (itemType) {
        case Triplet:
          return T;
        case Sun:
          return S;
        case Star:
          return R;
        default:
          throw new AssertionError();
      }
    }

    public String getToolTipText() {
      switch (this) {
        case T:
          return "Triplet";
        case S:
          return "Sun";
        case R:
          return "Star";
        default:
          throw new AssertionError();
      }
    }
  }
}
