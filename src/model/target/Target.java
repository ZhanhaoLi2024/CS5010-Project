package model.target;

import model.place.Place;

/**
 * The Target interface defines the behaviors of a target character in the game.
 * A target can move between different places, has health points, and can be damaged.
 */
public interface Target {
  /**
   * Moves the target to the next place in the list of places. If the target
   * is currently at the last place in the list, it will move back to the first
   * place.
   */
  void moveToNextPlace();

  /**
   * Retrieves the current place where the target is located.
   *
   * @return the current place as a Place object.
   */
  Place getCurrentPlace();

  /**
   * Retrieves the target's current health value.
   *
   * @return the health value of the target as an integer.
   */
  int getHealth();

  /**
   * Retrieves the target's name.
   *
   * @return the name of the target as a String.
   */
  String getName();

  /**
   * Applies damage to the target, reducing their health.
   *
   * @param damage the amount of damage to apply
   * @return true if the target is defeated (health <= 0), false otherwise
   * @throws IllegalArgumentException if damage is negative
   */
  boolean takeDamage(int damage);

  /**
   * Checks if the target has been defeated (health <= 0).
   *
   * @return true if the target is defeated, false otherwise
   */
  boolean isDefeated();
}