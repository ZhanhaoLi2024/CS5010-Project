package place;

import java.util.List;

import item.Item;

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
}