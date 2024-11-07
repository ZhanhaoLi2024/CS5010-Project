package controller.command;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import model.item.Item;
import model.item.ItemModel;
import model.place.Place;
import model.place.PlaceModel;
import model.player.Player;
import model.player.PlayerModel;
import model.town.Town;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for the LookAroundCommand.
 */
public class LookAroundCommandTest {

  private Player player;
  private StringBuilder output;
  private Place place;
  private List<Player> players;
  private Scanner scanner;
  private Town town;

  /**
   * Sets up the test fixture.
   */
  @Before
  public void setUp() {
    this.players = new ArrayList<>();
    output = new StringBuilder();
    place = new PlaceModel(0, 0, 1, 1, "TestPlace", String.valueOf(1));
    player = new PlayerModel("TestPlayer", false, 3, 1, System.out, scanner);
    players.add(player);
  }

  /**
   * Test the execute method with no neighbors and no items.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  @Test
  public void testNoNeighborsNoItems() throws IOException {
    LookAroundCommand command = new LookAroundCommand(output, town);
    command.execute();

    assertEquals("No neighbors found.\nNo items found.\n", output.toString());
  }

  /**
   * Test the execute method with neighbors and no items.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  @Test
  public void testWithNeighborsNoItems() throws IOException {
    Place neighbor1 = new PlaceModel(1, 0, 2, 1, "Neighbor1", String.valueOf(2));
    Place neighbor2 = new PlaceModel(0, 1, 1, 2, "Neighbor2", String.valueOf(3));
    place.addNeighbor(neighbor1);
    place.addNeighbor(neighbor2);

    LookAroundCommand command = new LookAroundCommand(output, town);
    command.execute();

    String expectedOutput = "Neighbors of TestPlace:\nNeighbor1\nNeighbor2\nNo items found.\n";
    assertEquals(expectedOutput, output.toString());
  }

  /**
   * Test the execute method with items and no neighbors.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  @Test
  public void testNoNeighborsWithItems() throws IOException {
    Item item1 = new ItemModel("Sword", 10);
    Item item2 = new ItemModel("Shield", 5);
    place.addItem(item1);
    place.addItem(item2);

    LookAroundCommand command = new LookAroundCommand(output, town);
    command.execute();

    String expectedOutput = "No neighbors found.\nItems in TestPlace:\n" + "Sword (Damage: 10)\n" +
        "Shield (Damage: 5)\n";
    assertEquals(expectedOutput, output.toString());
  }

  /**
   * Test the execute method with neighbors and items.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  @Test
  public void testWithNeighborsAndItems() throws IOException {
    Place neighbor1 = new PlaceModel(1, 0, 2, 1, "Neighbor1", String.valueOf(2));
    place.addNeighbor(neighbor1);
    Item item1 = new ItemModel("Sword", 10);
    place.addItem(item1);

    LookAroundCommand command = new LookAroundCommand(output, town);
    command.execute();

    String expectedOutput =
        "Neighbors of TestPlace:\nNeighbor1\n" + "Items in TestPlace:\nSword (Damage: 10)\n";
    assertEquals(expectedOutput, output.toString());
  }

  /**
   * Test the execute method with a null player.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  @Test(expected = NullPointerException.class)
  public void testExecuteWithNullPlayer() throws IOException {
    LookAroundCommand command = new LookAroundCommand(output, town);
    command.execute();
  }

  /**
   * Test the execute method with a null place.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testExecuteWithNullPlace() throws IOException {
    Player nullPlacePlayer = new PlayerModel("TestPlayer", false, 3, 0, System.out, scanner);
    players.add(nullPlacePlayer);
    LookAroundCommand command = new LookAroundCommand(output, town);
    command.execute();
  }

  /**
   * Test the current place is not item.
   */
  @Test
  public void testCurrentPlaceIsNotItem() throws IOException {
    LookAroundCommand command = new LookAroundCommand(output, town);
    Place currentPlace = town.getPlaceByNumber(player.getPlayerCurrentPlaceNumber());
    assertEquals("TestPlace", currentPlace.getName());

    Item item = new ItemModel("Sword", 10);
    place.addItem(item);
    command.execute();
  }

  /**
   * Test the current place have another player.
   */
  @Test
  public void testCurrentPlaceHaveAnotherPlayer() throws IOException {
    Player player2 = new PlayerModel("TestPlayer2", false, 3, 1, System.out, scanner);
    players.add(player2);
    LookAroundCommand command = new LookAroundCommand(output, town);
    Place currentPlace = town.getPlaceByNumber(player.getPlayerCurrentPlaceNumber());
    assertEquals("TestPlace", currentPlace.getName());
    command.execute();
    String expectedOutput =
        "No neighbors found.\nNo items found.\nTestPlayer2 is in this place.\n";
    assertEquals(expectedOutput, output.toString());
  }
}