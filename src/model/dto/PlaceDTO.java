package model.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a place.
 * This class is used to store the name, place number, items, neighbor numbers,
 * player names, and coordinates of a place.
 */
public class PlaceDTO {
  private final String name;
  private final int placeNumber;
  private final List<ItemDTO> items;
  private final List<Integer> neighborNumbers;
  private final List<String> playerNames;
  private final int row1;
  private final int col1;
  private final int row2;
  private final int col2;

  /**
   * Constructs a PlaceDTO object with the specified name, place number, items, neighbor numbers,
   * player names, and coordinates.
   *
   * @param name            the name of the place
   * @param placeNumber     the place number
   * @param items           the items in the place
   * @param neighborNumbers the neighbor numbers of the place
   * @param playerNames     the player names in the place
   * @param row1            the row of the first coordinate
   * @param col1            the column of the first coordinate
   * @param row2            the row of the second coordinate
   * @param col2            the column of the second coordinate
   */
  public PlaceDTO(String name, int placeNumber, List<ItemDTO> items,
                  List<Integer> neighborNumbers, List<String> playerNames,
                  int row1, int col1, int row2, int col2) {
    this.name = name;
    this.placeNumber = placeNumber;
    this.items = new ArrayList<>(items);
    this.neighborNumbers = new ArrayList<>(neighborNumbers);
    this.playerNames = new ArrayList<>(playerNames);
    this.row1 = row1;
    this.col1 = col1;
    this.row2 = row2;
    this.col2 = col2;
  }

  /**
   * Returns the name of the place.
   *
   * @return the name of the place
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the place number.
   *
   * @return the place number
   */
  public int getPlaceNumber() {
    return placeNumber;
  }

  /**
   * Returns the items in the place.
   *
   * @return the items in the place
   */
  public List<ItemDTO> getItems() {
    return new ArrayList<>(items);
  }

  /**
   * Returns the neighbor numbers of the place.
   *
   * @return the neighbor numbers of the place
   */
  public List<Integer> getNeighborNumbers() {
    return new ArrayList<>(neighborNumbers);
  }

  /**
   * Returns the player names in the place.
   *
   * @return the player names in the place
   */
  public List<String> getPlayerNames() {
    return new ArrayList<>(playerNames);
  }

  /**
   * Returns the row of the first coordinate.
   *
   * @return the row of the first coordinate
   */
  public int getRow1() {
    return row1;
  }

  /**
   * Returns the column of the first coordinate.
   *
   * @return the column of the first coordinate
   */
  public int getCol1() {
    return col1;
  }

  /**
   * Returns the row of the second coordinate.
   *
   * @return the row of the second coordinate
   */
  public int getRow2() {
    return row2;
  }

  /**
   * Returns the column of the second coordinate.
   *
   * @return the column of the second coordinate
   */
  public int getCol2() {
    return col2;
  }
}