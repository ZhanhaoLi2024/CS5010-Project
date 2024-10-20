package player;

import item.Item;
import java.util.List;
import place.Place;

/**
 * The Player interface defines the basic behaviors and attributes of a player in
 * a game. A player has a name, a current place, and a list of items.
 */
public interface Player {
  /**
   * Moves the player to a neighboring place.
   *
   * @param newPlace the neighboring place to move to
   */
  void moveToNeighbor(Place newPlace);

  /**
   * Picks up an item from the current place.
   *
   * @param item the item to pick up
   */
  void pickUpItem(Item item);

  /**
   * Looks around the current place.
   */
  void lookAround();

  /**
   * Retrieves the name of the player.
   *
   * @return the name of the player
   */
  String getDescription();

  /**
   * Retrieves the name of the player.
   *
   * @return the name of the player
   */
  String getName();

  /**
   * Retrieves the current place of the player.
   *
   * @return the current place of the player
   */
  Place getCurrentPlace();

  /**
   * Retrieves the items the player is carrying.
   *
   * @return the items the player is carrying
   */
  List<Item> getItems();
}