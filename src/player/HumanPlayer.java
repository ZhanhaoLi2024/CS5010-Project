package player;

import item.Item;
import place.Place;

/**
 * The HumanPlayer class represents a human player in the game.
 */
public class HumanPlayer extends PlayerModel {
  /**
   * Constructs a new PlayerModel with the specified name, starting place, and item
   * limit.
   *
   * @param name       the name of the player
   * @param startPlace the place where the player starts
   * @param itemLimit  the maximum number of items the player can carry
   */
  public HumanPlayer(String name, Place startPlace, int itemLimit) {
    super(name, startPlace, itemLimit);
  }

  /**
   * Moves the player to a neighboring place.
   *
   * @param newPlace the neighboring place the player wants to move to
   * @throws IllegalArgumentException if the new place is null
   * @throws IllegalStateException    if the current place is null
   */
  public void moveToNeighbor(Place newPlace) {
    if (newPlace == null) {
      throw new IllegalArgumentException("Place cannot be null.");
    }
    if (currentPlace == null) {
      throw new IllegalStateException("Current place is null, the player cannot move.");
    }
    if (currentPlace.isNeighbor(newPlace)) {
      super.moveToNeighbor(newPlace);
      System.out.println(name + " moves to " + newPlace.getName());
    } else {
      System.out.println("Invalid move. " + newPlace.getName() + " is not a neighbor.");
    }
  }

  /**
   * Picks up an item from the current place.
   *
   * @param item the item to be picked up
   * @throws IllegalArgumentException if the item is null
   */
  public void pickUpItem(Item item) {
    if (item == null) {
      throw new IllegalArgumentException("Item cannot be null.");
    }
    if (currentPlace.getItems().contains(item)) {
      super.pickUpItem(item);
      System.out.println(name + " picked up " + item.getName());
    } else {
      System.out.println("Item not found in the current place.");
    }
  }

  /**
   * Allows the player to look around the current place.
   */
  public void lookAround() {
    System.out.println(name + " is looking around...");
    super.lookAround();
  }

  /**
   * Returns a description of the player.
   *
   * @return a description of the player
   */
  public String getDescription() {
    return super.getDescription();
  }
}
