package controller.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
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
public class AddHumanPlayerCommandTest {

  private List<Player> players;
  private StringBuilder output;
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
        places.add(new PlaceModel(0, 0, 1, 1, "TestPlace"));
        List<Item> items = new ArrayList<>();
        return new TownData("TestTown", "TestTarget", 100, places, items);
      }
    };

    town = new TownModel(loader, "testfile.txt");
  }

  @Test
  public void testAddSinglePlayer() throws IOException {
    String input = "Player1\nno\n";
    Scanner scanner = new Scanner(input);

    AddHumanPlayerCommand command = new AddHumanPlayerCommand(players, output, town, scanner);
    command.execute();

    assertEquals(1, players.size());
    assertEquals("Player1", players.get(0).getName());

    assertTrue(output.toString().contains("Player added."));
    assertTrue(output.toString().contains("Enter the player's name:"));
  }

  @Test
  public void testAddMultiplePlayers() throws IOException {
    String input = "Player1\nyes\nPlayer2\nno\n";
    Scanner scanner = new Scanner(input);

    AddHumanPlayerCommand command = new AddHumanPlayerCommand(players, output, town, scanner);
    command.execute();

    assertEquals(2, players.size());

    assertEquals("Player1", players.get(0).getName());
    assertEquals("Player2", players.get(1).getName());

    assertTrue(output.toString().contains("Player added."));
  }

  @Test
  public void testAddComputerPlayer() throws IOException {
    AddHumanPlayerCommand command =
        new AddHumanPlayerCommand(players, output, town, new Scanner(""));
    command.addComputerPlayer();

    assertEquals(1, players.size());

    assertEquals("David(Computer)", players.get(0).getName());

    assertTrue(output.toString().contains("Computer player 'David' added."));
  }

  @Test
  public void testInvalidInput() throws IOException {
    String input = "Player1\nmaybe\nno\n";
    Scanner scanner = new Scanner(input);

    AddHumanPlayerCommand command = new AddHumanPlayerCommand(players, output, town, scanner);
    command.execute();

    assertEquals(1, players.size());

    assertTrue(output.toString().contains("Invalid input. Please enter 'yes' or 'no'."));
  }
}