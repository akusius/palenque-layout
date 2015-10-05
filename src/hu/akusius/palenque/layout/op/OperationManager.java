package hu.akusius.palenque.layout.op;

import hu.akusius.palenque.layout.data.Layout;
import hu.akusius.palenque.layout.data.LayoutContainer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Timer;

/**
 * A műveleteket összefogó és kezelő osztály.
 * @author Bujdosó Ákos
 */
public class OperationManager {

  private final LayoutContainer layoutContainer;

  private final EditManager editManager;

  private final PlayManager playManager;

  private final HistoryManager historyManager;

  private final LayoutManager layoutManager;

  private final DisplayManager displayManager;

  private final OpacityManager opacityManager;

  private final MemoryManager memoryManager;

  private final PropToggle normalizedViewToggle;

  private final PropAction modifyOpacityAction;

  private final PropAction showDataAction;

  private final PropAction screenshotAction;

  private final PropAction infoAction;

  public OperationManager() {
    this(null);
  }

  /**
   * Létrehozás saját {@link Timer}-rel, főleg tesztelési célból.
   * @param playerTimer A használt időzítő.
   */
  OperationManager(Timer playerTimer) {
    this.layoutContainer = new LayoutContainer(Layout.createDefault());
    this.layoutContainer.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (LayoutContainer.PROP_LAYOUT.equals(evt.getPropertyName())) {
          refreshStates();
        }
      }
    });
    this.editManager = new EditManager(layoutContainer);
    this.editManager.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (EditManager.PROP_CURRENTMODE.equals(evt.getPropertyName())) {
          refreshStates();
        }
      }
    });
    this.playManager = playerTimer == null ? new PlayManager() : new PlayManager(playerTimer);
    this.playManager.getFrameSlider().addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (PropSlider.PROP_VALUE.equals(evt.getPropertyName())) {
          refreshStates();
        }
      }
    });
    this.playManager.getPlayingToggle().addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (PropToggle.PROP_SELECTED.equals(evt.getPropertyName())) {
          refreshStates();
        }
      }
    });
    this.historyManager = new HistoryManager(layoutContainer);
    this.layoutManager = new LayoutManager(layoutContainer);
    this.displayManager = new DisplayManager(playManager.getFrameSlider());
    this.displayManager.getShowLidToggle().setSelected(true);
    this.displayManager.getShowNumbersToggle().setSelected(true);
    this.memoryManager = new MemoryManager(layoutContainer, 3);
    this.opacityManager = new OpacityManager();
    this.normalizedViewToggle = new PropToggle(true);
    this.modifyOpacityAction = new PropAction();
    this.showDataAction = new PropAction();
    this.screenshotAction = new PropAction();
    this.infoAction = new PropAction();
  }

  /**
   * @return
   */
  public LayoutContainer getLayoutContainer() {
    return layoutContainer;
  }

  /**
   * @return
   */
  public EditManager getEditManager() {
    return editManager;
  }

  /**
   * @return
   */
  public PlayManager getPlayManager() {
    return playManager;
  }

  /**
   * @return
   */
  public HistoryManager getHistoryManager() {
    return historyManager;
  }

  /**
   * @return
   */
  public LayoutManager getLayoutManager() {
    return layoutManager;
  }

  /**
   * @return
   */
  public DisplayManager getDisplayManager() {
    return displayManager;
  }

  /**
   * @return
   */
  public OpacityManager getOpacityManager() {
    return opacityManager;
  }

  /**
   * @return
   */
  public MemoryManager getMemoryManager() {
    return memoryManager;
  }

  /**
   * @return A normalizált megjelenítést vezérlő tulajdonság.
   */
  public PropToggle getNormalizedViewToggle() {
    return normalizedViewToggle;
  }

  /**
   * @return Az átlátszatlanság módosítását indító művelet.
   */
  public PropAction getModifyOpacityAction() {
    return modifyOpacityAction;
  }

  /**
   * @return Az adatok megjelenítését indító művelet.
   */
  public PropAction getShowDataAction() {
    return showDataAction;
  }

  /**
   * @return A képernyőkép mentését vezérlő művelet.
   */
  public PropAction getScreenshotAction() {
    return screenshotAction;
  }

  /**
   * @return Az információk megjelenítését kezelő művelet.
   */
  public PropAction getInfoAction() {
    return infoAction;
  }

  private void refreshStates() {
    PropSliderFrame fs = playManager.getFrameSlider();
    boolean playing = playManager.getPlayingToggle().isSelected();
    EditMode editMode = editManager.getCurrentMode();
    boolean editing = editMode == EditMode.Moving || editMode == EditMode.Add || editMode == EditMode.Remove;

    editManager.setEnabled(fs.isFirstFrame() && !playing);
    playManager.setEnabled(!editing);
    historyManager.setEnabled(!editing && !playing);
    layoutManager.setEnabled(!editing && !playing);
    memoryManager.setEnabled(!editing && !playing);
    displayManager.setEnabled(!playing);
    opacityManager.setEnabled(!editing && !playing);
    normalizedViewToggle.setEnabled(!editing & !playing);
    modifyOpacityAction.setEnabled(!editing && !playing);
    showDataAction.setEnabled(!editing && !playing);
    screenshotAction.setEnabled(!editing && !playing);
    infoAction.setEnabled(!editing && !playing);
  }
}
