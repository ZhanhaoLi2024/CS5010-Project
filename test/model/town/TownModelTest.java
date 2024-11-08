//package model.town;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertTrue;
//import static org.junit.Assert.fail;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.StringReader;
//import java.io.StringWriter;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.Scanner;
//import model.item.Item;
//import model.place.Place;
//import model.place.PlaceModel;
//import model.player.Player;
//import model.player.PlayerModel;
//import org.junit.Before;
//import org.junit.Test;
//
///**
// * The TownModelTest class implements the tests for the TownModel class.
// */
//public class TownModelTest {
//
//  //  private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//  private TownModel town;
//  private Scanner scanner;
//  private ByteArrayOutputStream outputStream;
//  private StringBuilder output;
//
//  /**
//   * Sets up the town for testing.
//   */
//  @Before
//  public void setUp() throws IOException {
//    output = new StringBuilder();
//    TownLoader loader = new TownLoader();
//    String worldFile = "res/SmallTownWorld.txt";
//    town = new TownModel(loader, worldFile, new StringReader(""), output, 3);
//  }
//
//  /**
//   * Tests the loading of the town.
//   */
//  @Test
//  public void testLoadTown() {
//    assertNotNull(town);
//
//    assertEquals(20, town.getPlaces().size());
//
//    Place firstPlace = town.getPlaces().get(0);
//    assertEquals("Park", firstPlace.getName());
//    assertEquals(0, ((PlaceModel) firstPlace).getRow1());
//    assertEquals(0, ((PlaceModel) firstPlace).getCol1());
//    assertEquals(2, ((PlaceModel) firstPlace).getRow2());
//    assertEquals(3, ((PlaceModel) firstPlace).getCol2());
//
//    Place secondPlace = town.getPlaces().get(1);
//    assertEquals("Grocery Store", secondPlace.getName());
//  }
//
//  /**
//   * Tests the loading of the items.
//   */
//  @Test
//  public void testLoadItems() {
//    Place firstPlace = town.getPlaces().get(0);
//    assertEquals(1, firstPlace.getItems().size());
//
//    Item firstItem = firstPlace.getItems().get(0);
//    assertEquals("Toy Ball", firstItem.getName());
//    assertEquals(8, firstItem.getDamage());
//
//    Place secondPlace = town.getPlaces().get(1);
//    Item secondItem = secondPlace.getItems().get(0);
//    assertEquals("Shopping Cart", secondItem.getName());
//    assertEquals(12, secondItem.getDamage());
//  }
//
//  /**
//   * Tests the movement of the character.
//   */
//  @Test
//  public void testCharacterMovement() {
//    Place firstPlace = town.getTarget().getCurrentPlace();
//    assertEquals("Park", firstPlace.getName());
//
//    town.moveTarget();
//    Place secondPlace = town.getTarget().getCurrentPlace();
//    assertEquals("Grocery Store", secondPlace.getName());
//
//    town.moveTarget();
//    Place thirdPlace = town.getTarget().getCurrentPlace();
//    assertEquals("School", thirdPlace.getName());
//  }
//
//  /**
//   * Tests the looping of the character movement.
//   */
//  @Test
//  public void testCharacterLoopMovement() {
//    for (int i = 0; i < 19; i++) {
//      town.moveTarget();
//    }
//    Place lastPlace = town.getTarget().getCurrentPlace();
//    assertEquals("Community Center", lastPlace.getName());
//
//    town.moveTarget();
//    Place firstPlace = town.getTarget().getCurrentPlace();
//    assertEquals("Park", firstPlace.getName());
//  }
//
//  /**
//   * Tests the retrieval of the place information.
//   */
//  @Test
//  public void testGetPlaceInfo() {
//    Place firstPlace = town.getTarget().getCurrentPlace();
//
//    assertEquals("Park", firstPlace.getName());
//    assertFalse(firstPlace.getItems().isEmpty());
//    assertEquals("Toy Ball", firstPlace.getItems().get(0).getName());
//  }
//
//  /**
//   * Tests the retrieval of the character in the town.
//   */
//  @Test
//  public void testGetPlaces() {
//    assertEquals(20, town.getPlaces().size());
//  }
//
//  /**
//   * Tests the retrieval of the items in the town.
//   */
//  @Test
//  public void testGetItems() {
//    assertEquals(20, town.getItems().size());
//  }
//
//  /**
//   * Tests the retrieval of the target name.
//   */
//  @Test
//  public void testGetTargetName() {
//    assertEquals("The Mayor", town.getTargetName());
//  }
//
//  /**
//   * Tests the retrieval of the target health.
//   */
//  @Test
//  public void testGetTargetHealth() {
//    assertEquals(50, town.getTargetHealth());
//  }
//
//  @Test
//  public void testGetTownName() {
//    assertEquals("Small Town World", town.getTownName());
//  }
//
//  /**
//   * Tests the loading of an invalid world description.
//   */
//  @Test(expected = IOException.class)
//  public void testInvalidWorldDescription() throws IOException {
//    TownLoader loader = new TownLoader();
//    String worldFile = "res/InvalidWorld.txt";
//    town = new TownModel(loader, worldFile, new InputStreamReader(System.in), System.out, 3);
//  }
//
//  /**
//   * Tests the loading of a world description with an invalid character.
//   */
//  @Test
//  public void testCharacterStartsInRoom0() {
//    Place firstPlace = town.getPlaces().get(0);
//    assertEquals(firstPlace, town.getTarget().getCurrentPlace());
//  }
//
//  @Test
//  public void testAddComputerPlayer() throws IOException {
//    town.addComputerPlayer();
//    assertEquals(1, town.getPlayers().size());
//    assertEquals("David(Computer)", town.getPlayers().get(0).getName());
//  }
//
//  @Test
//  public void testAddPlayer() throws IOException {
//    String simulatedInput = "Alice\n5\n5\n";
//    StringReader input = new StringReader(simulatedInput);
//
//    StringBuilder output = new StringBuilder();
//
//    TownLoaderInterface loader = new TownLoader();
//    String worldFile = "res/SmallTownWorld.txt";
//    TownModel AddPlayerTown = new TownModel(loader, worldFile, input, output, 3);
//
//    AddPlayerTown.addPlayer();
//
//    String outputContent = output.toString();
//    assertTrue(outputContent.contains("Enter the player's name:"));
//    assertTrue(outputContent.contains("Player name: Alice"));
//    assertTrue(outputContent.contains("Current place: Police Station"));
//    assertTrue(outputContent.contains("You can carry up to 5 items"));
//    assertTrue(outputContent.contains("Player added."));
//  }
//
//  /**
//   * Creates a test town with the specified configuration.
//   */
//  private Town createTestTown() throws IOException {
//    String worldData = "3 3 Test World\n"
//        + "50 Target\n"
//        + "TestPet\n"
//        + "3\n"
//        + "0 0 1 1 Place1\n"
//        + "1 1 2 2 Place2\n"
//        + "2 2 3 3 Place3\n"
//        + "0\n";
//
//    return new TownModel(
//        new TownLoader(),
//        "res/SmallTownWorld.txt",
//        new StringReader(worldData),
//        new StringWriter(),
//        10
//    );
//  }
//
//  /**
//   * Tests the visibility of a place when the pet is in the place.
//   */
//  @Test
//  public void testPlaceVisibleWhenPetElsewhere() throws IOException {
//    Town town = createTestTown();
//    Place placeToTest = town.getPlaceByNumber(2);
//
//    town.getPet().movePet(1);
//
//    assertTrue("Place should be visible when pet is elsewhere",
//        town.isPlaceVisible(placeToTest));
//  }
//
//  /**
//   * Tests the visibility of a place when the pet is in the place.
//   */
//  @Test
//  public void testPlaceNotVisibleWhenPetPresent() throws IOException {
//    Town town = createTestTown();
//    Place placeToTest = town.getPlaceByNumber(2);
//    town.getPet().movePet(2);
//    assertFalse("Place should not be visible when pet is present",
//        town.isPlaceVisible(placeToTest));
//  }
//
//  /**
//   * Tests the visibility of a place when the pet is in the place.
//   */
//  @Test
//  public void testVisibilityChangesWhenPetMoves() throws IOException {
//    Town town = createTestTown();
//    Place place1 = town.getPlaceByNumber(1);
//    Place place2 = town.getPlaceByNumber(2);
//    town.getPet().movePet(1);
//    assertFalse("Place1 should not be visible", town.isPlaceVisible(place1));
//    assertTrue("Place2 should be visible", town.isPlaceVisible(place2));
//    town.getPet().movePet(2);
//    assertTrue("Place1 should now be visible", town.isPlaceVisible(place1));
//    assertFalse("Place2 should now not be visible", town.isPlaceVisible(place2));
//  }
//
//  /**
//   * Tests the visibility of a place when the pet is in the place.
//   */
//  @Test
//  public void testVisibilityInFirstPlace() throws IOException {
//    Town town = createTestTown();
//    Place firstPlace = town.getPlaceByNumber(1);
//    town.getPet().movePet(1);
//    assertFalse("First place should not be visible when pet is there",
//        town.isPlaceVisible(firstPlace));
//  }
//
//  /**
//   * Tests the visibility of a place when the pet is in the place.
//   */
//  @Test
//  public void testVisibilityInLastPlace() throws IOException {
//    Town town = createTestTown();
//    Place lastPlace = town.getPlaceByNumber(3);
//    town.getPet().movePet(3);
//    assertFalse("Last place should not be visible when pet is there",
//        town.isPlaceVisible(lastPlace));
//  }
//
//  /**
//   * Tests the visibility of a place when the pet is in the place.
//   */
//  @Test(expected = IllegalArgumentException.class)
//  public void testNullPlace() throws IOException {
//    Town town = createTestTown();
//    town.isPlaceVisible(null);
//  }
//
//  /**
//   * Tests the visibility of a place when the pet is in the place.
//   */
//  @Test
//  public void testVisibilityWithMultiplePetMoves() throws IOException {
//    Town town = createTestTown();
//    Place place1 = town.getPlaceByNumber(1);
//    Place place2 = town.getPlaceByNumber(2);
//    Place place3 = town.getPlaceByNumber(3);
//    town.getPet().movePet(1);
//    assertFalse("Place1 should not be visible", town.isPlaceVisible(place1));
//    assertTrue("Place2 should be visible", town.isPlaceVisible(place2));
//    assertTrue("Place3 should be visible", town.isPlaceVisible(place3));
//    town.getPet().movePet(2);
//    assertTrue("Place1 should be visible", town.isPlaceVisible(place1));
//    assertFalse("Place2 should not be visible", town.isPlaceVisible(place2));
//    assertTrue("Place3 should be visible", town.isPlaceVisible(place3));
//    town.getPet().movePet(3);
//    assertTrue("Place1 should be visible", town.isPlaceVisible(place1));
//    assertTrue("Place2 should be visible", town.isPlaceVisible(place2));
//    assertFalse("Place3 should not be visible", town.isPlaceVisible(place3));
//  }
//
//  /**
//   * Tests the visibility of a place when the pet is in the place.
//   */
//  @Test
//  public void testConcurrentVisibilityChecks() throws IOException, InterruptedException {
//    final Town town = createTestTown();
//    final int threadCount = 3;
//    Thread[] threads = new Thread[threadCount];
//    final boolean[] results = new boolean[threadCount];
//    for (int i = 0; i < threadCount; i++) {
//      final int placeIndex = i + 1;
//      threads[i] = new Thread(() -> {
//        try {
//          Place place = town.getPlaceByNumber(placeIndex);
//          results[placeIndex - 1] = town.isPlaceVisible(place);
//        } catch (Exception e) {
//          e.printStackTrace();
//        }
//      });
//      threads[i].start();
//    }
//    for (Thread thread : threads) {
//      thread.join();
//    }
//    boolean atLeastOneVisible = false;
//    for (boolean result : results) {
//      if (result) {
//        atLeastOneVisible = true;
//        break;
//      }
//    }
//    assertTrue("At least one place should be visible", atLeastOneVisible);
//  }
//
//  /**
//   * Tests the performance of the visibility check.
//   */
//  @Test
//  public void testVisibilityCheckPerformance() throws IOException {
//    Town town = createTestTown();
//    Place placeToTest = town.getPlaceByNumber(1);
//    long startTime = System.nanoTime();
//    for (int i = 0; i < 10000; i++) {
//      town.isPlaceVisible(placeToTest);
//    }
//    long endTime = System.nanoTime();
//    long duration = (endTime - startTime) / 1000000;
//    assertTrue("Visibility check should complete within 100ms", duration < 100);
//  }
//
//  /**
//   * Tests the retrieval of the target name.
//   */
//  @Test
//  public void testVisibilityWithDuplicatePlaceNumbers() throws IOException {
//    Town town = createTestTown();
//    Place duplicatePlace = new PlaceModel(0, 0, 1, 1, "DuplicatePlace", "1");
//    town.getPet().movePet(1);
//    assertFalse("Should handle places with same number correctly",
//        town.isPlaceVisible(duplicatePlace));
//  }
//
//  /**
//   * Tests the retrieval of the target name.
//   */
//  private String createTempWorldFile() throws IOException {
//    String worldSpec = "7 8 Test World\n"
//        + "50 Doctor Lucky\n"
//        + "Fortune the Cat\n"
//        + "4\n"
//        + "0 0 1 1 Room1\n"
//        + "1 1 2 2 Room2\n"
//        + "2 2 3 3 Room3\n"
//        + "3 3 4 4 Room4\n"
//        + "2\n"
//        + "0 1 Sword\n"
//        + "1 2 Knife\n";
//    Path tempFile = Files.createTempFile("world", ".txt");
//    Files.write(tempFile, worldSpec.getBytes());
//    tempFile.toFile().deleteOnExit();
//    return tempFile.toString();
//  }
//
//  /**
//   * Creates a town with the specified user input.
//   */
//  private Town createTown(StringWriter output, String userInput) throws IOException {
//    String worldFile = createTempWorldFile();
//    return new TownModel(new TownLoader(), worldFile, new StringReader(userInput), output, 10);
//  }
//
//  /**
//   * Tests the retrieval of the target name.
//   */
//  @Test
//  public void testValidPetMove() throws IOException {
//    StringWriter output = new StringWriter();
//    Town town = createTown(output, "");
//    town.movePet(2);
//    assertEquals(2, town.getPet().getPetCurrentPlaceNumber());
//    String expectedOutput = String.format("Pet %s moved to Room2\n", town.getPet().getName());
//    assertEquals(expectedOutput, output.toString());
//  }
//
//  /**
//   * Tests the retrieval of the target name.
//   */
//  @Test
//  public void testMoveToCurrentLocation() throws IOException {
//    StringWriter output = new StringWriter();
//    Town town = createTown(output, "");
//    town.movePet(1);
//    assertEquals(1, town.getPet().getPetCurrentPlaceNumber());
//    String expectedOutput = String.format("Pet %s moved to Room1\n", town.getPet().getName());
//    assertEquals(expectedOutput, output.toString());
//  }
//
//  /**
//   * Tests the retrieval of the target name.
//   */
//  @Test
//  public void testMultipleMovesSequentially() throws IOException {
//    StringWriter output = new StringWriter();
//    Town town = createTown(output, "");
//    town.movePet(2);
//    town.movePet(3);
//    town.movePet(4);
//    assertEquals(4, town.getPet().getPetCurrentPlaceNumber());
//  }
//
//  /**
//   * Tests the retrieval of the target name.
//   */
//  @Test(expected = IllegalArgumentException.class)
//  public void testMoveToNegativeRoomNumber() throws IOException {
//    StringWriter output = new StringWriter();
//    Town town = createTown(output, "");
//    town.movePet(-1);
//  }
//
//  /**
//   * Tests the retrieval of the target name.
//   */
//  @Test(expected = IllegalArgumentException.class)
//  public void testMoveToZeroRoom() throws IOException {
//    StringWriter output = new StringWriter();
//    Town town = createTown(output, "");
//    town.movePet(0);
//  }
//
//  /**
//   * Tests the retrieval of the target name.
//   */
//  @Test(expected = IllegalArgumentException.class)
//  public void testMoveToNonExistentRoom() throws IOException {
//    StringWriter output = new StringWriter();
//    Town town = createTown(output, "");
//    town.movePet(99);
//  }
//
//  /**
//   * Tests the retrieval of the target name.
//   */
//  @Test
//  public void testRoomVisibilityAfterPetMove() throws IOException {
//    StringWriter output = new StringWriter();
//    Town town = createTown(output, "");
//    town.movePet(2);
//    assertEquals(false, town.isPlaceVisible(town.getPlaceByNumber(2)));
//    assertEquals(true, town.isPlaceVisible(town.getPlaceByNumber(1)));
//    assertEquals(true, town.isPlaceVisible(town.getPlaceByNumber(3)));
//  }
//
//  /**
//   * Tests the retrieval of the target name.
//   */
//  @Test
//  public void testRepeatedMovesToSameRoom() throws IOException {
//    StringWriter output = new StringWriter();
//    Town town = createTown(output, "");
//    town.movePet(2);
//    String firstMoveOutput = output.toString();
//    StringWriter newOutput = new StringWriter();
//    Town newTown = createTown(newOutput, "");
//    newTown.movePet(2);
//    assertEquals(firstMoveOutput, newOutput.toString());
//  }
//
//  /**
//   * Tests the retrieval of the target name.
//   */
//  @Test(expected = NullPointerException.class)
//  public void testMoveWithNullOutput() throws IOException {
//    String worldFile = createTempWorldFile();
//    Town town = new TownModel(new TownLoader(), worldFile, new StringReader(""), null, 10);
//    town.movePet(2);
//  }
//
//  /**
//   * Tests the retrieval of the target name.
//   */
//  @Test
//  public void testPetMoveEffectOnLookAround() throws IOException {
//    StringWriter output = new StringWriter();
//    String userInput = "TestPlayer\n1\n3\n";
//    Town town = createTown(output, userInput);
//    town.addPlayer();
//    town.movePet(2);
//    town.lookAround();
//    String outputStr = output.toString();
//    assert (outputStr.contains("Pet is here"));
//  }
//
//  /**
//   * Tests the retrieval of the target name.
//   */
//  @Test
//  public void testPetMoveEffectOnNeighboringRooms() throws IOException {
//    StringWriter output = new StringWriter();
//    Town town = createTown(output, "");
//    town.movePet(2);
//    assertEquals(true, town.isPlaceVisible(town.getPlaceByNumber(1)));
//    assertEquals(true, town.isPlaceVisible(town.getPlaceByNumber(3)));
//  }
//
//  /**
//   * Tests the retrieval of the target name.
//   */
//  @Test
//  public void testPetMoveDuringGame() throws IOException {
//    StringWriter output = new StringWriter();
//    String userInput = "TestPlayer\n1\n3\nno\n";
//    Town town = createTown(output, userInput);
//    town.addPlayer();
//    town.addComputerPlayer();
//    town.movePet(2);
//    assertEquals(2, town.getPet().getPetCurrentPlaceNumber());
//  }
//
//  /**
//   * Tests the retrieval of the target name.
//   */
//  @Test
//  public void testPetLocationPersistence() throws IOException {
//    StringWriter output = new StringWriter();
//    String userInput = "TestPlayer\n1\n3\n";
//    Town town = createTown(output, userInput);
//    town.movePet(2);
//    town.addPlayer();
//    town.lookAround();
//    assertEquals(2, town.getPet().getPetCurrentPlaceNumber());
//  }
//
//  /**
//   * Helper method to create a test town with a player in a specific location
//   */
//  private TownModel setupTestTown(int playerLocation) throws IOException {
//    StringBuilder testOutput = new StringBuilder();
//    TownModel testTown = new TownModel(
//        new TownLoader(),
//        "res/SmallTownWorld.txt",
//        new StringReader(""),
//        testOutput,
//        10
//    );
//
//    Player player = new PlayerModel("Alice", false, 10, playerLocation, testOutput, scanner);
//    testTown.getPlayers().add(player);
//
//    return testTown;
//  }
//
//  /**
//   * Test basic lookAround without pet influence (baseline test)
//   */
//  @Test
//  public void testLookAroundBasicNoPlayerNoPet() throws IOException {
//    StringBuilder output = new StringBuilder();
//    TownModel testTown = new TownModel(
//        new TownLoader(),
//        "res/SmallTownWorld.txt",
//        new StringReader(""),
//        output,
//        10
//    );
//
//    Player player =
//        new PlayerModel("TestPlayer", false, 10, 3, output, new Scanner(new StringReader("")));
//    testTown.getPlayers().add(player);
//
//    testTown.lookAround();
//
//    String outputContent = output.toString();
//    assertTrue("Should show current place",
//        outputContent.contains("Current place: School"));
//    assertTrue("Should show items in School",
//        outputContent.contains("Items in School:") &&
//            outputContent.contains("Textbook (Damage: 6)"));
//  }
//
//  /**
//   * Test lookAround with pet in one neighbor room - main functionality
//   */
//  @Test
//  public void testLookAroundWithPetInNeighbor() throws IOException {
//    StringBuilder output = new StringBuilder();
//    TownModel testTown = new TownModel(
//        new TownLoader(),
//        "res/SmallTownWorld.txt",
//        new StringReader(""),
//        output,
//        10
//    );
//    Player player =
//        new PlayerModel("TestPlayer", false, 10, 3, output, new Scanner(new StringReader("")));
//    testTown.getPlayers().add(player);
//
//    testTown.movePet(2);
//    testTown.lookAround();
//    String outputContent = output.toString();
//    assertTrue("Should show current place",
//        outputContent.contains("Current place: School"));
//    assertTrue("Should show pet in neighbor",
//        outputContent.contains("Grocery Store (Pet is here)"));
//  }
//
//  /**
//   * Test lookAround when pet is in player's current room
//   */
//  @Test
//  public void testLookAroundWithPetInCurrentRoom() throws IOException {
//    StringBuilder output = new StringBuilder();
//    TownModel testTown = new TownModel(
//        new TownLoader(),
//        "res/SmallTownWorld.txt",
//        new StringReader(""),
//        output,
//        10
//    );
//    Player player =
//        new PlayerModel("TestPlayer", false, 10, 3, output, new Scanner(new StringReader("")));
//    testTown.getPlayers().add(player);
//    testTown.addComputerPlayer();
//    testTown.movePet(3);
//    output.setLength(0);
//    testTown.lookAround();
//    String outputContent = output.toString();
//    assertTrue("Should show current place",
//        outputContent.contains("Current place: School"));
//    assertTrue("Should show items in current room",
//        outputContent.contains("Items in School:") &&
//            outputContent.contains("Textbook"));
//  }
//
//  @Test
//  public void testLookAroundWithPetMovingBetweenNeighbors() throws IOException {
//    StringBuilder output = new StringBuilder();
//    TownModel testTown = new TownModel(
//        new TownLoader(),
//        "res/SmallTownWorld.txt",
//        new StringReader(""),
//        output,
//        10
//    );
//    Player humanPlayer = new PlayerModel(
//        "TestPlayer",
//        false,
//        10,
//        3,
//        output,
//        new Scanner(new StringReader(""))
//    );
//    testTown.getPlayers().add(humanPlayer);
//    testTown.addComputerPlayer();
//    while (testTown.isComputerControllerPlayer()) {
//      testTown.switchToNextPlayer();
//    }
//    testTown.movePet(2);
//    output.setLength(0);
//    testTown.lookAround();
//    String firstLook = output.toString();
//    assertTrue("Player should still be in School",
//        humanPlayer.getPlayerCurrentPlaceNumber() == 3);
//    testTown.movePet(8);
//    output.setLength(0);
//    while (testTown.isComputerControllerPlayer()) {
//      testTown.switchToNextPlayer();
//    }
//    testTown.lookAround();
//    String secondLook = output.toString();
//    assertTrue("First look should show current place",
//        firstLook.contains("Current place: School"));
//    assertTrue("First look should show pet in Grocery Store",
//        firstLook.contains("Grocery Store (Pet is here)"));
//    assertTrue("Second look should show current place",
//        secondLook.contains("Current place: School"));
//    assertTrue("Second look should show pet in Hospital",
//        secondLook.contains("Hospital (Pet is here)"));
//    assertTrue("Second look should now show items in Grocery Store",
//        secondLook.contains("Shopping Cart") || secondLook.contains("Grocery Store"));
//  }
//
//  /**
//   * Test invalid pet movement
//   */
//  @Test(expected = IllegalArgumentException.class)
//  public void testLookAroundWithInvalidPetMove() throws IOException {
//    TownModel testTown = setupTestTown(3);
//    testTown.movePet(999); // Invalid room number
//  }
//
//  /**
//   * Test lookAround with no players in game
//   */
//  @Test(expected = IllegalStateException.class)
//  public void testLookAroundWithNoPlayers() throws IOException {
//    TownModel testTown = new TownModel(
//        new TownLoader(),
//        "res/SmallTownWorld.txt",
//        new StringReader(""),
//        new StringBuilder(),
//        10
//    );
//    testTown.lookAround();
//  }
//
//  /**
//   * Tests getting current player index with no players.
//   */
//  @Test
//  public void testGetCurrentPlayerIndexWithNoPlayers() {
//    assertEquals(0, town.getCurrentPlayerIndex());
//  }
//
//  /**
//   * Tests getting current player index with single player.
//   */
//  @Test
//  public void testGetCurrentPlayerIndexWithSinglePlayer() throws IOException {
//    String simulatedInput = "Alice\n1\n5\n";
//    TownModel testTown = new TownModel(
//        new TownLoader(),
//        "res/SmallTownWorld.txt",
//        new StringReader(simulatedInput),
//        new StringBuilder(),
//        3
//    );
//    testTown.addPlayer();
//    assertEquals(0, testTown.getCurrentPlayerIndex());
//  }
//
//  /**
//   * Tests current player index changes correctly after switching players.
//   */
//  @Test
//  public void testCurrentPlayerIndexAfterSwitching() throws IOException {
//    String simulatedInput = "Alice\n1\n5\n";
//    TownModel testTown = new TownModel(
//        new TownLoader(),
//        "res/SmallTownWorld.txt",
//        new StringReader(simulatedInput),
//        new StringBuilder(),
//        3
//    );
//
//    testTown.addComputerPlayer();
//    assertEquals(0, testTown.getCurrentPlayerIndex());
//
//    testTown.switchToNextPlayer();
//    assertEquals(0, testTown.getCurrentPlayerIndex());
//  }
//
//  /**
//   * Tests player visibility when players are in same place.
//   */
//  @Test
//  public void testPlayerVisibilityInSamePlace() throws IOException {
//    // Create two players in the same place
//    String simulatedInput = "Alice\n1\n5\nBob\n1\n5\n";
//    StringReader input = new StringReader(simulatedInput);
//    TownModel testTown =
//        new TownModel(new TownLoader(), "res/SmallTownWorld.txt", input, output, 3);
//
//    testTown.addPlayer(); // Add Alice
//    testTown.addPlayer(); // Add Bob
//
//    Player alice = testTown.getPlayers().get(0);
//    assertTrue("Players in same place should be visible to each other",
//        testTown.isPlayerVisible(alice));
//  }
//
//  /**
//   * Tests player visibility when players are in neighboring places.
//   */
//  @Test
//  public void testPlayerVisibilityInNeighboringPlaces() throws IOException {
//    // Set up players in neighboring places
//    String simulatedInput = "Alice\n1\n5\nBob\n2\n5\n";
//    StringReader input = new StringReader(simulatedInput);
//    TownModel testTown =
//        new TownModel(new TownLoader(), "res/SmallTownWorld.txt", input, output, 3);
//
//    testTown.addPlayer(); // Add Alice in place 1
//    testTown.addPlayer(); // Add Bob in place 2
//
//    Player alice = testTown.getPlayers().get(0);
//    assertTrue("Players in neighboring places should be visible to each other",
//        testTown.isPlayerVisible(alice));
//  }
//
//  /**
//   * Tests player visibility when players are not in neighboring places.
//   */
//  @Test
//  public void testPlayerVisibilityInDistantPlaces() throws IOException {
//    // Set up players in non-neighboring places
//    String simulatedInput = "Alice\n1\n5\nBob\n5\n5\n";
//    StringReader input = new StringReader(simulatedInput);
//    TownModel testTown =
//        new TownModel(new TownLoader(), "res/SmallTownWorld.txt", input, output, 3);
//
//    testTown.addPlayer(); // Add Alice in place 1
//    testTown.addPlayer(); // Add Bob in place 5
//
//    Player alice = testTown.getPlayers().get(0);
//    assertFalse("Players in distant places should not be visible to each other",
//        testTown.isPlayerVisible(alice));
//  }
//
//  /**
//   * Tests player visibility with single player in game.
//   */
//  @Test
//  public void testPlayerVisibilityWithSinglePlayer() throws IOException {
//    String simulatedInput = "Alice\n1\n5\n";
//    StringReader input = new StringReader(simulatedInput);
//    TownModel testTown =
//        new TownModel(new TownLoader(), "res/SmallTownWorld.txt", input, output, 3);
//
//    testTown.addPlayer(); // Add single player
//    Player alice = testTown.getPlayers().get(0);
//
//    assertFalse("Single player should not be visible to others",
//        testTown.isPlayerVisible(alice));
//  }
//
//  /**
//   * Tests player visibility with null player parameter.
//   */
//  @Test
//  public void testPlayerVisibilityWithNullPlayer() {
//    try {
//      town.isPlayerVisible(null);
//      fail("Expected IllegalArgumentException");
//    } catch (IllegalArgumentException e) {
//      assertEquals("Player cannot be null", e.getMessage());
//    }
//  }
//
//  /**
//   * Tests showing target info with full health.
//   */
//  @Test
//  public void testShowTargetInfoFullHealth() throws IOException {
//    StringBuilder output = new StringBuilder();
//    TownModel testTown = new TownModel(
//        new TownLoader(),
//        "res/SmallTownWorld.txt",
//        new StringReader(""),
//        output,
//        3
//    );
//
//    testTown.showTargetInfo();
//    String result = output.toString();
//    assertTrue(result.contains("The Mayor"));
//    assertTrue(result.contains("Health: 50"));
//    assertTrue(result.contains("Park")); // Starting place
//  }
//
//  /**
//   * Tests showing target info after target moves.
//   */
//  @Test
//  public void testShowTargetInfoAfterMove() throws IOException {
//    StringBuilder output = new StringBuilder();
//    TownModel testTown = new TownModel(
//        new TownLoader(),
//        "res/SmallTownWorld.txt",
//        new StringReader(""),
//        output,
//        3
//    );
//
//    testTown.moveTarget(); // Move to next place
//    testTown.showTargetInfo();
//    String result = output.toString();
//    assertTrue(result.contains("The Mayor"));
//    assertTrue(result.contains("Grocery Store")); // Second place
//  }
//
//  /**
//   * Tests starting new turn updates turn counter and shows info.
//   */
//  @Test
//  public void testStartTurnBasicFunctionality() throws IOException {
//    StringBuilder output = new StringBuilder();
//    TownModel testTown = new TownModel(
//        new TownLoader(),
//        "res/SmallTownWorld.txt",
//        new StringReader("Alice\n1\n5\n"),
//        output,
//        3
//    );
//
//    testTown.addPlayer();
//    int initialTurn = testTown.getCurrentTurn();
//    testTown.startTurn();
//
//    assertEquals("Turn counter should increment", initialTurn + 1, testTown.getCurrentTurn());
//    String result = output.toString();
//    assertTrue("Should show target info", result.contains("Target:"));
//    assertTrue("Should show player info", result.contains("Alice"));
//  }
//
//  /**
//   * Tests starting turn with no players.
//   */
//  @Test
//  public void testStartTurnWithNoPlayers() throws IOException {
//    StringBuilder output = new StringBuilder();
//    TownModel testTown = new TownModel(
//        new TownLoader(),
//        "res/SmallTownWorld.txt",
//        new StringReader(""),
//        output,
//        3
//    );
//
//    int initialTurn = testTown.getCurrentTurn();
//    try {
//      testTown.startTurn();
//      fail("Expected IllegalStateException");
//    } catch (IllegalStateException e) {
//      assertEquals("Cannot start turn: No players in the game", e.getMessage());
//    }
//    assertEquals("Turn counter should not increment without players",
//        initialTurn, testTown.getCurrentTurn());
//  }
//
//  /**
//   * Tests starting turn with multiple players.
//   */
//  @Test
//  public void testStartTurnWithMultiplePlayers() throws IOException {
//    StringBuilder output = new StringBuilder();
//    TownModel testTown = new TownModel(
//        new TownLoader(),
//        "res/SmallTownWorld.txt",
//        new StringReader("Alice\n1\n5\n"),
//        output,
//        3
//    );
//
//    testTown.addPlayer();
//    testTown.addComputerPlayer();
//
//    int initialTurn = testTown.getCurrentTurn();
//    testTown.startTurn();
//
//    assertEquals("Turn counter should increment", initialTurn + 1, testTown.getCurrentTurn());
//    String result = output.toString();
//    assertTrue("Should show target info", result.contains("Target:"));
//    assertTrue("Should show current player info",
//        result.contains(testTown.getPlayers().get(testTown.getCurrentPlayerIndex()).getName()));
//  }
//
//  /**
//   * Tests starting turn after maximum turns reached.
//   */
//  @Test
//  public void testStartTurnAfterMaxTurns() throws IOException {
//    StringBuilder output = new StringBuilder();
//    TownModel testTown = new TownModel(
//        new TownLoader(),
//        "res/SmallTownWorld.txt",
//        new StringReader("Alice\n1\n5\n"),
//        output,
//        2  // Set max turns to 2
//    );
//
//    testTown.addPlayer();
//    testTown.startTurn(); // Turn 2
//    testTown.startTurn(); // Turn 3 (exceeds max)
//
//    assertTrue("Game should be over", testTown.isGameOver());
//  }
//
//}