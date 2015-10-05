package hu.akusius.palenque.layout.op;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Timer;

/**
 * A lejátszást kezelő osztály.
 * @author Bujdosó Ákos
 */
public class PlayManager {

  private final Timer playTimer;

  private final PropToggle playingToggle;

  private final PropSliderFrame frameSlider;

  private final PropAction toStartAction;

  private final PropAction toEndAction;

  private final PropSlider speedSlider;

  private final PropToggle quickAnimationToggle;

  private Integer oldSpeedValue;

  /**
   * Létrehozás egyedi {@link Timer}-rel, főleg tesztelési célból.
   * @param timer Az egyedi {@link Timer} az időzítésekhez.
   */
  PlayManager(Timer timer) {
    if (timer == null) {
      throw new IllegalArgumentException();
    }
    this.playTimer = timer;
    this.playTimer.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        timerTick();
      }
    });
    this.frameSlider = new PropSliderFrame();
    this.frameSlider.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (PropSliderFrame.PROP_VALUE.equals(evt.getPropertyName())) {
          refreshStates();
        }
      }
    });
    this.speedSlider = new PropSlider(0, 6, 3);
    this.playingToggle = new PropToggle(false);
    this.playingToggle.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (PropToggle.PROP_SELECTED.equals(evt.getPropertyName())) {
          if ((boolean) evt.getNewValue()) {
            if (frameSlider.isLastFrame()) {
              frameSlider.setValueInternal(0);
            }
            playTimer.start();
          } else {
            playTimer.stop();
          }
        }
      }
    });
    this.quickAnimationToggle = new PropToggle(false);
    this.quickAnimationToggle.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (PropToggle.PROP_SELECTED.equals(evt.getPropertyName())) {
          if ((boolean) evt.getNewValue()) {
            assert speedSlider.isEnabled();
            oldSpeedValue = speedSlider.getValue();
            speedSlider.setValueInternal(speedSlider.getMax());
            speedSlider.setEnabledInternal(false);
          } else {
            assert oldSpeedValue != null;
            assert !speedSlider.isEnabled();
            speedSlider.setEnabledInternal(true);
            speedSlider.setValueInternal(oldSpeedValue);
          }
          refreshStates();
        }
      }
    });
    this.toStartAction = new PropAction() {
      @Override
      protected void action(Object param) {
        frameSlider.setValueInternal(0);
        refreshStates();
      }
    };
    this.toEndAction = new PropAction() {
      @Override
      protected void action(Object param) {
        playingToggle.setSelectedInternal(false);
        frameSlider.setValueInternal(frameSlider.getMax());
        refreshStates();
      }
    };
    refreshStates();
  }

  public PlayManager() {
    this(new Timer(25, null));
  }

  /**
   * @return A képkockát kezelő tulajdonság.
   */
  public PropSliderFrame getFrameSlider() {
    return frameSlider;
  }

  /**
   * @return A sebességet kezelő tulajdonság.
   */
  public PropSlider getSpeedSlider() {
    return speedSlider;
  }

  /**
   * @return A lejátszási állapotot kezelő tulajdonság.
   */
  public PropToggle getPlayingToggle() {
    return playingToggle;
  }

  /**
   * @return A gyorsanimációt kezelő tulajdonság. Bekapcsolva állapot esetén mindig maximális sebességű a lejátszás.
   */
  public PropToggle getQuickAnimationToggle() {
    return quickAnimationToggle;
  }

  /**
   * @return Az elejére ugrást kezelő tulajdonság.
   */
  public PropAction getToStartAction() {
    return toStartAction;
  }

  /**
   * @return A végére ugrást kezelő tulajdonság.
   */
  public PropAction getToEndAction() {
    return toEndAction;
  }

  /**
   * A lejátszási állapot reszetelése (alapállapotba állítás).
   */
  public void reset() {
    playingToggle.setSelectedInternal(false);
    frameSlider.setValueInternal(0);
  }

  private boolean enabled = true;

  /**
   * @return A jelenlegi engedélyezettségi állapot.
   */
  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Az engedélyezettségi állapot beállítása.
   * @param enabled Az új engedélyezettségi állapot.
   */
  public void setEnabled(boolean enabled) {
    if (this.enabled == enabled) {
      return;
    }
    this.enabled = enabled;
    refreshStates();
  }

  private void timerTick() {
    int frame = frameSlider.getValue();
    frame += (int) Math.pow(2, speedSlider.getValue());
    if (frame >= frameSlider.getMax()) {
      frame = frameSlider.getMax();
      playingToggle.setSelectedInternal(false);
    }
    frameSlider.setValueInternal(frame);
  }

  private void refreshStates() {
    playingToggle.setEnabledInternal(this.enabled);
    frameSlider.setEnabledInternal(this.enabled);
    toStartAction.setEnabledInternal(this.enabled && !frameSlider.isFirstFrame());
    toEndAction.setEnabledInternal(this.enabled && !frameSlider.isLastFrame());
    quickAnimationToggle.setEnabledInternal(this.enabled);
    speedSlider.setEnabledInternal(this.enabled && !quickAnimationToggle.isSelected());
  }

}
