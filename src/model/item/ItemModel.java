package model.item;

/**
 * The ItemModel class represents an item in a game. An item has a name and a damage value.
 */
public class ItemModel implements Item {
  private final String name;
  private final int damage;

  /**
   * Constructs a new ItemModel with the specified name and damage value.
   *
   * @param name   the name of the item
   * @param damage the damage value of the item
   * @throws IllegalArgumentException if the name is null or empty or if the damage value is negative
   */
  public ItemModel(String name, int damage) {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("Item name cannot be null or empty.");
    }
    if (damage < 0) {
      throw new IllegalArgumentException("Damage value must be non-negative.");
    }
    this.name = name;
    this.damage = damage;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int getDamage() {
    return damage;
  }
}
