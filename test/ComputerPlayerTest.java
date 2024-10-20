import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import item.Item;
import item.ItemModel;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;
import place.Place;
import place.PlaceModel;
import player.ComputerPlayer;

/**
 * The ComputerPlayerTest class tests the ComputerPlayer class.
 */
public class ComputerPlayerTest {

  private ComputerPlayer computerPlayer;
  private Place startPlace;
  private Place neighborPlace;
  private Random random;
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

    random = new Random();

    item1 = new ItemModel("Sword", 10);
    item2 = new ItemModel("Shield", 5);

    computerPlayer = new ComputerPlayer("David(Computer)", startPlace, 2, random);
  }

  @Test
  public void testConstructor_Valid() {
    assertEquals("David(Computer)", computerPlayer.getName());
    assertEquals(startPlace, computerPlayer.getCurrentPlace());
    assertTrue(computerPlayer.getItems().isEmpty());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructor_NullStartPlace() {
    new ComputerPlayer("David(Computer)", null, 2, random);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructor_InvalidItemLimit() {
    new ComputerPlayer("David(Computer)", startPlace, -1, random);
  }

  @Test
  public void testPickUpRandomItem_NoItems() {
    computerPlayer.pickUpRandomItem();
    assertTrue(computerPlayer.getItems().isEmpty());
  }

  @Test
  public void testPickUpRandomItem_WithItems() {
    startPlace.addItem(item1);
    startPlace.addItem(item2);

    computerPlayer.pickUpRandomItem();

    assertTrue(
        computerPlayer.getItems().contains(item1) || computerPlayer.getItems().contains(item2));
  }

  @Test
  public void testPickUpRandomItem_ExceedsItemLimit() {
    startPlace.addItem(item1);
    startPlace.addItem(item2);

    computerPlayer.pickUpRandomItem();
    computerPlayer.pickUpRandomItem();

    assertEquals(2, computerPlayer.getItems().size());

    Item extraItem = new ItemModel("Extra Item", 3);
    startPlace.addItem(extraItem);
    computerPlayer.pickUpRandomItem();

    assertEquals(2, computerPlayer.getItems().size());
  }


  @Test
  public void testLookAround() {
    neighborPlace.addItem(item1);
    neighborPlace.addPlayer(computerPlayer);

    computerPlayer.lookAround();

  }

  @Test
  public void testGetDescription() {
    String description = computerPlayer.getDescription();
    assertNotNull(description);
    assertTrue(description.contains("Looking around"));
  }
}