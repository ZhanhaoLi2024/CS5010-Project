package model.town;

import java.util.ArrayList;
import java.util.List;
import model.item.Item;
import model.item.ItemModel;
import model.place.Place;
import model.place.PlaceModel;

/**
 * Manages the state of a game session, providing methods to reset and initialize the game state.
 */
class GameState {
  private final String townName;
  private final String targetName;
  private final int targetHealth;
  private final String petName;
  private final List<Place> originalPlaces;
  private final List<Item> originalItems;

  /**
   * Constructs a GameState with the initial configuration of the town.
   *
   * @param townData the initial town configuration
   */
  public GameState(TownData townData) {
    this.townName = townData.getTownName();
    this.targetName = townData.getTargetName();
    this.targetHealth = townData.getTargetHealth();
    this.petName = townData.getPetName();
    this.originalPlaces = new ArrayList<>(townData.getPlaces());
    this.originalItems = new ArrayList<>(townData.getItems());
  }

  /**
   * Creates a fresh copy of places with their original items.
   *
   * @return a new list of places with reset state
   */
  public List<Place> createFreshPlaces() {
    List<Place> newPlaces = new ArrayList<>();

    // Create new places
    for (Place originalPlace : originalPlaces) {
      Place newPlace = new PlaceModel(
          originalPlace.getRow1(),
          originalPlace.getCol1(),
          originalPlace.getRow2(),
          originalPlace.getCol2(),
          originalPlace.getName(),
          originalPlace.getPlaceNumber()
      );
      newPlaces.add(newPlace);
    }

    // Restore neighbor relationships
    for (int i = 0; i < originalPlaces.size(); i++) {
      Place originalPlace = originalPlaces.get(i);
      Place newPlace = newPlaces.get(i);

      for (Place originalNeighbor : originalPlace.getNeighbors()) {
        int neighborIndex = originalPlaces.indexOf(originalNeighbor);
        newPlace.addNeighbor(newPlaces.get(neighborIndex));
      }
    }

    return newPlaces;
  }

  /**
   * Creates a fresh copy of items and distributes them to their original places.
   *
   * @param places the list of reset places
   * @return a new list of items
   */
  public List<Item> createFreshItems(List<Place> places) {
    List<Item> newItems = new ArrayList<>();

    for (Item originalItem : originalItems) {
      Item newItem = new ItemModel(originalItem.getName(), originalItem.getDamage());
      newItems.add(newItem);

      // Find the original place for this item
      for (Place place : places) {
        if (place.getItems().contains(originalItem)) {
          place.addItem(newItem);
          break;
        }
      }
    }

    return newItems;
  }

  public String getTownName() {
    return townName;
  }

  public String getTargetName() {
    return targetName;
  }

  public int getTargetHealth() {
    return targetHealth;
  }

  public String getPetName() {
    return petName;
  }
}