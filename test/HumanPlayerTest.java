import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import item.Item;
import item.ItemModel;
import org.junit.Before;
import org.junit.Test;
import place.Place;
import place.PlaceModel;
import player.HumanPlayer;

/**
 * The HumanPlayer class represents a human player in the game.
 */
public class HumanPlayerTest {

  private HumanPlayer humanPlayer;
  private Place startingPlace;
  private Place neighborPlace;
  private Place nonNeighborPlace;
  private Item item1;
  private Item item2;

  /**
   * Sets up the player, places, and items for testing.
   */
  @Before
  public void setUp() {
    startingPlace = new PlaceModel(0, 0, 2, 3, "Starting Place");
    neighborPlace = new PlaceModel(2, 0, 4, 1, "Neighbor Place");
    nonNeighborPlace = new PlaceModel(0, 10, 5, 12, "Non-Neighbor Place");

    startingPlace.addNeighbor(neighborPlace);

    item1 = new ItemModel("Gas Can", 11);
    item2 = new ItemModel("House Key", 15);
    startingPlace.addItem(item1);
    startingPlace.addItem(item2);

    humanPlayer = new HumanPlayer("Player1", startingPlace, 2);
  }

  @Test
  public void testMoveToValidNeighbor() {
    humanPlayer.moveToNeighbor(neighborPlace);
    assertEquals(neighborPlace, humanPlayer.getCurrentPlace());
  }

  @Test
  public void testMoveToInvalidNeighbor() {
    humanPlayer.moveToNeighbor(nonNeighborPlace);
    assertEquals(startingPlace, humanPlayer.getCurrentPlace());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMoveToNullPlace() {
    humanPlayer.moveToNeighbor(null);
  }

  @Test
  public void testPickUpValidItem() {
    humanPlayer.pickUpItem(item1);
    assertTrue(humanPlayer.getItems().contains(item1));
    assertFalse(startingPlace.getItems().contains(item1));
  }

  @Test
  public void testPickUpInvalidItem() {
    Item nonExistingItem = new ItemModel("Non-Existing Item", 5);
    humanPlayer.pickUpItem(nonExistingItem);
    assertFalse(humanPlayer.getItems().contains(nonExistingItem));
  }

  @Test
  public void testPickUpItemWhenOverLimit() {
    humanPlayer.pickUpItem(item1);
    humanPlayer.pickUpItem(item2);

    Item extraItem = new ItemModel("Extra Item", 5);
    startingPlace.addItem(extraItem);
    humanPlayer.pickUpItem(extraItem);

    assertFalse(humanPlayer.getItems().contains(extraItem));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPickUpNullItem() {
    humanPlayer.pickUpItem(null);
  }

  @Test
  public void testLookAroundWithItemsAndPlayers() {
    HumanPlayer otherPlayer = new HumanPlayer("Player2", startingPlace, 2);
    startingPlace.addPlayer(otherPlayer);

    humanPlayer.lookAround();
    assertTrue(startingPlace.getItems().contains(item1));
    assertTrue(startingPlace.getCurrentPlacePlayers().contains(otherPlayer));
  }

  @Test
  public void testLookAroundWithNoItemsOrPlayers() {
    startingPlace.removeItem(item1);
    startingPlace.removeItem(item2);
    startingPlace.removePlayer(humanPlayer);

    humanPlayer.lookAround();
    assertTrue(startingPlace.getItems().isEmpty());
    assertTrue(startingPlace.getCurrentPlacePlayers().isEmpty());
  }

  @Test
  public void testGetDescription() {
    String description = humanPlayer.getDescription();
    assertNotNull(description);
  }

  @Test
  public void testMoveToSamePlace() {
    humanPlayer.moveToNeighbor(startingPlace);
    assertEquals(startingPlace, humanPlayer.getCurrentPlace());
  }

  @Test
  public void testMoveWithCorruptedNeighborsList() {
    startingPlace.getNeighbors().clear();

    humanPlayer.moveToNeighbor(neighborPlace);
    assertEquals("Neighbor Place", humanPlayer.getCurrentPlace().getName());
  }

  @Test
  public void testPickUpItemAfterRemoval() {
    humanPlayer.lookAround();

    startingPlace.removeItem(item1);

    humanPlayer.pickUpItem(item1);
    assertFalse(humanPlayer.getItems().contains(item1));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMoveWhenCurrentPlaceIsNull() {
    humanPlayer = new HumanPlayer("Player1", null, 2);
    humanPlayer.moveToNeighbor(neighborPlace);
  }

  @Test
  public void testMoveWithNoNeighbors() {
    Place isolatedPlace = new PlaceModel(20, 20, 25, 25, "Isolated Place");

    humanPlayer.moveToNeighbor(isolatedPlace);
    assertEquals(startingPlace,
        humanPlayer.getCurrentPlace());
  }

  @Test
  public void testMoveAfterPickUpItemInPreviousPlace() {
    humanPlayer.pickUpItem(item1);
    assertTrue(humanPlayer.getItems().contains(item1));

    humanPlayer.moveToNeighbor(neighborPlace);
    assertEquals(neighborPlace, humanPlayer.getCurrentPlace());

    humanPlayer.pickUpItem(item2);
    assertFalse(humanPlayer.getItems().contains(item2));
  }
}