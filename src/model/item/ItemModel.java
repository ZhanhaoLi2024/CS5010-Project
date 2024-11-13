package model.item;

import java.util.Objects;

/**
 * The ItemModel class represents an item in a game. An item has a name and a damage value.
 */
public class ItemModel implements Item {
  private final String name;
  private final int damage;

  /**
   * Constructs a new ItemModel with the specified name and damage value.
   *
   * @param itemName   the name of the item
   * @param itemDamage the damage value of the item
   * @throws IllegalArgumentException if the name is null or empty or
   *                                  if the damage value is negative
   */
  public ItemModel(String itemName, int itemDamage) {
    if (itemName == null || itemName.isEmpty()) {
      throw new IllegalArgumentException("Item name cannot be null or empty.");
    }
    if (itemDamage < 0) {
      throw new IllegalArgumentException("Damage value must be non-negative.");
    }
    this.name = itemName;
    this.damage = itemDamage;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int getDamage() {
    return damage;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ItemModel)) {
      return false;
    }
    ItemModel other = (ItemModel) obj;
    return damage == other.damage
        && Objects.equals(name, other.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, damage);
  }
}
