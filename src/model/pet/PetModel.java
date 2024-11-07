package model.pet;

/**
 * The PetModel class represents a pet that can move between different places.
 * Implementing classes should provide the necessary functionality for retrieving the pet's name,
 * getting the current place number, and moving the pet to a new place.
 */
public class PetModel implements Pet {
  private final String petName;
  private int currentPlaceNumber;

  /**
   * Constructs a PetModel object with the specified name. The initial place number is set to 0.
   *
   * @param petName the name of the pet.
   * @throws IllegalArgumentException if the provided pet name is empty or null.
   */
  public PetModel(String petName) throws IllegalArgumentException {
    if (petName == null || petName.trim().isEmpty()) {
      throw new IllegalArgumentException("Pet name cannot be null, empty, or only whitespace!");
    }
    this.petName = petName;
    this.currentPlaceNumber = 1; // set initial place number as 0
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
    this.currentPlaceNumber = placeNumber;
  }
}
