package controller;

import item.Item;
import place.Place;
import player.PlayerModel;

public class PlayerControllerModel implements Controller {
  protected PlayerModel player;

  public PlayerControllerModel(PlayerModel player) {
    this.player = player;
  }

  protected void movePlayer(Place newPlace) {
    if (player.getCurrentPlace().isNeighbor(newPlace)) {
      player.moveToNeighbor(newPlace);
      System.out.println(player.getName() + " moves to " + newPlace.getName());
    } else {
      System.out.println("Invalid move. " + newPlace.getName() + " is not a neighbor.");
    }
  }

  protected void pickUpItem(Item item) {
    if (player.getCurrentPlace().getItems().contains(item)) {
      player.pickUpItem(item);
      System.out.println(player.getName() + " picked up " + item.getName());
    } else {
      System.out.println("Item not found in the current place.");
    }
  }

  protected void lookAround() {
    player.lookAround();
  }

  @Override
  public void takeTurn() {
  }

  @Override
  public void displayPlayerInfo() {
    System.out.println("Player: " + player.getName());
    System.out.println("Place: " + player.getCurrentPlace().getName());
    System.out.println("Items:");
    for (Item item : player.getItems()) {
      System.out.println("- " + item.getName() + " (Damage: " + item.getDamage() + ")");
    }
  }

  @Override
  public void displayPlaceInfo() {
    Place place = player.getCurrentPlace();
    System.out.println("Place: " + place.getName());
    System.out.println("Items in the place:");
    for (Item item : place.getItems()) {
      System.out.println("- " + item.getName() + " (Damage: " + item.getDamage() + ")");
    }
    System.out.println("Neighboring places:");
    for (Place neighbor : place.getNeighbors()) {
      System.out.println("- " + neighbor.getName());
    }
  }
}
