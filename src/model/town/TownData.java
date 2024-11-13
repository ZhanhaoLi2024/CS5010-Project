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
  private final String petName;
  private final int targetHealth;
  private final List<Place> places;
  private final List<Item> items;

  /**
   * Constructs a new TownData with the specified town name, target name, target health, list of
   * places, and list of items.
   *
   * @param townNameString   the name of the town
   * @param targetNameString the name of the target character
   * @param petNameString    the name of the pet character
   * @param targetHealthInt  the health status of the target character
   * @param townPlaces       the list of places in the town
   * @param townItems        the list of items in the town
   */
  public TownData(String townNameString, String targetNameString, String petNameString,
                  int targetHealthInt,
                  List<Place> townPlaces,
                  List<Item> townItems) {
    this.townName = townNameString;
    this.targetName = targetNameString;
    this.petName = petNameString;
    this.targetHealth = targetHealthInt;
    this.places = townPlaces;
    this.items = townItems;
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
   * Retrieves the name of the pet character.
   *
   * @return the name of the pet character as a String
   */
  public String getPetName() {
    return petName;
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

  /**
   * Retrieves the list of items in the town.
   *
   * @return the list of items in the town
   */
  public List<Item> getItems() {
    return items;
  }
}