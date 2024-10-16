package model.player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import model.item.Item;
import model.place.Place;

/**
 * PlayerModel implements the Player interface and represents a player in the game.
 */
public class PlayerModel implements Player {

  private final String name;
  private final boolean isComputerControlled;
  private final int carryLimit;
  private final Map<String, Integer> inventory;
  private Place currentPlace;

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
                     Place initialPlace) {
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
    this.inventory = new HashMap<>();
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
  public void pickUpItem(Item item) {
    if (inventory.size() >= carryLimit) {
      throw new IllegalStateException("Cannot pick up more items, inventory is full.");
    }
    inventory.put(item.getName(), item.getDamage());
  }

  @Override
  public Map<String, Integer> getInventory() {
    return new HashMap<>(this.inventory);
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
        inventory.isEmpty() ? "None" : inventory);
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