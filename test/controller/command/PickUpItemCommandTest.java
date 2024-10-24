package controller.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Scanner;
import model.item.Item;
import model.item.ItemModel;
import model.place.Place;
import model.place.PlaceModel;
import model.player.Player;
import model.player.PlayerModel;
import org.junit.Before;
import org.junit.Test;

public class PickUpItemCommandTest {

  private Player player;
  private StringBuilder output;
  private Place place;
  private Scanner scanner;

  @Before
  public void setUp() {
    place = new PlaceModel(0, 0, 1, 1, "TestPlace");
    output = new StringBuilder();

    // Initialize player with a carry limit of 3
    player = new PlayerModel("Player1", false, 3, place);

    // Add items to the place
    Item sword = new ItemModel("Sword", 10);
    Item shield = new ItemModel("Shield", 5);
    place.addItem(sword);
    place.addItem(shield);
  }

  @Test
  public void testPickUpItemWhenNoItems() throws IOException {
    // Empty the place of all items
    place.getItems().clear();

    scanner = new Scanner("");
    PickUpItemCommand command = new PickUpItemCommand(player, output, scanner);
    command.execute();

    assertTrue(output.toString().contains("No items found."));
  }

  @Test
  public void testComputerControlledPlayerPicksRandomItem() throws IOException {
    // Make the player controlled by the computer
    Player computerPlayer = new PlayerModel("Computer", true, 3, place);

    scanner = new Scanner(""); // Input is irrelevant for computer-controlled players
    PickUpItemCommand command = new PickUpItemCommand(computerPlayer, output, scanner);
    command.execute();

    assertEquals(1, computerPlayer.getInventory().size());
    assertEquals(1, place.getItems().size());
    assertTrue(output.toString().contains("Picked up"));
  }

  @Test
  public void testPlayerPicksValidItem() throws IOException {
    // Set up the scanner to simulate user input (user picks the second item, i.e., shield)
    scanner = new Scanner("2");

    PickUpItemCommand command = new PickUpItemCommand(player, output, scanner);
    command.execute();

    assertEquals(1, player.getInventory().size());
    assertEquals("Shield", player.getInventory().keySet().iterator().next());
    assertEquals(1, place.getItems().size()); // Only one item should remain in the place
    assertTrue(output.toString().contains("Picked up Shield."));
  }

  @Test
  public void testPlayerPicksInvalidItemNumber() throws IOException {
    // Set up the scanner to simulate user input (invalid item number, e.g., 3)
    scanner = new Scanner("3");

    PickUpItemCommand command = new PickUpItemCommand(player, output, scanner);
    command.execute();

    assertEquals(0, player.getInventory().size());
    assertEquals(2, place.getItems().size()); // No items should be picked up
    assertTrue(output.toString().contains("Invalid item number."));
  }

  @Test(expected = IllegalStateException.class)
  public void testPlayerPicksItemWithFullInventory() throws IOException {
    // Fill the player's inventory to the limit
    player.pickUpItem(new ItemModel("Item1", 1));
    player.pickUpItem(new ItemModel("Item2", 2));
    player.pickUpItem(new ItemModel("Item3", 3));

    // Set up the scanner to simulate user input (user tries to pick the first item)
    scanner = new Scanner("1");

    PickUpItemCommand command = new PickUpItemCommand(player, output, scanner);
    command.execute();
  }
}