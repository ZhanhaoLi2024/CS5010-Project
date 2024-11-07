package model.target;

import java.util.List;
import model.place.Place;

/**
 * The TargetModel class represents a target character in the game.
 * A target has a name, health value, and moves between places.
 */
public class TargetModel implements Target {
  private final String name;
  private final List<Place> places;
  private int health;
  private Place currentPlace;

  /**
   * Creates a new target with the specified name, health, starting place, and list of places.
   *
   * @param name       the name of the target
   * @param health     the initial health value of the target
   * @param startPlace the starting place of the target
   * @param places     the list of places the target can move to
   * @throws IllegalArgumentException if name is null or empty, health is negative,
   *                                  startPlace is null, or places is null/empty
   */
  public TargetModel(String name, int health, Place startPlace, List<Place> places) {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Target name cannot be null or empty");
    }
    if (health < 0) {
      throw new IllegalArgumentException("Health cannot be negative");
    }
    if (startPlace == null) {
      throw new IllegalArgumentException("Start place cannot be null");
    }
    if (places == null || places.isEmpty()) {
      throw new IllegalArgumentException("Places list cannot be null or empty");
    }

    this.name = name;
    this.health = health;
    this.currentPlace = startPlace;
    this.places = places;
  }

  @Override
  public void moveToNextPlace() {
    int currentIndex = places.indexOf(currentPlace);
    if (currentIndex < places.size() - 1) {
      currentPlace = places.get(currentIndex + 1);
    } else {
      currentPlace = places.get(0);
    }
  }

  @Override
  public Place getCurrentPlace() {
    return currentPlace;
  }

  @Override
  public int getHealth() {
    return health;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public boolean takeDamage(int damage) {
    if (damage < 0) {
      throw new IllegalArgumentException("Damage cannot be negative");
    }

    health = Math.max(0, health - damage);
    return isDefeated();
  }

  @Override
  public boolean isDefeated() {
    return health <= 0;
  }

  @Override
  public String toString() {
    return String.format("%s (Health: %d) at %s", name, health, currentPlace.getName());
  }
}