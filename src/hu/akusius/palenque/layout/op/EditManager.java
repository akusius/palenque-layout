package hu.akusius.palenque.layout.op;

import hu.akusius.palenque.layout.data.*;
import java.beans.*;

/**
 * A szerkesztést kezelő osztály.
 * @author Bujdosó Ákos
 */
public class EditManager {

  private final LayoutContainer layoutContainer;

  private final PropToggle selectToggle;

  private final PropToggle addToggle;

  private final PropToggle removeToggle;

  public EditManager(LayoutContainer layoutContainer) {
    if (layoutContainer == null) {
      throw new IllegalArgumentException();
    }
    this.layoutContainer = layoutContainer;
    this.layoutContainer.addPropertyChangeListener(new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        assert LayoutContainer.PROP_LAYOUT.equals(evt.getPropertyName());
        if (currentMode != EditMode.Disabled) {
          setCurrentMode(EditMode.Select);
        }
      }
    });
    this.selectToggle = new PropToggle(true);
    this.addToggle = new PropToggle(false);
    this.removeToggle = new PropToggle(false);
    PropToggle.configGroup(this.selectToggle, this.addToggle, this.removeToggle);
    this.currentMode = EditMode.Select;

    PropertyChangeListener toggleSelected = new PropertyChangeListener() {
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (PropToggle.PROP_SELECTED.equals(evt.getPropertyName()) && (boolean) evt.getNewValue()) {
          if (evt.getSource() == selectToggle) {
            setCurrentMode(EditMode.Select);
          } else if (evt.getSource() == addToggle) {
            setCurrentMode(EditMode.Add);
          } else if (evt.getSource() == removeToggle) {
            setCurrentMode(EditMode.Remove);
          }
        }
      }
    };
    this.selectToggle.addPropertyChangeListener(toggleSelected);
    this.addToggle.addPropertyChangeListener(toggleSelected);
    this.removeToggle.addPropertyChangeListener(toggleSelected);
  }

  public LayoutContainer getLayoutContainer() {
    return layoutContainer;
  }

  /**
   * @return A kiválasztás műveletet kezelő tulajdonság.
   */
  public PropToggle getSelectToggle() {
    return selectToggle;
  }

  /**
   * @return A hozzáadás műveletet kezelő tulajdonság.
   */
  public PropToggle getAddToggle() {
    return addToggle;
  }

  /**
   * @return A törlés műveletet kezelő tulajdonság.
   */
  public PropToggle getRemoveToggle() {
    return removeToggle;
  }

  private EditMode currentMode;

  public static final String PROP_CURRENTMODE = "currentMode";

  /**
   * @return Az aktuális szerkesztési mód.
   */
  public EditMode getCurrentMode() {
    return currentMode;
  }

  private void setCurrentMode(EditMode newMode) {
    EditMode oldMode = this.currentMode;
    switch (newMode) {
      case Select:
        activeItem = null;
        addItemType = null;
        movingReferenceCell = null;
        selectToggle.setEnabledInternal(true);
        addToggle.setEnabledInternal(layoutContainer.getLayout().canAddMore());
        removeToggle.setEnabledInternal(true);
        selectToggle.setSelectedInternal(true);
        break;
      case Moving:
        assert oldMode == EditMode.Select;
        addItemType = null;
        addToggle.setEnabledInternal(false);
        removeToggle.setEnabledInternal(false);
        break;
      case Add:
        activeItem = null;
        addItemType = ItemType.Triplet;
        movingReferenceCell = null;
        break;
      case Remove:
        activeItem = null;
        addItemType = null;
        movingReferenceCell = null;
        break;
      case Disabled:
        activeItem = null;
        addItemType = null;
        movingReferenceCell = null;
        selectToggle.setEnabledInternal(false);
        addToggle.setEnabledInternal(false);
        removeToggle.setEnabledInternal(false);
        break;
      default:
        throw new AssertionError();
    }
    this.currentMode = newMode;
    propertyChangeSupport.firePropertyChange(PROP_CURRENTMODE, oldMode, newMode);
  }

  private Item activeItem;

  public static final String PROP_ACTIVEITEM = "activeItem";

  /**
   * @return Az aktuális elem, vagy {@code null}, ha nincs ilyen.
   */
  public Item getActiveItem() {
    return activeItem;
  }

  /**
   * Az aktuális elem beállítása.
   * @param activeItem Az új aktuális elem, vagy {@code null}, ha nincs ilyen.
   * @throws IllegalArgumentException Érvénytelen elem lett megadva.
   */
  public void setActiveItem(Item activeItem) {
    if (activeItem != null && !this.layoutContainer.getLayout().hasItem(activeItem)) {
      throw new IllegalArgumentException();
    }
    Item oldActiveItem = this.activeItem;
    this.activeItem = activeItem;
    propertyChangeSupport.firePropertyChange(PROP_ACTIVEITEM, oldActiveItem, activeItem);
  }

  /**
   * Az aktív elem mozgatásának elkezdése.
   * @param reference A mozgatási referenciacella.
   */
  public void startMoving(Cell reference) {
    if (this.currentMode != EditMode.Select || this.activeItem == null) {
      throw new IllegalStateException();
    }
    if (reference == null) {
      throw new IllegalArgumentException();
    }
    movingReferenceCell = reference;
    setCurrentMode(EditMode.Moving);
  }

  private Cell movingReferenceCell;

  /**
   * @return A mozgatási referenciacella, vagy {@code null}, ha nincsen mozgatás.
   */
  public Cell getMovingReferenceCell() {
    return movingReferenceCell;
  }

  /**
   * Az aktuális mozgatási művelet megszakítása.
   */
  public void cancelMoving() {
    if (this.currentMode != EditMode.Moving) {
      throw new IllegalStateException();
    }
    setCurrentMode(EditMode.Select);
  }

  /**
   * Az aktuális mozgatási művelet befejezése.
   * @param target A mozgatási cél.
   */
  public void finishMoving(Cell target) {
    if (this.currentMode != EditMode.Moving || this.activeItem == null) {
      throw new IllegalStateException();
    }
    if (target == null) {
      throw new IllegalArgumentException();
    }
    Item newItem = activeItem.moveByReference(movingReferenceCell, target);
    if (newItem == null) {
      throw new IllegalArgumentException();
    }
    Layout newLayout = layoutContainer.getLayout().moveItem(activeItem, newItem.getCenter());
    layoutContainer.setLayout(newLayout);
  }

  private ItemType addItemType;

  public static final String PROP_ADDITEMTYPE = "addItemType";

  /**
   * @return Az éppen hozzáadandó elem típusa, vagy {@code null}, ha nem hozzáadás módban vagyunk.
   */
  public ItemType getAddItemType() {
    return addItemType;
  }

  private void setAddItemType(ItemType addItemType) {
    ItemType oldAddItemType = this.addItemType;
    this.addItemType = addItemType;
    propertyChangeSupport.firePropertyChange(PROP_ADDITEMTYPE, oldAddItemType, addItemType);
  }

  /**
   * Léptetés a következő elemtípusra hozzáadáshoz.
   */
  public void switchAddItemType() {
    if (currentMode != EditMode.Add || addItemType == null) {
      throw new IllegalStateException();
    }
    final ItemType newType;
    switch (addItemType) {
      case Triplet:
        newType = ItemType.Sun;
        break;
      case Sun:
        newType = ItemType.Star;
        break;
      case Star:
        newType = ItemType.Triplet;
        break;
      default:
        throw new AssertionError();
    }
    setAddItemType(newType);
  }

  /**
   * A megadott elem hozzáadása az elrendezéshez.
   * Ezt használjuk a közvetlen hozzáadás ({@link Layout#addItem(Item)}) helyett, hogy az üzemmódok szinkronban maradjanak.
   * @param item A hozzáadandó elem.
   */
  public void addItem(Item item) {
    if (currentMode != EditMode.Add) {
      throw new IllegalStateException();
    }
    if (item == null) {
      throw new IllegalArgumentException();
    }
    Layout newLayout = layoutContainer.getLayout().addItem(item);
    layoutContainer.setLayout(newLayout);
  }

  /**
   * A megadott elem törlése az elrendezésből.
   * Ezt használjuk a közvetlen törlés ({@link Layout#removeItem(Item)}) helyett, hogy az üzemmódok szinkronban maradjanak.
   * @param item A törlendő elem.
   */
  public void removeItem(Item item) {
    if (currentMode != EditMode.Remove) {
      throw new IllegalStateException();
    }
    if (item == null) {
      throw new IllegalArgumentException();
    }
    Layout newLayout = layoutContainer.getLayout().removeItem(item);
    layoutContainer.setLayout(newLayout);
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
    setCurrentMode(enabled ? EditMode.Select : EditMode.Disabled);
  }

  private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.removePropertyChangeListener(listener);
  }

}
