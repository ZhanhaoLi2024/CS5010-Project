import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import controller.command.AttackTargetCommand;
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
import model.town.TownData;
import model.town.TownLoader;
import model.town.TownModel;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for AttackTargetCommand. Tests various scenarios of player attacks
 * on the target character.
 */
public class AttackTargetCommandTest {
  private Town town;
  private StringWriter output;

  /**
   * Sets up test environment before each test case.
   */
  @Before
  public void setUp() throws IOException {
    output = new StringWriter();

    // Create mock TownLoader
    TownLoader mockLoader = new TownLoader() {
      @Override
      public TownData loadTown(String filename) {
        // Create test places
        List<Place> places = new ArrayList<>();
        places.add(new PlaceModel(0, 0, 1, 1, "Room1", "1"));
        places.add(new PlaceModel(1, 1, 2, 2, "Room2", "2"));

        // Create test items
        List<Item> items = new ArrayList<>();
        items.add(new ItemModel("Sword", 10));
        items.add(new ItemModel("Dagger", 5));

        return new TownData(
            "TestTown",  // townName
            "Target",    // targetName
            "Pet",       // petName
            20,         // targetHealth
            places,     // places
            items       // items
        );
      }
    };

    // Initialize town with mock data
    town = new TownModel(mockLoader, "dummy.txt", new StringReader(""), output, 10);
  }

  /**
   * Tests mixed human and computer players attack scenario.
   * 有人和电脑玩家攻击的情况。
   */
  @Test
  public void testMixedPlayersAttack() throws IOException {
    // Create input for scanning
    String inputString = "1\n";
    StringReader input = new StringReader(inputString);

    // Create town with the simulated input and output
    town = new TownModel(new TownLoader() {
      @Override
      public TownData loadTown(String filename) {
        List<Place> places = new ArrayList<>();
        places.add(new PlaceModel(0, 0, 1, 1, "Room1", "1"));
        places.add(new PlaceModel(1, 1, 2, 2, "Room2", "2"));

        List<Item> items = new ArrayList<>();
        items.add(new ItemModel("Sword", 10));
        items.add(new ItemModel("Dagger", 5));

        return new TownData(
            "TestTown",
            "Target",
            "Pet",
            20,
            places,
            items
        );
      }
    }, "dummy.txt", input, output, 10);

    // Add human player
    Player humanPlayer = new PlayerModel("Human", false, 3, 1);
    town.getPlayers().add(humanPlayer);

    // Add computer player
    Player computerPlayer = new PlayerModel("Computer", true, 3, 1);
    town.getPlayers().add(computerPlayer);

    // Give items to players
    Item sword = new ItemModel("Sword", 10);
    Item dagger = new ItemModel("Dagger", 5);
    humanPlayer.pickUpItem(sword);
    computerPlayer.pickUpItem(dagger);

    // Set target location to same room as players
    Place targetPlace = town.getPlaceByNumber(humanPlayer.getPlayerCurrentPlaceNumber());
    while (!town.getTarget().getCurrentPlace().equals(targetPlace)) {
      town.moveTarget();
    }

    // Test both player attacks in sequence
    // Both players and target are in the same room to ensure attacks can happen

    // Human player attack
    AttackTargetCommand humanCmd = new AttackTargetCommand(town, output);
    humanCmd.execute();

    // Get result after first attack
    String humanOutputText = output.toString();
    assertTrue("Human attack outcome not found",
        humanOutputText.contains("Attack successful")
            || humanOutputText.contains("Attack failed")
            || humanOutputText.contains("Congratulations"));

    // If game is not over, test computer attack
    if (!town.isGameOver()) {
      // Computer player attack
      AttackTargetCommand computerCmd = new AttackTargetCommand(town, output);
      computerCmd.execute();

      // Get complete output
      String fullOutput = output.toString();
      assertTrue("Computer attack outcome not found",
          fullOutput.contains("Attack successful with Dagger")
              || fullOutput.contains("Attack failed: Other players have witnessed your attempt")
              || fullOutput.contains("Attack failed: You must be in the same room"));
    }
  }

  /**
   * Tests scenario with only human players.
   */
  @Test
  public void testOnlyHumanPlayersAttack() throws IOException {
    // Set up input string for scanner
    String inputString = "1\n"; // Simulate user choosing option 1 for attack
    StringReader input = new StringReader(inputString);

    // Create town with the simulated input
    town = new TownModel(new TownLoader() {
      @Override
      public TownData loadTown(String filename) {
        // Create test places
        List<Place> places = new ArrayList<>();
        places.add(new PlaceModel(0, 0, 1, 1, "Room1", "1"));
        places.add(new PlaceModel(1, 1, 2, 2, "Room2", "2"));

        // Create test items
        List<Item> items = new ArrayList<>();
        items.add(new ItemModel("Sword", 10));
        items.add(new ItemModel("Dagger", 5));

        return new TownData(
            "TestTown",  // townName
            "Target",    // targetName
            "Pet",       // petName
            20,         // targetHealth
            places,     // places
            items       // items
        );
      }
    }, "dummy.txt", input, output, 10);

    // Add two human players in the same room as target
    Player human1 = new PlayerModel("Human1", false, 3, 1);
    Player human2 = new PlayerModel("Human2", false, 3, 2);
    town.getPlayers().add(human1);
    town.getPlayers().add(human2);

    // Give item to first human player and put them in same room as target
    Item sword = new ItemModel("Sword", 10);
    human1.pickUpItem(sword);

    // Move target to the same room as human1
    Place targetPlace = town.getPlaceByNumber(human1.getPlayerCurrentPlaceNumber());
    while (!town.getTarget().getCurrentPlace().equals(targetPlace)) {
      town.moveTarget();
    }

    AttackTargetCommand cmd = new AttackTargetCommand(town, output);
    cmd.execute();

    String outputText = output.toString();
    assertTrue("Expected attack outcome message not found",
        outputText.contains("Attack successful")
            || outputText.contains("Attack failed")
            || outputText.contains("Congratulations"));
  }

  /**
   * Tests scenario with only computer players.
   */
  @Test
  public void testOnlyComputerPlayersAttack() throws IOException {
    // Add two computer players
    Player computer1 = new PlayerModel("Computer1", true, 3, 1);
    Player computer2 = new PlayerModel("Computer2", true, 3, 2);
    town.getPlayers().add(computer1);
    town.getPlayers().add(computer2);

    // Give item to first computer player
    computer1.pickUpItem(new ItemModel("Sword", 10));

    AttackTargetCommand cmd = new AttackTargetCommand(town, output);
    cmd.execute();

    String outputText = output.toString();
    assertTrue(outputText.contains("Attack successful")
        || outputText.contains("Attack failed"));
  }

  /**
   * Tests successful attack that defeats the target.
   * Human-controller杀死目标的情况。
   */
  @Test
  public void testSuccessfulAttackDefeatTarget() throws IOException {
    // Set up input for the attack choice
    String inputString = "1\n"; // Simulate choosing to use the weapon
    StringReader input = new StringReader(inputString);

    // Create town with simulated input and reduced target health
    town = new TownModel(new TownLoader() {
      @Override
      public TownData loadTown(String filename) {
        // Create test places
        List<Place> places = new ArrayList<>();
        places.add(new PlaceModel(0, 0, 1, 1, "Room1", "1"));
        places.add(new PlaceModel(1, 1, 2, 2, "Room2", "2"));

        List<Item> items = new ArrayList<>();
        items.add(new ItemModel("SuperSword", 20));

        return new TownData(
            "TestTown",
            "Target",
            "Pet",
            15, // Low health to ensure one hit will defeat
            places,
            items
        );
      }
    }, "dummy.txt", input, output, 10);

    // Add player with powerful weapon
    Player player = new PlayerModel("Player", false, 3, 1);
    town.getPlayers().add(player);

    // Add a second player to enable turn switching but in a different room
    Player player2 = new PlayerModel("Player2", false, 3, 2);
    town.getPlayers().add(player2);

    // Give player a weapon that can defeat target in one hit
    Item superSword = new ItemModel("SuperSword", 20);
    player.pickUpItem(superSword);

    // Move target to same room as attacking player
    Place targetPlace = town.getPlaceByNumber(player.getPlayerCurrentPlaceNumber());
    while (!town.getTarget().getCurrentPlace().equals(targetPlace)) {
      town.moveTarget();
    }

    // Execute attack command
    AttackTargetCommand cmd = new AttackTargetCommand(town, output);
    cmd.execute();

    // Verify outcome
    String outputText = output.toString();
    assertTrue("Expected successful attack outcome not found",
        outputText.contains("Attack successful")
            && outputText.contains("Congratulations! You have defeated the target!"));

    // Verify game is actually over
    assertTrue("Game should be over after defeating target",
        town.isGameOver());
  }

  /**
   * Tests unsuccessful attack that doesn't defeat target.
   * target被攻击但还没死
   */
  @Test
  public void testUnsuccessfulAttackContinueGame() throws IOException {
    // Set up input for attack choice
    String inputString = "1\n"; // Simulate choosing to use the dagger
    StringReader input = new StringReader(inputString);

    // Create town with the simulated input
    town = new TownModel(new TownLoader() {
      @Override
      public TownData loadTown(String filename) {
        // Create test places
        List<Place> places = new ArrayList<>();
        places.add(new PlaceModel(0, 0, 1, 1, "Room1", "1"));
        places.add(new PlaceModel(1, 1, 2, 2, "Room2", "2"));

        List<Item> items = new ArrayList<>();
        items.add(new ItemModel("Dagger", 5));

        return new TownData(
            "TestTown",
            "Target",
            "Pet",
            50, // High health to ensure attack won't kill
            places,
            items
        );
      }
    }, "dummy.txt", input, output, 10);

    // Add two players to enable turn switching
    Player player1 = new PlayerModel("Player1", false, 3, 1);
    Player player2 = new PlayerModel("Player2", false, 3, 2);
    town.getPlayers().add(player1);
    town.getPlayers().add(player2);

    // Give first player a weak weapon
    Item dagger = new ItemModel("Dagger", 5);
    player1.pickUpItem(dagger);

    // Ensure target is in same room as attacking player
    Place targetPlace = town.getPlaceByNumber(player1.getPlayerCurrentPlaceNumber());
    while (!town.getTarget().getCurrentPlace().equals(targetPlace)) {
      town.moveTarget();
    }

    // Execute attack command
    AttackTargetCommand cmd = new AttackTargetCommand(town, output);
    cmd.execute();

    // Verify outcome
    String outputText = output.toString();
    // Check both the attack result and turn change
    assertTrue("Expected attack outcome and turn change not found",
        (outputText.contains("Attack successful") && outputText.contains("Turn changed from"))
            || (outputText.contains("Attack failed") && outputText.contains("Turn changed from")));

    // Additional verification that game is not over
    assertFalse("Game should not be over after unsuccessful attack",
        town.isGameOver());
  }

  /**
   * Tests attack when player is not in same room as target.
   * target和玩家不在一个place中
   */
  @Test
  public void testAttackFromDifferentRoom() throws IOException {
    // Set up input for the attack choice
    String inputString = "1\n"; // Simulate choosing to attack with weapon or poke
    StringReader input = new StringReader(inputString);

    // Create town with simulated input
    town = new TownModel(new TownLoader() {
      @Override
      public TownData loadTown(String filename) {
        // Create test places
        List<Place> places = new ArrayList<>();
        places.add(new PlaceModel(0, 0, 1, 1, "Room1", "1"));
        places.add(new PlaceModel(1, 1, 2, 2, "Room2", "2"));

        List<Item> items = new ArrayList<>();
        items.add(new ItemModel("Sword", 10));

        return new TownData(
            "TestTown",
            "Target",
            "Pet",
            20,
            places,
            items
        );
      }
    }, "dummy.txt", input, output, 10);

    // Add player in Room2
    Player player = new PlayerModel("Player", false, 3, 2);
    town.getPlayers().add(player);

    // Add a second player to enable turn switching
    Player player2 = new PlayerModel("Player2", false, 3, 2);
    town.getPlayers().add(player2);

    // Give player a weapon
    Item sword = new ItemModel("Sword", 10);
    player.pickUpItem(sword);

    // Ensure target is in Room1 (different from player's room)
    while (!town.getTarget().getCurrentPlace().equals(town.getPlaceByNumber(1))) {
      town.moveTarget();
    }

    // Execute attack command
    AttackTargetCommand cmd = new AttackTargetCommand(town, output);
    cmd.execute();

    // Verify outcome
    String outputText = output.toString();
    assertTrue("Expected attack failure message not found",
        outputText.contains("Attack failed: You must be in the same room as the target"));

    // Verify game continues
    assertFalse("Game should not be over after failed attack",
        town.isGameOver());
  }

  /**
   * Tests attack when player is visible to others.
   * 攻击被其他玩家看到的情况 - same place
   */
  @Test
  public void testVisiblePlayerAttack() throws IOException {
    // Set up input for the attack choice
    String inputString = "1\n"; // Simulate choosing to use the weapon
    StringReader input = new StringReader(inputString);

    // Create town with simulated input
    town = new TownModel(new TownLoader() {
      @Override
      public TownData loadTown(String filename) {
        // Create test places
        List<Place> places = new ArrayList<>();
        places.add(new PlaceModel(0, 0, 1, 1, "Room1", "1"));
        places.add(new PlaceModel(1, 1, 2, 2, "Room2", "2"));

        List<Item> items = new ArrayList<>();
        items.add(new ItemModel("Sword", 10));

        return new TownData(
            "TestTown",
            "Target",
            "Pet",
            20,
            places,
            items
        );
      }
    }, "dummy.txt", input, output, 10);

    // Add two players in same room
    Player player1 = new PlayerModel("Player1", false, 3, 1);
    Player player2 = new PlayerModel("Player2", false, 3, 1);
    town.getPlayers().add(player1);
    town.getPlayers().add(player2);

    // Give first player a weapon
    Item sword = new ItemModel("Sword", 10);
    player1.pickUpItem(sword);

    // Ensure target is in the same room as players
    Place targetPlace = town.getPlaceByNumber(player1.getPlayerCurrentPlaceNumber());
    while (!town.getTarget().getCurrentPlace().equals(targetPlace)) {
      town.moveTarget();
    }

    // Make sure pet is not in the same room (to not block visibility)
    if (town.getPet().getPetCurrentPlaceNumber() == 1) {
      town.movePet(2);
    }

    // Execute attack command
    AttackTargetCommand cmd = new AttackTargetCommand(town, output);
    cmd.execute();

    // Verify outcome
    String outputText = output.toString();
    assertTrue("Expected attack failure due to witness not found",
        outputText.contains("Attack failed")
            && outputText.contains("Other players have witnessed your attempt"));

    // Verify game continues
    assertFalse("Game should not be over after failed attack",
        town.isGameOver());

    // Verify target health unchanged
    assertEquals("Target health should remain unchanged",
        20, town.getTarget().getHealth());
  }

  /**
   * Test computer-only players successfully kill target.
   * 电脑玩家杀死目标的情况。
   */
  @Test
  public void testComputerOnlyPlayersKillTarget() throws IOException {
    StringReader input = new StringReader(""); // No input needed for computer players

    town = new TownModel(new TownLoader() {
      @Override
      public TownData loadTown(String filename) {
        List<Place> places = new ArrayList<>();
        places.add(new PlaceModel(0, 0, 1, 1, "Room1", "1"));
        places.add(new PlaceModel(1, 1, 2, 2, "Room2", "2"));

        List<Item> items = new ArrayList<>();
        items.add(new ItemModel("PowerSword", 25));

        return new TownData(
            "TestTown",
            "Target",
            "Pet",
            20, // Low enough to be killed by one hit
            places,
            items
        );
      }
    }, "dummy.txt", input, output, 10);

    // Add computer players
    Player computer1 = new PlayerModel("Computer1", true, 3, 1);
    Player computer2 = new PlayerModel("Computer2", true, 3, 2);
    town.getPlayers().add(computer1);
    town.getPlayers().add(computer2);

    // Give first computer a powerful weapon
    Item powerSword = new ItemModel("PowerSword", 25);
    computer1.pickUpItem(powerSword);

    // Ensure target is in same room as first computer
    Place targetPlace = town.getPlaceByNumber(computer1.getPlayerCurrentPlaceNumber());
    while (!town.getTarget().getCurrentPlace().equals(targetPlace)) {
      town.moveTarget();
    }

    // Execute attack
    AttackTargetCommand cmd = new AttackTargetCommand(town, output);
    cmd.execute();

    String outputText = output.toString();
    assertTrue("Expected successful computer player kill",
        outputText.contains("has eliminated the target"));
    assertTrue("Game should be over", town.isGameOver());
  }

  /**
   * Test computer-only players fail to kill target before turns end.
   * 电脑玩家在回合结束前未杀死目标的情况。
   */
  @Test
  public void testComputerOnlyPlayersTimeOut() throws IOException {
    StringReader input = new StringReader("");

    town = new TownModel(new TownLoader() {
      @Override
      public TownData loadTown(String filename) {
        List<Place> places = new ArrayList<>();
        places.add(new PlaceModel(0, 0, 1, 1, "Room1", "1"));
        places.add(new PlaceModel(1, 1, 2, 2, "Room2", "2"));

        List<Item> items = new ArrayList<>();
        items.add(new ItemModel("WeakSword", 5));

        return new TownData(
            "TestTown",
            "Target",
            "Pet",
            100, // High health
            places,
            items
        );
      }
    }, "dummy.txt", input, output, 1); // Set to 1 turn

    // Add computer players
    Player computer1 = new PlayerModel("Computer1", true, 3, 1);
    Player computer2 = new PlayerModel("Computer2", true, 3, 2);
    town.getPlayers().add(computer1);
    town.getPlayers().add(computer2);

    // Give weak weapons
    Item weakSword = new ItemModel("WeakSword", 5);
    computer1.pickUpItem(weakSword);

    // Ensure target is in same room as computer1 for attack
    Place targetPlace = town.getPlaceByNumber(computer1.getPlayerCurrentPlaceNumber());
    while (!town.getTarget().getCurrentPlace().equals(targetPlace)) {
      town.moveTarget();
    }

    // Execute first player's turn
    AttackTargetCommand cmd1 = new AttackTargetCommand(town, output);
    cmd1.execute();

    // Execute second player's turn which should exceed max turns
    AttackTargetCommand cmd2 = new AttackTargetCommand(town, output);
    cmd2.execute();

    // Verify game state
    assertTrue("Target should still be alive",
        town.getTarget().getHealth() > 0);

    assertTrue("Current turn should exceed max turns",
        town.getCurrentTurn() > town.getMaxTurns());

    // Verify neither computer player succeeded
    assertFalse("Target should not be defeated",
        town.getTarget().isDefeated());

    String outputText = output.toString();
    assertFalse("No player should have won",
        outputText.contains("has eliminated the target"));
  }

  /**
   * Test human-only players successfully kill target.
   * 人类玩家杀死目标的情况。
   */
  @Test
  public void testHumanOnlyPlayersKillTarget() throws IOException {
    String inputString = "1\n"; // Choose to use weapon
    StringReader input = new StringReader(inputString);

    town = new TownModel(new TownLoader() {
      @Override
      public TownData loadTown(String filename) {
        List<Place> places = new ArrayList<>();
        places.add(new PlaceModel(0, 0, 1, 1, "Room1", "1"));
        places.add(new PlaceModel(1, 1, 2, 2, "Room2", "2"));

        List<Item> items = new ArrayList<>();
        items.add(new ItemModel("PowerSword", 25));

        return new TownData(
            "TestTown",
            "Target",
            "Pet",
            20, // Low enough to be killed in one hit
            places,
            items
        );
      }
    }, "dummy.txt", input, output, 10);

    // Add human players in different rooms
    Player human1 = new PlayerModel("Human1", false, 3, 1);
    Player human2 = new PlayerModel("Human2", false, 3, 2);
    town.getPlayers().add(human1);
    town.getPlayers().add(human2);

    // Give first human a powerful weapon
    Item powerSword = new ItemModel("PowerSword", 25);
    human1.pickUpItem(powerSword);

    // Ensure target is with first human
    Place targetPlace = town.getPlaceByNumber(human1.getPlayerCurrentPlaceNumber());
    while (!town.getTarget().getCurrentPlace().equals(targetPlace)) {
      town.moveTarget();
    }

    AttackTargetCommand cmd = new AttackTargetCommand(town, output);
    cmd.execute();

    String outputText = output.toString();
    assertTrue("Expected successful human player kill",
        outputText.contains("Congratulations! You have defeated the target!"));
    assertTrue("Game should be over", town.isGameOver());
  }

  /**
   * Tests human-only players fail to kill target before turns end.
   * 人类玩家在回合结束前未杀死目标的情况。
   */
  @Test
  public void testHumanOnlyPlayersTimeOut() throws IOException {
    // Set up input for multiple turns
    String inputString = "1\n1\n1\n1\n1"; // Input for weapon choices
    StringReader input = new StringReader(inputString);

    // Create town with very limited turns
    town = new TownModel(new TownLoader() {
      @Override
      public TownData loadTown(String filename) {
        List<Place> places = new ArrayList<>();
        places.add(new PlaceModel(0, 0, 1, 1, "Room1", "1"));
        places.add(new PlaceModel(1, 1, 2, 2, "Room2", "2"));

        List<Item> items = new ArrayList<>();
        items.add(new ItemModel("WeakSword", 5));

        return new TownData(
            "TestTown",
            "Target",
            "Pet",
            100, // High health to prevent early kill
            places,
            items
        );
      }
    }, "dummy.txt", input, output, 2); // Only 3 turn

    // Add players
    Player human1 = new PlayerModel("Human1", false, 3, 1);
    Player human2 = new PlayerModel("Human2", false, 3, 2);
    town.getPlayers().add(human1);
    town.getPlayers().add(human2);

    // Give weak weapon
    Item weakSword = new ItemModel("WeakSword", 5);
    human1.pickUpItem(weakSword);

    // Ensure target is with attacking player
    Place targetPlace = town.getPlaceByNumber(human1.getPlayerCurrentPlaceNumber());
    while (!town.getTarget().getCurrentPlace().equals(targetPlace)) {
      town.moveTarget();
    }

    // Run through all turns
    for (int i = 0; i < 4; i++) {
      AttackTargetCommand cmd = new AttackTargetCommand(town, output);
      cmd.execute();
    }


    // Verify game state
    int currentTurn = town.getCurrentTurn();
    int maxTurns = town.getMaxTurns();

    // Verify target survived
    assertTrue("Target should be alive", town.getTarget().getHealth() > 0);

    // Verify turn count exceeded max
    assertTrue(String.format("Current turn (%d) should exceed max turns (%d)",
            currentTurn, maxTurns),
        currentTurn > maxTurns);

    // Verify neither player won
    String outputText = output.toString();
    assertFalse("No player should have eliminated target",
        outputText.contains("has eliminated the target"));
  }

  /**
   * Test mixed players successfully kill target.
   * human-controller和computer-controller杀死target
   */
  @Test
  public void testMixedPlayersKillTarget() throws IOException {
    String inputString = "1\n"; // For human player weapon choice
    StringReader input = new StringReader(inputString);

    town = new TownModel(new TownLoader() {
      @Override
      public TownData loadTown(String filename) {
        List<Place> places = new ArrayList<>();
        places.add(new PlaceModel(0, 0, 1, 1, "Room1", "1"));
        places.add(new PlaceModel(1, 1, 2, 2, "Room2", "2"));

        List<Item> items = new ArrayList<>();
        items.add(new ItemModel("PowerSword", 25));

        return new TownData(
            "TestTown",
            "Target",
            "Pet",
            20, // Low enough to be killed in one hit
            places,
            items
        );
      }
    }, "dummy.txt", input, output, 10);

    // Add mixed players in different rooms
    Player human = new PlayerModel("Human", false, 3, 1);
    Player computer = new PlayerModel("Computer", true, 3, 2);
    town.getPlayers().add(human);
    town.getPlayers().add(computer);

    // Give human player a powerful weapon
    Item powerSword = new ItemModel("PowerSword", 25);
    human.pickUpItem(powerSword);

    // Ensure target is with human
    Place targetPlace = town.getPlaceByNumber(human.getPlayerCurrentPlaceNumber());
    while (!town.getTarget().getCurrentPlace().equals(targetPlace)) {
      town.moveTarget();
    }

    AttackTargetCommand cmd = new AttackTargetCommand(town, output);
    cmd.execute();

    String outputText = output.toString();
    assertTrue("Expected successful kill",
        outputText.contains("Congratulations! You have defeated the target!"));
    assertTrue("Game should be over", town.isGameOver());
  }

  /**
   * Test mixed players fail to kill target before turns end.
   * human-controller和computer-controller在回合结束前未杀死target
   */
  @Test
  public void testMixedPlayersTimeOut() throws IOException {
    String inputString = "1\n1\n1\n"; // For multiple human player turns
    StringReader input = new StringReader(inputString);

    town = new TownModel(new TownLoader() {
      @Override
      public TownData loadTown(String filename) {
        List<Place> places = new ArrayList<>();
        places.add(new PlaceModel(0, 0, 1, 1, "Room1", "1"));
        places.add(new PlaceModel(1, 1, 2, 2, "Room2", "2"));

        List<Item> items = new ArrayList<>();
        items.add(new ItemModel("WeakSword", 5));

        return new TownData(
            "TestTown",
            "Target",
            "Pet",
            100, // High health
            places,
            items
        );
      }
    }, "dummy.txt", input, output, 1); // Only 3 turns

    // Add mixed players
    Player human = new PlayerModel("Human", false, 3, 1);
    Player computer = new PlayerModel("Computer", true, 3, 2);
    town.getPlayers().add(human);
    town.getPlayers().add(computer);

    // Give weak weapons
    Item weakSword = new ItemModel("WeakSword", 5);
    human.pickUpItem(weakSword);
    computer.pickUpItem(new ItemModel("WeakDagger", 3));

    // Start the first turn
    AttackTargetCommand cmd = new AttackTargetCommand(town, output);
    cmd.execute();

    // Start the second turn
    AttackTargetCommand cmd2 = new AttackTargetCommand(town, output);
    cmd2.execute();

    String outputText = output.toString();
    assertFalse("No player should have eliminated target",
        outputText.contains("has eliminated the target"));
  }
}