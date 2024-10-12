package place;

import item.Item;
import java.util.ArrayList;
import java.util.List;

/**
 * A PlaceModel object represents a place in a game. A place has a name, a list
 * of items, and a list of neighboring places.
 */
public class PlaceModel implements Place {
  private final int row1;
  private final int col1;
  private final int row2;
  private final int col2;
  private final String name;
  private final List<Item> items;
  private final List<Place> neighbors;

  /**
   * Constructs a new PlaceModel with the specified row and column coordinates,
   * and name.
   *
   * @param row1 the row coordinate of the top-left corner of the place
   * @param col1 the column coordinate of the top-left corner of the place
   * @param row2 the row coordinate of the bottom-right corner of the place
   * @param col2 the column coordinate of the bottom-right corner of the place
   * @param name the name of the place
   */
  public PlaceModel(int row1, int col1, int row2, int col2, String name) {
    if (row1 < 0 || col1 < 0 || row2 < 0 || col2 < 0) {
	    throw new IllegalArgumentException("Row and column values must be non-negative.");
	    }
    if (row1 > row2 || col1 > col2) {
        throw new IllegalArgumentException("Top-left corner must be less than bottom-right corner.");
    }
    this.row1 = row1;
    this.col1 = col1;
    this.row2 = row2;
    this.col2 = col2;
    this.name = name;
    this.items = new ArrayList<>();
    this.neighbors = new ArrayList<>();
  }

  @Override
  public void addItem(Item item) {
    items.add(item);
  }

  @Override
  public void addNeighbor(Place place) {
    neighbors.add(place);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public List<Item> getItems() {
    return items;
  }

  @Override
  public List<Place> getNeighbors() {
    return neighbors;
  }

  @Override
  public boolean isNeighbor(Place other) {
    if (other instanceof PlaceModel) {
      PlaceModel otherPlace = (PlaceModel) other;

      boolean horizontallyAdjacent = (this.col1 == otherPlace.col2 || this.col2 == otherPlace.col1);
      boolean verticallyAdjacent = (this.row1 == otherPlace.row2 || this.row2 == otherPlace.row1);

      boolean rowOverlap = this.row1 <= otherPlace.row2 && this.row2 >= otherPlace.row1;
      boolean colOverlap = this.col1 <= otherPlace.col2 && this.col2 >= otherPlace.col1;

      return (horizontallyAdjacent && rowOverlap) || (verticallyAdjacent && colOverlap);
    }
    return false;
  }

  public int getCol1() {
    return col1;
  }

  public int getRow1() {
    return row1;
  }

  public int getCol2() {
    return col2;
  }

  public int getRow2() {
    return row2;
  }

}
