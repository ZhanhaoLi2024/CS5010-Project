package place;

import item.Item;
import java.util.List;

/**
 * The Place interface defines the basic behaviors and attributes of a place in
 * a game. A place has a name, a list of items, and a list of neighboring
 * places.
 */
public interface Place {
  /**
   * Adds an item to the place.
   *
   * @param item the item to be added to the place
   */
  void addItem(Item item);

  /**
   * Adds a neighboring place to the current place.
   *
   * @param place the neighboring place to be added
   */
  void addNeighbor(Place place);

  /**
   * Retrieves the name of the place.
   *
   * @return the name of the place as a String
   */
  String getName();

  /**
   * Retrieves the list of items in the place.
   *
   * @return the list of items in the place
   */
  List<Item> getItems();

  /**
   * Retrieves the list of neighboring places.
   *
   * @return the list of neighboring places
   */
  List<Place> getNeighbors();

  /**
   * Determines if the specified place is a neighbor of the current place.
   *
   * @param other the place to check if it is a neighbor
   * @return true if the specified place is a neighbor, false otherwise
   */
  boolean isNeighbor(Place other);

  /**
   * Retrieves the column of the top-left corner of the place.
   *
   * @return the column of the top-left corner of the place
   */
  int getCol1();

  /**
   * Retrieves the row of the top-left corner of the place.
   *
   * @return the row of the top-left corner of the place
   */
  int getRow1();

  /**
   * Retrieves the column of the bottom-right corner of the place.
   *
   * @return the column of the bottom-right corner of the place
   */
  int getCol2();

  /**
   * Retrieves the row of the bottom-right corner of the place.
   *
   * @return the row of the bottom-right corner of the place
   */
  int getRow2();
}