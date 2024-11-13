package model.item;

/**
 * The Item interface defines the contract for items that can be found and used in the game.
 * Each item has a name and associated damage value for use in gameplay mechanics.
 */
public interface Item {
  /**
   * Retrieves the name of this item.
   *
   * @return the name of the item as a String
   */
  String getName();

  /**
   * Retrieves the damage value this item can inflict when used.
   *
   * @return the damage value as an integer
   */
  int getDamage();
}