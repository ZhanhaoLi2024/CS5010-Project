package model.town;

import java.util.List;
import model.item.Item;
import model.place.Place;

/**
 * TownData represents the data structure that holds the loaded town information.
 */
public class TownData {
  private final String townName;
  private final String targetName;
  private final int targetHealth;
  private final List<Place> places;
  private final List<Item> items;

  /**
   * Constructs a new TownData with the specified town name, target name, target health, list of
   * places, and list of items.
   *
   * @param townName     the name of the town
   * @param targetName   the name of the target character
   * @param targetHealth the health status of the target character
   * @param places       the list of places in the town
   * @param items        the list of items in the town
   */
  public TownData(String townName, String targetName, int targetHealth, List<Place> places,
                  List<Item> items) {
    this.townName = townName;
    this.targetName = targetName;
    this.targetHealth = targetHealth;
    this.places = places;
    this.items = items;
  }

  /**
   * Retrieves the name of the town.
   *
   * @return the name of the town as a String
   */
  public String getTownName() {
    return townName;
  }

  /**
   * Retrieves the name of the target character.
   *
   * @return the name of the target character as a String
   */
  public String getTargetName() {
    return targetName;
  }

  /**
   * Retrieves the health status of the target character.
   *
   * @return the health status of the target character
   */
  public int getTargetHealth() {
    return targetHealth;
  }

  /**
   * Retrieves the list of places in the town.
   *
   * @return the list of places in the town
   */
  public List<Place> getPlaces() {
    return places;
  }

  public List<Item> getItems() {
    return items;
  }
}