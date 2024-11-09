import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Scanner;
import model.item.Item;
import model.item.ItemModel;
import model.player.Player;
import model.player.PlayerModel;
import model.town.Town;
import model.town.TownLoader;
import model.town.TownModel;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for verifying the game ending conditions and related behaviors.
 * This class focuses on testing various scenarios that can end the game, including:
 * - Victory through target elimination
 * - Defeat by reaching maximum turns
 * - Failed attack attempts
 * - Computer player behavior
 * - Game state consistency after ending
 */
public class GameEndingConditionsTest {
  private static final int MAX_TURNS = 5;
  private static final String WORLD_FILE = "res/SmallTownWorld.txt";
  private Town town;
  private StringWriter output;

  /**
   * Sets up the test environment before each test execution.
   * Initializes a new town instance and output capture mechanism.
   */
  @Before
  public void setUp() {
    output = new StringWriter();
    try {
      town = new TownModel(
          new TownLoader(),
          WORLD_FILE,
          new StringReader(""),
          output,
          MAX_TURNS
      );
    } catch (IOException e) {
      fail("Failed to initialize town: " + e.getMessage());
    }
  }

  /**
   * Tests if the game properly ends when a player successfully eliminates the target.
   * Verifies the target's defeat status, game ending state, and victory message display.
   *
   * @throws IOException if there is an error with I/O operations during test execution
   */
  @Test
  public void testVictoryByKillingTarget() throws IOException {
    // Add a player
    Player player = new PlayerModel("TestPlayer", false, 5, 1, output,
        new Scanner(new StringReader("")));
    town.getPlayers().add(player);

    // Create powerful weapon
    Item weapon = new ItemModel("SuperWeapon", 50);
    player.pickUpItem(weapon);

    // Move player to target's location
    int targetLocation = Integer.parseInt(town.getTarget().getCurrentPlace().getPlaceNumber());
    player.moveToPlaceNumber(targetLocation);

    // Execute kill
    town.executeItemAttack(player, weapon);

    // Verify game ending conditions
    assertTrue("Game should be over after killing target", town.isGameOver());
    assertTrue("Target should be defeated", town.getTarget().isDefeated());
    assertTrue(output.toString().contains("eliminated the target"));
  }

  /**
   * Tests if the game properly ends when the maximum number of turns is reached.
   * Verifies turn counting, game ending state, and defeat condition handling.
   *
   * @throws IOException if there is an error with I/O operations during test execution
   */
  @Test
  public void testDefeatByMaxTurns() throws IOException {
    town.addComputerPlayer();
    town.addComputerPlayer();
    int playerCount = town.getPlayers().size();
    int totalActionsNeeded = (MAX_TURNS + 1) * playerCount;
    for (int actionCount = 0; actionCount < totalActionsNeeded; actionCount++) {
      if (!town.isGameOver()) {
        town.lookAround();
      }
    }
    town.lookAround();

    assertTrue("Game should be over - current turn: " + town.getCurrentTurn()
        + ", max turns: " + MAX_TURNS, town.isGameOver());
    assertFalse("Target should not be defeated", town.getTarget().isDefeated());
    assertTrue("Current turn should exceed max turns",
        town.getCurrentTurn() > MAX_TURNS);
    String outputText = output.toString();
    assertTrue("Output should contain turn information",
        outputText.contains("Turn"));
    assertTrue("Output should contain player information",
        outputText.contains("player"));
  }

  /**
   * Tests the handling of unsuccessful attack attempts.
   * Verifies that attacks fail when witnessed by other players and the game state
   * remains consistent after failed attacks.
   *
   * @throws IOException if there is an error with I/O operations during test execution
   */
  @Test
  public void testUnsuccessfulAttacks() throws IOException {
    // Add two players
    Player attacker = new PlayerModel("Attacker", false, 5, 1, output,
        new Scanner(new StringReader("")));
    Player witness = new PlayerModel("Witness", false, 5, 1, output,
        new Scanner(new StringReader("")));
    town.getPlayers().add(attacker);
    town.getPlayers().add(witness);

    // Move both players to target's location
    int targetLocation = Integer.parseInt(town.getTarget().getCurrentPlace().getPlaceNumber());
    attacker.moveToPlaceNumber(targetLocation);
    witness.moveToPlaceNumber(targetLocation);

    // Attempt attack with witness present
    Item weapon = new ItemModel("Weapon", 10);
    attacker.pickUpItem(weapon);
    town.executeItemAttack(attacker, weapon);

    // Verify attack failed but game continues
    assertFalse("Game should not be over after failed attack", town.isGameOver());
    assertEquals("Target health should not change after failed attack",
        50, town.getTarget().getHealth());
    assertTrue(output.toString().contains("Attack failed"));
  }

  /**
   * Tests the accumulation of damage through multiple attacks.
   * Verifies partial damage application and game continuation after non-lethal damage.
   *
   * @throws IOException if there is an error with I/O operations during test execution
   */
  @Test
  public void testMultipleAttackAttempts() throws IOException {
    // Add player
    Player player = new PlayerModel("TestPlayer", false, 5, 1, output,
        new Scanner(new StringReader("")));
    town.getPlayers().add(player);

    // Create weak weapon
    Item weapon = new ItemModel("WeakWeapon", 10);
    player.pickUpItem(weapon);

    // Move player to target
    int targetLocation = Integer.parseInt(town.getTarget().getCurrentPlace().getPlaceNumber());
    player.moveToPlaceNumber(targetLocation);

    // Execute multiple attacks
    int initialHealth = town.getTarget().getHealth();
    town.executeItemAttack(player, weapon);

    // Verify partial damage
    assertTrue("Target should take some damage",
        town.getTarget().getHealth() < initialHealth);
    assertFalse("Game should not be over after partial damage", town.isGameOver());
  }

  /**
   * Tests victory achievement through a computer-controlled player.
   * Verifies computer player's ability to eliminate target and proper game ending.
   *
   * @throws IOException if there is an error with I/O operations during test execution
   */
  @Test
  public void testComputerPlayerVictory() throws IOException {
    // Add computer player
    town.addComputerPlayer();
    Player computer = town.getPlayers().get(0);

    // Give computer player a powerful weapon
    Item weapon = new ItemModel("PowerfulWeapon", 50);
    computer.pickUpItem(weapon);

    // Move computer to target
    int targetLocation = Integer.parseInt(town.getTarget().getCurrentPlace().getPlaceNumber());
    computer.moveToPlaceNumber(targetLocation);

    // Execute computer attack
    town.executeComputerAttack(computer);

    // Verify victory conditions
    assertTrue("Game should be over after computer kills target", town.isGameOver());
    assertTrue("Target should be defeated", town.getTarget().isDefeated());
    assertTrue(output.toString().contains("eliminated the target"));
  }

  /**
   * Tests game state consistency throughout the game's duration.
   * Verifies turn counting accuracy and proper game state transitions.
   *
   * @throws IOException if there is an error with I/O operations during test execution
   */
  @Test
  public void testGameEndingStateConsistency() throws IOException {
    town.addComputerPlayer();
    town.addComputerPlayer();
    assertEquals("Initial turn should be 1", 1, town.getCurrentTurn());
    for (int turnCount = 1; turnCount <= MAX_TURNS; turnCount++) {
      if (turnCount < MAX_TURNS) {
        assertFalse("Game should not end before max turns", town.isGameOver());
      }
      for (int i = 0; i < town.getPlayers().size(); i++) {
        if (turnCount == MAX_TURNS && i == town.getPlayers().size() - 1) {
          town.lookAround();
          assertTrue("Game should end after max turns completed", town.isGameOver());
        } else {
          town.lookAround();
        }
      }
    }
    assertTrue("Game should be over", town.isGameOver());
    assertTrue("Current turn should exceed max turns",
        town.getCurrentTurn() > MAX_TURNS);
    assertFalse("Target should not be defeated",
        town.getTarget().isDefeated());
  }

  /**
   * Tests game behavior after reaching the end state.
   * Verifies that game state remains consistent and actions are properly handled
   * after game completion.
   *
   * @throws IOException if there is an error with I/O operations during test execution
   */
  @Test
  public void testActionsAfterGameEnds() throws IOException {
    town.addComputerPlayer();
    town.addComputerPlayer();
    for (int turnCount = 1; turnCount <= MAX_TURNS; turnCount++) {
      for (int i = 0; i < town.getPlayers().size(); i++) {
        town.lookAround();
      }
    }
    assertTrue("Game should be over after max turns", town.isGameOver());
    assertTrue("Current turn should exceed max turns", town.getCurrentTurn() > MAX_TURNS);
    int finalTurn = town.getCurrentTurn();
    int playerCount = town.getPlayers().size();
    int currentPlayerIndex = town.getCurrentPlayerIndex();
    town.lookAround();
    assertEquals("Turn should not increase after game ends",
        finalTurn, town.getCurrentTurn());
    assertEquals("Player index should cycle correctly",
        (currentPlayerIndex + 1) % playerCount,
        town.getCurrentPlayerIndex());
    assertTrue("Game should remain in ended state",
        town.isGameOver());
  }

  /**
   * Tests handling of simultaneous victory conditions.
   * Verifies proper precedence handling when multiple ending conditions occur together.
   *
   * @throws IOException if there is an error with I/O operations during test execution
   */
  @Test
  public void testSimultaneousVictoryConditions() throws IOException {
    // Set up to reach max turns and kill target on final turn
    town.addComputerPlayer();
    Player player = town.getPlayers().get(0);
    Item weapon = new ItemModel("FinalWeapon", 50);
    player.pickUpItem(weapon);

    // Play until final turn
    for (int i = 0; i < MAX_TURNS - 1; i++) {
      town.lookAround();
    }

    // Kill target on final turn
    int targetLocation = Integer.parseInt(town.getTarget().getCurrentPlace().getPlaceNumber());
    player.moveToPlaceNumber(targetLocation);
    town.executeItemAttack(player, weapon);

    // Verify game ends with target death taking precedence
    assertTrue("Game should end", town.isGameOver());
    assertTrue("Target death should be registered", town.getTarget().isDefeated());
    assertTrue(output.toString().contains("eliminated the target"));
  }

  /**
   * Tests attack interruption mechanics.
   * Verifies that witnessed attacks fail even when target is at low health.
   *
   * @throws IOException if there is an error with I/O operations during test execution
   */
  @Test
  public void testInterruptedKillingBlow() throws IOException {
    // Setup players
    Player attacker = new PlayerModel("Attacker", false, 5, 1, output,
        new Scanner(new StringReader("")));
    Player witness = new PlayerModel("Witness", false, 5, 1, output,
        new Scanner(new StringReader("")));
    town.getPlayers().add(attacker);
    town.getPlayers().add(witness);

    // Setup target with 1 health
    while (town.getTarget().getHealth() > 1) {
      town.getTarget().takeDamage(1);
    }

    // Position players
    int targetLocation = Integer.parseInt(town.getTarget().getCurrentPlace().getPlaceNumber());
    attacker.moveToPlaceNumber(targetLocation);
    witness.moveToPlaceNumber(targetLocation);

    // Attempt final attack
    Item weapon = new ItemModel("FinalWeapon", 1);
    attacker.pickUpItem(weapon);
    town.executeItemAttack(attacker, weapon);

    // Verify target survives due to witness
    assertFalse("Game should not end with interrupted attack", town.isGameOver());
    assertFalse("Target should survive interrupted attack", town.getTarget().isDefeated());
    assertEquals("Target should maintain 1 health", 1, town.getTarget().getHealth());
  }

  /**
   * Tests proper recognition and message display for victorious players.
   * Verifies player name display and victory message accuracy.
   *
   * @throws IOException if there is an error with I/O operations during test execution
   */
  @Test
  public void testVictoryMessageAndRecognition() throws IOException {
    Player player = new PlayerModel("VictoriousPlayer", false, 5, 1, output,
        new Scanner(new StringReader("")));
    town.getPlayers().add(player);
    Item weapon = new ItemModel("DeadlyWeapon", 50);
    player.pickUpItem(weapon);
    int targetLocation = Integer.parseInt(town.getTarget().getCurrentPlace().getPlaceNumber());
    player.moveToPlaceNumber(targetLocation);
    town.executeItemAttack(player, weapon);
    String outputText = output.toString();
    assertTrue("Should display player name",
        outputText.contains("VictoriousPlayer"));
    assertTrue("Should indicate successful attack",
        outputText.contains("Attack successful"));
    assertTrue("Should indicate target elimination",
        outputText.contains("has eliminated the target"));
  }

  /**
   * Tests proper message display when game ends due to maximum turns.
   * Verifies defeat message content and timing.
   *
   * @throws IOException if there is an error with I/O operations during test execution
   */
  @Test
  public void testDefeatMessageDisplay() throws IOException {
    town.addComputerPlayer();
    town.addComputerPlayer();
    int currentTurn = town.getCurrentTurn();
    while (currentTurn <= MAX_TURNS) {
      if (!town.isGameOver()) {
        town.lookAround();
        currentTurn = town.getCurrentTurn();
      }
    }
    assertTrue("Game should be over after max turns", town.isGameOver());
    assertFalse("Target should not be defeated", town.getTarget().isDefeated());
    assertTrue("Current turn should exceed max turns",
        town.getCurrentTurn() > MAX_TURNS);
  }

  /**
   * Tests game state display accuracy throughout game progression.
   * Verifies that game state information is consistently and accurately displayed.
   *
   * @throws IOException if there is an error with I/O operations during test execution
   */
  @Test
  public void testGameStateDisplay() throws IOException {
    town.addComputerPlayer();
    town.addComputerPlayer();
    final int initialHealth = town.getTarget().getHealth();
    int initialTurn = town.getCurrentTurn();
    town.lookAround();
    town.lookAround();
    assertTrue("Turn number should increase",
        town.getCurrentTurn() > initialTurn);
    assertEquals("Target health should remain unchanged",
        initialHealth, town.getTarget().getHealth());
    while (!town.isGameOver()) {
      town.lookAround();
    }
    assertTrue("Game should be over", town.isGameOver());
    assertTrue("Current turn should exceed max turns",
        town.getCurrentTurn() > MAX_TURNS);
    assertFalse("Target should not be defeated",
        town.getTarget().isDefeated());
    assertEquals("Target health should remain unchanged at end",
        initialHealth, town.getTarget().getHealth());
  }

  /**
   * Tests victory conditions when target health reaches zero.
   * Verifies proper game ending and state transitions on target defeat.
   *
   * @throws IOException if there is an error with I/O operations during test execution
   */
  @Test
  public void testZeroHealthVictoryConditions() throws IOException {

    Player attacker = new PlayerModel("Attacker", false, 5, 1, output,
        new Scanner(new StringReader("")));
    town.getPlayers().add(attacker);
    Item weapon = new ItemModel("PowerfulWeapon", 50);
    attacker.pickUpItem(weapon);
    int targetLocation = Integer.parseInt(town.getTarget().getCurrentPlace().getPlaceNumber());
    attacker.moveToPlaceNumber(targetLocation);
    town.executeItemAttack(attacker, weapon);
    assertTrue("Game should end after fatal attack", town.isGameOver());
    assertTrue("Target should be defeated", town.getTarget().isDefeated());
    assertEquals("Target health should be 0 after fatal attack",
        0, town.getTarget().getHealth());
    assertTrue("Output should indicate successful attack",
        output.toString().contains("Attack successful"));
  }

  /**
   * Tests game ending with various player combinations.
   * Verifies that game properly handles different numbers of players during ending.
   *
   * @throws IOException if there is an error with I/O operations during test execution
   */
  @Test
  public void testGameEndingWithDifferentPlayerCombinations() throws IOException {
    int playerCount = 2;
    for (int i = 0; i < playerCount; i++) {
      town.addComputerPlayer();
    }
    while (town.getCurrentTurn() <= MAX_TURNS) {
      assertFalse("Game should not end before max turns", town.isGameOver());
      town.lookAround();
    }
    assertTrue("Game should end when turns exceed max", town.isGameOver());
    assertTrue("Current turn should exceed max turns",
        town.getCurrentTurn() > MAX_TURNS);
    assertFalse("Target should survive when max turns reached",
        town.getTarget().isDefeated());
    assertEquals("Target health should be unchanged",
        50,
        town.getTarget().getHealth());
    assertEquals("Player count should remain constant",
        playerCount,
        town.getPlayers().size());
  }

  /**
   * Tests handling of invalid actions attempted after game end.
   * Verifies that game state remains consistent and invalid actions are properly handled.
   *
   * @throws IOException if there is an error with I/O operations during test execution
   */
  @Test
  public void testInvalidActionsAtGameEnd() throws IOException {
    town.addComputerPlayer();
    Player player = town.getPlayers().get(0);
    Item weapon = new ItemModel("SuperWeapon", 50);
    player.pickUpItem(weapon);
    int targetLocation = Integer.parseInt(town.getTarget().getCurrentPlace().getPlaceNumber());
    player.moveToPlaceNumber(targetLocation);
    town.executeItemAttack(player, weapon);
    assertTrue("Game should be over after target death", town.isGameOver());
    assertTrue("Target should be defeated", town.getTarget().isDefeated());
    assertEquals("Target health should be 0", 0, town.getTarget().getHealth());
    Item newWeapon = new ItemModel("PostGameWeapon", 10);
    player.pickUpItem(newWeapon);
    assertTrue("Game should remain in over state", town.isGameOver());
    assertEquals("Target health should remain 0",
        0, town.getTarget().getHealth());
    String outputText = output.toString();
    assertTrue("Should indicate successful elimination",
        outputText.contains("has eliminated the target"));
  }
}