package controller.command;

import java.util.Scanner;
import model.item.Item;
import model.item.ItemModel;
import model.place.Place;
import model.place.PlaceModel;
import model.player.Player;
import model.player.PlayerModel;
import org.junit.Before;

/**
 * Test class for PickUpItemCommand.
 */
public class PickUpItemCommandTest {

  private Player player;
  private StringBuilder output;
  private Place place;
  private Scanner scanner;

  /**
   * Sets up the test fixture.
   */
  @Before
  public void setUp() {
    place = new PlaceModel(0, 0, 1, 1, "TestPlace", String.valueOf(1));
    output = new StringBuilder();

    player = new PlayerModel("Player1", false, 3, place, System.out, scanner);

    Item sword = new ItemModel("Sword", 10);
    Item shield = new ItemModel("Shield", 5);
    place.addItem(sword);
    place.addItem(shield);
  }

  /**
   * Tests the constructor of PickUpItemCommand.
   */
//  @Test
//  public void testPickUpItemWhenNoItems() throws IOException {
//    place.getItems().clear();
//
//    scanner = new Scanner("");
//    PickUpItemCommand command = new PickUpItemCommand(twon, output, scanner);
//    command.execute();
//
//    assertTrue(output.toString().contains("No items found."));
//  }
//
//  /**
//   * Tests the constructor of PickUpItemCommand.
//   */
//  @Test
//  public void testComputerControlledPlayerPicksRandomItem() throws IOException {
//    Player computerPlayer = new PlayerModel("Computer", true, 3, place, System.out, scanner);
//
//    scanner = new Scanner("");
//    PickUpItemCommand command = new PickUpItemCommand(computerPlayer, output, scanner);
//    command.execute();
//
//    assertEquals(1, computerPlayer.getCurrentCarriedItems().size());
//    assertEquals(1, place.getItems().size());
//    assertTrue(output.toString().contains("Picked up"));
//  }
//
//  /**
//   * Tests the constructor of PickUpItemCommand.
//   */
//  @Test
//  public void testPlayerPicksValidItem() throws IOException {
//    scanner = new Scanner("2");
//
//    PickUpItemCommand command = new PickUpItemCommand(player, output, scanner);
//    command.execute();
//
//    assertEquals(1, player.getCurrentCarriedItems().size());
//    assertEquals("Shield", player.getCurrentCarriedItems());
////    assertEquals("Shield", player.getCurrentCarriedItems().keySet().iterator().next());
//    assertEquals(1, place.getItems().size());
//    assertTrue(output.toString().contains("Picked up Shield."));
//  }
//
//  /**
//   * Tests the constructor of PickUpItemCommand.
//   */
//  @Test
//  public void testPlayerPicksInvalidItemNumber() throws IOException {
//    scanner = new Scanner("3");
//
//    PickUpItemCommand command = new PickUpItemCommand(player, output, scanner);
//    command.execute();
//
//    assertEquals(0, player.getCurrentCarriedItems().size());
//    assertEquals(2, place.getItems().size());
//    assertTrue(output.toString().contains("Invalid item number."));
//  }
//
//  /**
//   * Tests the constructor of PickUpItemCommand.
//   */
//  @Test(expected = IllegalStateException.class)
//  public void testPlayerPicksItemWithFullInventory() throws IOException {
//    player.pickUpItem(new ItemModel("Item1", 1));
//    player.pickUpItem(new ItemModel("Item2", 2));
//    player.pickUpItem(new ItemModel("Item3", 3));
//
//    scanner = new Scanner("1");
//
//    PickUpItemCommand command = new PickUpItemCommand(player, output, scanner);
//    command.execute();
//  }
}