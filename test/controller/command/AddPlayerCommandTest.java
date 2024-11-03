package controller.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import model.item.Item;
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
 * Test class for AddHumanPlayerCommand.
 */
public class AddPlayerCommandTest {

  private List<Player> players;
  private StringBuilder output;
  private Appendable scanner;
  private Town town;
  private List<Place> places;

  /**
   * Sets up the test fixture.
   */
  @Before
  public void setUp() throws IOException {
    players = new ArrayList<>();
    output = new StringBuilder();

    TownLoaderInterface loader = new TownLoaderInterface() {
      @Override
      public TownData loadTown(String filename) throws IOException {
        List<Place> places = new ArrayList<>();
        places.add(new PlaceModel(0, 0, 1, 1, "TestPlace", String.valueOf(1)));
        List<Item> items = new ArrayList<>();
        return new TownData("TestTown", "TestTarget", 100, places, items);
      }
    };

    town = new TownModel(loader, "testfile.txt", new InputStreamReader(System.in), System.out, 3);
  }

  /**
   * Tests the add single player.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  @Test
  public void testAddSinglePlayer() throws IOException {
    String input = "Player1\nno\n";
    Scanner scanner = new Scanner(input);

    AddPlayerCommand command = new AddPlayerCommand(output, town, scanner, false);
    command.execute();

    assertEquals(1, players.size());
    assertEquals("Player1", players.get(0).getName());
    assertEquals("TestPlace", players.get(0).getCurrentPlace().getName());

    assertTrue(output.toString().contains("Player added."));
    assertTrue(output.toString().contains("Enter the player's name:"));
  }

  /**
   * Tests the add multiple players.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  @Test
  public void testAddMultiplePlayers() throws IOException {
    String input = "Player1\nyes\nPlayer2\nno\n";
    Scanner scanner = new Scanner(input);

    AddPlayerCommand command = new AddPlayerCommand(output, town, scanner, false);
    command.execute();

    assertEquals(2, players.size());

    assertEquals("Player1", players.get(0).getName());
    assertEquals("Player2", players.get(1).getName());

    assertTrue(output.toString().contains("Player added."));
  }

  /**
   * Tests the add computer player.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  @Test
  public void testAddComputerPlayer() throws IOException {
    AddPlayerCommand command =
        new AddPlayerCommand(output, town, new Scanner(""), true);
    command.execute();

    assertEquals(1, players.size());

    assertEquals("David(Computer)", players.get(0).getName());

    assertTrue(output.toString().contains("Computer player 'David' added."));
  }

  /**
   * Tests the add computer player.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  @Test
  public void testInvalidInput() throws IOException {
    String input = "Player1\nmaybe\nno\n";
    Scanner scanner = new Scanner(input);

    AddPlayerCommand command = new AddPlayerCommand(output, town, scanner, false);
    command.execute();

    assertEquals(1, players.size());

    assertTrue(output.toString().contains("Invalid input. Please enter 'yes' or 'no'."));
  }
}