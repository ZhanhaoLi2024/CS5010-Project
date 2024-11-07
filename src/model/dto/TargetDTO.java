package model.dto;

/**
 * Represents a target.
 * This class is used to store the name, health, and current place number of a target.
 */
public class TargetDTO {
  private final String name;
  private final int health;
  private final int currentPlaceNumber;

  /**
   * Constructs a TargetDTO object with the specified name, health, and current place number.
   *
   * @param name               the name of the target
   * @param health             the health of the target
   * @param currentPlaceNumber the current place number of the target
   */
  public TargetDTO(String name, int health, int currentPlaceNumber) {
    this.name = name;
    this.health = health;
    this.currentPlaceNumber = currentPlaceNumber;
  }

  /**
   * Returns the name of the target.
   *
   * @return the name of the target
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the health of the target.
   *
   * @return the health of the target
   */
  public int getHealth() {
    return health;
  }

  /**
   * Returns the current place number of the target.
   *
   * @return the current place number of the target
   */
  public int getCurrentPlaceNumber() {
    return currentPlaceNumber;
  }
}
