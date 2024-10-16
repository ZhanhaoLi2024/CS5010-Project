package model.player;

import java.util.Map;
import model.item.Item;
import model.place.Place;

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
  Place getCurrentPlace();

  /**
   * Move the player to a specified neighboring place.
   *
   * @param newPlace the Place to move the player to.
   * @throws IllegalArgumentException if the place is not a valid neighbor.
   */
  void moveTo(Place newPlace) throws IllegalArgumentException;

  /**
   * Allow the player to pick up an item from the current location.
   *
   * @param item the Item to be picked up.
   * @throws IllegalStateException if the player cannot carry any more items.
   */
  void pickUpItem(Item item) throws IllegalStateException;

  /**
   * Retrieve a list of the items currently in the player's inventory.
   *
   * @return a Map where the keys are item names and the values are item details (such as damage or quantity).
   */
  Map<String, Integer> getInventory();

  /**
   * Get the maximum number of items the player can carry.
   *
   * @return the limit of items the player can carry.
   */
  int getCarryLimit();

  /**
   * Return a detailed description of the player, including name, current place, and items carried.
   *
   * @return the description of the player.
   */
  String getDescription();
}