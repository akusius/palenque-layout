package hu.akusius.palenque.layout.ui;

import hu.akusius.palenque.layout.data.Cell;
import hu.akusius.palenque.layout.data.Item;
import hu.akusius.palenque.layout.data.ItemType;
import hu.akusius.palenque.layout.data.LayoutContainer;
import hu.akusius.palenque.layout.op.EditManager;
import hu.akusius.palenque.layout.op.EditMode;
import hu.akusius.palenque.layout.op.OperationManager;
import hu.akusius.palenque.layout.op.PropSliderFrame;
import hu.akusius.palenque.layout.ui.rendering.ItemRenderer;
import hu.akusius.palenque.layout.ui.rendering.Transformer;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;
import javax.swing.JPanel;

/**
 * Panel a szerkesztések kezeléséhez.
 * @author Bujdosó Ákos
 */
public class EditPanel extends JPanel {

  private final OperationManager om;

  private final EditManager em;

  private final LayoutContainer lc;

  private final PropSliderFrame frameSlider;

  private final MouseHandler mouseHandler = new MouseHandler();

  private final CellFinder cellFinder = new CellFinder();

  private final Color dragColor = new Color(25, 125, 25);

  private Dimension dim;

  private boolean refreshingCursor;

  public EditPanel(OperationManager operationManager) {
    this.addComponentListener(new ComponentAdapter() {
      @Override
      public void componentResized(ComponentEvent e) {
        dim = getSize();
      }
    });
    this.om = operationManager;
    this.em = om.getEditManager();
    this.em.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (EditManager.PROP_CURRENTMODE.equals(evt.getPropertyName())) {
          modeChanged((EditMode) evt.getOldValue());
        }
      }
    });
    this.lc = om.getLayoutContainer();
    this.lc.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (LayoutContainer.PROP_LAYOUT.equals(evt.getPropertyName())) {
          refreshCursor();
        }
      }
    });
    this.frameSlider = om.getPlayManager().getFrameSlider();

    this.setOpaque(false);
    dim = getSize();
    modeChanged(EditMode.Disabled);

    addMouseListener(mouseHandler);
    addMouseMotionListener(mouseHandler);
  }

  @Override
  protected void paintComponent(Graphics g) {
    // System.out.println("repaint: " + this.getClass().getSimpleName() + " clip: " + g.getClip());

    ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    EditMode mode = em.getCurrentMode();
    if ((mode == EditMode.Add || mode == EditMode.Moving) && mouseHandler.dragItem != null) {
      g.setColor(dragColor);
      ItemRenderer.render2D(mouseHandler.dragItem, g, dim);
    }
  }

  private void modeChanged(EditMode oldMode) {
    switch (oldMode) {
      case Disabled:
        setToolTipText(null);
        break;
      case Select:
        setToolTipText(null);
        break;
      case Moving:
        mouseHandler.clearDragItem();
        break;
      case Add:
        mouseHandler.clearDragItem();
        break;
      case Remove:
        break;
    }

    EditMode newMode = em.getCurrentMode();
    switch (newMode) {
      case Disabled:
        setToolTipText(null);
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        break;
      case Select:
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        break;
      case Moving:
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        break;
      case Add:
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        break;
      case Remove:
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        break;
    }

    refreshCursor();
  }

  private void refreshCursor() throws HeadlessException {
    refreshingCursor = true;
    try {
      // Szimuláljuk az első egérmozgást
      Point mp = getMousePosition();
      if (mp != null) {
        processMouseMotionEvent(new MouseEvent(this, MouseEvent.MOUSE_MOVED, 0, 0, mp.x, mp.y, 0, false));
      }
    } finally {
      refreshingCursor = false;
    }
  }

  private String formatCell(Cell cell) {
    return cell != null ? String.format("(%d, %d)", cell.getX(), cell.getY()) : null;
  }

  private class MouseHandler extends MouseAdapter {

    private Item dragItem;

    private Cell getCurrentCell(MouseEvent e) {
      return cellFinder.findCell(e.getX(), e.getY(), dim);
    }

    private Item getCurrentItem(MouseEvent e) {
      return getCurrentItem(getCurrentCell(e));
    }

    private Item getCurrentItem(Cell cell) {
      return cell != null ? lc.getLayout().getItem(cell) : null;
    }

    private void repaintItem(Item item) {
      if (item != null) {
        int[][] ps = Transformer.project(item.getRect(), null, dim);
        repaint(ps[0][0], ps[0][1], ps[1][0] - ps[0][0] + 1, ps[3][1] - ps[0][1] + 1);
      }
    }

    private void refreshDragItem(Item item) {
      if (!Objects.equals(item, dragItem)) {
        repaintItem(dragItem);
        dragItem = item;
        repaintItem(dragItem);
        setToolTipText(dragItem != null ? formatCell(dragItem.getCenter()) : null);
      }
    }

    private void refreshDragItem(ItemType type, Cell center) {
      refreshDragItem(Item.isValid(type, center) ? new Item(type, center) : null);
    }

    private void refreshAddDragItem(MouseEvent e) {
      refreshDragItem(em.getAddItemType(), getCurrentCell(e));
    }

    private void refreshMoveDragItem(MouseEvent e) {
      Cell target = getCurrentCell(e);
      refreshDragItem(target != null ? em.getActiveItem().moveByReference(em.getMovingReferenceCell(), target) : null);
    }

    void clearDragItem() {
      refreshDragItem(null);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
      EditMode mode = em.getCurrentMode();

      if (mode == EditMode.Select) {
        Item item = getCurrentItem(e);
        if (item != null) {
          setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
          setToolTipText(formatCell(item.getCenter()));
        } else {
          setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
          setToolTipText(null);
        }
      } else if (mode == EditMode.Moving) {
        refreshMoveDragItem(e);
      } else if (mode == EditMode.Add) {
        refreshAddDragItem(e);
      } else if (mode == EditMode.Remove) {
        Item item = getCurrentItem(e);
        if (item != null) {
          setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
          setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
      } else if (mode == EditMode.Disabled) {
        if (!frameSlider.isFirstFrame() && !refreshingCursor) {
          setToolTipText(formatCell(getCurrentCell(e)));
        }
      }
    }

    @Override
    public void mousePressed(MouseEvent e) {
      EditMode mode = em.getCurrentMode();

      if (mode == EditMode.Select) {
        if (e.getButton() == MouseEvent.BUTTON1) {
          Cell cell = getCurrentCell(e);
          Item item = getCurrentItem(cell);
          if (item != null) {
            em.setActiveItem(item);
            em.startMoving(cell);
          }
        }
      } else if (mode == EditMode.Moving) {
        if (e.getButton() == MouseEvent.BUTTON1 && !e.isShiftDown()) {
          if (dragItem != null) {
            em.finishMoving(getCurrentCell(e));
            dragItem = null;
          }
        } else {
          em.cancelMoving();
          em.setActiveItem(null);
        }
      } else if (mode == EditMode.Add) {
        if (e.getButton() == MouseEvent.BUTTON1 && !e.isShiftDown()) {
          if (dragItem != null) {
            em.addItem(dragItem);
            dragItem = null;
          }
        } else {
          em.switchAddItemType();
          refreshAddDragItem(e);
        }
      } else if (mode == EditMode.Remove) {
        if (e.getButton() == MouseEvent.BUTTON1) {
          Item item = getCurrentItem(e);
          if (item != null) {
            em.removeItem(item);
          }
        }
      }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
      EditMode mode = em.getCurrentMode();

      if (mode == EditMode.Moving) {
        refreshMoveDragItem(e);
      }
    }
  }
}
