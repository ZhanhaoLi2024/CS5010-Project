package controller.command;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import model.place.Place;
import model.place.PlaceModel;
import model.player.Player;
import model.player.PlayerModel;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for the DisplayPlayerInfoCommand.
 */
public class DisplayPlayerInfoCommandTest {

  private List<Player> players;
  private StringBuilder output;
  private Scanner scanner;

  /**
   * Sets up the test fixture.
   */
  @Before
  public void setUp() {
    players = new ArrayList<>();
    output = new StringBuilder();

    Place testPlace = new PlaceModel(0, 0, 1, 1, "TestPlace");

    Player player1 = new PlayerModel("Player1", false, 3, testPlace, System.out, scanner);
    Player player2 = new PlayerModel("Player2", false, 3, testPlace, System.out, scanner);
    players.add(player1);
    players.add(player2);
  }

  /**
   * Tests the DisplayPlayerInfoCommand.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  @Test
  public void testShowAllPlayersInfo() throws IOException {
    String input = "1\n0\n";
    scanner = new Scanner(input);

    DisplayPlayerInfoCommand command = new DisplayPlayerInfoCommand(players, output, scanner);
    command.execute();

    assertTrue(output.toString().contains("All players info:"));
    assertTrue(output.toString().contains("Player1"));
    assertTrue(output.toString().contains("Player2"));
  }

  /**
   * Tests the DisplayPlayerInfoCommand.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  @Test
  public void testShowSpecificPlayerInfo_Found() throws IOException {
    String input = "2\nPlayer1\n0\n";
    scanner = new Scanner(input);

    DisplayPlayerInfoCommand command = new DisplayPlayerInfoCommand(players, output, scanner);
    command.execute();

    assertTrue(output.toString().contains("Player name: Player1"));
    assertTrue(output.toString().contains("Player current place: TestPlace"));
  }

  /**
   * Tests the DisplayPlayerInfoCommand.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  @Test
  public void testShowSpecificPlayerInfo_NotFound() throws IOException {
    String input = "2\nUnknownPlayer\n0\n";
    scanner = new Scanner(input);

    DisplayPlayerInfoCommand command = new DisplayPlayerInfoCommand(players, output, scanner);
    command.execute();

    assertTrue(output.toString().contains("Player not found."));
  }

  /**
   * Tests the DisplayPlayerInfoCommand.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  @Test
  public void testNoPlayersFound() throws IOException {
    players.clear();

    String input = "1\n0\n";
    scanner = new Scanner(input);

    DisplayPlayerInfoCommand command = new DisplayPlayerInfoCommand(players, output, scanner);
    command.execute();

    assertTrue(output.toString().contains("No players found."));
  }
}