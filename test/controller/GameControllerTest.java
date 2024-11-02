package controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import model.item.Item;
import model.item.ItemModel;
import model.place.Place;
import model.place.PlaceModel;
import model.player.Player;
import model.town.Town;
import model.town.TownData;
import model.town.TownLoaderInterface;
import model.town.TownModel;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for the GameController.
 */
public class GameControllerTest {

  private Town town;
  private List<Player> players;
  private StringBuilder output;
  private GameController controller;
  private Appendable scanner;

  /**
   * Sets up the test fixture.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  @Before
  public void setUp() throws IOException {
    List<Place> places = new ArrayList<>();
    Place place1 = new PlaceModel(0, 0, 2, 3, "TestPlace", String.valueOf(1));
    Place place2 = new PlaceModel(2, 0, 4, 1, "Target", String.valueOf(2));
    places.add(place1);
    places.add(place2);
    place1.addNeighbor(place2);
    place2.addNeighbor(place1);
    List<Item> items = new ArrayList<>();
    Item item1 = new ItemModel("TestItem", 10);
    Item item2 = new ItemModel("TestItem2", 5);
    items.add(item1);
    items.add(item2);
    place1.addItem(item1);
    place2.addItem(item2);
    TownData townData = new TownData("TestTown", "Target", 100, places, items);
    TownLoaderInterface loader = filename -> townData;
    town = new TownModel(loader, "testfile", new InputStreamReader(System.in), System.out);
    output = new StringBuilder();
    players = new ArrayList<>();
    Readable input = new StringReader("2\nPlayer1\n0\n");
    controller = new GameController(town, input, output, 10);
  }

  /**
   * Tests the GameController constructor.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  @Test
  public void testAddMultiplePlayers() throws IOException {
    Readable scanner = new StringReader("2\nPlayer1\nyes\nPlayer2\nno\n0\n");
    controller = new GameController(town, scanner, output, 10);

    controller.startGame();

    assertEquals(3, controller.getPlayers().size());
    assertTrue(output.toString().contains("Player1"));
    assertTrue(output.toString().contains("Player2"));
  }

  /**
   * Tests the GameController constructor.
   * maxTurns = 10
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  @Test
  public void testInvalidMenuChoice() throws IOException {
    Readable scanner = new StringReader("99\n0\n");
    controller = new GameController(town, scanner, output, 10);

    controller.startGame();

    assertTrue(output.toString().contains("Invalid choice, please try again."));
  }

  /**
   * Tests the GameController constructor. maxTurns = 1
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  @Test
  public void testGameOver() throws IOException {
    Readable input = new StringReader("2\nPlayer1\nno\n5\n3\n0\n");
    controller = new GameController(town, input, output, 1);

    controller.startGame();

    assertTrue(output.toString().contains("Game over."));
  }

  /**
   * Tests Move Player to the neighbor place.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  @Test
  public void testMovePlayer() throws IOException {
    Readable input = new StringReader("2\nPlayer1\nno\n5\n1\n1\n0\n");
    controller = new GameController(town, input, output, 1);

    controller.startGame();

    assertTrue(output.toString().contains("Moved to "));
  }

  /**
   * Tests picking up an item in the current place.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  @Test
  public void testPickUpItem() throws IOException {
    Readable input = new StringReader("2\nPlayer1\nno\n5\n2\n1\n0\n");
    controller = new GameController(town, input, output, 1);

    controller.startGame();

    assertTrue(output.toString().contains("Picked up"));
  }

  /**
   * Tests look around in the current place.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  @Test
  public void testLookAround() throws IOException {
    Readable input = new StringReader("2\nPlayer1\nno\n5\n3\n0\n");
    controller = new GameController(town, input, output, 1);

    controller.startGame();

    assertTrue(output.toString().contains("Neighbors of "));
    assertTrue(output.toString().contains("Items in "));
  }

  /**
   * Tests display all player info.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  @Test
  public void testDisplayAllPlayerInfo() throws IOException {
    Readable input = new StringReader("2\nPlayer1\nno\n3\n1\n0\n0\n");
    controller = new GameController(town, input, output, 1);

    controller.startGame();

    assertTrue(output.toString().contains("All players info:"));
  }

  /**
   * Tests display specific player info.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  @Test
  public void testDisplaySpecificPlayerInfo() throws IOException {
    Readable input = new StringReader("2\nPlayer1\nno\n3\n2\nPlayer1\n0\n0\n");
    controller = new GameController(town, input, output, 1);

    controller.startGame();

    assertTrue(output.toString().contains("Player name: Player1"));
  }

  /**
   * Tests getting graphical representation of the town.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  @Test
  public void testDisplayTown() throws IOException {
    Readable input = new StringReader("6\n0\n");
    controller = new GameController(town, input, output, 1);

    controller.startGame();

    assertTrue(output.toString().contains("Map saved as map.png"));
  }

}
