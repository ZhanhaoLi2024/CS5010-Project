package model.town;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import model.item.Item;
import model.place.Place;
import model.player.Player;
import model.target.Target;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for TownModel. Focuses on basic object creation, initial target state,
 * and places/items loading verification.
 */
public class TownModelTest {
  private static final String TEST_FILE = "res/SmallTownWorld.txt";
  private static final int MAX_TURNS = 50;
  private TownModel townModel;
  private StringWriter output;

  @Before
  public void setUp() throws IOException {
    output = new StringWriter();
    TownLoader loader = new TownLoader();
    townModel = new TownModel(loader, TEST_FILE, output, MAX_TURNS);
  }

  // Test basic object creation
  @Test
  public void testValidTownModelCreation() throws IOException {
    assertNotNull("Town model should not be null", townModel);
    assertEquals("Max turns should be set correctly", MAX_TURNS, townModel.getMaxTurns());
    assertEquals("Initial turn should be 1", 1, townModel.getCurrentTurn());
    assertNotNull("Players list should be initialized", townModel.getPlayers());
    assertTrue("Players list should be empty initially", townModel.getPlayers().isEmpty());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidMaxTurnsZero() throws IOException {
    TownLoader loader = new TownLoader();
    new TownModel(loader, TEST_FILE, output, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidMaxTurns() throws IOException {
    TownLoader loader = new TownLoader();
    new TownModel(loader, TEST_FILE, output, -1);
  }

  @Test(expected = IOException.class)
  public void testInvalidWorldFile() throws IOException {
    TownLoader loader = new TownLoader();
    new TownModel(loader, "nonexistent.txt", output, MAX_TURNS);
  }

  // Test initial target state
  @Test
  public void testInitialTargetState() {
    Target target = townModel.getTarget();
    assertNotNull("Target should not be null", target);
    assertEquals("Target name should be 'The Mayor'", "The Mayor", target.getName());
    assertEquals("Target health should be 50", 50, target.getHealth());
    assertEquals("Target should start in first place",
        townModel.getPlaces().get(0), target.getCurrentPlace());
    assertFalse("Target should not be defeated initially", target.isDefeated());
  }

  @Test
  public void testTargetMovement() {
    Place initialPlace = townModel.getTarget().getCurrentPlace();
    townModel.moveTarget();
    Place newPlace = townModel.getTarget().getCurrentPlace();
    assertNotEquals("Target should move to a different place", initialPlace, newPlace);
  }

  // Test places and items loading
  @Test
  public void testPlacesLoading() {
    List<Place> places = townModel.getPlaces();
    assertNotNull("Places list should not be null", places);
    assertEquals("Should have 20 places", 20, places.size());

    // Verify first place properties
    Place firstPlace = places.get(0);
    assertEquals("First place should be 'Park'", "Park", firstPlace.getName());
    assertEquals("First place number should be '1'", "1", firstPlace.getPlaceNumber());
    assertEquals("First place should have correct row1", 0, firstPlace.getRow1());
    assertEquals("First place should have correct col1", 0, firstPlace.getCol1());
    assertEquals("First place should have correct row2", 2, firstPlace.getRow2());
    assertEquals("First place should have correct col2", 3, firstPlace.getCol2());
  }

  @Test
  public void testItemsInPlaces() {
    Place firstPlace = townModel.getPlaces().get(0);
    List<Item> items = firstPlace.getItems();
    assertNotNull("Items list should not be null", items);

    // Based on SmallTownWorld.txt, first place (Park) should have a Toy Ball with damage 8
    boolean foundToyBall = false;
    for (Item item : items) {
      if (item.getName().equals("Toy Ball")) {
        foundToyBall = true;
        assertEquals("Toy Ball should have damage 8", 8, item.getDamage());
        break;
      }
    }
    assertTrue("Should find Toy Ball in first place", foundToyBall);
  }

  @Test
  public void testNeighboringPlaces() {
    Place park = townModel.getPlaces().get(0); // Park
    Place groceryStore = townModel.getPlaces().get(1); // Grocery Store

    assertTrue("Park should be neighbor of Grocery Store", park.isNeighbor(groceryStore));
    assertTrue("Grocery Store should be neighbor of Park", groceryStore.isNeighbor(park));

    List<Place> parkNeighbors = park.getNeighbors();
    assertNotNull("Neighbors list should not be null", parkNeighbors);
    assertTrue("Park should have Grocery Store as neighbor",
        parkNeighbors.contains(groceryStore));
  }

  @Test
  public void testPetInitialization() {
    String petInfo = townModel.petCurrentInfo();
    assertNotNull("Pet info should not be null", petInfo);
    assertTrue("Pet info should contain 'Fortune the Cat'",
        petInfo.contains("Fortune the Cat"));

    // Pet should start in same place as target
    String[] petInfoParts = petInfo.split(",");
    assertEquals("Pet should start in first place (Park)",
        townModel.getPlaces().get(0).getName(), petInfoParts[1]);
  }

  @Test
  public void testGetPlaceByNumber() {
    Place place = townModel.getPlaceByNumber(1);
    assertNotNull("Should get place by number", place);
    assertEquals("First place should be Park", "Park", place.getName());

    place = townModel.getPlaceByNumber(20);
    assertNotNull("Should get last place", place);
    assertEquals("Last place should be Community Center",
        "Community Center", place.getName());
  }

  // Player Management Tests
  @Test
  public void testAddHumanPlayer() throws IOException {
    // Test adding a valid human player
    townModel.addPlayer("TestPlayer", 1, 3, false);
    assertEquals("Should have 1 player", 1, townModel.getPlayers().size());
    assertEquals("Player name should match", "TestPlayer",
        townModel.getPlayers().get(0).getName());
    assertFalse("Should be human player",
        townModel.getPlayers().get(0).isComputerControlled());
    assertEquals("Should have correct carry limit", 3,
        townModel.getPlayers().get(0).getCarryLimit());
    assertEquals("Should be in correct starting place", 1,
        townModel.getPlayers().get(0).getPlayerCurrentPlaceNumber());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddPlayerWithInvalidName() throws IOException {
    townModel.addPlayer("", 1, 3, false);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddPlayerWithNegativeCarryLimit() throws IOException {
    townModel.addPlayer("TestPlayer", 1, -1, false);
  }

  @Test
  public void testAddComputerPlayer() throws IOException {
    townModel.addPlayer("Computer1", 1, 5, true);
    assertEquals("Should have 1 player", 1, townModel.getPlayers().size());
    assertTrue("Should be computer player",
        townModel.getPlayers().get(0).isComputerControlled());
    assertEquals("Should have correct carry limit", 5,
        townModel.getPlayers().get(0).getCarryLimit());
  }

  @Test
  public void testMultiplePlayersInSameLocation() throws IOException {
    townModel.addPlayer("Player1", 1, 3, false);
    townModel.addPlayer("Player2", 1, 3, false);

    Place place = townModel.getPlaceByNumber(1);
    assertEquals("Place should have 2 players", 2,
        place.getCurrentPlacePlayers().size());
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void testAddPlayerWithInvalidStartingPlace() throws IOException {
    townModel.addPlayer("TestPlayer", 21, 3, false); // There are only 20 places
  }

  @Test
  public void testPlayerCarryLimitRestriction() throws IOException {
    // Add player with carry limit 2
    townModel.addPlayer("TestPlayer", 1, 2, false);
    Player player = townModel.getPlayers().get(0);

    // Get items from first place
    Place firstPlace = townModel.getPlaces().get(0);
    List<Item> items = firstPlace.getItems();

    // Add items up to limit
    player.pickUpItem(items.get(0));
    player.pickUpItem(items.get(0));

    // Try to add one more item - should throw exception
    boolean exceptionThrown = false;
    try {
      player.pickUpItem(items.get(0));
    } catch (IllegalStateException e) {
      exceptionThrown = true;
    }
    assertTrue("Should throw exception when exceeding carry limit", exceptionThrown);
    assertEquals("Should only have 2 items", 2,
        player.getCurrentCarriedItems().size());
  }

  @Test
  public void testPlayersStartingPositionInPlace() throws IOException {
    townModel.addPlayer("TestPlayer", 1, 3, false);
    Player player = townModel.getPlayers().get(0);
    Place startingPlace = townModel.getPlaceByNumber(1);

    assertTrue("Starting place should contain player",
        startingPlace.getCurrentPlacePlayers().contains(player));
    assertEquals("Player should be in correct starting place",
        1, player.getPlayerCurrentPlaceNumber());
  }

  @Test
  public void testComputerPlayerDefaultBehavior() throws IOException {
    townModel.addPlayer("Computer1", 1, 5, true);
    Player computerPlayer = townModel.getPlayers().get(0);

    assertTrue("Should be computer controlled", computerPlayer.isComputerControlled());
    assertEquals("Should have default carry limit of 5", 5,
        computerPlayer.getCarryLimit());
    assertTrue("Should start with empty inventory",
        computerPlayer.getCurrentCarriedItems().isEmpty());
  }

  // Turn System Tests
  @Test
  public void testSwitchToNextPlayer() throws IOException {
    // Add multiple players
    townModel.addPlayer("Player1", 1, 3, false);
    townModel.addPlayer("Player2", 2, 3, false);
    townModel.addPlayer("Player3", 3, 3, false);

    assertEquals("Should start with player 0", 0, townModel.getCurrentPlayerIndex());
    assertEquals("Should start at turn 1", 1, townModel.getCurrentTurn());

    // Test switching to next player
    townModel.switchToNextPlayer();
    assertEquals("Should switch to player 1", 1, townModel.getCurrentPlayerIndex());
    assertEquals("Should still be turn 1", 1, townModel.getCurrentTurn());

    // Test switching again
    townModel.switchToNextPlayer();
    assertEquals("Should switch to player 2", 2, townModel.getCurrentPlayerIndex());

    // Test completing a round (back to first player)
    townModel.switchToNextPlayer();
    assertEquals("Should return to player 0", 0, townModel.getCurrentPlayerIndex());
    assertEquals("Should increment turn counter", 2, townModel.getCurrentTurn());
  }

  @Test
  public void testTargetMovementOnRoundCompletion() throws IOException {
    townModel.addPlayer("Player1", 1, 3, false);
    townModel.addPlayer("Player2", 2, 3, false);

    Place initialTargetPlace = townModel.getTarget().getCurrentPlace();

    // Complete one round
    townModel.switchToNextPlayer(); // to player 2
    townModel.switchToNextPlayer(); // back to player 1, should move target

    Place newTargetPlace = townModel.getTarget().getCurrentPlace();
    assertNotEquals("Target should move after round completion",
        initialTargetPlace, newTargetPlace);
  }

  @Test
  public void testMaxTurnsLimit() throws IOException {
    TownLoader loader = new TownLoader();
    TownModel limitedTurnModel = new TownModel(loader, TEST_FILE, output, 2);

    limitedTurnModel.addPlayer("Player1", 1, 3, false);
    limitedTurnModel.addPlayer("Player2", 2, 3, false);

    assertFalse("Game should not be over at start", limitedTurnModel.isGameOver());

    // Complete first round
    limitedTurnModel.switchToNextPlayer();
    limitedTurnModel.switchToNextPlayer();
    assertFalse("Game should not be over after first round", limitedTurnModel.isGameOver());

    // Complete second round
    limitedTurnModel.switchToNextPlayer();
    limitedTurnModel.switchToNextPlayer();
    assertTrue("Game should be over after max turns", limitedTurnModel.isGameOver());
  }

  @Test
  public void testGameEndConditions() throws IOException {
    townModel.addPlayer("Player1", 1, 3, false);
    townModel.addPlayer("Player2", 2, 3, false);

    // Test game not over at start
    assertFalse("Game should not be over at start", townModel.isGameOver());

    // Test game over when target defeated
    Target target = townModel.getTarget();
    while (!target.isDefeated()) {
      target.takeDamage(10);
    }
    assertTrue("Game should be over when target defeated", townModel.isGameOver());
  }

  @Test
  public void testSinglePlayerTurnManagement() throws IOException {
    townModel.addPlayer("Player1", 1, 3, false);

    // Test that single player game maintains correct turn count
    assertEquals("Should start at turn 1", 1, townModel.getCurrentTurn());
    townModel.switchToNextPlayer();
    assertEquals("Should remain at turn 1 with single player", 1, townModel.getCurrentTurn());
  }

  @Test
  public void testInvalidTurnSwitch() throws IOException {
    // Test switching turns with no players
    townModel.switchToNextPlayer();
    assertEquals("Should maintain turn 1 with no players", 1, townModel.getCurrentTurn());
    assertEquals("Should maintain player index 0 with no players", 0,
        townModel.getCurrentPlayerIndex());
  }

  @Test
  public void testTargetMovementPattern() throws IOException {
    Place firstPlace = townModel.getTarget().getCurrentPlace();
    townModel.moveTarget();
    Place secondPlace = townModel.getTarget().getCurrentPlace();
    townModel.moveTarget();
    Place thirdPlace = townModel.getTarget().getCurrentPlace();

    assertNotEquals("Target should move to different place", firstPlace, secondPlace);
    assertNotEquals("Target should move to different place again", secondPlace, thirdPlace);
  }

  // Combat System Tests
  @Test
  public void testSuccessfulTargetAttack() throws IOException {
    // Setup players and initial conditions
    townModel.addPlayer("Player1", 1, 5, false);
    townModel.addPlayer("Player2", 2, 5, false); // In different place

    // Get initial target health
    int initialHealth = townModel.getTarget().getHealth();

    // Execute attack with Toy Ball (8 damage)
    boolean attackResult = townModel.attackTarget("Toy Ball");

    // Verify attack results
    assertFalse("Attack should not kill target", attackResult);
    assertEquals("Target health should be reduced by 8",
        initialHealth - 8, townModel.getTarget().getHealth());
  }

  @Test
  public void testPokeAttack() throws IOException {
    // Setup player in same place as target
    townModel.addPlayer("Player1", 1, 5, false);
    int initialHealth = townModel.getTarget().getHealth();

    // Execute poke attack
    boolean attackResult = townModel.attackTarget("Poke Target");

    // Verify poke attack results
    assertFalse("Poke attack should not kill target", attackResult);
    assertEquals("Target health should be reduced by 1",
        initialHealth - 1, townModel.getTarget().getHealth());
  }

  @Test
  public void testKillingTargetAttack() throws IOException {
    // Setup player with powerful weapon
    townModel.addPlayer("Player1", 1, 5, false);
    Target target = townModel.getTarget();

    // Reduce target health to make next attack fatal
    while (target.getHealth() > 20) {
      target.takeDamage(10);
    }

    // Execute killing attack with Community Badge (20 damage)
    boolean attackResult = townModel.attackTarget("Community Badge");

    // Verify kill
    assertTrue("Attack should kill target", attackResult);
    assertTrue("Target should be defeated", target.isDefeated());
    assertEquals("Target health should be 0", 0, target.getHealth());
  }

  @Test
  public void testItemRemovalAfterAttack() throws IOException {
    // Setup player with item
    townModel.addPlayer("Player1", 1, 5, false);
    Player player = townModel.getPlayers().get(0);

    // Get Toy Ball and use it for attack
    townModel.pickUpItem("Toy Ball");
    townModel.attackTarget("Toy Ball");

    // Verify item was consumed
    List<Item> playerItems = player.getCurrentCarriedItems();
    boolean hasToyBall = playerItems.stream()
        .anyMatch(item -> item.getName().equals("Toy Ball"));
    assertFalse("Item should be removed after attack", hasToyBall);
  }

  @Test
  public void testPetAffectsVisibility() throws IOException {
    // Setup players
    townModel.addPlayer("Player1", 1, 5, false); // Player1 in Park
    townModel.addPlayer("Player2", 2, 5, false); // Player2 in Grocery Store

    Player player1 = townModel.getPlayers().get(0);

    // First move pet away and verify player is visible
    townModel.movePet(20); // Move pet to a distant place
    assertTrue("Player should be visible when pet is away",
        townModel.isPlayerVisible(player1));

    // Move pet to player's location
    townModel.movePet(1); // Move pet to Park
    assertFalse("Player should not be visible when pet is in same place",
        townModel.isPlayerVisible(player1));
  }

  @Test
  public void testVisiblePlayerCannotAttack() throws IOException {
    // Setup players
    townModel.addPlayer("Player1", 1, 5, false); // Player1 in Park
    townModel.addPlayer("Player2", 2, 5, false); // Player2 in Grocery Store

    // Get places and players
    Place place1 = townModel.getPlaceByNumber(1);
    Place place2 = townModel.getPlaceByNumber(2);
    Player player1 = townModel.getPlayers().get(0);
    Player player2 = townModel.getPlayers().get(1);

    // Move pet away from Player1's location
    townModel.movePet(20);

    // Move target to same place as Player1
    while (!townModel.getTarget().getCurrentPlace().equals(place1)) {
      townModel.moveTarget();
    }

    // Make sure we're the current player before picking up item
    // In case we're not the current player, we need to make moves until it's our turn
    while (townModel.getCurrentPlayerIndex() != 0) {
      townModel.switchToNextPlayer();
    }

    // Try to pick up the item and verify
    townModel.pickUpItem("Toy Ball");

    assertFalse("Player1's inventory should not be empty",
        player1.getCurrentCarriedItems().isEmpty());

    // Verify place setup
    assertTrue("Places should be neighbors", place1.isNeighbor(place2));
    assertTrue("Place1 should contain player1",
        place1.getCurrentPlacePlayers().contains(player1));
    assertTrue("Place2 should contain player2",
        place2.getCurrentPlacePlayers().contains(player2));

    // Check visibility
    boolean isVisible = townModel.isPlayerVisible(player1);

    assertTrue("Player should be visible to neighbors", isVisible);

    // Attempt attack
    townModel.attackTarget("Toy Ball");

    // Verify attack had no effect
    assertEquals("Target health should not change when attacked by visible player",
        42, townModel.getTarget().getHealth());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAttackWithNonexistentItem() throws IOException {
    townModel.addPlayer("Player1", 1, 5, false);
    townModel.attackTarget("NonexistentItem");
  }

  @Test
  public void testPetBlocksVisibility() throws IOException {
    // Setup players and pet
    townModel.addPlayer("Player1", 1, 5, false);
    townModel.addPlayer("Player2", 2, 5, false);
    townModel.movePet(1); // Move pet to Player1's location

    Player player1 = townModel.getPlayers().get(0);

    // Verify player is not visible due to pet
    assertFalse("Player should not be visible when pet is present",
        townModel.isPlayerVisible(player1));

    // Verify attack is possible
    int initialHealth = townModel.getTarget().getHealth();
    townModel.attackTarget("Toy Ball");
    assertNotEquals("Attack should succeed when player is not visible",
        initialHealth, townModel.getTarget().getHealth());
  }

  // Pet System Tests
  @Test
  public void testPetMovement() throws IOException {
    // Test valid pet movement
    int newPlace = 2; // Moving to Grocery Store
    townModel.movePet(newPlace);

    String petInfo = townModel.petCurrentInfo();
    String[] petInfoParts = petInfo.split(",");
    assertEquals("Pet should be in Grocery Store",
        townModel.getPlaceByNumber(newPlace).getName(),
        petInfoParts[1]);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidPetMovementNegativePlace() throws IOException {
    townModel.movePet(-1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidPetMovementTooLargePlace() throws IOException {
    townModel.movePet(21); // Only 20 places exist
  }

  @Test
  public void testPetVisibilityEffect() throws IOException {
    // Add two players in neighboring places
    townModel.addPlayer("Player1", 1, 3, false); // In Park
    townModel.addPlayer("Player2", 2, 3, false); // In Grocery Store

    // Initially players can see each other since pet is in Place 1 (Park) with target
    // but Player1 is in Park so they're not visible
    assertFalse("Players should not be visible due to pet in Place 1",
        townModel.isPlayerVisible(townModel.getPlayers().get(0)));

    // Move pet to a different location (e.g., Place 3 - School)
    townModel.movePet(3);

    // Now players should be visible to each other since pet is elsewhere
    assertTrue("Players should be visible when pet is elsewhere",
        townModel.isPlayerVisible(townModel.getPlayers().get(0)));

    // Move pet to Player1's location (Park)
    townModel.movePet(1);

    // Players shouldn't be visible when pet is in Player1's location
    assertFalse("Players shouldn't be visible when pet is in same place as player",
        townModel.isPlayerVisible(townModel.getPlayers().get(0)));
  }

  @Test
  public void testPetLocationUpdate() throws IOException {
    // Test initial pet location
    String initialPetInfo = townModel.petCurrentInfo();
    String[] initialInfoParts = initialPetInfo.split(",");
    assertEquals("Pet should start in first place",
        townModel.getPlaces().get(0).getName(),
        initialInfoParts[1]);

    // Move pet and verify location update
    townModel.movePet(3); // Move to School

    String updatedPetInfo = townModel.petCurrentInfo();
    String[] updatedInfoParts = updatedPetInfo.split(",");
    assertEquals("Pet name should remain same",
        initialInfoParts[0],
        updatedInfoParts[0]);
    assertEquals("Pet should be in School",
        townModel.getPlaceByNumber(3).getName(),
        updatedInfoParts[1]);
  }

  @Test
  public void testPetVisibilityWithMultiplePlayers() throws IOException {
    // Setup players with non-adjacent locations
    townModel.addPlayer("Player1", 1, 3, false);  // Park
    townModel.addPlayer("Player2", 2, 3, false);  // Grocery Store
    townModel.addPlayer("Player3", 20, 3,
        false); // Community Center (definitely not adjacent to Park)

    // Initially pet is in Place 1 (Park), so Player1 is not visible
    assertFalse("Player1 should not be visible initially due to pet in Park",
        townModel.isPlayerVisible(townModel.getPlayers().get(0)));

    // Move pet to a different location (e.g., Place 15 - Gym)
    townModel.movePet(15);

    // Now Player1 should be visible to Player2 (they're neighbors)
    assertTrue("Player1 should be visible to Player2 when pet is elsewhere",
        townModel.isPlayerVisible(townModel.getPlayers().get(0)));

    // Player1 and Player3 are in non-adjacent places (Park and Community Center)
    Player player3 = townModel.getPlayers().get(2);
    Place player3Place = townModel.getPlaceByNumber(player3.getPlayerCurrentPlaceNumber());
    Place player1Place =
        townModel.getPlaceByNumber(townModel.getPlayers().get(0).getPlayerCurrentPlaceNumber());

    // Verify places are not adjacent (Community Center is far from Park)
    assertFalse("Park and Community Center should not be adjacent",
        player1Place.isNeighbor(player3Place));

    // Now test visibility between non-adjacent players
    assertFalse("Player1 should not be visible to Player3 (in Community Center)",
        townModel.isPlayerVisible(player3));
  }

  @Test
  public void testPetInfoConsistency() throws IOException {
    // Get initial pet info
    String initialInfo = townModel.petCurrentInfo();
    assertNotNull("Pet info should not be null", initialInfo);
    assertTrue("Pet info should contain pet name",
        initialInfo.contains("Fortune the Cat"));

    // Move pet to multiple locations and verify info consistency
    int[] testLocations = {2, 3, 4, 5};
    for (int location : testLocations) {
      townModel.movePet(location);
      String currentInfo = townModel.petCurrentInfo();
      String[] infoParts = currentInfo.split(",");

      assertEquals("Pet name should remain consistent",
          "Fortune the Cat",
          infoParts[0]);
      assertEquals("Pet location should match moved location",
          townModel.getPlaceByNumber(location).getName(),
          infoParts[1]);
    }
  }
}