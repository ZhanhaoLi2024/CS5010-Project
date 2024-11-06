package model.town;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Scanner;
import model.item.Item;
import model.item.ItemModel;
import model.player.Player;
import model.player.PlayerModel;
import org.junit.Before;
import org.junit.Test;

/**
 * Comprehensive test class for the attack functionality in the game.
 * Tests all aspects of attack mechanics including item attacks, poke attacks,
 * computer-controlled player attacks, and human player attacks.
 */
public class TownModelAttackTest {
  private final String testFile = "res/SmallTownWorld.txt";
  private Town town;
  private StringWriter output;
  private StringReader input;

  /**
   * Sets up the test environment by initializing the TownModel and providing input.
   */
  @Before
  public void setUp() throws IOException {
    output = new StringWriter();
    input = new StringReader("0\n");
    town = new TownModel(new TownLoader(), testFile, input, output, 50);
  }

  /**
   * Helper method to add a test item to a player.
   */
  private void addItemToPlayer(Player player, String name, int damage) {
    Item item = new ItemModel(name, damage);
    player.pickUpItem(item);
  }

  /**
   * Helper method to create a Scanner with predefined input.
   */
  private Scanner createScannerWithInput(String input) {
    return new Scanner(new StringReader(input));
  }

  // =================== Item Attack Tests ===================

  @Test
  public void testSuccessfulItemAttack() throws IOException {
    Player player = new PlayerModel("TestPlayer", false, 5, 1, output,
        createScannerWithInput("1\n"));
    town.getPlayers().add(player);
    addItemToPlayer(player, "Sword", 10);

    // Move player to target's location
    player.moveToPlaceNumber(Integer.parseInt(town.getTarget().getCurrentPlace().getPlaceNumber()));

    // Execute item attack
    town.executeItemAttack(player, player.getCurrentCarriedItems().get(0));

    // Verify attack results
    assertEquals(40, town.getTarget().getHealth());  // Damage was dealt
    assertTrue(player.getCurrentCarriedItems().isEmpty());  // Item was consumed
    assertTrue(output.toString().contains("successful"));
  }

  @Test
  public void testItemAttackWrongRoom() throws IOException {
    Player player = new PlayerModel("TestPlayer", false, 5, 2, output,
        createScannerWithInput("1\n"));
    town.getPlayers().add(player);
    addItemToPlayer(player, "Sword", 10);

    // Player in different room than target
    town.executeItemAttack(player, player.getCurrentCarriedItems().get(0));

    // Verify attack failed
    assertEquals(50, town.getTarget().getHealth());
    assertFalse(player.getCurrentCarriedItems().isEmpty());
    assertTrue(output.toString().contains("same room"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testItemAttackNullPlayer() throws IOException {
    Item item = new ItemModel("Sword", 10);
    town.executeItemAttack(null, item);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testItemAttackNullItem() throws IOException {
    Player player = new PlayerModel("TestPlayer", false, 5, 1, output,
        createScannerWithInput(""));
    town.executeItemAttack(player, null);
  }

  // =================== Poke Attack Tests ===================

  @Test
  public void testSuccessfulPokeAttack() throws IOException {
    Player player = new PlayerModel("TestPlayer", false, 5, 1, output,
        createScannerWithInput("0\n"));
    town.getPlayers().add(player);

    // Move player to target's location
    player.moveToPlaceNumber(Integer.parseInt(town.getTarget().getCurrentPlace().getPlaceNumber()));

    // Execute poke attack
    town.executePoke(player);

    // Verify attack results
    assertEquals(49, town.getTarget().getHealth());  // 1 damage dealt
    assertTrue(output.toString().contains("poke"));
  }

  @Test
  public void testPokeAttackWrongRoom() throws IOException {
    Player player = new PlayerModel("TestPlayer", false, 5, 2, output,
        createScannerWithInput("0\n"));
    town.getPlayers().add(player);

    // Execute poke attack from wrong room
    town.executePoke(player);

    // Verify attack failed
    assertEquals(50, town.getTarget().getHealth());
    assertTrue(output.toString().contains("same room"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPokeAttackNullPlayer() throws IOException {
    town.executePoke(null);
  }

  // =================== Computer Attack Tests ===================

  @Test
  public void testComputerAttackWithBestItem() throws IOException {
    Player computer = new PlayerModel("Computer", true, 5, 1, output,
        createScannerWithInput(""));
    town.getPlayers().add(computer);

    // Add multiple items
    addItemToPlayer(computer, "Weak Sword", 5);
    addItemToPlayer(computer, "Strong Sword", 15);
    addItemToPlayer(computer, "Medium Sword", 10);

    // Move computer to target's location
    computer.moveToPlaceNumber(
        Integer.parseInt(town.getTarget().getCurrentPlace().getPlaceNumber()));

    // Execute computer attack
    town.executeComputerAttack(computer);

    // Verify computer used strongest weapon
    assertEquals(35, town.getTarget().getHealth());  // Used 15 damage weapon
    assertEquals(2, computer.getCurrentCarriedItems().size());  // One item used
  }

  @Test
  public void testComputerAttackNoItems() throws IOException {
    Player computer = new PlayerModel("Computer", true, 5, 1, output,
        createScannerWithInput(""));
    town.getPlayers().add(computer);

    // Move computer to target's location
    computer.moveToPlaceNumber(
        Integer.parseInt(town.getTarget().getCurrentPlace().getPlaceNumber()));

    // Execute computer attack with no items
    town.executeComputerAttack(computer);

    // Verify computer used poke attack
    assertEquals(49, town.getTarget().getHealth());
    assertTrue(output.toString().contains("poke"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testComputerAttackWithHumanPlayer() throws IOException {
    Player human = new PlayerModel("Human", false, 5, 1, output,
        createScannerWithInput(""));
    town.executeComputerAttack(human);
  }

  // =================== Human Attack Tests ===================

  @Test
  public void testHumanAttackItemChoice() throws IOException {
    input = new StringReader("1\n");
    town = new TownModel(new TownLoader(), testFile, input, output, 50);

    Player human = new PlayerModel("Human", false, 5, 1, output,
        createScannerWithInput(""));
    town.getPlayers().add(human);
    addItemToPlayer(human, "Sword", 10);

    assertEquals(1, human.getCurrentCarriedItems().size());
    assertEquals(50, town.getTarget().getHealth());

    human.moveToPlaceNumber(Integer.parseInt(town.getTarget().getCurrentPlace().getPlaceNumber()));

    town.handleHumanAttack(human);

    assertEquals(40, town.getTarget().getHealth());
    assertTrue(human.getCurrentCarriedItems().isEmpty());
    assertTrue(output.toString().contains("successful"));
  }

  @Test
  public void testHumanAttackWithItems() throws IOException {
    input = new StringReader("1\n");
    town = new TownModel(new TownLoader(), testFile, input, output, 50);

    Player human = new PlayerModel("Human", false, 5, 1, output,
        createScannerWithInput(""));
    town.getPlayers().add(human);
    addItemToPlayer(human, "Sword", 10);

    human.moveToPlaceNumber(Integer.parseInt(town.getTarget().getCurrentPlace().getPlaceNumber()));

    town.handleHumanAttack(human);

    assertEquals(40, town.getTarget().getHealth());
    assertTrue(human.getCurrentCarriedItems().isEmpty());
  }

  @Test
  public void testHumanAttackWithMultipleItems() throws IOException {
    input = new StringReader("2\n");
    town = new TownModel(new TownLoader(), testFile, input, output, 50);

    Player human = new PlayerModel("Human", false, 5, 1, output,
        createScannerWithInput(""));
    town.getPlayers().add(human);

    addItemToPlayer(human, "Weak Sword", 5);
    addItemToPlayer(human, "Strong Sword", 15);

    assertEquals(2, human.getCurrentCarriedItems().size());
    assertEquals(50, town.getTarget().getHealth());

    human.moveToPlaceNumber(Integer.parseInt(town.getTarget().getCurrentPlace().getPlaceNumber()));

    town.handleHumanAttack(human);

    assertEquals(35, town.getTarget().getHealth());
    assertEquals(1, human.getCurrentCarriedItems().size());
    assertEquals(5, human.getCurrentCarriedItems().get(0).getDamage());
    assertTrue(output.toString().contains("successful"));
  }

  @Test
  public void testHumanAttackPokeChoice() throws IOException {
    // Setup
    Player human = new PlayerModel("Human", false, 5, 1, output,
        createScannerWithInput(""));
    town.getPlayers().add(human);

    // Move human to target's location
    human.moveToPlaceNumber(Integer.parseInt(town.getTarget().getCurrentPlace().getPlaceNumber()));

    // Execute human attack choosing poke
    town.handleHumanAttack(human);

    // Verify poke attack succeeded
    assertEquals(49, town.getTarget().getHealth());
    String outputStr = output.toString();
    assertTrue(outputStr.contains("poke"));
  }

  @Test
  public void testHumanAttackInvalidChoice() throws IOException {
    // Prepare input for both town initialization and attack choice
    // Format: data needed for town initialization + attack choice
    String allInput = "dummy\ndummy\ndummy\n99\n";  // Add enough input lines for initialization
    StringReader reader = new StringReader(allInput);
    town = new TownModel(new TownLoader(), testFile, reader, output, 50);

    // Create player with its own scanner for any player-specific input
    Player human = new PlayerModel("Human", false, 5, 1, output,
        createScannerWithInput("dummy\n")); // Add input for player if needed
    town.getPlayers().add(human);
    addItemToPlayer(human, "Sword", 10);

    // Move human to target's location
    human.moveToPlaceNumber(Integer.parseInt(town.getTarget().getCurrentPlace().getPlaceNumber()));

    // Execute human attack with invalid choice
    String attackInput = "99\n";
    town = new TownModel(new TownLoader(), testFile,
        new StringReader(attackInput), output, 50);
    town.handleHumanAttack(human);

    // Verify attack failed gracefully
    assertEquals(50, town.getTarget().getHealth());
    assertFalse(human.getCurrentCarriedItems().isEmpty());
    assertTrue(output.toString().contains("Invalid"));
  }

  @Test
  public void testInvalidAttackChoice() throws IOException {
    input = new StringReader("invalid\n");
    town = new TownModel(new TownLoader(), testFile, input, output, 50);

    Player human = new PlayerModel("Human", false, 5, 1, output,
        createScannerWithInput(""));
    town.getPlayers().add(human);
    addItemToPlayer(human, "Sword", 10);

    human.moveToPlaceNumber(Integer.parseInt(town.getTarget().getCurrentPlace().getPlaceNumber()));

    town.handleHumanAttack(human);

    assertEquals(50, town.getTarget().getHealth());
    assertTrue(output.toString().contains("Invalid"));
  }

  // =================== Visibility Tests ===================

  @Test
  public void testAttackWhenVisible() throws IOException {
    // Setup two players in same room
    Player attacker = new PlayerModel("Attacker", false, 5, 1, output,
        createScannerWithInput("1\n"));
    Player witness = new PlayerModel("Witness", false, 5, 1, output,
        createScannerWithInput(""));
    town.getPlayers().add(attacker);
    town.getPlayers().add(witness);
    addItemToPlayer(attacker, "Sword", 10);

    // Move both players to target's location
    String targetRoom = town.getTarget().getCurrentPlace().getPlaceNumber();
    attacker.moveToPlaceNumber(Integer.parseInt(targetRoom));
    witness.moveToPlaceNumber(Integer.parseInt(targetRoom));

    // Attempt attack
    town.executeItemAttack(attacker, attacker.getCurrentCarriedItems().get(0));

    // Verify attack was prevented
    assertEquals(50, town.getTarget().getHealth());
    assertFalse(attacker.getCurrentCarriedItems().isEmpty());
    assertTrue(output.toString().contains("witnessed"));
  }

  @Test
  public void testAttackFromNeighboringRoom() throws IOException {
    Player attacker = new PlayerModel("Attacker", false, 5, 1, output,
        createScannerWithInput("1\n"));
    town.getPlayers().add(attacker);
    addItemToPlayer(attacker, "Sword", 10);

    // Put player in room next to target
    String targetRoom = town.getTarget().getCurrentPlace().getPlaceNumber();
    attacker.moveToPlaceNumber(Integer.parseInt(targetRoom) + 1);  // Adjacent room

    // Attempt attack
    town.executeItemAttack(attacker, attacker.getCurrentCarriedItems().get(0));

    // Verify attack failed
    assertEquals(50, town.getTarget().getHealth());
    assertFalse(attacker.getCurrentCarriedItems().isEmpty());
    assertTrue(output.toString().contains("same room"));
  }

  // =================== Game Ending Tests ===================

  @Test
  public void testGameEndsWithTargetDeath() throws IOException {
    Player player = new PlayerModel("Player", false, 5, 1, output,
        createScannerWithInput("1\n"));
    town.getPlayers().add(player);
    addItemToPlayer(player, "Fatal Weapon", 50);  // Weapon that can kill in one hit

    // Move player to target's location
    player.moveToPlaceNumber(Integer.parseInt(town.getTarget().getCurrentPlace().getPlaceNumber()));

    // Execute fatal attack
    town.executeItemAttack(player, player.getCurrentCarriedItems().get(0));

    // Verify game ended
    assertTrue(town.getTarget().isDefeated());
    assertTrue(output.toString().contains("eliminated"));
    assertTrue(town.isGameOver());
  }

  @Test
  public void testTurnIncrementWithTwoPlayers() throws IOException {
    // Setup
    Town testTown = new TownModel(new TownLoader(), testFile, new StringReader(""), output, 5);
    Player human = new PlayerModel("Human", false, 5, 1, output,
        createScannerWithInput("1\n"));
    Player computer = new PlayerModel("Computer", true, 5, 1, output,
        createScannerWithInput(""));
    testTown.getPlayers().add(human);
    testTown.getPlayers().add(computer);

    // Initial state
    assertEquals(1, testTown.getCurrentTurn());

    // One complete round
    testTown.switchToNextPlayer(); // to computer
    assertEquals(1, testTown.getCurrentTurn());
    testTown.switchToNextPlayer(); // back to human, turn should increment
    assertEquals(2, testTown.getCurrentTurn());
  }

  @Test
  public void testNoTurnIncrementWithOnePlayer() throws IOException {
    // Setup
    Town testTown = new TownModel(new TownLoader(), testFile, new StringReader(""), output, 5);
    Player human = new PlayerModel("Human", false, 5, 1, output,
        createScannerWithInput("1\n"));
    testTown.getPlayers().add(human);

    // Initial state
    assertEquals(1, testTown.getCurrentTurn());

    // Try switching turns
    testTown.switchToNextPlayer();
    assertEquals("Turn should not increment with only one player",
        1, testTown.getCurrentTurn());
  }

  @Test
  public void testGameNotOverBeforeMaxTurns() throws IOException {
    // Setup
    Town testTown = new TownModel(new TownLoader(), testFile, new StringReader(""), output, 5);
    Player human = new PlayerModel("Human", false, 5, 1, output,
        createScannerWithInput("1\n"));
    Player computer = new PlayerModel("Computer", true, 5, 1, output,
        createScannerWithInput(""));
    testTown.getPlayers().add(human);
    testTown.getPlayers().add(computer);

    // Verify game not over at start
    assertFalse("Game should not be over at start", testTown.isGameOver());

    // Play a few turns but not exceed max
    for (int i = 0; i < 6; i++) { // 3 complete rounds
      testTown.switchToNextPlayer();
      if (testTown.getCurrentTurn() < testTown.getMaxTurns()) {
        assertFalse("Game should not be over before max turns",
            testTown.isGameOver());
      }
    }
  }

}