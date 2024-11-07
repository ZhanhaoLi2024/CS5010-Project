package model.dto;

/**
 * Represents a pet.
 * This class is used to store the name and current place number of a pet.
 */
public class PetDTO {
  private final String name;
  private final int currentPlaceNumber;

  /**
   * Constructs a PetDTO object with the specified name and current place number.
   *
   * @param name               the name of the pet
   * @param currentPlaceNumber the current place number of the pet
   */
  public PetDTO(String name, int currentPlaceNumber) {
    this.name = name;
    this.currentPlaceNumber = currentPlaceNumber;
  }

  /**
   * Returns the name of the pet.
   *
   * @return the name of the pet
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the current place number of the pet.
   *
   * @return the current place number of the pet
   */
  public int getCurrentPlaceNumber() {
    return currentPlaceNumber;
  }
}