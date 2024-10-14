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
   * Moving to a neighboring place.
   */
  public void moveToNeighbor(Place newPlace) {
    if (currentPlace.isNeighbor(newPlace)) {
      super.moveToNeighbor(newPlace);
      System.out.println(name + " moves to " + newPlace.getName());
    } else {
      System.out.println("Invalid move. " + newPlace.getName() + " is not a neighbor.");
    }
  }

  /**
   * Picking up an item from the space they are currently occupying.
   */
  public void pickUpItem(Item item) {
    if (currentPlace.getItems().contains(item)) {
      super.pickUpItem(item);
      System.out.println(name + " picked up " + item.getName());
    } else {
      System.out.println("Item not found in the current place.");
    }
  }

  public void lookAround() {
    System.out.println(name + " is looking around...");
    super.lookAround();
  }

  public String getDescription() {
    return super.getDescription();
  }
}
