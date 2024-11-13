package model.pet;

import model.target.Target;

/**
 * The PetModel class represents a pet that can move between different places.
 * Implementing classes should provide the necessary functionality for retrieving the pet's name,
 * getting the current place number, and moving the pet to a new place.
 */
public class PetModel implements Pet {
  private final String petName;
  private int currentPlaceNumber;
  private Target gameTarget;

  /**
   * Constructs a PetModel object with the specified name. The initial place number is set to 0.
   *
   * @param petName             the name of the pet.
   * @param startingPlaceNumber the starting place number of the pet.
   * @throws IllegalArgumentException if the provided pet name is empty or null.
   */
  public PetModel(String petName, int startingPlaceNumber) throws IllegalArgumentException {
    if (petName == null || petName.trim().isEmpty()) {
      throw new IllegalArgumentException("Pet name cannot be null, empty, or only whitespace!");
    }
    this.petName = petName;
    this.currentPlaceNumber = startingPlaceNumber;
  }

  /**
   * Gets the name of the pet.
   *
   * @return The name of the pet.
   */
  @Override
  public String getName() {
    return this.petName;
  }

  /**
   * Gets the current place number where the pet is located.
   *
   * @return The current place number.
   */
  @Override
  public int getPetCurrentPlaceNumber() {
    return this.currentPlaceNumber;
  }

  /**
   * Moves the pet to a specified neighboring place.
   *
   * @param placeNumber The number of the place to move to.
   * @throws IllegalArgumentException If the place is not a valid neighbor.
   */
  @Override
  public void movePet(int placeNumber) {
    if (placeNumber <= 0) {
      throw new IllegalArgumentException(
          "Error in Pet movePet: place number must be positive!");
    }
    if (placeNumber > 20) {
      throw new IllegalArgumentException(
          "Error in Pet movePet: place number must be less than or equal to 20!");
    }
    this.currentPlaceNumber = placeNumber;
  }
}
