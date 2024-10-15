import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import controller.PlayerControllerModel;
import item.Item;
import item.ItemModel;
import org.junit.Before;
import org.junit.Test;
import place.Place;
import place.PlaceModel;
import player.PlayerModel;

/**
 * The PlayerControllerModelTest class tests the PlayerControllerModel class.
 */
public class PlayerControllerModelTest {

  private PlayerControllerModel controller;
  private PlayerModel player;
  private Place startPlace;
  private Place neighborPlace;
  private Item item1;
  private Item item2;

  /**
   * Sets up the player, places, and items for testing.
   */
  @Before
  public void setUp() {
    startPlace = new PlaceModel(0, 0, 2, 3, "Starting Place");
    neighborPlace = new PlaceModel(2, 0, 4, 1, "Neighbor Place");
    startPlace.addNeighbor(neighborPlace);

    item1 = new ItemModel("Sword", 10);
    item2 = new ItemModel("Shield", 5);

    player = new PlayerModel("John", startPlace, 3);
    controller = new PlayerControllerModel(player);
  }

  @Test
  public void testMovePlayer_ValidMove() {
    controller.movePlayer(neighborPlace);
    assertEquals(neighborPlace, player.getCurrentPlace());
  }

  @Test
  public void testMovePlayer_InvalidMove() {
    Place invalidPlace = new PlaceModel(20, 20, 25, 25, "Invalid Place");
    controller.movePlayer(invalidPlace);
    assertEquals(startPlace, player.getCurrentPlace());
  }

  @Test
  public void testPickUpItem_ItemExists() {
    startPlace.addItem(item1);
    controller.pickUpItem(item1);
    assertTrue(player.getItems().contains(item1));
    assertFalse(startPlace.getItems().contains(item1));
  }

  @Test
  public void testPickUpItem_ItemDoesNotExist() {
    controller.pickUpItem(item2);
    assertFalse(player.getItems().contains(item2));
  }

  @Test
  public void testLookAround() {
    startPlace.addItem(item1);
    controller.lookAround();
  }

  @Test
  public void testDisplayPlayerInfo() {
    startPlace.addItem(item1);
    controller.displayPlayerInfo();
  }

  @Test
  public void testDisplayPlaceInfo() {
    startPlace.addItem(item2);
    controller.displayPlaceInfo();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMovePlayer_NullPlace() {
    controller.movePlayer(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPickUpItem_NullItem() {
    controller.pickUpItem(null);
  }
}