import static org.junit.Assert.assertEquals;

import controller.ComputerPlayerController;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;
import place.Place;
import place.PlaceModel;
import player.ComputerPlayer;

/**
 * The ComputerPlayerControllerTest class tests the ComputerPlayerController class.
 */
public class ComputerPlayerControllerTest {

  private ComputerPlayerController controller;
  private ComputerPlayer player;
  private Place startPlace;
  private Place neighborPlace;
  private Random random;

  /**
   * Sets up the player, places, and items for testing.
   */
  @Before
  public void setUp() {
    startPlace = new PlaceModel(0, 0, 2, 3, "Starting Place");
    neighborPlace = new PlaceModel(2, 0, 4, 1, "Neighbor Place");
    startPlace.addNeighbor(neighborPlace);

    List<Place> places = new ArrayList<>();
    places.add(startPlace);
    places.add(neighborPlace);

    random = new Random(1);
    player = new ComputerPlayer("ComputerPlayer", startPlace, 3, random);
    controller = new ComputerPlayerController(player, random);
  }

  @Test
  public void testTakeTurn_MoveToNeighbor() {
    controller.takeTurn();
    assertEquals(neighborPlace, player.getCurrentPlace());
  }

  @Test
  public void testTakeTurn_PickUpRandomItem() {
    Place placeWithItem = new PlaceModel(2, 1, 4, 4, "Place With Item");
    placeWithItem.addNeighbor(startPlace);
    startPlace.addNeighbor(placeWithItem);

    player.moveToNeighbor(placeWithItem);

    player.getCurrentPlace().addItem(new item.ItemModel("Sword", 10));

    Random controlledRandom = new Random() {
      @Override
      public int nextInt(int bound) {
        return 1;
      }
    };

    controller = new ComputerPlayerController(player, controlledRandom);
    controller.takeTurn();

    assertEquals(1, player.getItems().size());
    assertEquals("Sword", player.getItems().get(0).getName());
  }

  @Test
  public void testMovePlayerRandomly_NoNeighbors() {
    Place isolatedPlace = new PlaceModel(0, 0, 5, 5, "Isolated Place"); // 无邻居

    Random controlledRandom = new Random() {
      @Override
      public int nextInt(int bound) {
        return 0;
      }
    };

    player = new ComputerPlayer("ComputerPlayer", isolatedPlace, 3, controlledRandom);
    controller = new ComputerPlayerController(player, controlledRandom);

    controller.takeTurn();

    assertEquals("Isolated Place", player.getCurrentPlace().getName());
  }

  @Test
  public void testMovePlayerRandomly_InvalidNeighbor() {
    Place invalidPlace = new PlaceModel(0, 0, 5, 5, "Invalid Place");
    player.moveToNeighbor(invalidPlace);
    assertEquals(startPlace, player.getCurrentPlace());
  }

  @Test
  public void testPickUpRandomItem_NoItems() {
    controller.takeTurn();
    assertEquals(0, player.getItems().size());
  }

  @Test
  public void testLookAround() {
    controller.takeTurn();
  }
}