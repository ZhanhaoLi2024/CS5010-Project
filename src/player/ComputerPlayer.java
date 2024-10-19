package player;

import item.Item;
import place.Place;

public class ComputerPlayer extends PlayerModel {
  private final Random random;

  /**
   * Constructs a new PlayerModel with the specified name, starting place, and item
   * limit.
   *
   * @param name       the name of the player
   * @param startPlace the place where the player starts
   * @param itemLimit  the maximum number of items the player can carry
   */
  public ComputerPlayer(String name, Place startPlace, int itemLimit) {
    super(name, startPlace, itemLimit);
    this.random = random;
  }

//  /**
//   * Makes the computer player take a turn by randomly choosing an action to perform.
//   */
//  public void takeTurn() {
//    int action = random.nextInt(3);
//    switch (action) {
//      case 0:
//        moveRandomly();
//        break;
//      case 1:
//        pickUpRandomItem();
//        break;
//      case 2:
//        lookAround();
//        break;
//      default:
//        System.out.println(name + " does nothing this turn.");
//        break;
//    }
//  }

  /**
   * Moving to a random neighboring place.
   */
  public void moveRandomly() {
    if (currentPlace.getNeighbors().isEmpty()) {
      System.out.println(name + " has no neighboring places to move to.");
      return;
    }
    Place nextPlace =
        currentPlace.getNeighbors().get(random.nextInt(currentPlace.getNeighbors().size()));
    moveToNeighbor(nextPlace);
    System.out.println(name + " moves to " + nextPlace.getName());
  }

  /**
   * Picking up an item from the space they are currently occupying.
   */
  public void pickUpRandomItem() {
    if (currentPlace.getItems().isEmpty()) {
      System.out.println(name + " finds no items to pick up.");
      return;
    }
    Item item = currentPlace.getItems().get(random.nextInt(currentPlace.getItems().size()));
    pickUpItem(item);
    System.out.println(name + " picked up " + item.getName());
  }

  public void lookAround() {
    System.out.println(name + " is looking around...");
    super.lookAround();
  }

    public String getDescription() {
        return super.getDescription();
    }
}

}
