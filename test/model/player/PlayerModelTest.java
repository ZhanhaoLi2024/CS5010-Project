package model.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;

import java.util.List;
import java.util.Scanner;
import model.item.Item;
import model.item.ItemModel;
import model.place.Place;
import model.place.PlaceModel;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for the PlayerModel.
 */
public class PlayerModelTest {

  private Place place1;
  private Place place2;
  private Player player;
  private Scanner scanner;

  /**
   * Sets up the test fixture.
   */
  @Before
  public void setUp() {
    place1 = new PlaceModel(0, 0, 1, 1, "Place 1");
    place2 = new PlaceModel(1, 1, 2, 2, "Place 2");
    place1.addNeighbor(place2);
    place2.addNeighbor(place1);

    player = new PlayerModel("Player1", false, 3, place1, System.out, scanner);
  }

  /**
   * Tests the PlayerModel constructor.
   */
  @Test
  public void testGetName() {
    assertEquals("Player1", player.getName());
  }

  /**
   * Tests the PlayerModel constructor.
   */
  @Test
  public void testIsComputerControlled() {
    assertFalse(player.isComputerControlled());
  }

  /**
   * Tests the PlayerModel constructor.
   */
  @Test
  public void testGetCurrentPlace() {
    assertEquals(place1, player.getCurrentPlace());
  }

  /**
   * Tests the PlayerModel constructor.
   */
  @Test
  public void testMoveToValidPlace() {
    player.moveTo(place2);
    assertEquals(place2, player.getCurrentPlace());
  }

  /**
   * Tests the PlayerModel constructor.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testMoveToInvalidPlace() {
    Place place3 = new PlaceModel(2, 2, 3, 3, "Place 3");
    player.moveTo(place3);
  }

  /**
   * Tests the PlayerModel constructor.
   */
  @Test
  public void testPickUpItem() {
    Item item1 = new ItemModel("Sword", 10);
    Item item2 = new ItemModel("Shield", 5);

    player.pickUpItem(item1);
    player.pickUpItem(item2);

    List<Item> inventory = player.getCurrentCarriedItems();
    assertEquals(2, inventory.size());
    assertEquals(item1, inventory.get(0));
    assertEquals(item2, inventory.get(1));
  }

  /**
   * Tests the player with no items.
   */
  @Test
  public void testPickUpItemWhenNoItems() {
    place1.getItems().clear();
    List<Item> inventory = player.getCurrentCarriedItems();
    assertEquals(0, inventory.size());
  }

  /**
   * Tests the PlayerModel constructor.
   */
  @Test(expected = IllegalStateException.class)
  public void testPickUpItemWhenInventoryFull() {
    player.pickUpItem(new ItemModel("Sword", 10));
    player.pickUpItem(new ItemModel("Shield", 5));
    player.pickUpItem(new ItemModel("Potion", 2));

    player.pickUpItem(new ItemModel("Bow", 7));
  }

  /**
   * Tests the PlayerModel constructor.
   */
  @Test
  public void testGetCarryLimit() {
    assertEquals(3, player.getCarryLimit());
  }

  /**
   * Tests the PlayerModel constructor.
   */
  @Test
  public void testGetDescription() {
    String expectedDescription = "Player: Player1\nLocation: Place 1\nInventory: None\n";
    assertEquals(expectedDescription, player.getDescription());

    Item item = new ItemModel("Sword", 10);
    player.pickUpItem(item);
    expectedDescription = String.format("Player: Player1\nLocation: Place 1\nInventory: %s\n",
        player.getCurrentCarriedItems());
    assertEquals(expectedDescription, player.getDescription());
  }

  /**
   * Tests the PlayerModel constructor.
   */
  @Test
  public void testEqualsAndHashCode() {
    Player samePlayer = new PlayerModel("Player1", false, 3, place1, System.out, scanner);
    Player differentPlayer = new PlayerModel("Player2", true, 3, place1, System.out, scanner);

    assertEquals(player, samePlayer);
    assertNotEquals(player, differentPlayer);

    assertEquals(player.hashCode(), samePlayer.hashCode());
    assertNotEquals(player.hashCode(), differentPlayer.hashCode());
  }
}