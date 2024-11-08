import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import model.place.Place;
import model.place.PlaceModel;
import model.player.Player;
import model.player.PlayerModel;
import model.target.Target;
import model.target.TargetModel;
import model.town.Town;
import model.town.TownLoader;
import model.town.TownModel;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for the target character (The Mayor) functionality including visibility,
 * position information, movement behavior, and health status.
 */
public class TargetCharacterTest {
  private Target targetCharacter;
  private List<Place> places;
  private StringWriter output;
  private Town town;

  /**
   * Sets up the test fixture with target character and town initialization.
   */
  @Before
  public void setUp() {
    // Initialize test places
    places = new ArrayList<>();
    places.add(new PlaceModel(0, 0, 2, 3, "Place1", "1"));
    places.add(new PlaceModel(2, 0, 4, 1, "Place2", "2"));
    places.add(new PlaceModel(4, 0, 6, 2, "Place3", "3"));

    // Initialize target character (The Mayor)
    targetCharacter = new TargetModel("The Mayor", 50, places.get(0), places);

    // Initialize output capture
    output = new StringWriter();

    // Initialize town with test configuration
    try {
      town = new TownModel(
          new TownLoader(),
          "res/SmallTownWorld.txt",
          new StringReader(""),
          output,
          10
      );
    } catch (Exception e) {
      fail("Failed to initialize town: " + e.getMessage());
    }
  }

  /**
   * Test target character creation and initial state
   */
  @Test
  public void testTargetCharacterInitialization() {
    assertEquals("The Mayor", targetCharacter.getName());
    assertEquals(50, targetCharacter.getHealth());
    assertEquals(places.get(0), targetCharacter.getCurrentPlace());
    assertFalse(targetCharacter.isDefeated());
  }

  /**
   * Test target character position display
   *
   * @throws Exception if the target character position display fails
   */
  @Test
  public void testTargetCharacterPositionDisplay() throws Exception {
    town.showTargetInfo();
    String outputText = output.toString();
    assertTrue(outputText.contains("The Mayor"));
    assertTrue(outputText.contains("Health: 50"));
    assertTrue(outputText.contains(town.getTarget().getCurrentPlace().getName()));
  }

  /**
   * Test target character automatic movement
   */
  @Test
  public void testTargetCharacterAutomaticMovement() {
    Place initialPlace = targetCharacter.getCurrentPlace();
    targetCharacter.moveToNextPlace();
    assertFalse("Target character should move to a new place",
        initialPlace.equals(targetCharacter.getCurrentPlace()));
    assertEquals("Target character should move to second place",
        places.get(1), targetCharacter.getCurrentPlace());
  }

  /**
   * Test target character movement cycle
   */
  @Test
  public void testTargetCharacterMovementCycle() {
    // Move through all places
    for (int i = 0; i < places.size(); i++) {
      targetCharacter.moveToNextPlace();
    }
    // Should return to first place
    assertEquals("Target character should return to first place after full cycle",
        places.get(0), targetCharacter.getCurrentPlace());
  }

  /**
   * Test target character health management
   */
  @Test
  public void testTargetCharacterHealthManagement() {
    assertEquals(50, targetCharacter.getHealth());
    targetCharacter.takeDamage(20);
    assertEquals(30, targetCharacter.getHealth());
    assertFalse(targetCharacter.isDefeated());

    targetCharacter.takeDamage(30);
    assertEquals(0, targetCharacter.getHealth());
    assertTrue(targetCharacter.isDefeated());
  }

  /**
   * Test target character health management with invalid damage value
   */
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidDamageValue() {
    targetCharacter.takeDamage(-10);
  }


  /**
   * Test target character position after multiple turns
   *
   * @throws Exception if the target character position after multiple turns fails
   */
  @Test
  public void testTargetCharacterPositionAfterMultipleTurns() throws Exception {
    Place initialPlace = town.getTarget().getCurrentPlace();

    // Simulate multiple turns
    for (int i = 0; i < 3; i++) {
      town.moveTarget();
    }

    assertFalse("Target character should not be in initial place after movement",
        initialPlace.equals(town.getTarget().getCurrentPlace()));
  }

  /**
   * Test target character initialization with invalid values
   */
  @Test(expected = IllegalArgumentException.class)
  public void testTargetCharacterInvalidHealth() {
    new TargetModel("The Mayor", -10, places.get(0), places);
  }

  /**
   * Test target character initialization with invalid values
   */
  @Test(expected = IllegalArgumentException.class)
  public void testTargetCharacterNullName() {
    new TargetModel(null, 50, places.get(0), places);
  }

  /**
   * Test target character initialization with invalid values
   */
  @Test(expected = IllegalArgumentException.class)
  public void testTargetCharacterEmptyName() {
    new TargetModel("", 50, places.get(0), places);
  }

  /**
   * Test target character initialization with invalid values
   */
  @Test(expected = IllegalArgumentException.class)
  public void testTargetCharacterNullStartPlace() {
    new TargetModel("The Mayor", 50, null, places);
  }

  /**
   * Test target character initialization with invalid values
   */
  @Test(expected = IllegalArgumentException.class)
  public void testTargetCharacterNullPlacesList() {
    new TargetModel("The Mayor", 50, places.get(0), null);
  }

  /**
   * Test target character info display with extreme health values
   *
   * @throws Exception if the target character info display with extreme health values fails
   */
  @Test
  public void testTargetCharacterInfoDisplayWithExtremeHealth() throws Exception {
    // Test with very high health
    Target highHealthTarget = new TargetModel("High Health Mayor",
        Integer.MAX_VALUE, places.get(0), places);
    assertEquals(Integer.MAX_VALUE, highHealthTarget.getHealth());

    // Test with minimum valid health
    Target lowHealthTarget = new TargetModel("Low Health Mayor",
        0, places.get(0), places);
    assertEquals(0, lowHealthTarget.getHealth());
    assertTrue(lowHealthTarget.isDefeated());
  }

  /**
   * Test consecutive damage calculations
   */
  @Test
  public void testConsecutiveDamageCalculations() {
    targetCharacter.takeDamage(10);
    assertEquals(40, targetCharacter.getHealth());

    targetCharacter.takeDamage(15);
    assertEquals(25, targetCharacter.getHealth());

    targetCharacter.takeDamage(25);
    assertEquals(0, targetCharacter.getHealth());
    assertTrue(targetCharacter.isDefeated());

    // Additional damage should not reduce health below 0
    targetCharacter.takeDamage(10);
    assertEquals(0, targetCharacter.getHealth());
  }

  // Test target movement during player turns
  @Test
  public void testTargetMovementDuringPlayerTurns() throws Exception {
    // Add multiple players
    town.addComputerPlayer(); // First player
    town.addComputerPlayer(); // Second player

    Place initialTargetPlace = town.getTarget().getCurrentPlace();

    // Simulate multiple player turns
    town.switchToNextPlayer(); // Player 1's turn ends
    town.switchToNextPlayer(); // Player 2's turn ends, should trigger target movement

    assertFalse("Target should move after all players complete their turns",
        initialTargetPlace.equals(town.getTarget().getCurrentPlace()));
  }

  // Test target visibility in different game states
  @Test
  public void testTargetVisibilityInformation() throws Exception {
    // Add a player to the game
    town.addComputerPlayer();
    Player player = town.getPlayers().get(0);

    // Get target's current place and move player there
    Place targetPlace = town.getTarget().getCurrentPlace();
    player.moveToPlaceNumber(Integer.parseInt(targetPlace.getPlaceNumber()));

    // Clear any previous output
    output.getBuffer().setLength(0);

    // Look around from target's location
    town.showTargetInfo();
    String targetInfo = output.toString();

    // Verify basic target information
    assertTrue("Should show target name",
        targetInfo.contains(town.getTargetName()));
    assertTrue("Should show target health",
        targetInfo.contains(String.valueOf(town.getTargetHealth())));
    assertTrue("Should show target location",
        targetInfo.contains(targetPlace.getName()));
  }

  // Test target position information after max turns
  @Test
  public void testTargetPositionAfterMaxTurns() throws Exception {
    // Add two players to properly test turn cycling
    town.addComputerPlayer(); // First player
    town.addComputerPlayer(); // Second player

    Place initialPlace = town.getTarget().getCurrentPlace();
    int initialTurn = town.getCurrentTurn();

    // Track the current turn and continue until max turns is reached
    while (town.getCurrentTurn() <= town.getMaxTurns()) {
      // Simulate an action for the current player
      town.lookAround(); // This method includes switchToNextPlayer()

      // Verify turn progression
      if (town.getCurrentTurn() > initialTurn) {
        assertFalse("Target should move after complete round",
            initialPlace.equals(town.getTarget().getCurrentPlace()));
      }
    }

    assertTrue("Game should be over after max turns",
        town.getCurrentTurn() > town.getMaxTurns());
    assertTrue("Game should be in over state", town.isGameOver());
  }

  // Test target info display during combat
  @Test
  public void testTargetInfoDuringCombat() throws Exception {
    // Add a player in the same room as target
    Player player = new PlayerModel("TestPlayer", false, 5, 1, output,
        new Scanner(new StringReader("")));

    // Record target's initial health
    int initialHealth = town.getTarget().getHealth();

    // Execute a poke attack (1 damage)
    town.executePoke(player);

    // Verify health reduction
    assertEquals("Target health should decrease by 1 after poke",
        initialHealth - 1, town.getTarget().getHealth());

    // Verify the target info display is updated
    town.showTargetInfo();
    String outputText = output.toString();
    assertTrue("Target info should show updated health",
        outputText.contains(String.valueOf(town.getTarget().getHealth())));
  }

  // Test target movement pattern
  @Test
  public void testTargetMovementPattern() throws Exception {
    List<String> movementPattern = new ArrayList<>();
    Place startPlace = town.getTarget().getCurrentPlace();
    movementPattern.add(startPlace.getName());

    // Track target movement for a full cycle
    for (int i = 0; i < town.getPlaces().size(); i++) {
      town.moveTarget();
      Place currentPlace = town.getTarget().getCurrentPlace();
      movementPattern.add(currentPlace.getName());
    }

    // Verify target has visited all places in order
    assertEquals("Target should return to start after full cycle",
        startPlace.getName(), movementPattern.get(movementPattern.size() - 1));
    assertEquals("Movement pattern should include all places plus start",
        town.getPlaces().size() + 1, movementPattern.size());
  }

  // Test target position display during game state changes
  @Test
  public void testTargetPositionDisplayDuringGameStateChanges() throws Exception {
    town.addComputerPlayer();

    // Test display at game start
    town.showTargetInfo();
    String initialDisplay = output.toString();
    assertTrue("Initial display should show target location",
        initialDisplay.contains(town.getTarget().getCurrentPlace().getName()));

    // Record the current place before moving
    String initialPlace = town.getTarget().getCurrentPlace().getName();

    // Move target
    town.moveTarget();

    // Clear output buffer but maintain the connection
    output.getBuffer().setLength(0);

    // Display new info
    town.showTargetInfo();
    String afterMoveDisplay = output.toString();

    // Verify that the new location is different and shown in display
    String newPlace = town.getTarget().getCurrentPlace().getName();
    assertFalse("Target should have moved to a new location",
        initialPlace.equals(newPlace));
    assertTrue("Display after move should show new location",
        afterMoveDisplay.contains(newPlace));
  }

  // Test target health display edge cases
  @Test
  public void testTargetHealthDisplayEdgeCases() throws Exception {
    // Test near-death state
    Target target = town.getTarget();
    while (target.getHealth() > 1) {
      target.takeDamage(1);
    }
    town.showTargetInfo();
    String lowHealthDisplay = output.toString();
    assertTrue("Should show critical health status",
        lowHealthDisplay.contains("Health: 1"));

    // Test defeated state
    target.takeDamage(1);
    town.showTargetInfo();
    String zeroHealthDisplay = output.toString();
    assertTrue("Should show zero health status",
        zeroHealthDisplay.contains("Health: 0"));
  }

  /**
   * Test concurrent player attack attempts on target
   */
  @Test
  public void testConcurrentPlayerAttackAttempts() throws Exception {
    // Prepare input for adding players
    String input = "Player1\n1\n3\nPlayer2\n1\n3\n"; // name, place number, carry limit
    StringReader inputReader = new StringReader(input);
    town = new TownModel(
        new TownLoader(),
        "res/SmallTownWorld.txt",
        inputReader,
        output,
        10
    );

    // Add players
    town.addPlayer(); // Will use "Player1" inputs
    town.addPlayer(); // Will use "Player2" inputs

    // Get the added players
    Player player1 = town.getPlayers().get(0);
    Player player2 = town.getPlayers().get(1);

    // Get target's location
    Place targetPlace = town.getTarget().getCurrentPlace();
    int targetPlaceNumber = Integer.parseInt(targetPlace.getPlaceNumber());

    // Move both players to target's location
    player1.moveToPlaceNumber(targetPlaceNumber);
    player2.moveToPlaceNumber(targetPlaceNumber);

    // Update player locations in places
    targetPlace.addPlayer(player1);
    targetPlace.addPlayer(player2);

    // Record initial health
    int initialHealth = town.getTarget().getHealth();

    // First player attacks while second player is present
    assertTrue("Player1 should be visible with Player2 present",
        town.isPlayerVisible(player1));
    town.executePoke(player1);

    // Verify health unchanged due to witness
    assertEquals("Health should not change when attack is witnessed",
        initialHealth, town.getTarget().getHealth());

    // Move player2 to a far away place that's not neighboring
    // Find a place that's not neighboring targetPlace
    Place farPlace = null;
    for (Place place : town.getPlaces()) {
      if (!targetPlace.isNeighbor(place) && !place.equals(targetPlace)) {
        farPlace = place;
        break;
      }
    }

    assertNotNull("Should find a non-neighboring place", farPlace);

    // Move player2 to the far place
    player2.moveToPlaceNumber(Integer.parseInt(farPlace.getPlaceNumber()));
    targetPlace.removePlayer(player2);
    farPlace.addPlayer(player2);

    // Player1 attacks again when not visible
    assertFalse("Player1 should not be visible now",
        town.isPlayerVisible(player1));
    town.executePoke(player1);

    // Verify damage was done
    assertEquals("Health should decrease after unseen attack",
        initialHealth - 1, town.getTarget().getHealth());

    // Additional verifications
    assertTrue("Player1 should be in target's place",
        targetPlace.getCurrentPlacePlayers().contains(player1));
    assertTrue("Player2 should be in different place",
        farPlace.getCurrentPlacePlayers().contains(player2));
    assertFalse("Players should be in different places",
        player1.getPlayerCurrentPlaceNumber() == player2.getPlayerCurrentPlaceNumber());
    assertFalse("Player2's new place should not be neighbor of target's place",
        targetPlace.isNeighbor(farPlace));
  }

  /**
   * Test target movement behavior when spaces are occupied
   */
  @Test
  public void testTargetMovementWithOccupiedSpaces() throws Exception {
    // Add multiple players in different spaces
    town.addComputerPlayer(); // Player 1
    town.addComputerPlayer(); // Player 2

    // Get initial position
    Place initialPlace = town.getTarget().getCurrentPlace();
    List<String> visitedPlaces = new ArrayList<>();
    visitedPlaces.add(initialPlace.getName());

    // Track movement through multiple turns
    for (int i = 0; i < town.getPlaces().size() * 2; i++) {
      town.moveTarget();
      Place newPlace = town.getTarget().getCurrentPlace();
      visitedPlaces.add(newPlace.getName());

      // Verify target moves regardless of player presence
      assertNotNull("Target should always move to a valid place", newPlace);
      assertTrue("Target should move to a place in the world",
          town.getPlaces().contains(newPlace));
    }

    // Verify movement pattern includes all spaces
    assertEquals("Target should visit all spaces in a cycle",
        town.getPlaces().size(),
        visitedPlaces.subList(0, town.getPlaces().size() + 1).stream()
            .distinct()
            .count());
  }

  /**
   * Test target behavior at world boundaries
   */
  @Test
  public void testTargetBehaviorAtWorldBoundaries() {
    Place currentPlace = town.getTarget().getCurrentPlace();
    List<Place> neighbors = town.getCurrentPlaceNeighbors(currentPlace);

    // Find places at world boundaries (those with fewer neighbors)
    List<Place> boundaryPlaces = places.stream()
        .filter(p -> town.getCurrentPlaceNeighbors(p).size() <
            Math.max(town.getCurrentPlaceNeighbors(places.get(0)).size(),
                town.getCurrentPlaceNeighbors(places.get(places.size() / 2)).size()))
        .collect(java.util.stream.Collectors.toList());

    // Test movement from boundary places
    for (Place boundaryPlace : boundaryPlaces) {
      targetCharacter.moveToNextPlace();
      assertFalse("Target should never be stuck at boundaries",
          targetCharacter.getCurrentPlace().equals(boundaryPlace));
      assertTrue("Target should move to a valid place after boundary",
          places.contains(targetCharacter.getCurrentPlace()));
    }
  }

  /**
   * Test target state persistence during game events
   */
  @Test
  public void testTargetStatePersistence() throws Exception {
    // Initial state
    int initialHealth = town.getTarget().getHealth();
    Place initialPlace = town.getTarget().getCurrentPlace();

    // Add players and simulate game events
    town.addComputerPlayer();
    town.addComputerPlayer();

    // Simulate multiple turns and events
    for (int i = 0; i < 5; i++) {
      // Take damage
      town.getTarget().takeDamage(5);

      // Move target
      town.moveTarget();

      // Get current state
      Target target = town.getTarget();
      Place currentPlace = target.getCurrentPlace();

      // Verify state consistency
      assertTrue("Health should not exceed initial health",
          target.getHealth() <= initialHealth);
      assertNotNull("Target should always have a valid position",
          currentPlace);
      assertTrue("Target should be in a valid place",
          town.getPlaces().contains(currentPlace));

      // Verify target moves according to rules
      int expectedPlaceIndex = (i + 1) % town.getPlaces().size();
      assertEquals("Target should move to next place in sequence",
          town.getPlaces().get(expectedPlaceIndex),
          currentPlace);
    }

    // Verify final state
    assertEquals("Health should reflect total damage taken",
        initialHealth - 25, town.getTarget().getHealth());
    assertFalse("Position should have changed",
        initialPlace.equals(town.getTarget().getCurrentPlace()));

    // Additional state verifications
    assertFalse("Target should not be defeated from partial damage",
        town.getTarget().isDefeated());
    assertTrue("Target's current place should be valid",
        town.getPlaces().contains(town.getTarget().getCurrentPlace()));
    assertTrue("Target's health should be positive",
        town.getTarget().getHealth() > 0);

    // Verify target can still move after damage
    Place beforeFinalMove = town.getTarget().getCurrentPlace();
    town.moveTarget();
    assertNotEquals("Target should still be able to move after taking damage",
        beforeFinalMove, town.getTarget().getCurrentPlace());
  }

  /**
   * Test target interaction with pet's visibility effect
   */
  @Test
  public void testTargetInteractionWithPetVisibility() throws Exception {
    // Add a player
    town.addComputerPlayer();
    Player player = town.getPlayers().get(0);

    // Move pet to target's location
    Place targetPlace = town.getTarget().getCurrentPlace();
    town.movePet(Integer.parseInt(targetPlace.getPlaceNumber()));

    // Verify pet affects space visibility
    assertFalse("Target's space should not be visible when pet is present",
        town.isPlaceVisible(targetPlace));

    // Try to attack target in pet's presence
    player.moveToPlaceNumber(Integer.parseInt(targetPlace.getPlaceNumber()));
    int initialHealth = town.getTarget().getHealth();
    town.executePoke(player);

    // Health should still change since player is in same room
    assertEquals("Attack should succeed if player is in same room, regardless of pet",
        initialHealth - 1, town.getTarget().getHealth());
  }
}