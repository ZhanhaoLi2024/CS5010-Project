package model.pet;

/**
 * The Pet interface defines the behavior of a pet in the game world.
 * Pets can move between places and affect gameplay by blocking visibility
 * between spaces.
 */
public interface Pet {
  /**
   * Retrieves the name of the pet.
   *
   * @return the name of the pet as a String
   */
  String getName();

  /**
   * Retrieves the current location of the pet.
   *
   * @return the current place identifier
   */
  int getPetCurrentPlaceNumber();

  /**
   * Moves the pet to a specified location in the game world.
   *
   * @param placeNumber the identifier of the destination place
   * @throws IllegalArgumentException if the place number is invalid
   */
  void movePet(int placeNumber);
}
