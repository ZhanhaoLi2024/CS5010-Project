package model.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import model.item.Item;


/**
 * PlayerModel implements the Player interface and represents a player in the game.
 */
public class PlayerModel implements Player {

  private final String name;
  private final boolean isComputerControlled;
  private final int carryLimit;
  private final List<Item> items;
  private final Scanner scanner;
  private int currentPlaceNumber;
  private Appendable output;

  /**
   * Constructs a new PlayerModel with the specified name, initial place, whether it is controlled by a computer,
   * and the carrying limit.
   *
   * @param name                 the name of the player.
   * @param isComputerControlled whether the player is controlled by a computer.
   * @param carryLimit           the maximum number of items the player can carry.
   * @param currentPlaceNumber   the starting Place of the player.
   */
  public PlayerModel(String name, boolean isComputerControlled, int carryLimit,
                     int currentPlaceNumber, Appendable output, Scanner scanner) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Player name cannot be null or empty.");
    }
    if (carryLimit <= 0) {
      throw new IllegalArgumentException("Carry limit must be positive.");
    }
    if (currentPlaceNumber == 0) {
      throw new IllegalArgumentException("Initial place cannot be null.");
    }

    this.name = name;
    this.isComputerControlled = isComputerControlled;
    this.carryLimit = carryLimit;
    this.currentPlaceNumber = currentPlaceNumber;
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
  public int getPlayerCurrentPlaceNumber() {
    return this.currentPlaceNumber;
  }

  @Override
  public void moveToPlaceNumber(int newPlaceNumber) {
    if (newPlaceNumber > 0) {
      this.currentPlaceNumber = newPlaceNumber;
    } else {
      throw new IllegalArgumentException("Invalid move. The place is not a neighbor.");
    }
  }

  @Override
  public void pickUpItem(Item item) {
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