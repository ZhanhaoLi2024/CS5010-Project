package model.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import model.item.Item;


/**
 * PlayerModel implements the Player interface and represents a player in the game.
 */
public class PlayerModel implements Player {

  private final String name;
  private final boolean isComputerControlled;
  private final int carryLimit;
  private final List<Item> items;
  private int currentPlaceNumber;

  /**
   * Constructs a new PlayerModel with the specified name, initial place,
   * whether it is controlled by a computer, and the carrying limit.
   *
   * @param playerName          the name of the player.
   * @param isComputer          whether the player is controlled by a computer.
   * @param playerCarryLimit    the maximum number of items the player can carry.
   * @param playerStartingPlace the starting Place of the player.
   */
  public PlayerModel(String playerName, boolean isComputer, int playerCarryLimit,
                     int playerStartingPlace) {
    if (playerName == null || playerName.trim().isEmpty()) {
      throw new IllegalArgumentException("Player name cannot be null or empty.");
    }
    if (playerCarryLimit <= 0) {
      throw new IllegalArgumentException("Carry limit must be positive.");
    }
    if (playerStartingPlace == 0) {
      throw new IllegalArgumentException("Initial place cannot be null.");
    }

    this.name = playerName;
    this.isComputerControlled = isComputer;
    this.carryLimit = playerCarryLimit;
    this.currentPlaceNumber = playerStartingPlace;
    this.items = new ArrayList<>();
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
    return Objects.equals(this.name, other.name)
        && this.isComputerControlled == other.isComputerControlled;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, isComputerControlled);
  }
}