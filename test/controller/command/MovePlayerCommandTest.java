package controller.command;

import static org.junit.Assert.assertEquals;
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
 * Test class for the MovePlayerCommand.
 */
public class MovePlayerCommandTest {

  private List<Place> neighbors;
  private Place currentPlace;
  private Player player;
  private StringBuilder output;
  private Scanner scanner;

  /**
   * Sets up the test fixture.
   */
  @Before
  public void setUp() {
    output = new StringBuilder();
    neighbors = new ArrayList<>();
    currentPlace = new PlaceModel(0, 0, 1, 1, "CurrentPlace");
    player = new PlayerModel("TestPlayer", false, 3, currentPlace, System.out, scanner);
  }

  /**
   * Test the execute method with no neighbors.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  @Test
  public void testPlayerWithNoNeighbors() throws IOException {
    Scanner scanner = new Scanner("");
    MovePlayerCommand command = new MovePlayerCommand(player, output, scanner);
    command.execute();

    assertEquals("No neighbors found.\n", output.toString());
  }

  /**
   * Test the execute method with a computer-controlled player.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  @Test
  public void testComputerControlledPlayerMovesRandomly() throws IOException {
    Place neighbor1 = new PlaceModel(0, 1, 1, 2, "Neighbor1");
    Place neighbor2 = new PlaceModel(1, 0, 2, 1, "Neighbor2");
    neighbors.add(neighbor1);
    neighbors.add(neighbor2);
    currentPlace.addNeighbor(neighbor1);
    currentPlace.addNeighbor(neighbor2);
    player = new PlayerModel("ComputerPlayer", true, 3, currentPlace, System.out, scanner);

    MovePlayerCommand command = new MovePlayerCommand(player, output, new Scanner(""));
    command.execute();

    assertTrue(
        player.getCurrentPlace().equals(neighbor1) || player.getCurrentPlace().equals(neighbor2));
    assertTrue(output.toString().contains("Moved to "));
  }

  /**
   * Test the execute method with valid input.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  @Test
  public void testPlayerMovesWithValidInput() throws IOException {
    Place neighbor1 = new PlaceModel(0, 1, 1, 2, "Neighbor1");
    Place neighbor2 = new PlaceModel(1, 0, 2, 1, "Neighbor2");
    neighbors.add(neighbor1);
    neighbors.add(neighbor2);
    currentPlace.addNeighbor(neighbor1);
    currentPlace.addNeighbor(neighbor2);

    Scanner scanner = new Scanner("2");
    MovePlayerCommand command = new MovePlayerCommand(player, output, scanner);
    command.execute();

    System.out.println("Actual output: " + output.toString());

    assertTrue(output.toString().contains("Neighbors of CurrentPlace:"));
    assertTrue(output.toString().contains("1. Neighbor1"));
    assertTrue(output.toString().contains("2. Neighbor2"));
    assertTrue(output.toString().contains("Enter the neighbor number to move to:"));

    assertTrue(output.toString().contains("Moved to Neighbor2"));

    assertEquals("Neighbor2", player.getCurrentPlace().getName());
  }

  /**
   * Test the execute method with invalid neighbor input.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  @Test
  public void testPlayerMovesWithInvalidNeighborInput() throws IOException {
    Place neighbor1 = new PlaceModel(0, 1, 1, 2, "Neighbor1");
    Place neighbor2 = new PlaceModel(1, 0, 2, 1, "Neighbor2");
    neighbors.add(neighbor1);
    neighbors.add(neighbor2);
    currentPlace.addNeighbor(neighbor1);
    currentPlace.addNeighbor(neighbor2);

    Scanner scanner = new Scanner("3");
    MovePlayerCommand command = new MovePlayerCommand(player, output, scanner);
    command.execute();

    assertTrue(output.toString().contains("Invalid neighbor number."));
    assertEquals("CurrentPlace", player.getCurrentPlace().getName());
  }

  /**
   * Test the execute method with non-numeric input.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  @Test
  public void testPlayerMovesWithNonNumericInput() throws IOException {
    Place neighbor1 = new PlaceModel(0, 1, 1, 2, "Neighbor1");
    Place neighbor2 = new PlaceModel(1, 0, 2, 1, "Neighbor2");
    neighbors.add(neighbor1);
    neighbors.add(neighbor2);
    currentPlace.addNeighbor(neighbor1);
    currentPlace.addNeighbor(neighbor2);

    Scanner scanner = new Scanner("invalid");
    MovePlayerCommand command = new MovePlayerCommand(player, output, scanner);
    command.execute();

    assertTrue(output.toString().contains("Invalid input. Please enter a number."));
    assertEquals("CurrentPlace", player.getCurrentPlace().getName());
  }
}