package model.place;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import model.item.Item;
import model.item.ItemModel;
import model.player.Player;
import model.player.PlayerModel;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for PlaceModel. Tests basic creation, neighbor relationships,
 * item management, and player management.
 */
public class PlaceModelTest {
  private PlaceModel testPlace;
  private Item testItem;
  private Player testPlayer;

  @Before
  public void setUp() {
    // Initialize a test place with valid parameters
    testPlace = new PlaceModel(1, 1, 3, 3, "Test Room", "1");
    testItem = new ItemModel("Test Item", 10);
    testPlayer = new PlayerModel("Test Player", false, 5, 1);
  }

  // Place Creation Tests
  // Test that a place with valid parameters is created successfully
  @Test
  public void testValidPlaceCreation() {
    assertNotNull("Place should be created successfully", testPlace);
    assertEquals("Place name should match", "Test Room", testPlace.getName());
    assertEquals("Place number should match", "1", testPlace.getPlaceNumber());
    assertEquals("Row1 should match", 1, testPlace.getRow1());
    assertEquals("Col1 should match", 1, testPlace.getCol1());
    assertEquals("Row2 should match", 3, testPlace.getRow2());
    assertEquals("Col2 should match", 3, testPlace.getCol2());
  }

  // Test that a place with null name is invalid
  @Test(expected = IllegalArgumentException.class)
  public void testNegativeRowCol1() {
    new PlaceModel(-1, 1, 3, 3, "Test Room", "1");
  }

  // Test that a place with negative row2/col2 is invalid
  @Test(expected = IllegalArgumentException.class)
  public void testNegativeRowCol2() {
    new PlaceModel(1, -1, 3, 3, "Test Room", "1");
  }

  // Test that a place with row1/col1 greater than row2/col2 is invalid
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCoordinates() {
    new PlaceModel(3, 3, 1, 1, "Test Room", "1"); // row1/col1 greater than row2/col2
  }

  // Neighbor Relationship Tests
// Test that rooms that share a horizontal wall are neighbors
  @Test
  public void testValidNeighborHorizontal() {
    // Create a room that shares a vertical wall with testPlace (1,1,3,3)
    PlaceModel neighbor = new PlaceModel(1, 3, 3, 5, "Neighbor Room", "2");
    assertTrue("Places should be neighbors", testPlace.isNeighbor(neighbor));
    assertTrue("Neighbor relationship should be symmetric", neighbor.isNeighbor(testPlace));
  }

  // Test that rooms that share a vertical wall are neighbors
  @Test
  public void testValidNeighborVertical() {
    // Create a room that shares a horizontal wall with testPlace (1,1,3,3)
    PlaceModel neighbor = new PlaceModel(3, 1, 5, 3, "Neighbor Room", "2");
    assertTrue("Places should be neighbors", testPlace.isNeighbor(neighbor));
    assertTrue("Neighbor relationship should be symmetric", neighbor.isNeighbor(testPlace));
  }

  // Test that rooms that share a corner are not considered neighbors
  @Test
  public void testNotNeighbor() {
    PlaceModel nonNeighbor = new PlaceModel(5, 5, 7, 7, "Non-Neighbor Room", "2");
    assertFalse("Places should not be neighbors", testPlace.isNeighbor(nonNeighbor));
    assertFalse("Non-neighbor relationship should be symmetric", nonNeighbor.isNeighbor(testPlace));
  }

  // Neighbor Management Tests
  @Test
  public void testAddNeighbor() {
    PlaceModel neighbor = new PlaceModel(1, 4, 3, 6, "Neighbor Room", "2");
    testPlace.addNeighbor(neighbor);
    List<Place> neighbors = testPlace.getNeighbors();
    assertTrue("Neighbor should be in neighbors list", neighbors.contains(neighbor));
    assertEquals("Should have exactly one neighbor", 1, neighbors.size());
  }

  // Item Management Tests
  @Test
  public void testAddItem() {
    testPlace.addItem(testItem);
    List<Item> items = testPlace.getItems();
    assertTrue("Item should be in items list", items.contains(testItem));
    assertEquals("Should have exactly one item", 1, items.size());
  }

  // Test that adding the same item twice does not duplicate it
  @Test
  public void testRemoveItem() {
    testPlace.addItem(testItem);
    testPlace.removeItem(testItem);
    List<Item> items = testPlace.getItems();
    assertFalse("Item should not be in items list", items.contains(testItem));
    assertTrue("Items list should be empty", items.isEmpty());
  }

  // Test that removing an item that is not in the list does not change the list
  @Test
  public void testRemoveNonexistentItem() {
    Item nonexistentItem = new ItemModel("Nonexistent Item", 5);
    testPlace.removeItem(nonexistentItem);
    assertTrue("Items list should remain empty", testPlace.getItems().isEmpty());
  }

  // Test that multiple items can be added to a place
  @Test
  public void testMultipleItems() {
    Item item2 = new ItemModel("Second Item", 15);
    testPlace.addItem(testItem);
    testPlace.addItem(item2);
    List<Item> items = testPlace.getItems();
    assertEquals("Should have exactly two items", 2, items.size());
    assertTrue("Should contain first item", items.contains(testItem));
    assertTrue("Should contain second item", items.contains(item2));
  }

  // Player Management Tests
  @Test
  public void testAddPlayer() {
    testPlace.addPlayer(testPlayer);
    List<Player> players = testPlace.getCurrentPlacePlayers();
    assertTrue("Player should be in players list", players.contains(testPlayer));
    assertEquals("Should have exactly one player", 1, players.size());
  }

  // Test that Removing a player from a place removes it from the list
  @Test
  public void testRemovePlayer() {
    testPlace.addPlayer(testPlayer);
    testPlace.removePlayer(testPlayer);
    List<Player> players = testPlace.getCurrentPlacePlayers();
    assertFalse("Player should not be in players list", players.contains(testPlayer));
    assertTrue("Players list should be empty", players.isEmpty());
  }

  // Test that removing a player that is not in the list does not change the list
  @Test
  public void testRemoveNonexistentPlayer() {
    Player nonexistentPlayer = new PlayerModel("Nonexistent Player", false, 5, 1);
    testPlace.removePlayer(nonexistentPlayer);
    assertTrue("Players list should remain empty", testPlace.getCurrentPlacePlayers().isEmpty());
  }

  // Test that multiple players can be added to a place
  @Test
  public void testMultiplePlayers() {
    Player player2 = new PlayerModel("Second Player", false, 5, 1);
    testPlace.addPlayer(testPlayer);
    testPlace.addPlayer(player2);
    List<Player> players = testPlace.getCurrentPlacePlayers();
    assertEquals("Should have exactly two players", 2, players.size());
    assertTrue("Should contain first player", players.contains(testPlayer));
    assertTrue("Should contain second player", players.contains(player2));
  }
}