import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import model.item.Item;
import model.item.ItemModel;
import model.place.Place;
import model.place.PlaceModel;
import model.player.Player;
import model.player.PlayerModel;
import model.town.Town;
import model.town.TownLoader;
import model.town.TownModel;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for the attack mechanism functionality in the game.
 * Tests different aspects of combat including attack conditions, visibility rules,
 * different attack types, item usage, and computer player behavior.
 */
public class AttackMechanismTest {
  private Town town;
  private StringWriter output;
  private Player humanPlayer;
  private Player computerPlayer;
  private Item weapon;

  /**
   * Sets up the test environment before each test.
   * Initializes a new town, creates test players (both human and computer-controlled),
   * and sets up a test weapon.
   *
   * @throws IOException if there is an error reading the world file
   */
  @Before
  public void setUp() throws IOException {
    // Initialize output capture
    output = new StringWriter();

    // Create a new town instance
    town = new TownModel(
        new TownLoader(),
        "res/SmallTownWorld.txt",
        new StringReader(""),
        output,
        20
    );

    // Create test places
    List<Place> testPlaces = new ArrayList<>();
    testPlaces.add(new PlaceModel(0, 0, 2, 3, "TestPlace1", "1"));
    testPlaces.add(new PlaceModel(2, 0, 4, 1, "TestPlace2", "2"));

    // Create test weapon
    weapon = new ItemModel("TestWeapon", 10);

    // Create test players
    humanPlayer =
        new PlayerModel("TestHuman", false, 5, 1);
    computerPlayer =
        new PlayerModel("TestComputer", true, 5, 1);
  }

  /**
   * Tests that attacks can only be executed when the player is in the same space as the target.
   * A player in a different space should not be able to attack the target.
   * 和Target不在一个place
   *
   * @throws IOException if there is an error executing the attack
   */
  @Test
  public void testAttackSameSpaceRequirement() throws IOException {
    // Move target to place 1
    while (!town.getTarget().getCurrentPlace().getPlaceNumber().equals("1")) {
      town.moveTarget();
    }

    // Move player to place 2 (different from target)
    humanPlayer.moveToPlaceNumber(2);

    // Attempt attack with weapon
    town.executeItemAttack(humanPlayer, weapon);

    // Verify attack failed due to different locations
    assertTrue(output.toString().contains("must be in the same room"));
    assertEquals(town.getTarget().getHealth(), 50); // Health unchanged
  }

  /**
   * Tests that attacks fail when they can be witnessed by other players.
   * Verifies that a player cannot successfully attack the target when another player
   * is in the same space.
   * 相邻有其他player
   *
   * @throws IOException if there is an error executing the attack
   */
  @Test
  public void testAttackVisibilityConditions() throws IOException {
    // Prepare input data for adding players
    String inputs =
        "Player1\n2\n5\nPlayer2\n3\n5\n";
    Town testTown = new TownModel(
        new TownLoader(),
        "res/SmallTownWorld.txt",
        new StringReader(inputs),
        output,
        20
    );

    // Add players with prepared input data
    testTown.addPlayer(); // Add human player
    testTown.addPlayer(); // Add another player to witness

    Player attacker = testTown.getPlayers().get(0);
    Player witness = testTown.getPlayers().get(1);

    // Give weapon to attacker
    ((PlayerModel) attacker).pickUpItem(weapon);

    // Move both players to target's location
    int targetLocation = Integer.parseInt(testTown.getTarget().getCurrentPlace().getPlaceNumber());
    attacker.moveToPlaceNumber(targetLocation);
    witness.moveToPlaceNumber(targetLocation);

    // Attempt attack
    testTown.executeItemAttack(attacker, weapon);

    // Verify attack failed due to witness
    String outputStr = output.toString();
    assertTrue("Attack should fail due to witness",
        outputStr.contains("witnessed your attempt")
            || outputStr.contains("Attack failed"));
    assertEquals("Target health should be unchanged",
        50, testTown.getTarget().getHealth());
  }

  /**
   * Tests that invisible players can successfully attack the target.
   * Verifies that a player can attack the target when no other players are present
   * to witness the attack.
   * 相邻有其他player，但当前位置有pet
   *
   * @throws IOException if there is an error executing the attack
   */
  @Test
  public void testItemAttackTargetInvisible() throws IOException {
    // Prepare input data for adding players
    String inputs =
        "attacker\n1\n5\nwitness\n2\n5\n";
    Town testTown = new TownModel(
        new TownLoader(),
        "res/SmallTownWorld.txt",
        new StringReader(inputs),
        output,
        20
    );

    // Add players with prepared input data
    testTown.addPlayer(); // Add human player
    testTown.addPlayer(); // Add another player to witness

    Player attacker = testTown.getPlayers().get(0);
    Player witness = testTown.getPlayers().get(1);
    System.out.println(attacker.getPlayerCurrentPlaceNumber());
    System.out.println(witness.getPlayerCurrentPlaceNumber());
    System.out.println(town.getPet().getPetCurrentPlaceNumber());

    // Give weapon to attacker
    ((PlayerModel) attacker).pickUpItem(weapon);
    System.out.println(weapon.getDamage());

    // Attempt attack
    testTown.executeItemAttack(attacker, weapon);

    // Verify attack failed due to witness
    String outputStr = output.toString();
    assertFalse("Attack should fail due to witness",
        outputStr.contains("witnessed your attempt")
            || outputStr.contains("Attack failed"));
    assertEquals("Target health should be changed",
        40, testTown.getTarget().getHealth());
  }

  /**
   * Tests that invisible players can successfully attack the target.
   * Verifies that a player can attack the target when no other players are present
   * to witness the attack.
   * 用Item攻击target
   *
   * @throws IOException if there is an error executing the attack
   */
  @Test
  public void testItemKillTargetInvisible() throws IOException {
    // Prepare input data for adding players
    String inputs =
        "attacker\n1\n5\nwitness\n2\n5\n";
    Town testTown = new TownModel(
        new TownLoader(),
        "res/SmallTownWorld.txt",
        new StringReader(inputs),
        output,
        20
    );

    // Add players with prepared input data
    testTown.addPlayer(); // Add human player
    testTown.addPlayer(); // Add another player to witness

    Player attacker = testTown.getPlayers().get(0);

    // Give weapon to attacker
    ((PlayerModel) attacker).pickUpItem(weapon);

    // Attempt attack
    testTown.executeItemAttack(attacker, weapon);
    testTown.executeItemAttack(attacker, weapon);
    testTown.executeItemAttack(attacker, weapon);
    testTown.executeItemAttack(attacker, weapon);
    testTown.executeItemAttack(attacker, weapon);

    // Verify attack failed due to witness
    String outputStr = output.toString();
    assertTrue(outputStr.contains("attacker has eliminated the target!"));
  }

  /**
   * Tests that invisible players can successfully attack the target.
   * Verifies that a player can attack the target when no other players are present
   * to witness the attack.
   * 用poke攻击target
   *
   * @throws IOException if there is an error executing the attack
   */
  @Test
  public void testPokeKillTargetInvisible() throws IOException {
    // Prepare input data for adding players
    String inputs =
        "attacker\n1\n5\nwitness\n2\n5\n";
    Town testTown = new TownModel(
        new TownLoader(),
        "res/SmallTownWorld.txt",
        new StringReader(inputs),
        output,
        20
    );

    // Add players with prepared input data
    testTown.addPlayer(); // Add human player
    testTown.addPlayer(); // Add another player to witness

    Player attacker = testTown.getPlayers().get(0);

    // Give weapon to attacker
    ((PlayerModel) attacker).pickUpItem(weapon);

    // Attempt attack
    int i = 0;
    while (i < 50) {
      testTown.executePoke(attacker);
      i++;
    }


    // Verify attack failed due to witness
    String outputStr = output.toString();
    System.out.println(outputStr);
    assertTrue(outputStr.contains("attacker has eliminated the target"));
  }

  @Test
  public void testPokeAttackTargetInvisible() throws IOException {
    // Prepare input data for adding players
    String inputs =
        "attacker\n1\n5\nwitness\n2\n5\n";
    Town testTown = new TownModel(
        new TownLoader(),
        "res/SmallTownWorld.txt",
        new StringReader(inputs),
        output,
        20
    );

    // Add players with prepared input data
    testTown.addPlayer(); // Add human player
    testTown.addPlayer(); // Add another player to witness

    Player attacker = testTown.getPlayers().get(0);

    // Attempt attack
    testTown.executePoke(attacker);

    // Verify attack failed due to witness
    String outputStr = output.toString();
    assertFalse("Attack should fail due to witness",
        outputStr.contains("witnessed your attempt")
            || outputStr.contains("Attack failed"));
    assertEquals("Target health should be changed",
        49, testTown.getTarget().getHealth());
  }

  @Test
  public void testItemAttackTargetVisible() throws IOException {
    // Prepare input data for adding players
    String inputs =
        "attacker\n1\n5\nwitness\n2\n5\n";
    Town testTown = new TownModel(
        new TownLoader(),
        "res/SmallTownWorld.txt",
        new StringReader(inputs),
        output,
        20
    );

    // Add players with prepared input data
    testTown.addPlayer(); // Add human player
    testTown.addPlayer(); // Add another player to witness

    Player attacker = testTown.getPlayers().get(0);

    // Move the pet location
    testTown.movePet(4);

    // Give weapon to attacker
    ((PlayerModel) attacker).pickUpItem(weapon);

    // Attempt attack
    testTown.executeItemAttack(attacker, weapon);

    // Verify attack failed due to witness
    String outputStr = output.toString();
    assertTrue("Attack should fail due to witness",
        outputStr.contains("witnessed your attempt")
            || outputStr.contains("Attack failed"));
    assertEquals("Target health should be unchanged",
        50, testTown.getTarget().getHealth());
  }

  /**
   * 相邻位置没人的时候攻击target.
   *
   * @throws IOException if there is an error executing the attack
   */
  @Test
  public void testPokeAttackTargetInvisibleWithNoPet() throws IOException {
    // Prepare input data for adding players
    String inputs =
        "attacker\n1\n5\nwitness\n3\n5\n";
    Town testTown = new TownModel(
        new TownLoader(),
        "res/SmallTownWorld.txt",
        new StringReader(inputs),
        output,
        20
    );

    // Add players with prepared input data
    testTown.addPlayer(); // Add human player
    testTown.addPlayer(); // Add another player to witness

    Player attacker = testTown.getPlayers().get(0);

    // Move the pet location
    testTown.movePet(4);

    // Attempt attack
    testTown.executePoke(attacker);

    // Verify attack failed due to witness
    String outputStr = output.toString();
    assertFalse("Attack should fail due to witness",
        outputStr.contains("witnessed your attempt")
            || outputStr.contains("Attack failed"));
    assertEquals("Target health should be unchanged",
        49, testTown.getTarget().getHealth());
  }

  @Test
  public void testItemAttackTargetInvisibleWithNoPet() throws IOException {
    // Prepare input data for adding players
    String inputs =
        "attacker\n5\n5\nwitness\n8\n5\n";
    Town testTown = new TownModel(
        new TownLoader(),
        "res/SmallTownWorld.txt",
        new StringReader(inputs),
        output,
        20
    );

    // Add players with prepared input data
    testTown.addPlayer(); // Add human player
    testTown.addPlayer(); // Add another player to witness

    Player attacker = testTown.getPlayers().get(0);

    // move target to place 5
    while (!testTown.getTarget().getCurrentPlace().getPlaceNumber().equals("5")) {
      testTown.moveTarget();
    }

    // Give weapon to attacker
    ((PlayerModel) attacker).pickUpItem(weapon);

    // Attempt attack
    testTown.executeItemAttack(attacker, weapon);

    // Verify attack failed due to witness
    String outputStr = output.toString();
    assertFalse("Attack should fail due to witness",
        outputStr.contains("witnessed your attempt")
            || outputStr.contains("Attack failed"));
    assertEquals("Target health should be unchanged",
        40, testTown.getTarget().getHealth());
  }

  /**
   * Tests a successful item-based attack on the target.
   * Verifies that the attack reduces target health correctly and consumes the used item.
   *
   * @throws IOException if there is an error executing the attack
   */
  @Test
  public void testSuccessfulItemAttack() throws IOException {
    // Setup player with weapon
    humanPlayer.pickUpItem(weapon);

    // Move player to target's location
    int targetLocation = Integer.parseInt(town.getTarget().getCurrentPlace().getPlaceNumber());
    humanPlayer.moveToPlaceNumber(targetLocation);

    // Execute attack
    town.executeItemAttack(humanPlayer, weapon);

    // Verify attack success
    assertEquals(40, town.getTarget().getHealth()); // Health reduced by weapon damage
    assertFalse(humanPlayer.getCurrentCarriedItems().contains(weapon)); // Weapon consumed
    assertTrue(output.toString().contains("Attack successful"));
  }

  /**
   * Tests the basic poke attack mechanism.
   * Verifies that a poke attack deals exactly 1 damage when successful.
   *
   * @throws IOException if there is an error executing the attack
   */
  @Test
  public void testPokeAttack() throws IOException {
    // Move player to target's location
    int targetLocation = Integer.parseInt(town.getTarget().getCurrentPlace().getPlaceNumber());
    humanPlayer.moveToPlaceNumber(targetLocation);

    // Execute poke attack
    town.executePoke(humanPlayer);

    // Verify attack results
    assertEquals(49, town.getTarget().getHealth()); // Health reduced by 1
    assertTrue(output.toString().contains("poked the target"));
  }

  /**
   * Tests the attack behavior of computer-controlled players.
   * Verifies that computer players always choose their strongest weapon for attacks
   * and that the weapon is properly consumed after use.
   * Computer-controller 玩家有多个weapon的时候，选择最强的weapon
   *
   * @throws IOException if there is an error executing the attack
   */
  @Test
  public void testComputerPlayerAttackBehavior() throws IOException {
    // Give computer player multiple weapons
    Item weakWeapon = new ItemModel("WeakWeapon", 5);
    Item strongWeapon = new ItemModel("StrongWeapon", 15);
    computerPlayer.pickUpItem(weakWeapon);
    computerPlayer.pickUpItem(strongWeapon);

    // Move computer player to target's location
    int targetLocation = Integer.parseInt(town.getTarget().getCurrentPlace().getPlaceNumber());
    computerPlayer.moveToPlaceNumber(targetLocation);

    // Execute computer attack
    town.executeComputerAttack(computerPlayer);

    // Verify computer used the strongest weapon
    assertEquals(35, town.getTarget().getHealth()); // Damage from the strongest weapon
    assertFalse(
        computerPlayer.getCurrentCarriedItems().contains(strongWeapon)); // Strong weapon consumed
    assertTrue(
        computerPlayer.getCurrentCarriedItems().contains(weakWeapon)); // Weak weapon retained
  }

  /**
   * Tests that computer players use poke attack when they have no items.
   * Verifies that the poke attack deals the correct amount of damage.
   * Computer-controller 玩家没有weapon的时候，使用poke攻击
   *
   * @throws IOException if there is an error executing the attack
   */
  @Test
  public void testComputerPlayerPokeAttack() throws IOException {
    // Move computer player to target's location
    int targetLocation = Integer.parseInt(town.getTarget().getCurrentPlace().getPlaceNumber());
    computerPlayer.moveToPlaceNumber(targetLocation);

    // Execute computer attack with no items
    town.executeComputerAttack(computerPlayer);

    // Verify computer used poke attack
    assertEquals(49, town.getTarget().getHealth()); // 1 damage from poke
    assertTrue(output.toString().contains("poked the target"));
  }

  /**
   * Tests that an IllegalArgumentException is thrown when attempting to attack with a null player.
   *
   * @throws IOException if there is an error executing the attack
   */
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidAttackParameters() throws IOException {
    town.executeItemAttack(null, weapon); // Should throw IllegalArgumentException
  }

  /**
   * Tests that an IllegalArgumentException is thrown when attempting to attack with a null item.
   * 当玩家没有item时无法用item攻击
   *
   * @throws IOException if there is an error executing the attack
   */
  @Test(expected = IllegalArgumentException.class)
  public void testNullItemAttack() throws IOException {
    town.executeItemAttack(humanPlayer, null); // Should throw IllegalArgumentException
  }

  /**
   * Tests the target defeat scenario when their health reaches zero.
   * Verifies that the game ends properly when the target is defeated.
   *
   * @throws IOException if there is an error executing the attack
   */
  @Test
  public void testTargetDefeat() throws IOException {
    // Create powerful weapon
    Item powerfulWeapon = new ItemModel("PowerfulWeapon", 50);
    humanPlayer.pickUpItem(powerfulWeapon);

    // Move player to target's location
    int targetLocation = Integer.parseInt(town.getTarget().getCurrentPlace().getPlaceNumber());
    humanPlayer.moveToPlaceNumber(targetLocation);

    // Execute attack
    town.executeItemAttack(humanPlayer, powerfulWeapon);

    // Verify target defeated
    assertEquals(0, town.getTarget().getHealth());
    assertTrue(town.isGameOver());
    assertTrue(output.toString().contains("eliminated the target"));
  }

  /**
   * Tests the execution of multiple consecutive attacks.
   * Verifies that damage is properly accumulated and health is correctly reduced
   * over multiple successful attacks.
   * 连续攻击
   *
   * @throws IOException if there is an error executing the attacks
   */
  @Test
  public void testConsecutiveAttacks() throws IOException {
    // Setup player with multiple weapons
    Item weapon1 = new ItemModel("Weapon1", 10);
    Item weapon2 = new ItemModel("Weapon2", 15);
    humanPlayer.pickUpItem(weapon1);
    humanPlayer.pickUpItem(weapon2);

    // Move player to target's location
    int targetLocation = Integer.parseInt(town.getTarget().getCurrentPlace().getPlaceNumber());
    humanPlayer.moveToPlaceNumber(targetLocation);

    // Execute first attack
    town.executeItemAttack(humanPlayer, weapon1);
    assertEquals(40, town.getTarget().getHealth());

    // Execute second attack
    town.executeItemAttack(humanPlayer, weapon2);
    assertEquals(25, town.getTarget().getHealth());
  }

  /**
   * Tests that attacks can still be executed when the pet is present in the same space.
   * Verifies that the pet's presence does not affect direct attack mechanics.
   *
   * @throws IOException if there is an error executing the attack
   */
  @Test
  public void testAttackWithPetPresence() throws IOException {
    // Move pet to target's location
    int targetLocation = Integer.parseInt(town.getTarget().getCurrentPlace().getPlaceNumber());
    town.getPet().movePet(targetLocation);

    // Move player to target's location
    humanPlayer.moveToPlaceNumber(targetLocation);
    humanPlayer.pickUpItem(weapon);

    // Execute attack
    town.executeItemAttack(humanPlayer, weapon);

    // Verify attack execution (pet shouldn't affect direct attacks)
    assertEquals(40, town.getTarget().getHealth());
  }

  /**
   * Tests that items are properly removed from player inventory after a successful attack.
   * Verifies that used items are consumed and removed from the player's possession.
   * 使用item攻击后，item被消耗
   *
   * @throws IOException if there is an error executing the attack
   */
  @Test
  public void testItemRemovalAfterAttack() throws IOException {
    // Give player a weapon
    humanPlayer.pickUpItem(weapon);
    assertTrue(humanPlayer.getCurrentCarriedItems().contains(weapon));

    // Move player to target's location
    int targetLocation = Integer.parseInt(town.getTarget().getCurrentPlace().getPlaceNumber());
    humanPlayer.moveToPlaceNumber(targetLocation);

    // Execute attack
    town.executeItemAttack(humanPlayer, weapon);

    // Verify weapon was removed
    assertFalse(humanPlayer.getCurrentCarriedItems().contains(weapon));
    assertEquals(0, humanPlayer.getCurrentCarriedItems().size());
  }

  /**
   * Tests the game end conditions after a successful fatal attack.
   * Verifies that the game properly ends when the target is eliminated and
   * the correct winning conditions are met.
   * 成功杀死target后游戏结束
   *
   * @throws IOException if there is an error executing the attack
   */
  @Test
  public void testGameEndConditionsAfterAttack() throws IOException {
    // Create fatal weapon
    Item fatalWeapon = new ItemModel("FatalWeapon", 50);
    humanPlayer.pickUpItem(fatalWeapon);

    // Move player to target's location
    int targetLocation = Integer.parseInt(town.getTarget().getCurrentPlace().getPlaceNumber());
    humanPlayer.moveToPlaceNumber(targetLocation);

    // Execute fatal attack
    town.executeItemAttack(humanPlayer, fatalWeapon);

    // Verify game end conditions
    assertTrue(town.isGameOver());
    assertTrue(town.getTarget().isDefeated());
    assertTrue(output.toString().contains("eliminated the target"));
  }
}