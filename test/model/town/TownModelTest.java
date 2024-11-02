package model.town;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Scanner;
import model.item.Item;
import model.place.Place;
import model.place.PlaceModel;
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
    town = new TownModel(loader, worldFile, new InputStreamReader(System.in), System.out);
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
    Place firstPlace = town.getCharacter().getCurrentPlace();
    assertEquals("Park", firstPlace.getName());

    town.moveCharacter();
    Place secondPlace = town.getCharacter().getCurrentPlace();
    assertEquals("Grocery Store", secondPlace.getName());

    town.moveCharacter();
    Place thirdPlace = town.getCharacter().getCurrentPlace();
    assertEquals("School", thirdPlace.getName());
  }

  /**
   * Tests the looping of the character movement.
   */
  @Test
  public void testCharacterLoopMovement() {
    for (int i = 0; i < 19; i++) {
      town.moveCharacter();
    }
    Place lastPlace = town.getCharacter().getCurrentPlace();
    assertEquals("Community Center", lastPlace.getName());

    town.moveCharacter();
    Place firstPlace = town.getCharacter().getCurrentPlace();
    assertEquals("Park", firstPlace.getName());
  }

  /**
   * Tests the retrieval of the place information.
   */
  @Test
  public void testGetPlaceInfo() {
    Place firstPlace = town.getCharacter().getCurrentPlace();

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
    assertEquals(100, town.getTargetHealth());
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
    town = new TownModel(loader, worldFile, new InputStreamReader(System.in), System.out);
  }

  /**
   * Tests the loading of a world description with an invalid character.
   */
  @Test
  public void testCharacterStartsInRoom0() {
    Place firstPlace = town.getPlaces().get(0);
    assertEquals(firstPlace, town.getCharacter().getCurrentPlace());
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
    TownModel AddPlayerTown = new TownModel(loader, worldFile, input, output);

    AddPlayerTown.addPlayer();

    String outputContent = output.toString();
    assertTrue(outputContent.contains("Enter the player's name:"));
    assertTrue(outputContent.contains("Player name: Alice"));
    assertTrue(outputContent.contains("Current place: Police Station"));
    assertTrue(outputContent.contains("You can carry up to 5 items"));
    assertTrue(outputContent.contains("Player added."));
  }
}