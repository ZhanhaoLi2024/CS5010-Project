package model.dto;

/**
 * Represents an item.
 * This class is used to store the name and damage of an item.
 */
public class ItemDTO {
  private final String name;
  private final int damage;

  /**
   * Constructs an ItemDTO object with the specified name and damage.
   *
   * @param name   the name of the item
   * @param damage the damage to the item
   */
  public ItemDTO(String name, int damage) {
    this.name = name;
    this.damage = damage;
  }

  /**
   * Returns the name of the item.
   *
   * @return the name of the item
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the damage to the item.
   *
   * @return the damage to the item
   */
  public int getDamage() {
    return damage;
  }
}
