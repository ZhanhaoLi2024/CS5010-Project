import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import item.Item;
import item.ItemModel;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import place.Place;
import place.PlaceModel;
import player.PlayerModel;

/**
 * The PlayerModelTest class tests the PlayerModel class.
 */
public class PlayerModelTest {

  private PlayerModel player;
  private Place startingPlace;
  private Place neighborPlace;
  private Item item1;
  private Item item2;
  private List<Item> items;

  /**
   * Sets up the player, places, and items for testing.
   */
  @Before
  public void setUp() {
    startingPlace = new PlaceModel(0, 0, 2, 3, "Starting Place");
    neighborPlace = new PlaceModel(2, 0, 4, 1, "Neighbor Place");
    startingPlace.addNeighbor(neighborPlace);

    item1 = new ItemModel("Gas Can", 11);
    item2 = new ItemModel("House Key", 15);
    items = new ArrayList<>();
    items.add(item1);
    items.add(item2);
    startingPlace.addItem(item1);
    startingPlace.addItem(item2);

    player = new PlayerModel("Player1", startingPlace, 2);
  }

  @Test
  public void testMoveToNeighborValid() {
    player.moveToNeighbor(neighborPlace);
    assertEquals(neighborPlace, player.getCurrentPlace());
  }

  @Test
  public void testMoveToNeighborInvalid() {
    Place nonNeighborPlace = new PlaceModel(20, 20, 25, 25, "Non-Neighbor Place");
    player.moveToNeighbor(nonNeighborPlace);
    assertEquals(startingPlace, player.getCurrentPlace());
  }

  @Test
  public void testPickUpItemValid() {
    player.pickUpItem(item1);
    assertTrue(player.getItems().contains(item1));
    assertFalse(startingPlace.getItems().contains(item1));
  }

  @Test
  public void testPickUpItemWhenItemLimitReached() {
    player.pickUpItem(item1);
    player.pickUpItem(item2);

    Item extraItem = new ItemModel("New Item", 5);
    startingPlace.addItem(extraItem);
    player.pickUpItem(extraItem);
    assertFalse(player.getItems().contains(extraItem));
  }

  @Test
  public void testPickUpItemWhenItemNotInPlace() {
    Item nonExistingItem = new ItemModel("Non-Existing Item", 10);
    player.pickUpItem(nonExistingItem);
    assertFalse(player.getItems().contains(nonExistingItem));
  }

  @Test
  public void testLookAroundWithItemsAndPlayers() {
    PlayerModel otherPlayer = new PlayerModel("Player2", startingPlace, 1);
    startingPlace.addPlayer(otherPlayer);

    player.lookAround();
    assertEquals(2, startingPlace.getItems().size());
    assertEquals(3, startingPlace.getCurrentPlacePlayers().size());
  }

  @Test
  public void testLookAroundWithNoItemsAndPlayers() {
    startingPlace.removeItem(item1);
    startingPlace.removeItem(item2);
    startingPlace.removePlayer(player);

    player.lookAround();
    assertTrue(startingPlace.getItems().isEmpty());
    assertTrue(startingPlace.getCurrentPlacePlayers().isEmpty());
  }

  @Test
  public void testGetDescription() {
    player.getDescription();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPlayerConstructorWithInvalidItemLimit() {
    new PlayerModel("Invalid Player", startingPlace, -1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPlayerConstructorWithNullName() {
    new PlayerModel(null, startingPlace, 2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPlayerConstructorWithNullPlace() {
    new PlayerModel("Player", null, 2);
  }

  @Test
  public void testGetName() {
    assertEquals("Player1", player.getName());
  }

  @Test
  public void testGetCurrentPlace() {
    assertEquals(startingPlace, player.getCurrentPlace());
  }

  @Test
  public void testGetItems() {
    assertEquals(0, player.getItems().size());
  }

  @Test
  public void testPickUpItemWhenNoItemsAvailable() {
    startingPlace.removeItem(item1);
    startingPlace.removeItem(item2);

    player.pickUpItem(item1);
    assertFalse(player.getItems().contains(item1));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMovePlayerWhenCurrentPlaceIsNull() {
    player = new PlayerModel("Player1", null, 2);
  }


  @Test
  public void testMovePlayerToSamePlace() {
    player.moveToNeighbor(startingPlace);
    assertEquals(startingPlace,
        player.getCurrentPlace());
  }

  @Test
  public void testPickUpItemAlreadyPickedUp() {
    player.pickUpItem(item1);

    player.pickUpItem(item1);
    assertEquals(1,
        player.getItems().size());
  }

  @Test
  public void testRemovePlayerWhenNotInPlace() {
    PlayerModel newPlayer = new PlayerModel("NewPlayer", neighborPlace, 2);

    startingPlace.removePlayer(
        newPlayer);
    assertFalse(startingPlace.getCurrentPlacePlayers().contains(newPlayer));
  }

  @Test
  public void testAddSamePlayerMultipleTimes() {
    startingPlace.addPlayer(player);
    startingPlace.addPlayer(player);

    assertEquals(3, startingPlace.getCurrentPlacePlayers().stream()
        .filter(p -> p.getName().equals(player.getName()))
        .count());
  }

  @Test
  public void testMovePlayerWithCorruptedNeighborsList() {
    startingPlace.getNeighbors().clear();

    player.moveToNeighbor(neighborPlace);
    assertEquals("Neighbor Place", player.getCurrentPlace().getName());
  }

  @Test
  public void testPickUpItemWhenItemLimitIsZero() {
    PlayerModel noItemsPlayer = new PlayerModel("NoItemsPlayer", startingPlace, 0);

    noItemsPlayer.pickUpItem(item1);
    assertTrue(
        noItemsPlayer.getItems().isEmpty());
  }

  @Test
  public void testDynamicItemLimitChange() {
    player.pickUpItem(item1);

    player = new PlayerModel("Player1", startingPlace, 1);

    player.pickUpItem(item2);
    assertEquals(1, player.getItems().size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPickUpNullItem() {
    player.pickUpItem(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMoveToNonExistentPlace() {
    player.moveToNeighbor(null);
  }

  @Test
  public void testGetItemsInitiallyEmpty() {
    assertTrue(player.getItems().isEmpty());
  }

  @Test
  public void testGetItemsAfterPickingUpItem() {
    Item item = new ItemModel("Sword", 10);
    startingPlace.addItem(item);

    player.pickUpItem(item);

    assertTrue(player.getItems().contains(item));
    assertFalse(startingPlace.getItems().contains(item));
  }

  @Test
  public void testGetItemsAfterPickingMultipleItems() {
    Item item1 = new ItemModel("Shield", 5);
    Item item2 = new ItemModel("Health Potion", 2);
    startingPlace.addItem(item1);
    startingPlace.addItem(item2);

    player.pickUpItem(item1);
    player.pickUpItem(item2);

    List<Item> items = player.getItems();
    assertTrue(items.contains(item1));
    assertTrue(items.contains(item2));
    assertEquals(2, items.size());
  }

  @Test
  public void testGetItemsAfterRemovingItem() {
    Item item = new ItemModel("Bow", 8);
    startingPlace.addItem(item);
    player.pickUpItem(item);

    player.getItems().remove(item);

    assertFalse(player.getItems().contains(item));
  }

}
