package controller;

import item.Item;
import java.util.Scanner;
import place.Place;
import player.PlayerModel;

public class HumanPlayerController extends PlayerControllerModel {
  private final Scanner scanner;

  public HumanPlayerController(PlayerModel player) {
    super(player);
    this.scanner = new Scanner(System.in);
  }

  @Override
  public void takeTurn() {
    System.out.println("It's " + player.getName() + "'s turn.");
    System.out.println("Choose an action: ");
    System.out.println("1. Move to a neighboring place");
    System.out.println("2. Pick up an item");
    System.out.println("3. Look around");

    int choice = scanner.nextInt();
    scanner.nextLine();  // 清理换行符

    switch (choice) {
      case 1:
        movePlayer();
        break;
      case 2:
        pickUpItem();
        break;
      case 3:
        lookAround();
        break;
      default:
        System.out.println("Invalid choice.");
    }
  }

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
    } else {
      System.out.println("Invalid choice.");
    }
  }

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
