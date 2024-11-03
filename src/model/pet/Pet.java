package model.pet;

/**
 * The Pet interface represents a pet in a game world.
 * Pets have various attributes and can move to different places.
 */
public interface Pet {
  /**
   * Gets the name of the pet.
   *
   * @return The name of the pet.
   */
  String getName();

  /**
   * Gets the current place number where the pet is located.
   *
   * @return The current place number.
   */
  int getPetCurrentPlaceNumber();

  /**
   * Moves the pet to a specified neighboring place.
   *
   * @param placeNumber The number of the place to move to.
   * @throws IllegalArgumentException If the place is not a valid neighbor.
   */
  void movePet(int placeNumber);
}
