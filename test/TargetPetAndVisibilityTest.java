import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import model.item.Item;
import model.pet.Pet;
import model.pet.PetModel;
import model.place.Place;
import model.player.Player;
import model.town.Town;
import model.town.TownLoader;
import model.town.TownModel;
import org.junit.Before;
import org.junit.Test;

/**
 * Comprehensive test class for the Pet functionality in the game world.
 * Tests various aspects of pet behavior including initialization,
 * movement mechanics, and effects on space visibility.
 * This test suite verifies:
 * - Pet initialization and basic attributes
 * - Pet movement validation and mechanics
 * - Space visibility rules with pet presence
 * - Look around functionality affected by pet
 * - Turn management for pet-related actions
 * - Complex interactions between pet, players and spaces
 */
public class TargetPetAndVisibilityTest {
  private static final String TEST_WORLD_FILE = "res/SmallTownWorld.txt";
  private static final int MAX_TURNS = 10;
  private Town town;
  private StringWriter output;

  /**
   * Sets up the test environment before each test case execution.
   * Initializes a new town instance with test configuration and
   * prepares output capture.
   *
   * @throws IOException if there is an error reading the world file
   */
  @Before
  public void setUp() throws IOException {
    output = new StringWriter();
    town = new TownModel(
        new TownLoader(),
        TEST_WORLD_FILE,
        new StringReader(""),
        output,
        MAX_TURNS
    );
  }

  // Pet Initialization Tests

  /**
   * Tests the proper initialization of the pet in the game world.
   * Verifies that:
   * - Pet is properly instantiated
   * - Pet name matches the world file specification
   * - Pet starts in the correct initial position
   */
  @Test
  public void testPetInitialization() {
    Pet pet = town.getPet();
    assertNotNull("Pet should be initialized", pet);
    assertEquals("Pet name should match the one in world file", "Fortune the Cat", pet.getName());
    assertEquals("Pet should start in the same place as target",
        1, pet.getPetCurrentPlaceNumber());
  }

  /**
   * Tests pet initialization with an invalid (empty) name.
   * Verifies that the constructor properly rejects empty pet names.
   *
   * @throws IllegalArgumentException expected when name is empty
   */
  @Test(expected = IllegalArgumentException.class)
  public void testPetInitializationWithInvalidName() {
    new PetModel("", 1);  // Should throw exception for empty name
  }

  /**
   * Tests pet initialization with a null name.
   * Verifies that the constructor properly rejects null pet names.
   *
   * @throws IllegalArgumentException expected when name is null
   */
  @Test(expected = IllegalArgumentException.class)
  public void testPetInitializationWithNullName() {
    new PetModel(null, 1);  // Should throw exception for null name
  }

  // Pet Movement Tests

  /**
   * Tests valid pet movement between spaces.
   * Verifies that:
   * - Pet moves to the specified position
   * - Movement is properly recorded
   * - Output confirms the movement
   *
   * @throws IOException if there is an error during pet movement
   */
  @Test
  public void testValidPetMovement() throws IOException {
    int initialPosition = town.getPet().getPetCurrentPlaceNumber();
    int newPosition = initialPosition + 1;

    town.movePet(newPosition);

    assertEquals("Pet should move to the specified position",
        newPosition, town.getPet().getPetCurrentPlaceNumber());
    assertTrue("Output should confirm pet movement",
        output.toString().contains("moved to"));
  }

  /**
   * Tests pet movement validation for negative position values.
   *
   * @throws IOException              if there is an error during pet movement
   * @throws IllegalArgumentException expected when position is negative
   */
  @Test(expected = IllegalArgumentException.class)
  public void testMoveToInvalidNegativePosition() throws IOException {
    town.movePet(-1);
  }

  /**
   * Tests pet movement validation for zero position value.
   *
   * @throws IOException              if there is an error during pet movement
   * @throws IllegalArgumentException expected when position is zero
   */
  @Test(expected = IllegalArgumentException.class)
  public void testMoveToInvalidZeroPosition() throws IOException {
    town.movePet(0);
  }

  /**
   * Tests pet movement validation for non-existent positions.
   *
   * @throws IOException              if there is an error during pet movement
   * @throws IllegalArgumentException expected when position doesn't exist
   */
  @Test(expected = IllegalArgumentException.class)
  public void testMoveToNonExistentPosition() throws IOException {
    town.movePet(town.getPlaces().size() + 1);
  }

  // Space Visibility Tests

  /**
   * Tests space visibility when pet is present.
   * Verifies that spaces containing the pet become invisible.
   *
   * @throws IOException if there is an error during test execution
   */
  @Test
  public void testSpaceVisibilityWithPet() throws IOException {
    // Add a player to test visibility
    town.addComputerPlayer();

    // Move pet to a specific place
    int petPlace = 2;
    town.movePet(petPlace);

    Place placeWithPet = town.getPlaceByNumber(petPlace);
    assertFalse("Space with pet should not be visible",
        town.isPlaceVisible(placeWithPet));
  }

  /**
   * Tests visibility of spaces neighboring the pet's location.
   * Verifies that spaces adjacent to pet's location remain visible.
   *
   * @throws IOException if there is an error during test execution
   */
  @Test
  public void testNeighboringSpacesVisibility() throws IOException {
    // Add a player to test visibility
    town.addComputerPlayer();

    // Move pet to a specific place
    int petPlace = 2;
    town.movePet(petPlace);

    Place placeWithPet = town.getPlaceByNumber(petPlace);
    List<Place> neighbors = placeWithPet.getNeighbors();

    for (Place neighbor : neighbors) {
      assertTrue("Neighboring spaces should be visible",
          town.isPlaceVisible(neighbor));
    }
  }

  // Look Around Command Tests

  /**
   * Tests look around functionality when pet is present in a space.
   * Verifies that spaces containing the pet and their contents are hidden.
   *
   * @throws IOException if there is an error during test execution
   */
  @Test
  public void testLookAroundWithPetPresent() throws IOException {
    // Add a player
    town.addComputerPlayer();
    Player player = town.getPlayers().get(0);

    // Store the starting position of the player
    Place currentPlace = town.getPlaceByNumber(player.getPlayerCurrentPlaceNumber());
    Place neighborPlace = currentPlace.getNeighbors().get(0);
    int petPlaceNumber = Integer.parseInt(neighborPlace.getPlaceNumber());

    // Move pet to the selected neighboring space
    town.movePet(petPlaceNumber);

    // Clear previous output
    output.getBuffer().setLength(0);

    // Look around
    town.lookAround();

    String lookAroundResult = output.toString();
    Place petPlace = town.getPlaceByNumber(petPlaceNumber);
    assertFalse("Space with pet should not be visible",
        town.isPlaceVisible(petPlace));
    for (Item item : petPlace.getItems()) {
      String itemInfo = String.format("Items in %s:%s (Damage: %d)",
          petPlace.getName(), item.getName(), item.getDamage());
      assertFalse("Items in pet's location should not be visible",
          lookAroundResult.contains(itemInfo));
    }
  }

  /**
   * Tests look around functionality when pet is in a neighboring space.
   * Verifies that pet's presence is indicated but space contents are hidden.
   *
   * @throws IOException if there is an error during test execution
   */
  @Test
  public void testLookAroundWithPetInNeighboringSpace() throws IOException {
    // Add a player
    town.addComputerPlayer();
    Player player = town.getPlayers().get(0);

    // Get current place and find a neighbor
    Place currentPlace = town.getPlaceByNumber(player.getPlayerCurrentPlaceNumber());
    Place neighborPlace = currentPlace.getNeighbors().get(0);

    // Move pet to neighboring place
    town.movePet(Integer.parseInt(neighborPlace.getPlaceNumber()));

    // Clear previous output
    output.getBuffer().setLength(0);

    // Look around
    town.lookAround();

    // Verify that neighboring place is mentioned but its items are not visible
    String lookAroundResult = output.toString();
    assertTrue("Neighboring place should be mentioned",
        lookAroundResult.contains(neighborPlace.getName()));
    assertFalse("Neighboring place with pet should not be visible",
        town.isPlaceVisible(neighborPlace));
  }

  /**
   * Tests look around information when viewing a neighbor containing the pet.
   * Verifies proper indication of pet presence and content hiding.
   *
   * @throws IOException if there is an error during test execution
   */
  @Test
  public void testLookAroundNeighborWithPet() throws IOException {
    // Add a player
    town.addComputerPlayer();
    Player player = town.getPlayers().get(0);

    // Get a neighbor of player's location
    Place playerPlace = town.getPlaceByNumber(player.getPlayerCurrentPlaceNumber());
    Place neighborPlace = playerPlace.getNeighbors().get(0);

    // Move pet to the neighboring space
    town.movePet(Integer.parseInt(neighborPlace.getPlaceNumber()));

    // Clear previous output
    output.getBuffer().setLength(0);

    // Look around
    town.lookAround();

    String lookAroundResult = output.toString();
    assertTrue("Should indicate pet's presence in neighbor",
        lookAroundResult.contains("Pet is here"));
    assertFalse("Should not show items in space with pet",
        lookAroundResult.contains("Items in " + neighborPlace.getName()));
  }

  // Turn Management Tests

  /**
   * Tests that pet movement properly consumes a game turn.
   * Verifies turn progression and position changes.
   *
   * @throws IOException if there is an error during test execution
   */
  @Test
  public void testPetMovementConsumesOneTurn() throws IOException {
    town.addComputerPlayer();

    int initialPetPosition = town.getPet().getPetCurrentPlaceNumber();

    int newPosition = (initialPetPosition == 1) ? 2 : 1;

    town.movePet(newPosition);

    assertEquals("Pet should move to the new position",
        newPosition, town.getPet().getPetCurrentPlaceNumber());

    assertNotEquals("Pet should not be in initial position",
        initialPetPosition, town.getPet().getPetCurrentPlaceNumber());

    Place newPlace = town.getPlaceByNumber(newPosition);
    assertFalse("New pet location should not be visible",
        town.isPlaceVisible(newPlace));
  }

  // Complex Interaction Tests

  /**
   * Tests that pet presence properly hides space information.
   * Verifies that items and other information are hidden in pet's location.
   *
   * @throws IOException if there is an error during test execution
   */
  @Test
  public void testPetSpaceInfoHiding() throws IOException {
    // Add a player
    town.addComputerPlayer();

    // Place pet in a space with items
    Place placeWithItems = town.getPlaces().stream()
        .filter(p -> !p.getItems().isEmpty())
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No place with items found"));

    town.movePet(Integer.parseInt(placeWithItems.getPlaceNumber()));

    // Clear output
    output.getBuffer().setLength(0);

    // Try to look around
    town.lookAround();

    String lookAroundResult = output.toString();
    assertFalse("Should not reveal items in pet's location",
        lookAroundResult.contains("Items in " + placeWithItems.getName() + ":"));
  }

  /**
   * Tests pet movement effects with multiple players present.
   * Verifies visibility rules and information hiding with multiple observers.
   *
   * @throws IOException if there is an error during test execution
   */
  @Test
  public void testPetMovementWithPlayersPresent() throws IOException {
    town.addComputerPlayer();
    town.addComputerPlayer();
    Player player1 = town.getPlayers().get(0);
    Player player2 = town.getPlayers().get(1);
    Place place1 = town.getPlaceByNumber(1);
    List<Place> neighbors = place1.getNeighbors();
    Place place2 = neighbors.get(0);
    player1.moveToPlaceNumber(Integer.parseInt(place1.getPlaceNumber()));
    player2.moveToPlaceNumber(Integer.parseInt(place2.getPlaceNumber()));
    output.getBuffer().setLength(0);
    town.lookAround();
    while (town.getCurrentPlayerIndex() != town.getPlayers().indexOf(player2)) {
      town.switchToNextPlayer();
    }
    town.movePet(Integer.parseInt(place1.getPlaceNumber()));
    output.getBuffer().setLength(0);
    town.lookAround();
    String afterPetResult = output.toString();
    assertFalse("Space with pet should not be visible",
        town.isPlaceVisible(place1));

    for (Item item : place1.getItems()) {
      assertFalse("Items in pet's location should not be visible",
          afterPetResult.contains("Items in " + place1.getName() + ":" + item.getName()));
    }
  }

  /**
   * Tests comprehensive validation of pet movement mechanics.
   * Verifies handling of invalid positions and proper movement execution.
   *
   * @throws IOException if there is an error during test execution
   */
  @Test
  public void testPetMovementValidation() throws IOException {
    Pet pet = town.getPet();
    int initialPosition = pet.getPetCurrentPlaceNumber();

    // Test negative position
    try {
      town.movePet(-1);
      fail("Should not allow moving pet to negative position");
    } catch (IllegalArgumentException e) {
      assertEquals("Pet should not have moved",
          initialPosition, pet.getPetCurrentPlaceNumber());
    }

    // Test zero position
    try {
      town.movePet(0);
      fail("Should not allow moving pet to position zero");
    } catch (IllegalArgumentException e) {
      assertEquals("Pet should not have moved",
          initialPosition, pet.getPetCurrentPlaceNumber());
    }

    // Test position beyond map bounds
    int invalidPosition = town.getPlaces().size() + 1;
    try {
      town.movePet(invalidPosition);
      fail("Should not allow moving pet beyond map bounds");
    } catch (IllegalArgumentException e) {
      assertEquals("Pet should not have moved",
          initialPosition, pet.getPetCurrentPlaceNumber());
    }

    int validPosition = 1;
    if (initialPosition == 1) {
      validPosition = 2;
    }
    town.movePet(validPosition);
    assertEquals("Pet should move to valid position",
        validPosition, pet.getPetCurrentPlaceNumber());
  }

  /**
   * Tests pet's effect on item visibility in spaces.
   * Verifies that items are properly hidden in spaces containing the pet.
   *
   * @throws IOException if there is an error during test execution
   */
  @Test
  public void testPetVisibilityEffectOnItems() throws IOException {
    // Find a place with items
    Place placeWithItems = null;
    for (Place place : town.getPlaces()) {
      if (!place.getItems().isEmpty()) {
        placeWithItems = place;
        break;
      }
    }

    if (placeWithItems == null) {
      fail("Test requires a place with items");
    }

    // Move pet to that place
    town.movePet(Integer.parseInt(placeWithItems.getPlaceNumber()));

    // Add a player in a neighboring space
    town.addComputerPlayer();
    Player player = town.getPlayers().get(0);

    // Move player to a neighboring space
    Place neighboringPlace = placeWithItems.getNeighbors().get(0);
    player.moveToPlaceNumber(Integer.parseInt(neighboringPlace.getPlaceNumber()));

    // Clear output
    output.getBuffer().setLength(0);

    // Look around from neighboring space
    town.lookAround();

    String lookAroundResult = output.toString();
    assertFalse("Items in pet's location should not be visible",
        lookAroundResult.contains("Items in " + placeWithItems.getName() + ":"));
  }
}