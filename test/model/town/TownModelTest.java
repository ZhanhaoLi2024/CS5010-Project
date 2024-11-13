package model.town;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import model.item.Item;
import model.item.ItemModel;
import model.place.Place;
import model.place.PlaceModel;
import model.player.Player;
import model.player.PlayerModel;
import org.junit.Before;
import org.junit.Test;

/**
 * The TownModelTest class implements the tests for the TownModel class.
 */
public class TownModelTest {
  private TownModel town;
  private Scanner scanner;
  private StringBuilder output;

  /**
   * Sets up the town for testing.
   */
  @Before
  public void setUp() throws IOException {
    output = new StringBuilder();
    TownLoader loader = new TownLoader();
    String worldFile = "res/SmallTownWorld.txt";
    town = new TownModel(loader, worldFile, new StringReader(""), output, 3);
  }

  /**
   * Tests the loading of the town.
   */
  @Test
  public void testLoadTown() {
    assertNotNull(town);

    assertEquals(20, town.getPlaces().size());

    Place firstPlace = town.getPlaces().get(0);
    assertEquals("Park", firstPlace.getName());
    assertEquals(0, ((PlaceModel) firstPlace).getRow1());
    assertEquals(0, ((PlaceModel) firstPlace).getCol1());
    assertEquals(2, ((PlaceModel) firstPlace).getRow2());
    assertEquals(3, ((PlaceModel) firstPlace).getCol2());

    Place secondPlace = town.getPlaces().get(1);
    assertEquals("Grocery Store", secondPlace.getName());
  }

  /**
   * Tests the loading of the items.
   */
  @Test
  public void testLoadItems() {
    Place firstPlace = town.getPlaces().get(0);
    assertEquals(1, firstPlace.getItems().size());

    Item firstItem = firstPlace.getItems().get(0);
    assertEquals("Toy Ball", firstItem.getName());
    assertEquals(8, firstItem.getDamage());

    Place secondPlace = town.getPlaces().get(1);
    Item secondItem = secondPlace.getItems().get(0);
    assertEquals("Shopping Cart", secondItem.getName());
    assertEquals(12, secondItem.getDamage());
  }

  /**
   * Tests the movement of the character.
   */
  @Test
  public void testCharacterMovement() {
    Place firstPlace = town.getTarget().getCurrentPlace();
    assertEquals("Park", firstPlace.getName());

    town.moveTarget();
    Place secondPlace = town.getTarget().getCurrentPlace();
    assertEquals("Grocery Store", secondPlace.getName());

    town.moveTarget();
    Place thirdPlace = town.getTarget().getCurrentPlace();
    assertEquals("School", thirdPlace.getName());
  }

  /**
   * Tests the looping of the character movement.
   */
  @Test
  public void testCharacterLoopMovement() {
    for (int i = 0; i < 19; i++) {
      town.moveTarget();
    }
    Place lastPlace = town.getTarget().getCurrentPlace();
    assertEquals("Community Center", lastPlace.getName());

    town.moveTarget();
    Place firstPlace = town.getTarget().getCurrentPlace();
    assertEquals("Park", firstPlace.getName());
  }

  /**
   * Tests the retrieval of the place information.
   */
  @Test
  public void testGetPlaceInfo() {
    Place firstPlace = town.getTarget().getCurrentPlace();

    assertEquals("Park", firstPlace.getName());
    assertFalse(firstPlace.getItems().isEmpty());
    assertEquals("Toy Ball", firstPlace.getItems().get(0).getName());
  }

  /**
   * Tests the retrieval of the character in the town.
   */
  @Test
  public void testGetPlaces() {
    assertEquals(20, town.getPlaces().size());
  }

  /**
   * Tests the retrieval of the items in the town.
   */
  @Test
  public void testGetItems() {
    assertEquals(20, town.getItems().size());
  }

  /**
   * Tests the retrieval of the target name.
   */
  @Test
  public void testGetTargetName() {
    assertEquals("The Mayor", town.getTargetName());
  }

  /**
   * Tests the retrieval of the target health.
   */
  @Test
  public void testGetTargetHealth() {
    assertEquals(50, town.getTargetHealth());
  }

  @Test
  public void testGetTownName() {
    assertEquals("Small Town World", town.getTownName());
  }

  /**
   * Tests the loading of an invalid world description.
   */
  @Test(expected = IOException.class)
  public void testInvalidWorldDescription() throws IOException {
    TownLoader loader = new TownLoader();
    String worldFile = "res/InvalidWorld.txt";
    town = new TownModel(loader, worldFile, new InputStreamReader(System.in), System.out, 3);
  }

  /**
   * Tests the loading of a world description with an invalid character.
   */
  @Test
  public void testCharacterStartsInRoom0() {
    Place firstPlace = town.getPlaces().get(0);
    assertEquals(firstPlace, town.getTarget().getCurrentPlace());
  }

  /**
   * Creates a test town with the specified configuration.
   */
  private Town createTestTown() throws IOException {
    String worldData = "3 3 Test World\n"
        + "50 Target\n"
        + "TestPet\n"
        + "3\n"
        + "0 0 1 1 Place1\n"
        + "1 1 2 2 Place2\n"
        + "2 2 3 3 Place3\n"
        + "0\n";

    return new TownModel(
        new TownLoader(),
        "res/SmallTownWorld.txt",
        new StringReader(worldData),
        new StringWriter(),
        10
    );
  }

  /**
   * Tests the visibility of a place when the pet is in the place.
   */
  @Test
  public void testPlaceVisibleWhenPetElsewhere() throws IOException {
    Town testTown = createTestTown();
    Place placeToTest = testTown.getPlaceByNumber(2);

    testTown.getPet().movePet(1);

    assertTrue("Place should be visible when pet is elsewhere",
        testTown.isPlaceVisible(placeToTest));
  }

  /**
   * Tests the visibility of a place when the pet is in the place.
   */
  @Test
  public void testPlaceNotVisibleWhenPetPresent() throws IOException {
    Town testTown = createTestTown();
    Place placeToTest = testTown.getPlaceByNumber(2);
    testTown.getPet().movePet(2);
    assertFalse("Place should not be visible when pet is present",
        testTown.isPlaceVisible(placeToTest));
  }

  /**
   * Tests the visibility of a place when the pet is in the place.
   */
  @Test
  public void testVisibilityChangesWhenPetMoves() throws IOException {
    Town testTown = createTestTown();
    Place place1 = testTown.getPlaceByNumber(1);
    Place place2 = testTown.getPlaceByNumber(2);
    testTown.getPet().movePet(1);
    assertFalse("Place1 should not be visible", testTown.isPlaceVisible(place1));
    assertTrue("Place2 should be visible", testTown.isPlaceVisible(place2));
    testTown.getPet().movePet(2);
    assertTrue("Place1 should now be visible", testTown.isPlaceVisible(place1));
    assertFalse("Place2 should now not be visible", testTown.isPlaceVisible(place2));
  }

  /**
   * Tests the visibility of a place when the pet is in the place.
   */
  @Test
  public void testVisibilityInFirstPlace() throws IOException {
    Town testTown = createTestTown();
    Place firstPlace = testTown.getPlaceByNumber(1);
    testTown.getPet().movePet(1);
    assertFalse("First place should not be visible when pet is there",
        testTown.isPlaceVisible(firstPlace));
  }

  /**
   * Tests the visibility of a place when the pet is in the place.
   */
  @Test
  public void testVisibilityInLastPlace() throws IOException {
    Town testTown = createTestTown();
    Place lastPlace = testTown.getPlaceByNumber(3);
    testTown.getPet().movePet(3);
    assertFalse("Last place should not be visible when pet is there",
        testTown.isPlaceVisible(lastPlace));
  }

  /**
   * Tests the visibility of a place when the pet is in the place.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testNullPlace() throws IOException {
    Town testTown = createTestTown();
    testTown.isPlaceVisible(null);
  }

  /**
   * Tests the visibility of a place when the pet is in the place.
   */
  @Test
  public void testVisibilityWithMultiplePetMoves() throws IOException {
    Town testTown = createTestTown();
    Place place1 = testTown.getPlaceByNumber(1);
    Place place2 = testTown.getPlaceByNumber(2);
    final Place place3 = testTown.getPlaceByNumber(3);
    testTown.getPet().movePet(1);
    assertFalse("Place1 should not be visible", testTown.isPlaceVisible(place1));
    assertTrue("Place2 should be visible", testTown.isPlaceVisible(place2));
    assertTrue("Place3 should be visible", testTown.isPlaceVisible(place3));
    testTown.getPet().movePet(2);
    assertTrue("Place1 should be visible", testTown.isPlaceVisible(place1));
    assertFalse("Place2 should not be visible", testTown.isPlaceVisible(place2));
    assertTrue("Place3 should be visible", testTown.isPlaceVisible(place3));
    testTown.getPet().movePet(3);
    assertTrue("Place1 should be visible", testTown.isPlaceVisible(place1));
    assertTrue("Place2 should be visible", testTown.isPlaceVisible(place2));
    assertFalse("Place3 should not be visible", testTown.isPlaceVisible(place3));
  }

  /**
   * Tests the visibility of a place when the pet is in the place.
   */
  @Test
  public void testConcurrentVisibilityChecks() throws IOException, InterruptedException {
    final Town testTown = createTestTown();
    final int threadCount = 3;
    Thread[] threads = new Thread[threadCount];
    final boolean[] results = new boolean[threadCount];
    for (int i = 0; i < threadCount; i++) {
      final int placeIndex = i + 1;
      threads[i] = new Thread(() -> {
        Place place = testTown.getPlaceByNumber(placeIndex);
        results[placeIndex - 1] = testTown.isPlaceVisible(place);
      });
      threads[i].start();
    }
    for (Thread thread : threads) {
      thread.join();
    }
    boolean atLeastOneVisible = false;
    for (boolean result : results) {
      if (result) {
        atLeastOneVisible = true;
        break;
      }
    }
    assertTrue("At least one place should be visible", atLeastOneVisible);
  }

  /**
   * Tests the performance of the visibility check.
   */
  @Test
  public void testVisibilityCheckPerformance() throws IOException {
    Town testTown = createTestTown();
    Place placeToTest = testTown.getPlaceByNumber(1);
    long startTime = System.nanoTime();
    for (int i = 0; i < 10000; i++) {
      testTown.isPlaceVisible(placeToTest);
    }
    long endTime = System.nanoTime();
    long duration = (endTime - startTime) / 1000000;
    assertTrue("Visibility check should complete within 100ms", duration < 100);
  }

  /**
   * Tests the retrieval of the target name.
   */
  @Test
  public void testVisibilityWithDuplicatePlaceNumbers() throws IOException {
    Town testTown = createTestTown();
    Place duplicatePlace = new PlaceModel(0, 0, 1, 1, "DuplicatePlace", "1");
    testTown.getPet().movePet(1);
    assertFalse("Should handle places with same number correctly",
        testTown.isPlaceVisible(duplicatePlace));
  }

  /**
   * Tests the retrieval of the target name.
   */
  private String createTempWorldFile() throws IOException {
    String worldSpec = "7 8 Test World\n"
        + "50 Doctor Lucky\n"
        + "Fortune the Cat\n"
        + "4\n"
        + "0 0 1 1 Room1\n"
        + "1 1 2 2 Room2\n"
        + "2 2 3 3 Room3\n"
        + "3 3 4 4 Room4\n"
        + "2\n"
        + "0 1 Sword\n"
        + "1 2 Knife\n";
    Path tempFile = Files.createTempFile("world", ".txt");
    Files.write(tempFile, worldSpec.getBytes());
    tempFile.toFile().deleteOnExit();
    return tempFile.toString();
  }

  /**
   * Creates a town with the specified user input.
   */
  private Town createTown(StringWriter testOutput, String userInput) throws IOException {
    String worldFile = createTempWorldFile();
    return new TownModel(new TownLoader(), worldFile, new StringReader(userInput), testOutput, 10);
  }

  /**
   * Tests the retrieval of the target name.
   */
  @Test
  public void testValidPetMove() throws IOException {
    StringWriter testOutput = new StringWriter();
    Town testTown = createTown(testOutput, "");
    testTown.movePet(2);
    assertEquals(2, testTown.getPet().getPetCurrentPlaceNumber());
    String expectedOutput = String.format("Pet %s moved to Room2\n", testTown.getPet().getName());
    assertEquals(expectedOutput, testOutput.toString());
  }

  /**
   * Tests the retrieval of the target name.
   */
  @Test
  public void testMoveToCurrentLocation() throws IOException {
    StringWriter testOutput = new StringWriter();
    Town testTown = createTown(testOutput, "");
    testTown.movePet(1);
    assertEquals(1, testTown.getPet().getPetCurrentPlaceNumber());
    String expectedOutput = String.format("Pet %s moved to Room1\n", testTown.getPet().getName());
    assertEquals(expectedOutput, testOutput.toString());
  }

  /**
   * Tests the retrieval of the target name.
   */
  @Test
  public void testMultipleMovesSequentially() throws IOException {
    StringWriter testOutput = new StringWriter();
    Town testTown = createTown(testOutput, "");
    testTown.movePet(2);
    testTown.movePet(3);
    testTown.movePet(4);
    assertEquals(4, testTown.getPet().getPetCurrentPlaceNumber());
  }

  /**
   * Tests the retrieval of the target name.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testMoveToNegativeRoomNumber() throws IOException {
    StringWriter testOutput = new StringWriter();
    Town testTown = createTown(testOutput, "");
    testTown.movePet(-1);
  }

  /**
   * Tests the retrieval of the target name.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testMoveToZeroRoom() throws IOException {
    StringWriter testOutput = new StringWriter();
    Town testTown = createTown(testOutput, "");
    testTown.movePet(0);
  }

  /**
   * Tests the retrieval of the target name.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testMoveToNonExistentRoom() throws IOException {
    StringWriter testOutput = new StringWriter();
    Town testTown = createTown(testOutput, "");
    testTown.movePet(99);
  }

  /**
   * Tests the retrieval of the target name.
   */
  @Test
  public void testRoomVisibilityAfterPetMove() throws IOException {
    StringWriter testOutput = new StringWriter();
    Town testTown = createTown(testOutput, "");
    testTown.movePet(2);
    assertFalse(testTown.isPlaceVisible(testTown.getPlaceByNumber(2)));
    assertTrue(testTown.isPlaceVisible(testTown.getPlaceByNumber(1)));
    assertTrue(testTown.isPlaceVisible(testTown.getPlaceByNumber(3)));
  }

  /**
   * Tests the retrieval of the target name.
   */
  @Test
  public void testRepeatedMovesToSameRoom() throws IOException {
    StringWriter testOutput = new StringWriter();
    Town testTown = createTown(testOutput, "");
    testTown.movePet(2);
    String firstMoveOutput = testOutput.toString();
    StringWriter newOutput = new StringWriter();
    Town newTown = createTown(newOutput, "");
    newTown.movePet(2);
    assertEquals(firstMoveOutput, newOutput.toString());
  }

  /**
   * Tests the retrieval of the target name.
   */
  @Test(expected = NullPointerException.class)
  public void testMoveWithNullOutput() throws IOException {
    String worldFile = createTempWorldFile();
    Town testTown = new TownModel(new TownLoader(), worldFile, new StringReader(""), null, 10);
    testTown.movePet(2);
  }

  /**
   * Tests the retrieval of the target name.
   */
  @Test
  public void testPetMoveEffectOnLookAround() throws IOException {
    StringWriter testOutput = new StringWriter();
    String userInput = "TestPlayer\n1\n3\n";
    Town testTown = createTown(testOutput, userInput);
    testTown.addPlayer();
    testTown.movePet(2);
    testTown.lookAround();
    String outputStr = testOutput.toString();
    assert (outputStr.contains("Pet is here"));
  }

  /**
   * Tests the retrieval of the target name.
   */
  @Test
  public void testPetMoveEffectOnNeighboringRooms() throws IOException {
    StringWriter testOutput = new StringWriter();
    Town testTown = createTown(testOutput, "");
    testTown.movePet(2);
    assertTrue(testTown.isPlaceVisible(testTown.getPlaceByNumber(1)));
    assertTrue(testTown.isPlaceVisible(testTown.getPlaceByNumber(3)));
  }

  /**
   * Tests the retrieval of the target name.
   */
  @Test
  public void testPetMoveDuringGame() throws IOException {
    StringWriter testOutput = new StringWriter();
    String userInput = "TestPlayer\n1\n3\nno\n";
    Town testTown = createTown(testOutput, userInput);
    testTown.addPlayer();
    testTown.addComputerPlayer();
    testTown.movePet(2);
    assertEquals(2, testTown.getPet().getPetCurrentPlaceNumber());
  }

  /**
   * Tests the retrieval of the target name.
   */
  @Test
  public void testPetLocationPersistence() throws IOException {
    StringWriter testOutput = new StringWriter();
    String userInput = "TestPlayer\n1\n3\n";
    Town testTown = createTown(testOutput, userInput);
    testTown.movePet(2);
    testTown.addPlayer();
    testTown.lookAround();
    assertEquals(2, testTown.getPet().getPetCurrentPlaceNumber());
  }

  // Helper method to create a test town with a player in a specific location
  private TownModel setupTestTown() throws IOException {
    StringBuilder testOutput = new StringBuilder();
    TownModel testTown = new TownModel(
        new TownLoader(),
        "res/SmallTownWorld.txt",
        new StringReader(""),
        testOutput,
        10
    );

    Player player = new PlayerModel("Alice", false, 10, 3);
    testTown.getPlayers().add(player);

    return testTown;
  }

  // Test basic lookAround without pet influence (baseline test)
  @Test
  public void testLookAroundBasicNoPlayerNoPet() throws IOException {
    StringBuilder testOutput = new StringBuilder();
    TownModel testTown = new TownModel(
        new TownLoader(),
        "res/SmallTownWorld.txt",
        new StringReader(""),
        testOutput,
        10
    );

    Player player =
        new PlayerModel("TestPlayer", false, 10, 3);
    testTown.getPlayers().add(player);

    testTown.lookAround();

    String outputContent = testOutput.toString();
    assertTrue("Should show current place",
        outputContent.contains("Current place: School"));
    assertTrue("Should show items in School",
        outputContent.contains("Items in School:")
            && outputContent.contains("Textbook (Damage: 6)"));
  }

  // Test lookAround with pet in one neighbor room - main functionality
  @Test
  public void testLookAroundWithPetInNeighbor() throws IOException {
    StringBuilder testOutput = new StringBuilder();
    TownModel testTown = new TownModel(
        new TownLoader(),
        "res/SmallTownWorld.txt",
        new StringReader(""),
        testOutput,
        10
    );
    Player player =
        new PlayerModel("TestPlayer", false, 10, 3);
    testTown.getPlayers().add(player);

    testTown.movePet(2);
    testTown.lookAround();
    String outputContent = testOutput.toString();
    assertTrue("Should show current place",
        outputContent.contains("Current place: School"));
    assertTrue("Should show pet in neighbor",
        outputContent.contains("Grocery Store (Pet is here)"));
  }

  // Test lookAround with pet in current room - main functionality
  @Test
  public void testLookAroundWithPetInCurrentRoom() throws IOException {
    StringBuilder testOutput = new StringBuilder();
    TownModel testTown = new TownModel(
        new TownLoader(),
        "res/SmallTownWorld.txt",
        new StringReader(""),
        testOutput,
        10
    );
    Player player =
        new PlayerModel("TestPlayer", false, 10, 3);
    testTown.getPlayers().add(player);
    testTown.addComputerPlayer();
    testTown.movePet(3);
    testOutput.setLength(0);
    testTown.lookAround();
    String outputContent = testOutput.toString();
    assertTrue("Should show current place",
        outputContent.contains("Current place: School"));
    assertTrue("Should show items in current room",
        outputContent.contains("Items in School:")
            && outputContent.contains("Textbook"));
  }

  @Test
  public void testLookAroundWithPetMovingBetweenNeighbors() throws IOException {
    StringBuilder testOutput = new StringBuilder();
    TownModel testTown = new TownModel(
        new TownLoader(),
        "res/SmallTownWorld.txt",
        new StringReader(""),
        testOutput,
        10
    );
    Player humanPlayer = new PlayerModel(
        "TestPlayer",
        false,
        10,
        3
    );
    testTown.getPlayers().add(humanPlayer);
    testTown.addComputerPlayer();
    while (testTown.isComputerControllerPlayer()) {
      testTown.switchToNextPlayer();
    }
    testTown.movePet(2);
    testOutput.setLength(0);
    testTown.lookAround();
    final String firstLook = testOutput.toString();
    assertEquals("Player should still be in School", 3, humanPlayer.getPlayerCurrentPlaceNumber());
    testTown.movePet(8);
    testOutput.setLength(0);
    while (testTown.isComputerControllerPlayer()) {
      testTown.switchToNextPlayer();
    }
    testTown.lookAround();
    String secondLook = testOutput.toString();
    assertTrue("First look should show current place",
        firstLook.contains("Current place: School"));
    assertTrue("First look should show pet in Grocery Store",
        firstLook.contains("Grocery Store (Pet is here)"));
    assertTrue("Second look should show current place",
        secondLook.contains("Current place: School"));
    assertTrue("Second look should show pet in Hospital",
        secondLook.contains("Hospital (Pet is here)"));
    assertTrue("Second look should now show items in Grocery Store",
        secondLook.contains("Shopping Cart") || secondLook.contains("Grocery Store"));
  }

  // Test invalid pet movement
  @Test(expected = IllegalArgumentException.class)
  public void testLookAroundWithInvalidPetMove() throws IOException {
    TownModel testTown = setupTestTown();
    testTown.movePet(999); // Invalid room number
  }

  // Test lookAround with no players in game
  @Test(expected = IllegalStateException.class)
  public void testLookAroundWithNoPlayers() throws IOException {
    TownModel testTown = new TownModel(
        new TownLoader(),
        "res/SmallTownWorld.txt",
        new StringReader(""),
        new StringBuilder(),
        10
    );
    testTown.lookAround();
  }

  /**
   * Tests getting current player index with no players.
   */
  @Test
  public void testGetCurrentPlayerIndexWithNoPlayers() {
    assertEquals(0, town.getCurrentPlayerIndex());
  }

  /**
   * Tests getting current player index with single player.
   */
  @Test
  public void testGetCurrentPlayerIndexWithSinglePlayer() throws IOException {
    String simulatedInput = "Alice\n1\n5\n";
    TownModel testTown = new TownModel(
        new TownLoader(),
        "res/SmallTownWorld.txt",
        new StringReader(simulatedInput),
        new StringBuilder(),
        3
    );
    testTown.addPlayer();
    assertEquals(0, testTown.getCurrentPlayerIndex());
  }

  /**
   * Tests current player index changes correctly after switching players.
   */
  @Test
  public void testCurrentPlayerIndexAfterSwitching() throws IOException {
    String simulatedInput = "Alice\n1\n5\n";
    TownModel testTown = new TownModel(
        new TownLoader(),
        "res/SmallTownWorld.txt",
        new StringReader(simulatedInput),
        new StringBuilder(),
        3
    );

    testTown.addComputerPlayer();
    assertEquals(0, testTown.getCurrentPlayerIndex());

    testTown.switchToNextPlayer();
    assertEquals(0, testTown.getCurrentPlayerIndex());
  }

  /**
   * Tests player visibility when players are in same place.
   */
  @Test
  public void testPlayerVisibilityInSamePlace() throws IOException {
    // Create two players in the same place
    String simulatedInput = "Alice\n1\n5\nBob\n1\n5\n";
    StringReader input = new StringReader(simulatedInput);
    TownModel testTown =
        new TownModel(new TownLoader(), "res/SmallTownWorld.txt", input, output, 3);

    testTown.addPlayer(); // Add Alice
    testTown.addPlayer(); // Add Bob

    Player alice = testTown.getPlayers().get(0);
    assertTrue("Players in same place should be visible to each other",
        testTown.isPlayerVisible(alice));
  }

  /**
   * Tests player visibility when players are in neighboring places.
   */
  @Test
  public void testPlayerVisibilityInNeighboringPlaces() throws IOException {
    // Set up players in neighboring places
    String simulatedInput = "Alice\n3\n5\nBob\n4\n5\n";
    StringReader input = new StringReader(simulatedInput);
    TownModel testTown =
        new TownModel(new TownLoader(), "res/SmallTownWorld.txt", input, output, 3);

    testTown.addPlayer(); // Add Alice in place 1
    testTown.addPlayer(); // Add Bob in place 2

    Player alice = testTown.getPlayers().get(0);
    assertTrue("Players in neighboring places should be visible to each other",
        testTown.isPlayerVisible(alice));
  }

  /**
   * Tests player visibility when players are not in neighboring places.
   */
  @Test
  public void testPlayerVisibilityInDistantPlaces() throws IOException {
    // Set up players in non-neighboring places
    String simulatedInput = "Alice\n1\n5\nBob\n5\n5\n";
    StringReader input = new StringReader(simulatedInput);
    TownModel testTown =
        new TownModel(new TownLoader(), "res/SmallTownWorld.txt", input, output, 3);

    testTown.addPlayer(); // Add Alice in place 1
    testTown.addPlayer(); // Add Bob in place 5

    Player alice = testTown.getPlayers().get(0);
    assertFalse("Players in distant places should not be visible to each other",
        testTown.isPlayerVisible(alice));
  }

  /**
   * Tests player visibility with single player in game.
   */
  @Test
  public void testPlayerVisibilityWithSinglePlayer() throws IOException {
    String simulatedInput = "Alice\n1\n5\n";
    StringReader input = new StringReader(simulatedInput);
    TownModel testTown =
        new TownModel(new TownLoader(), "res/SmallTownWorld.txt", input, output, 3);

    testTown.addPlayer(); // Add single player
    Player alice = testTown.getPlayers().get(0);

    assertFalse("Single player should not be visible to others",
        testTown.isPlayerVisible(alice));
  }

  /**
   * Tests player visibility with null player parameter.
   */
  @Test
  public void testPlayerVisibilityWithNullPlayer() {
    try {
      town.isPlayerVisible(null);
      fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException e) {
      assertEquals("Player cannot be null", e.getMessage());
    }
  }

  /**
   * Tests showing target info with full health.
   */
  @Test
  public void testShowTargetInfoFullHealth() throws IOException {
    StringBuilder testOutput = new StringBuilder();
    TownModel testTown = new TownModel(
        new TownLoader(),
        "res/SmallTownWorld.txt",
        new StringReader(""),
        testOutput,
        3
    );

    testTown.showTargetInfo();
    String result = testOutput.toString();
    assertTrue(result.contains("The Mayor"));
    assertTrue(result.contains("Health: 50"));
    assertTrue(result.contains("Park")); // Starting place
  }

  /**
   * Tests showing target info after target moves.
   */
  @Test
  public void testShowTargetInfoAfterMove() throws IOException {
    StringBuilder testOutput = new StringBuilder();
    TownModel testTown = new TownModel(
        new TownLoader(),
        "res/SmallTownWorld.txt",
        new StringReader(""),
        testOutput,
        3
    );

    testTown.moveTarget(); // Move to next place
    testTown.showTargetInfo();
    String result = testOutput.toString();
    assertTrue(result.contains("The Mayor"));
    assertTrue(result.contains("Grocery Store")); // Second place
  }

  /**
   * Tests starting new turn updates turn counter and shows info.
   */
  @Test
  public void testStartTurnBasicFunctionality() throws IOException {
    StringBuilder testOutput = new StringBuilder();
    TownModel testTown = new TownModel(
        new TownLoader(),
        "res/SmallTownWorld.txt",
        new StringReader("Alice\n1\n5\n"),
        testOutput,
        3
    );

    testTown.addPlayer();
    int initialTurn = testTown.getCurrentTurn();
    testTown.startTurn();

    assertEquals("Turn counter should increment", initialTurn + 1, testTown.getCurrentTurn());
    String result = testOutput.toString();
    assertTrue("Should show target info", result.contains("Target:"));
    assertTrue("Should show player info", result.contains("Alice"));
  }

  /**
   * Tests starting turn with no players.
   */
  @Test
  public void testStartTurnWithNoPlayers() throws IOException {
    StringBuilder testOutput = new StringBuilder();
    TownModel testTown = new TownModel(
        new TownLoader(),
        "res/SmallTownWorld.txt",
        new StringReader(""),
        testOutput,
        3
    );

    int initialTurn = testTown.getCurrentTurn();
    try {
      testTown.startTurn();
      fail("Expected IllegalStateException");
    } catch (IllegalStateException e) {
      assertEquals("Cannot start turn: No players in the game", e.getMessage());
    }
    assertEquals("Turn counter should not increment without players",
        initialTurn, testTown.getCurrentTurn());
  }

  /**
   * Tests starting turn with multiple players.
   */
  @Test
  public void testStartTurnWithMultiplePlayers() throws IOException {
    StringBuilder testOutput = new StringBuilder();
    TownModel testTown = new TownModel(
        new TownLoader(),
        "res/SmallTownWorld.txt",
        new StringReader("Alice\n1\n5\n"),
        testOutput,
        3
    );

    testTown.addPlayer();
    testTown.addComputerPlayer();

    int initialTurn = testTown.getCurrentTurn();
    testTown.startTurn();

    assertEquals("Turn counter should increment", initialTurn + 1, testTown.getCurrentTurn());
    String result = testOutput.toString();
    assertTrue("Should show target info", result.contains("Target:"));
    assertTrue("Should show current player info",
        result.contains(testTown.getPlayers().get(testTown.getCurrentPlayerIndex()).getName()));
  }

  /**
   * Tests starting turn after maximum turns reached.
   */
  @Test
  public void testStartTurnAfterMaxTurns() throws IOException {
    StringBuilder testOutput = new StringBuilder();
    TownModel testTown = new TownModel(
        new TownLoader(),
        "res/SmallTownWorld.txt",
        new StringReader("Alice\n1\n5\n"),
        testOutput,
        2  // Set max turns to 2
    );

    testTown.addPlayer();
    testTown.startTurn(); // Turn 2
    testTown.startTurn(); // Turn 3 (exceeds max)

    assertTrue("Game should be over", testTown.isGameOver());
  }

  public StringBuilder getOutput() {
    return this.output;
  }

  /**
   * Tests showing the basic location information.
   */
  @Test
  public void testShowBasicLocationInfo()
      throws IOException, NoSuchFieldException, IllegalAccessException {
    List<Place> places = new ArrayList<>();
    places.add(new PlaceModel(0, 0, 2, 3, "Park", "1"));
    places.add(new PlaceModel(2, 0, 4, 1, "Grocery Store", "2"));
    List<Item> items = new ArrayList<>();
    items.add(new ItemModel("Toy Ball", 8));
    items.add(new ItemModel("Shopping Cart", 12));

    String simulatedInput = "Player 1\n1\n5\n";
    TownModel townModel =
        new TownModel(new TownLoader(), "res/SmallTownWorld.txt", new StringReader(simulatedInput),
            new StringBuilder(), 10);
    townModel.resetGameState();
    townModel.addPlayer();

    townModel.showBasicLocationInfo();

    Field outputField = townModel.getClass().getDeclaredField("output");
    outputField.setAccessible(true);
    StringBuilder localOutput = (StringBuilder) outputField.get(townModel);

    assertTrue(localOutput.toString().contains("Hi Player 1, you are in Park"));
    assertTrue(localOutput.toString().contains("Target is in Park"));
    assertTrue(localOutput.toString().contains("Pet is in Park"));
    assertTrue(localOutput.toString().contains("Item in this place: "));
    assertTrue(localOutput.toString().contains("Toy Ball (Damage: 8)"));
  }

  /**
   * Tests showing the basic location information with other players.
   */
  @Test
  public void testShowBasicLocationInfoWithOther()
      throws IOException, NoSuchFieldException, IllegalAccessException {
    List<Place> places = new ArrayList<>();
    places.add(new PlaceModel(0, 0, 2, 3, "Park", "1"));
    places.add(new PlaceModel(2, 0, 4, 1, "Grocery Store", "2"));
    List<Item> items = new ArrayList<>();
    items.add(new ItemModel("Toy Ball", 8));
    items.add(new ItemModel("Shopping Cart", 12));

    String simulatedInput = "Player 1\n1\n5\nPlayer 2\n1\n5\n";
    TownModel townModel =
        new TownModel(new TownLoader(), "res/SmallTownWorld.txt", new StringReader(simulatedInput),
            new StringBuilder(), 10);
    townModel.resetGameState();
    townModel.addPlayer();
    townModel.addPlayer();

    townModel.showBasicLocationInfo();

    Field outputField = townModel.getClass().getDeclaredField("output");
    outputField.setAccessible(true);
    StringBuilder localOutput = (StringBuilder) outputField.get(townModel);

    assertTrue(localOutput.toString().contains("Hi Player 1, you are in Park"));
    assertTrue(localOutput.toString().contains("Players in this place:"));
    assertTrue(localOutput.toString().contains("Player 2"));
    assertTrue(localOutput.toString().contains("Target is in Park"));
    assertTrue(localOutput.toString().contains("Pet is in Park"));
    assertTrue(localOutput.toString().contains("Item in this place: "));
    assertTrue(localOutput.toString().contains("Toy Ball (Damage: 8)"));
  }

  /**
   * Tests showing the basic location information without items.
   */
  @Test
  public void testShowBasicLocationInfoWithoutItem()
      throws IOException, NoSuchFieldException, IllegalAccessException {
    List<Place> places = new ArrayList<>();
    places.add(new PlaceModel(0, 0, 2, 3, "Park", "1"));
    places.add(new PlaceModel(2, 0, 4, 1, "Grocery Store", "2"));
    List<Item> items = new ArrayList<>();
    items.add(new ItemModel("Toy Ball", 8));
    items.add(new ItemModel("Shopping Cart", 12));

    String simulatedInput = "Player 1\n1\n5\nPlayer 2\n1\n5\n";
    TownModel townModel =
        new TownModel(new TownLoader(), "res/SmallTownWorld.txt", new StringReader(simulatedInput),
            new StringBuilder(), 10);
    townModel.resetGameState();
    townModel.addPlayer();
    townModel.addPlayer();

    // Remove all items
    for (Place place : townModel.getPlaces()) {
      place.getItems().clear();
    }

    townModel.showBasicLocationInfo();

    Field outputField = townModel.getClass().getDeclaredField("output");
    outputField.setAccessible(true);
    StringBuilder localOutput = (StringBuilder) outputField.get(townModel);

    assertTrue(localOutput.toString().contains("Hi Player 1, you are in Park"));
    assertTrue(localOutput.toString().contains("Players in this place:"));
    assertTrue(localOutput.toString().contains("Player 2"));
    assertTrue(localOutput.toString().contains("Target is in Park"));
    assertTrue(localOutput.toString().contains("Pet is in Park"));
  }

  /**
   * Tests showing the basic location information with pet in other place.
   */
  @Test
  public void testShowBasicLocationInfoPetInOtherPlace()
      throws IOException, NoSuchFieldException, IllegalAccessException {
    List<Place> places = new ArrayList<>();
    places.add(new PlaceModel(0, 0, 2, 3, "Park", "1"));
    places.add(new PlaceModel(2, 0, 4, 1, "Grocery Store", "2"));
    List<Item> items = new ArrayList<>();
    items.add(new ItemModel("Toy Ball", 8));
    items.add(new ItemModel("Shopping Cart", 12));

    String simulatedInput = "Player 1\n1\n5\nPlayer 2\n1\n5\n";
    TownModel townModel =
        new TownModel(new TownLoader(), "res/SmallTownWorld.txt", new StringReader(simulatedInput),
            new StringBuilder(), 10);
    townModel.resetGameState();
    townModel.addPlayer();
    townModel.addPlayer();

    // move pet to other place
    townModel.movePet(2);

    townModel.showBasicLocationInfo();

    Field outputField = townModel.getClass().getDeclaredField("output");
    outputField.setAccessible(true);
    StringBuilder localOutput = (StringBuilder) outputField.get(townModel);

    assertTrue(localOutput.toString().contains("Hi Player 1, you are in Park"));
    assertTrue(localOutput.toString().contains("Players in this place:"));
    assertTrue(localOutput.toString().contains("Player 2"));
    assertTrue(localOutput.toString().contains("Target is in Park"));
    assertTrue(localOutput.toString().contains("Item in this place: "));
    assertTrue(localOutput.toString().contains("Toy Ball (Damage: 8)"));
  }
}