package model.place;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import model.item.Item;
import model.item.ItemModel;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import model.place.Place;
import model.place.PlaceModel;

/**
 * The PlaceModelTest class implements the tests for the PlaceModel class.
 */
public class PlaceModelTest {

  private PlaceModel place1;
  private PlaceModel place2;
  private PlaceModel place3;
  private ItemModel item1;
  private ItemModel item2;

  /**
   * Sets up the places and items for testing.
   */
  @Before
  public void setUp() {
    place1 = new PlaceModel(4, 2, 6, 7, "Hospital");
    place2 = new PlaceModel(6, 3, 9, 5, "Library");
    place3 = new PlaceModel(9, 3, 11, 7, "Fire Station");

    item1 = new ItemModel("Medical Kit", 18);
    item2 = new ItemModel("Storybook", 10);
  }

  /**
   * Tests the initialization of the places.
   */
  @Test
  public void testPlaceInitialization() {
    assertEquals("Fire Station", place3.getName());
    assertEquals(4, place1.getRow1());
    assertEquals(7, place1.getCol2());
  }

  /**
   * Tests the retrieval of the name of the places.
   */
  @Test
  public void testAddItem() {
    place1.addItem(item1);
    place1.addItem(item2);

    List<Item> items = place1.getItems();
    assertEquals(2, items.size());
    assertEquals("Medical Kit", items.get(0).getName());
    assertEquals(10, items.get(1).getDamage());
  }

  /**
   * Tests the addition of neighbors to the places.
   */
  @Test
  public void testAddNeighbor() {
    place2.addNeighbor(place1);
    place2.addNeighbor(place3);

    List<Place> neighbors = place2.getNeighbors();
    assertEquals(2, neighbors.size());
    assertEquals("Hospital", neighbors.get(0).getName());
    assertEquals("Fire Station", neighbors.get(1).getName());
  }

  /**
   * Tests the retrieval of the neighbors of the places.
   */
  @Test
  public void testGetNeighbors() {
    place1.addNeighbor(place2);
    List<Place> neighbors = place1.getNeighbors();

    assertEquals(1, neighbors.size());
    assertEquals("Library", neighbors.get(0).getName());
  }

  /**
   * Tests if the specified place is a neighbor of the current place.
   */
  @Test
  public void testIsNeighbor() {
    assertTrue(place1.isNeighbor(place2));
    assertFalse(place1.isNeighbor(place3));
  }

  /**
   * Tests the retrieval of the items in the places.
   */
  @Test
  public void testGetItems() {
    place1.addItem(item1);
    List<Item> items = place1.getItems();

    assertEquals(1, items.size());
    assertEquals("Medical Kit", items.get(0).getName());
  }

  /**
   * Tests the retrieval of the top-left corner column of the places.
   */
  @Test
  public void testGetCol1() {
    assertEquals(2, place1.getCol1());
    assertEquals(3, place2.getCol1());
    assertEquals(3, place3.getCol1());
  }

  /**
   * Tests the retrieval of the top-left corner row of the places.
   */
  @Test
  public void testGetRow1() {
    assertEquals(4, place1.getRow1());
    assertEquals(6, place2.getRow1());
    assertEquals(9, place3.getRow1());
  }

  /**
   * Tests the retrieval of the bottom-right corner column of the places.
   */
  @Test
  public void testGetCol2() {
    assertEquals(7, place1.getCol2());
    assertEquals(5, place2.getCol2());
    assertEquals(7, place3.getCol2());
  }

  /**
   * Tests the retrieval of the bottom-right corner row of the places.
   */
  @Test
  public void testGetRow2() {
    assertEquals(6, place1.getRow2());
    assertEquals(9, place2.getRow2());
    assertEquals(11, place3.getRow2());
  }

  /**
   * Tests the loading of an invalid item description.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidSpaceSpecification() {
    new PlaceModel(-1, 2, 6, 7, "Invalid Hospital");
  }

  /**
   * Tests the loading of an invalid item description.
   */
  @Test
  public void testNoNeighbors() {
    // No neighbors added to place1
    List<Place> neighbors = place1.getNeighbors();
    assertEquals(0, neighbors.size());
  }

  /**
   * Tests the loading of an invalid item description.
   */
  @Test
  public void testDisplaySpaceWithNoNeighbors() {
    assertEquals(0, place1.getNeighbors().size());
    System.out.println("Place: " + place1.getName() + " has no neighbors.");
  }

  /**
   * Tests the loading of an invalid item description.
   */
  @Test
  public void testDisplaySpaceWithNoItems() {
    // No items added to place1
    List<Item> items = place1.getItems();
    assertEquals(0, items.size()); // Verify that place1 has no items
    System.out.println("Place: " + place1.getName() + " has no items.");
  }
}