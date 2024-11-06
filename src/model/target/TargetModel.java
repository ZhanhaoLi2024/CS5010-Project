package model.target;

import java.util.List;
import model.place.Place;

/**
 * The TargetModel class represents a character in the game. A character has a name, health value,
 * and a list of places it can move to.
 */
public class TargetModel implements Target {
  private final String name;
  private final int health;
  private final List<Place> places;
  private Place currentPlace;

  /**
   * Creates a new character with the specified name, health, starting place, and list of places.
   *
   * @param name       the name of the character
   * @param health     the health value of the character
   * @param startPlace the starting place of the character
   * @param places     the list of places the character can move to
   */
  public TargetModel(String name, int health, Place startPlace, List<Place> places) {
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
    System.out.println(name + " moved to " + currentPlace.getName());
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
}
