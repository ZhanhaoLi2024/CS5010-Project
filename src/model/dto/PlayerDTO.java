package model.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player.
 * This class is used to store the name, current place number, items, carry limit,
 * and computer controlled status of a player.
 */
public class PlayerDTO {
  private final String name;
  private final int currentPlaceNumber;
  private final List<ItemDTO> items;
  private final int carryLimit;
  private final boolean isComputerControlled;

  /**
   * Constructs a PlayerDTO object with the specified name, current place number, items,
   * carry limit, and computer controlled status.
   *
   * @param name                 the name of the player
   * @param currentPlaceNumber   the current place number of the player
   * @param items                the items of the player
   * @param carryLimit           the carry limit of the player
   * @param isComputerControlled the computer controlled status of the player
   */
  public PlayerDTO(String name, int currentPlaceNumber, List<ItemDTO> items,
                   int carryLimit, boolean isComputerControlled) {
    this.name = name;
    this.currentPlaceNumber = currentPlaceNumber;
    this.items = new ArrayList<>(items);
    this.carryLimit = carryLimit;
    this.isComputerControlled = isComputerControlled;
  }

  /**
   * Returns the name of the player.
   *
   * @return the name of the player
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the current place number of the player.
   *
   * @return the current place number of the player
   */
  public int getCurrentPlaceNumber() {
    return currentPlaceNumber;
  }

  /**
   * Returns the items of the player.
   *
   * @return the items of the player
   */
  public List<ItemDTO> getItems() {
    return new ArrayList<>(items);
  }

  /**
   * Returns the carry limit of the player.
   *
   * @return the carry limit of the player
   */
  public int getCarryLimit() {
    return carryLimit;
  }

  /**
   * Returns the computer controlled status of the player.
   *
   * @return the computer controlled status of the player
   */
  public boolean isComputerControlled() {
    return isComputerControlled;
  }
}