package model.player;

import java.util.List;
import model.item.Item;

/**
 * The Player interface defines the basic behaviors and attributes for a player in the game.
 */
public interface Player {

  /**
   * Get the name of the player.
   *
   * @return the player's name as a String.
   */
  String getName();

  /**
   * Check if the player is controlled by a computer.
   *
   * @return true if the player is computer-controlled, false otherwise.
   */
  boolean isComputerControlled();

  /**
   * Retrieve the current place where the player is located.
   *
   * @return the current Place the player is in.
   */
  int getPlayerCurrentPlaceNumber();

  /**
   * Move the player to a specified neighboring place.
   *
   * @param placeNumber the number of the place to move to.
   * @throws IllegalArgumentException if the place is not a valid neighbor.
   */
  void moveToPlaceNumber(int placeNumber) throws IllegalArgumentException;

  /**
   * Allow the player to pick up an item from the current location.
   *
   * @param item the Item to be picked up.
   * @throws IllegalStateException if the player cannot carry any more items.
   */
  void pickUpItem(Item item) throws IllegalStateException;

  /**
   * Get the item currently held by the player.
   *
   * @return the item currently held by the player.
   */
  List<Item> getCurrentCarriedItems();

  /**
   * Get the maximum number of items the player can carry.
   *
   * @return the limit of items the player can carry.
   */
  int getCarryLimit();


}