import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import controller.command.MovePetCommand;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import model.place.Place;
import model.place.PlaceModel;
import model.player.Player;
import model.player.PlayerModel;
import model.town.Town;
import model.town.TownData;
import model.town.TownLoader;
import model.town.TownModel;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for MovePetCommand. Tests various scenarios of pet movement
 * in the game world.
 */
public class MovePetCommandTest {
  private static final int MAX_TURNS = 10;
  private Town town;
  private StringWriter output;

  /**
   * Sets up the test environment before each test.
   */
  @Before
  public void setUp() throws IOException {
    output = new StringWriter();
    TownLoader mockLoader = new MockTownLoader();
    town = new TownModel(mockLoader, "dummy.txt", new StringReader(""), output, MAX_TURNS);

    // Add some test players
    Player player1 = new PlayerModel("Player1", false, 3, 1);
    Player player2 = new PlayerModel("Player2", false, 3, 2);
    town.getPlayers().add(player1);
    town.getPlayers().add(player2);
  }

  /**
   * Tests canceling pet movement with input 0.
   */
  @Test
  public void testCancelPetMovement() throws IOException {
    // Create input with '0' to cancel movement
    StringReader input = new StringReader("0\n");
    Scanner scanner = new Scanner(input);

    MovePetCommand command = new MovePetCommand(town, output, scanner);
    command.execute();

    String outputText = output.toString();
    assertTrue("Should show cancellation message",
        outputText.contains("Pet movement cancelled"));
  }

  /**
   * Tests invalid place number input.
   */
  @Test
  public void testInvalidPlaceNumber() throws IOException {
    // Test with invalid place number (99)
    StringReader input = new StringReader("99\n");
    Scanner scanner = new Scanner(input);

    MovePetCommand command = new MovePetCommand(town, output, scanner);
    command.execute();

    String outputText = output.toString();
    assertTrue("Should show invalid place number message",
        outputText.contains("Invalid place number"));
  }

  /**
   * Tests valid pet movement to a new location.
   */
  @Test
  public void testValidPetMovement() throws IOException {
    // Move pet to Room2 (place number 2)
    StringReader input = new StringReader("2\n");
    Scanner scanner = new Scanner(input);

    // Record initial pet location
    int initialPetLocation = town.getPet().getPetCurrentPlaceNumber();

    MovePetCommand command = new MovePetCommand(town, output, scanner);
    command.execute();

    // Verify pet moved to new location
    assertEquals("Pet should move to Room2",
        2, town.getPet().getPetCurrentPlaceNumber());
    assertTrue("Pet location should change",
        initialPetLocation != town.getPet().getPetCurrentPlaceNumber());

    String outputText = output.toString();
    assertTrue("Should show successful movement message",
        outputText.contains("moved to"));
  }

  /**
   * Tests turn switching after pet movement.
   */
  @Test
  public void testTurnSwitchingAfterMovement() throws IOException {
    // Record initial player index
    int initialPlayerIndex = town.getCurrentPlayerIndex();

    // Move pet to valid location
    StringReader input = new StringReader("2\n");
    Scanner scanner = new Scanner(input);

    MovePetCommand command = new MovePetCommand(town, output, scanner);
    command.execute();

    // Verify turn switched to next player
    int newPlayerIndex = town.getCurrentPlayerIndex();
    assertTrue("Player turn should change",
        initialPlayerIndex != newPlayerIndex);

    String outputText = output.toString();
    assertTrue("Should show turn change message",
        outputText.contains("Turn changed from"));
  }

  /**
   * Tests pet movement with negative number input.
   */
  @Test
  public void testNegativeNumberInput() throws IOException {
    // Test with negative number
    StringReader input = new StringReader("-1\n");
    Scanner scanner = new Scanner(input);

    MovePetCommand command = new MovePetCommand(town, output, scanner);
    command.execute();

    String outputText = output.toString();
    assertTrue("Should show invalid place number message",
        outputText.contains("Invalid place number"));
  }

  /**
   * Tests pet movement display of available places.
   */
  @Test
  public void testDisplayAvailablePlaces() throws IOException {
    StringReader input = new StringReader("1\n");
    Scanner scanner = new Scanner(input);

    MovePetCommand command = new MovePetCommand(town, output, scanner);
    command.execute();

    String outputText = output.toString();
    assertTrue("Should list available places",
        outputText.contains("Available places to move the pet"));
    assertTrue("Should show Room1",
        outputText.contains("Room1"));
    assertTrue("Should show Room2",
        outputText.contains("Room2"));
    assertTrue("Should show Room3",
        outputText.contains("Room3"));
  }

  /**
   * Creates a mock town loader for testing.
   */
  private static class MockTownLoader extends TownLoader {
    @Override
    public TownData loadTown(String filename) {
      // Create test places
      List<Place> places = new ArrayList<>();
      places.add(new PlaceModel(0, 0, 1, 1, "Room1", "1"));
      places.add(new PlaceModel(1, 1, 2, 2, "Room2", "2"));
      places.add(new PlaceModel(2, 2, 3, 3, "Room3", "3"));

      // Return test town data
      return new TownData(
          "TestTown",    // townName
          "Target",      // targetName
          "TestPet",     // petName
          20,           // targetHealth
          places,       // places
          new ArrayList<>() // empty items list
      );
    }
  }
}