package controller;

import item.Item;
import java.util.List;
import java.util.Scanner;
import place.Place;
import player.PlayerModel;

/**
 * The HumanPlayerController class extends the PlayerControllerModel class and
 * represents a human player in a
 * game. It allows the player to take a turn by choosing an action to perform.
 */
public class HumanPlayerController extends PlayerControllerModel {
  private final Scanner scanner;

  /**
   * Constructs a new HumanPlayerController with the specified player.
   *
   * @param player the player to control
   */
  public HumanPlayerController(PlayerModel player) {
    super(player);
    this.scanner = new Scanner(System.in);
  }

  /**
   * Allows the player to take a turn by choosing an action to perform.
   */
  @Override
  public void takeTurn() {
    System.out.println("Choose an action: ");
    System.out.println("1. Move to a neighboring place");
    List<Item> currentPlaceItems = player.getCurrentPlace().getItems();
    System.out.print("2. Pick up an item (Current place item: ");
    if (currentPlaceItems.isEmpty()) {
      System.out.println("No items");
    } else {
      for (Item item : currentPlaceItems) {
        System.out.print(item.getName() + " - Damage:" + item.getDamage());
      }
    }
    System.out.println(")");
    System.out.println("3. Look around");

    int choice = scanner.nextInt();
    scanner.nextLine();

    switch (choice) {
      case 1:
        movePlayer();
        break;
      case 2:
        pickUpItem();
        break;
      case 3:
        player.lookAround();
        break;
      default:
        System.out.println("Invalid choice.");
    }
  }

  /**
   * Closes the scanner used to read input from the player.
   */
  private void movePlayer() {
    System.out.println("Current location: " + player.getCurrentPlace().getName());
    System.out.println("Available neighbors: ");
    for (int i = 0; i < player.getCurrentPlace().getNeighbors().size(); i++) {
      Place neighbor = player.getCurrentPlace().getNeighbors().get(i);
      System.out.println((i + 1) + ". " + neighbor.getName());
    }

    System.out.print("Choose a neighbor to move to: ");
    int choice = scanner.nextInt();
    scanner.nextLine();

    if (choice > 0 && choice <= player.getCurrentPlace().getNeighbors().size()) {
      Place newPlace = player.getCurrentPlace().getNeighbors().get(choice - 1);
      movePlayer(newPlace);
      System.out.println(player.getName() + " moves to " + newPlace.getName());
    } else {
      System.out.println("Invalid choice.");
    }
  }

  /**
   * Allows the player to pick up an item from the current place.
   */
  private void pickUpItem() {
    if (player.getCurrentPlace().getItems().isEmpty()) {
      System.out.println("No items to pick up.");
      return;
    }

    System.out.println("Available items: ");
    for (int i = 0; i < player.getCurrentPlace().getItems().size(); i++) {
      Item item = player.getCurrentPlace().getItems().get(i);
      System.out.println((i + 1) + ". " + item.getName());
    }

    System.out.print("Choose an item to pick up: ");
    int choice = scanner.nextInt();
    scanner.nextLine();

    if (choice > 0 && choice <= player.getCurrentPlace().getItems().size()) {
      Item item = player.getCurrentPlace().getItems().get(choice - 1);
      pickUpItem(item);
    } else {
      System.out.println("Invalid choice.");
    }
  }
}
