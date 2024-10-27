package model.player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import model.item.Item;
import model.place.Place;

/**
 * PlayerModel implements the Player interface and represents a player in the game.
 */
public class PlayerModel implements Player {

  private final String name;
  private final boolean isComputerControlled;
  private final int carryLimit;
  private final List<Item> items;
  private final Scanner scanner;
  private Place currentPlace;
  private Appendable output;

  /**
   * Constructs a new PlayerModel with the specified name, initial place, whether it is controlled by a computer,
   * and the carrying limit.
   *
   * @param name                 the name of the player.
   * @param isComputerControlled whether the player is controlled by a computer.
   * @param carryLimit           the maximum number of items the player can carry.
   * @param initialPlace         the starting Place of the player.
   */
  public PlayerModel(String name, boolean isComputerControlled, int carryLimit,
                     Place initialPlace, Appendable output, Scanner scanner) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Player name cannot be null or empty.");
    }
    if (carryLimit <= 0) {
      throw new IllegalArgumentException("Carry limit must be positive.");
    }
    if (initialPlace == null) {
      throw new IllegalArgumentException("Initial place cannot be null.");
    }

    this.name = name;
    this.isComputerControlled = isComputerControlled;
    this.carryLimit = carryLimit;
    this.currentPlace = initialPlace;
    this.items = new ArrayList<>();
    this.output = output;
    this.scanner = scanner;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public boolean isComputerControlled() {
    return this.isComputerControlled;
  }

  @Override
  public Place getCurrentPlace() {
    return this.currentPlace;
  }

  @Override
  public void moveTo(Place newPlace) {
    if (newPlace == null || !currentPlace.getNeighbors().contains(newPlace)) {
      throw new IllegalArgumentException("Invalid move. The place is not a neighbor.");
    }
    this.currentPlace = newPlace;
  }

  @Override
  public void moveToNextPlace() throws IOException {
    List<Place> neighbors = currentPlace.getNeighbors();
    if (neighbors.isEmpty()) {
      throw new IllegalStateException("No neighbors found.");
    } else {
      if (isComputerControlled) {
        int randomNeighborIndex = (int) (Math.random() * neighbors.size());
        this.moveTo(neighbors.get(randomNeighborIndex));
      } else {
        output.append("Neighbors of ").append(currentPlace.getName()).append(":\n");
        for (int i = 0; i < neighbors.size(); i++) {
          int currentIndex = i + 1;
          output.append(String.valueOf(currentIndex)).append(". ")
              .append(neighbors.get(i).getName())
              .append("\n");
        }
        output.append("Enter the neighbor number to move to:\n");
        String userInput = scanner.nextLine();
        try {
          int neighborNumber = Integer.parseInt(userInput);
          if (neighborNumber < 1 || neighborNumber > neighbors.size()) {
            output.append("Invalid neighbor number.\n");
          } else {
            this.moveTo(neighbors.get(neighborNumber - 1));
          }
        } catch (NumberFormatException e) {
          output.append("Invalid input. Please enter a number.\n");
        }
      }
    }
  }

  @Override
  public void pickUpItem(Item item) {
//    List<Item> items = currentPlace.getItems()
    if (items.size() >= carryLimit) {
      throw new IllegalStateException("Cannot pick up more items, inventory is full.");
    }
    items.add(item);
  }

  @Override
  public List<Item> getCurrentCarriedItems() {
    return items;
  }

  @Override
  public int getCarryLimit() {
    return this.carryLimit;
  }

  @Override
  public String getDescription() {
    return String.format("Player: %s\nLocation: %s\nInventory: %s\n",
        name,
        currentPlace.getName(),
        items.isEmpty() ? "None" : items);
  }

  @Override
  public void getPlayerCurrentPlaceInfo() throws IOException {
    List<Place> neighbors = currentPlace.getNeighbors();
    if (neighbors.isEmpty()) {
      output.append("No neighbors found.\n");
    } else {
      output.append("Neighbors of ").append(currentPlace.getName()).append(":\n");
      for (Place neighbor : neighbors) {
        output.append(neighbor.getName()).append("\n");
      }
    }
    List<Item> items = currentPlace.getItems();
    if (items.isEmpty()) {
      output.append("No items found.\n");
    } else {
      output.append("Items in ").append(currentPlace.getName()).append(":\n");
      for (Item item : items) {
        output.append(item.getName()).append(" (Damage: ")
            .append(String.valueOf(item.getDamage()))
            .append(")\n");
      }
    }
    if (currentPlace.getCurrentPlacePlayers().size() > 1) {
      for (Player player : currentPlace.getCurrentPlacePlayers()) {
        if (!player.equals(this)) {
          output.append(player.getName()).append(" is in this place.\n");
        }
      }
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof PlayerModel)) {
      return false;
    }
    PlayerModel other = (PlayerModel) obj;
    return Objects.equals(this.name, other.name) &&
        this.isComputerControlled == other.isComputerControlled;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.name, this.isComputerControlled);
  }
}