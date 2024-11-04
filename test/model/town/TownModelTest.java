package model.town;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Scanner;
import model.item.Item;
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

  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  private TownModel town;
  private Scanner scanner;

  /**
   * Sets up the town for testing.
   */
  @Before
  public void setUp() throws IOException {
    TownLoader loader = new TownLoader();
    String worldFile = "res/SmallTownWorld.txt";
    town = new TownModel(loader, worldFile, new InputStreamReader(System.in), System.out, 3);
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

    town.getPlaceInfo(firstPlace);

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

  @Test
  public void testAddComputerPlayer() throws IOException {
    town.addComputerPlayer();
    assertEquals(1, town.getPlayers().size());
    assertEquals("David(Computer)", town.getPlayers().get(0).getName());
  }

  @Test
  public void testAddPlayer() throws IOException {
    String simulatedInput = "Alice\n5\n5\n";
    StringReader input = new StringReader(simulatedInput);

    StringBuilder output = new StringBuilder();

    TownLoaderInterface loader = new TownLoader();
    String worldFile = "res/SmallTownWorld.txt";
    TownModel AddPlayerTown = new TownModel(loader, worldFile, input, output, 3);

    AddPlayerTown.addPlayer();

    String outputContent = output.toString();
    System.out.println("Actual Output: \n" + outputContent);
    assertTrue(outputContent.contains("Enter the player's name:"));
    assertTrue(outputContent.contains("Player name: Alice"));
    assertTrue(outputContent.contains("Current place: Police Station"));
    assertTrue(outputContent.contains("You can carry up to 5 items"));
    assertTrue(outputContent.contains("Player added."));
  }

  @Test
  public void testLookAroundNoPetNoPlayer() throws IOException {
    StringBuilder output = new StringBuilder();
    String simulatedInput = "Alice\n1\n5\n";
    StringReader input = new StringReader(simulatedInput);
    TownLoaderInterface loader = new TownLoader();
    String worldFile = "res/SmallTownWorld.txt";
    TownModel town = new TownModel(loader, worldFile, input, output, 10);
    Player player1 = new PlayerModel("Alice", false, 10, 3, System.out, scanner);
    town.getPlayers().add(player1);
//    town.switchToNextPlayer();
    town.lookAround();

    String outputContent = output.toString();
//    System.out.println("Actual Output: \n" + outputContent);
    assertTrue(outputContent.contains("Current place: School"));
    assertTrue(outputContent.contains("Items in School:"));
    assertTrue(outputContent.contains("Textbook (Damage: 6)"));
    assertTrue(outputContent.contains("Neighbors of School:"));
    assertTrue(outputContent.contains(" - Grocery Store"));
    assertTrue(outputContent.contains("   Items in Grocery Store:Shopping Cart (Damage: 12)"));
    assertTrue(outputContent.contains(" - Post Office"));
    assertTrue(outputContent.contains("   Items in Post Office:Envelope (Damage: 8)"));
    assertTrue(outputContent.contains(" - Hospital"));
    assertTrue(outputContent.contains("   Items in Hospital:Medical Kit (Damage: 18)"));
    assertTrue(outputContent.contains(" - Restaurant"));
    assertTrue(outputContent.contains("   Items in Restaurant:Menu (Damage: 22)"));
  }

  @Test
  public void testLookAroundNoPet() throws IOException {
    StringBuilder output = new StringBuilder();
    String simulatedInput = "Alice\n1\n5\n";
    StringReader input = new StringReader(simulatedInput);
    TownLoaderInterface loader = new TownLoader();
    String worldFile = "res/SmallTownWorld.txt";
    TownModel town = new TownModel(loader, worldFile, input, output, 10);
    Player player1 = new PlayerModel("Alice", false, 10, 3, System.out, scanner);
    Player player2 = new PlayerModel("Bob", false, 10, 8, System.out, scanner);
    town.getPlayers().add(player1);
    town.getPlayers().add(player2);
    town.lookAround();

    String outputContent = output.toString();
    assertTrue(outputContent.contains("Current place: School"));
    assertTrue(outputContent.contains("Items in School:"));
    assertTrue(outputContent.contains("Textbook (Damage: 6)"));
    assertTrue(outputContent.contains("Neighbors of School:"));
    assertTrue(outputContent.contains(" - Grocery Store"));
    assertTrue(outputContent.contains("   Items in Grocery Store:Shopping Cart (Damage: 12)"));
    assertTrue(outputContent.contains(" - Post Office"));
    assertTrue(outputContent.contains("   Items in Post Office:Envelope (Damage: 8)"));
    assertTrue(outputContent.contains(" - Hospital"));
    assertTrue(outputContent.contains("   Items in Hospital:Medical Kit (Damage: 18)"));
    assertTrue(outputContent.contains("   Players in this place:Bob"));
    assertTrue(outputContent.contains(" - Restaurant"));
    assertTrue(outputContent.contains("   Items in Restaurant:Menu (Damage: 22)"));
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
    Town town = createTestTown();
    Place placeToTest = town.getPlaceByNumber(2);

    town.getPet().movePet(1);

    assertTrue("Place should be visible when pet is elsewhere",
        town.isPlaceVisible(placeToTest));
  }

  /**
   * Tests the visibility of a place when the pet is in the place.
   */
  @Test
  public void testPlaceNotVisibleWhenPetPresent() throws IOException {
    Town town = createTestTown();
    Place placeToTest = town.getPlaceByNumber(2);

    town.getPet().movePet(2);

    assertFalse("Place should not be visible when pet is present",
        town.isPlaceVisible(placeToTest));
  }

  @Test
  public void testVisibilityChangesWhenPetMoves() throws IOException {
    Town town = createTestTown();
    Place place1 = town.getPlaceByNumber(1);
    Place place2 = town.getPlaceByNumber(2);

    town.getPet().movePet(1);
    assertFalse("Place1 should not be visible", town.isPlaceVisible(place1));
    assertTrue("Place2 should be visible", town.isPlaceVisible(place2));

    town.getPet().movePet(2);
    assertTrue("Place1 should now be visible", town.isPlaceVisible(place1));
    assertFalse("Place2 should now not be visible", town.isPlaceVisible(place2));
  }
}