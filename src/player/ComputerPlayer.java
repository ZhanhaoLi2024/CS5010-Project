package player;

import item.Item;
import java.util.Random;
import place.Place;

/**
 * The ComputerPlayer class extends the PlayerModel class and represents a computer
 * player in a game. A computer player has a name, a current place, a list of items,
 * and an item limit.
 */
public class ComputerPlayer extends PlayerModel {
  private final Random random;

  /**
   * Constructs a new PlayerModel with the specified name, starting place, item
   * limit, and random number generator for random actions.
   *
   * @param name       the name of the player
   * @param startPlace the place where the player starts
   * @param itemLimit  the maximum number of items the player can carry
   * @param random     the random number generator used for picking up random items
   */
  public ComputerPlayer(String name, Place startPlace, int itemLimit, Random random) {
    super(name, startPlace, itemLimit);
    this.random = random;
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
