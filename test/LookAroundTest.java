import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
import model.town.Town;
import model.town.TownData;
import model.town.TownLoader;
import model.town.TownModel;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for the enhanced Look Around functionality in Milestone 3.
 * Tests the ability to view current space information and neighboring spaces.
 */
public class LookAroundTest {
  private Town town;
  private StringWriter output;

  /**
   * Sets up the test environment before each test.
   */
  @Before
  public void setUp() {
    output = new StringWriter();
    setupTestEnvironment();
  }

  /**
   * Creates a controlled test environment with known places, items, and players.
   */
  private void setupTestEnvironment() {
    // Create test places
    List<Place> testPlaces = new ArrayList<>();
    testPlaces.add(new PlaceModel(0, 0, 2, 2, "Living Room", "1"));
    testPlaces.add(new PlaceModel(2, 0, 4, 2, "Kitchen", "2"));
    testPlaces.add(new PlaceModel(0, 2, 2, 4, "Bedroom", "3"));

    // Set up neighboring relationships
    for (Place place : testPlaces) {
      for (Place other : testPlaces) {
        if (place != other && place.isNeighbor(other)) {
          place.addNeighbor(other);
        }
      }
    }

    // Create test items
    Item knife = new ItemModel("Kitchen Knife", 10);
    Item book = new ItemModel("Book", 2);
    Item pillow = new ItemModel("Pillow", 1);

    // Add items to places
    testPlaces.get(0).addItem(book);      // Living Room
    testPlaces.get(1).addItem(knife);     // Kitchen
    testPlaces.get(2).addItem(pillow);    // Bedroom


    try {
      // Initialize town with actual game file
      town = new TownModel(
          new TownLoader(),
          "res/SmallTownWorld.txt",
          new StringReader(""),
          output,
          20
      );
    } catch (IOException e) {
      fail("Failed to initialize town: " + e.getMessage());
    }
  }

  /**
   * Test looking around in the current space.
   */
  @Test
  public void testBasicLookAround() throws IOException {
    // Add a player to the game
    town.addComputerPlayer();

    // Execute look around
    town.lookAround();
    String result = output.toString();

    // Verify current location is shown
    assertTrue("Should show current location",
        result.contains("Current place:"));

    // Verify neighboring places are shown
    assertTrue("Should show information about neighbors",
        result.contains("Neighbors of"));
  }

  /**
   * Test looking around with items in the current space.
   */
  @Test
  public void testLookAroundWithItems() throws IOException {
    // Add a player
    town.addComputerPlayer();
    Player player = town.getPlayers().get(0);

    // Get player's current place
    Place currentPlace = town.getPlaceByNumber(player.getPlayerCurrentPlaceNumber());

    // Add test item to current place
    Item testItem = new ItemModel("Test Item", 5);
    currentPlace.addItem(testItem);

    // Look around
    town.lookAround();
    String result = output.toString();

    // Verify item is visible
    assertTrue("Should show item in current place",
        result.contains("Test Item"));
    assertTrue("Should show item damage",
        result.contains("Damage: 5"));
  }

  /**
   * Test looking around with multiple players in the same space.
   */
  @Test
  public void testLookAroundWithMultiplePlayers() throws IOException {
    // Add two players in the same space
    town.addComputerPlayer(); // First player
    town.addComputerPlayer(); // Second player

    Player player1 = town.getPlayers().get(0);
    Player player2 = town.getPlayers().get(1);

    // Ensure they're in the same place
    int place = player1.getPlayerCurrentPlaceNumber();
    player2.moveToPlaceNumber(place);

    // Look around with first player
    town.lookAround();
    String result = output.toString();

    // Verify other player is visible
    assertTrue("Should show other player in same space",
        result.contains(player2.getName()));
  }

  /**
   * Test looking around with pet blocking visibility of items in neighboring space.
   */
  @Test
  public void testLookAroundWithPetBlocking() throws IOException {
    // Create a controlled test environment
    final StringWriter mockOutput = new StringWriter();
    List<Place> places = new ArrayList<>();

    // Create a center room and a neighbor
    Place centerRoom = new PlaceModel(0, 0, 2, 2, "Center Room", "1");
    Place neighborRoom = new PlaceModel(2, 0, 4, 2, "Neighbor Room", "2");

    // Add test items to neighbor room
    Item testItem = new ItemModel("Test Item", 5);
    neighborRoom.addItem(testItem);

    // Set up neighboring relationship
    places.add(centerRoom);
    places.add(neighborRoom);
    centerRoom.addNeighbor(neighborRoom);
    neighborRoom.addNeighbor(centerRoom);

    // Create mock town
    Town mockTown = new TownModel(
        (filename) -> new TownData(
            "Test Town",
            "Test Target",
            "Test Pet",
            50,
            places,
            new ArrayList<>()
        ),
        "test.txt",
        new StringReader(""),
        mockOutput,
        10
    );

    // Add a player to the center room
    mockTown.addComputerPlayer();
    Player player = mockTown.getPlayers().get(0);
    // Ensure player is in center room
    player.moveToPlaceNumber(1);

    // Move pet to the neighboring room
    mockTown.movePet(2);

    // Look around
    mockOutput.getBuffer().setLength(0); // Clear previous output
    mockTown.lookAround();
    String result = mockOutput.toString();

    // Verify results
    assertTrue("Should show current room",
        result.contains("Center Room"));
    assertTrue("Should indicate pet's presence in neighbor",
        result.contains("Pet is here"));
    assertFalse("Should not show items in blocked room",
        result.contains("Test Item"));
    assertTrue("Should still show neighbor room name",
        result.contains("Neighbor Room"));
  }

  /**
   * Test looking around with the pet in the current room.
   */
  @Test
  public void testLookAroundWithPetInCurrentRoom() throws IOException {
    // Add a player
    town.addComputerPlayer();
    Player player = town.getPlayers().get(0);

    // Move pet to player's current room
    int currentPlaceNumber = player.getPlayerCurrentPlaceNumber();
    town.movePet(currentPlaceNumber);

    // Look around
    town.lookAround();
    String result = output.toString();

    // Pet's presence shouldn't affect visibility of current room
    assertTrue("Should show current room details",
        result.contains("Current place:"));
    assertTrue("Should still show items in current room",
        !result.contains("No items found")
            || town.getPlaceByNumber(currentPlaceNumber).getItems().isEmpty());
  }

  /**
   * Test looking around with pet movement.
   */
  @Test
  public void testLookAroundWithPetMovement() throws IOException {
    // Add a player
    town.addComputerPlayer();
    Player player = town.getPlayers().get(0);
    Place currentPlace = town.getPlaceByNumber(player.getPlayerCurrentPlaceNumber());

    // Initial look around without pet
    town.lookAround();

    // Clear output
    output.getBuffer().setLength(0);

    // Move pet to a neighboring space
    List<Place> neighbors = currentPlace.getNeighbors();
    if (!neighbors.isEmpty()) {
      Place neighborPlace = neighbors.get(0);
      town.movePet(Integer.parseInt(neighborPlace.getPlaceNumber()));

      // Look around again
      town.lookAround();
      String afterPet = output.toString();

      // Verify change in visibility
      assertTrue("Should show pet's presence",
          afterPet.contains("Pet is here"));
      assertFalse("Should not show details of blocked space",
          afterPet.contains("Items in " + neighborPlace.getName()));
    }
  }

  /**
   * Test looking around with no neighbors.
   */
  @Test
  public void testLookAroundWithNoNeighbors() throws IOException {
    // Create a mock town with a single isolated place
    StringWriter mockOutput = new StringWriter();
    List<Place> places = new ArrayList<>();
    Place isolatedPlace = new PlaceModel(0, 0, 2, 2, "Isolated Room", "1");
    places.add(isolatedPlace);

    // Create a new town with minimal configuration for testing
    Town mockTown = new TownModel(
        (filename) -> new TownData(
            "Test Town",
            "Test Target",
            "Test Pet",
            50,
            places,
            new ArrayList<>()
        ),
        "test.txt",
        new StringReader(""),
        mockOutput,
        10
    );

    // Add a player to the mock town (they will automatically start in the only available place)
    mockTown.addComputerPlayer();

    // Look around
    mockTown.lookAround();
    String result = mockOutput.toString();

    // Verify results
    assertTrue("Should show current place name",
        result.contains("Isolated Room"));
    assertTrue("Should indicate no neighbors",
        result.contains("No neighbors found"));
  }

  /**
   * Test looking around with blocked neighbors.
   */
  @Test
  public void testLookAroundWithBlockedNeighbors() throws IOException {
    // Add a player to the game
    town.addComputerPlayer();
    Player player = town.getPlayers().get(0);

    // Get the current place and its neighbors
    Place currentPlace = town.getPlaceByNumber(player.getPlayerCurrentPlaceNumber());
    List<Place> neighbors = currentPlace.getNeighbors();

    // Move pet to block all neighboring spaces
    for (Place neighbor : neighbors) {
      town.movePet(Integer.parseInt(neighbor.getPlaceNumber()));

      // Look around
      output.getBuffer().setLength(0); // Clear previous output
      town.lookAround();
      String result = output.toString();

      // Verify blocked neighbor is indicated
      assertTrue("Should indicate pet's presence",
          result.contains("Pet is here"));
      assertFalse("Should not show items in blocked space",
          result.contains("Items in " + neighbor.getName()));
    }
  }

  /**
   * Test looking around with no players.
   */
  @Test(expected = IllegalStateException.class)
  public void testLookAroundNoPlayers() throws IOException {
    town.lookAround(); // Should throw IllegalStateException
  }
}