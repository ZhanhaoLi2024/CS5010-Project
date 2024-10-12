package item;

/**
 * The Item interface defines the basic behaviors and attributes of an item in a
 * game. An item has a name and a damage value.
 */
public interface Item {
  /**
   * Retrieves the name of the item.
   *
   * @return the name of the item as a String.
   */
  String getName();

  /**
   * Retrieves the damage value of the item.
   *
   * @return the damage value of the item as an integer.
   */
  int getDamage();
}