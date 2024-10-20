package controller;

import item.Item;
import java.util.Random;
import place.Place;
import player.ComputerPlayer;

/**
 * The ComputerPlayerController class is responsible for controlling a computer
 * player in the game.
 */
public class ComputerPlayerController extends PlayerControllerModel {
  private final Random random;

  /**
   * Constructs a new ComputerPlayerController with the specified computer player and random
   * number generator.
   *
   * @param player the computer player to control
   * @param random the random number generator to use
   */
  public ComputerPlayerController(ComputerPlayer player, Random random) {
    super(player);
    this.random = random;
  }

  @Override
  public void takeTurn() {
    System.out.println("It's " + player.getName() + "'s turn (Computer).");

    int action = random.nextInt(3);
    switch (action) {
      case 0:
        movePlayerRandomly();
        break;
      case 1:
        pickUpRandomItem();
        break;
      case 2:
        lookAround();
        break;
      default:
        System.out.println(player.getName() + " does nothing this turn.");
    }
  }

  /**
   * Makes the computer player look around the place they are currently in.
   */
  private void pickUpRandomItem() {
    if (player.getCurrentPlace().getItems().isEmpty()) {
      System.out.println(player.getName() + " finds no items to pick up.");
      return;
    }
    Item item = player.getCurrentPlace().getItems()
        .get(random.nextInt(player.getCurrentPlace().getItems().size()));
    pickUpItem(item);
  }

  /**
   * Makes the computer player move to a random neighboring place.
   */
  private void movePlayerRandomly() {
    if (player.getCurrentPlace().getNeighbors().isEmpty()) {
      System.out.println(player.getName() + " has no neighboring places to move to.");
      return;
    }
    Place newPlace = player.getCurrentPlace().getNeighbors()
        .get(random.nextInt(player.getCurrentPlace().getNeighbors().size()));
    movePlayer(newPlace);
  }
}
