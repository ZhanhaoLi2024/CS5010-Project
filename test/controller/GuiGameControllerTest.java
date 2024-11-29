package controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import mock.MockTownModel;
import mock.MockView;
import model.place.Place;
import model.place.PlaceModel;
import model.player.Player;
import model.player.PlayerModel;
import model.target.Target;
import model.target.TargetModel;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for GuiGameController, focusing on command execution and player management.
 */
public class GuiGameControllerTest {
  private GuiGameController controller;
  private MockTownModel mockModel;
  private MockView mockView;

  @Before
  public void setUp() {
    // Create mock model with initialized places
    mockModel = new MockTownModel();

    // Add 20 mock places to match the game's requirements
    List<Place> mockPlaces = new ArrayList<>();
    for (int i = 1; i <= 20; i++) {
      Place mockPlace = new PlaceModel(0, 0, 1, 1, "TestPlace" + i, String.valueOf(i));
      mockPlaces.add(mockPlace);
    }
    mockModel.setPlaces(mockPlaces);

    // Add a mock player
    Player mockPlayer = new PlayerModel("TestPlayer", false, 5, 1);
    mockModel.setPlayers(Arrays.asList(mockPlayer));

    controller = new GuiGameController(mockModel, 50);
    mockView = new MockView();
    controller.setView(mockView, true);
  }

  // Test adding a human player with valid parameters
  @Test
  public void testAddHumanPlayerValid() throws IOException {
    // Execute add player command with valid parameters
    controller.executeCommand("ADD_PLAYER TestPlayer 1 5");

    // Verify model interaction
    assertTrue(mockModel.getLog().contains("addPlayer called"));
    assertTrue(mockModel.getLog().contains("Player added: TestPlayer (human)"));

    // Verify view interaction - should show success message
    assertTrue(mockView.getLog().contains("showGuiMessage called"));
    assertTrue(mockView.getLastMessage().contains("TestPlayer player added successfully"));
  }

  // Test adding a human player with invalid name (empty)
  @Test
  public void testAddHumanPlayerEmptyName() {
    try {
      controller.executeCommand("ADD_PLAYER  1 5");
      fail("Expected IllegalArgumentException for empty player name");
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid player name", e.getMessage());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void testAddHumanPlayerInvalidPlace() {
    try {
      controller.executeCommand("ADD_PLAYER TestPlayer 21 5");
      fail("Expected IllegalArgumentException for invalid place number");
    } catch (IllegalArgumentException | IOException e) {
      assertEquals("Invalid place number", e.getMessage());
    }
  }

  // Test adding a human player with invalid carry limit
  @Test
  public void testAddHumanPlayerInvalidCarryLimit() throws IOException {
    try {
      controller.executeCommand("ADD_PLAYER TestPlayer 1 -1");
    } catch (IllegalArgumentException e) {
      assertEquals("Invalid carry limit", e.getMessage());
    }
  }

  // Test adding multiple human players
  @Test
  public void testAddMultipleHumanPlayers() throws IOException {
    controller.executeCommand("ADD_PLAYER Player1 1 5");
    controller.executeCommand("ADD_PLAYER Player2 2 4");

    // Verify both players were added
    String log = mockModel.getLog();
    assertTrue(log.contains("Player added: Player1"));
    assertTrue(log.contains("Player added: Player2"));
  }

  // Test adding a computer player
  @Test
  public void testAddComputerPlayer() throws IOException {
    // Execute command
    boolean result = controller.executeCommand("ADD_COMPUTER");

    // Verify command execution
    assertTrue(result);

    // Verify model interaction
    assertTrue(mockModel.getLog().contains("addPlayer called"));

    // Verify view message
    assertTrue(mockView.getLog().contains("showGuiMessage called"));
    assertTrue(mockView.getLastMessage().contains("successfully"));
  }

  // Test adding player when view is not set
  @Test
  public void testAddPlayerNoView() {
    Controller newController = new GuiGameController(mockModel, 50);
    try {
      newController.executeCommand("ADD_PLAYER TestPlayer 1 5");
    } catch (Exception e) {
      assertTrue("IllegalStateException或NullPointerException",
          e instanceof IllegalStateException ||
              e instanceof NullPointerException);
    }
  }

  // Test setting view multiple times
  @Test(expected = IllegalStateException.class)
  public void testSetViewMultipleTimes() {
    MockView secondView = new MockView();
    controller.setView(secondView, true);
  }

  // Test getting town model
  @Test
  public void testGetTownModel() {
    assertEquals(mockModel, controller.getTown());
  }

  /**
   * Tests for invalid command handling
   */
  @Test
  public void testInvalidCommand() throws IOException {
    controller.executeCommand("INVALID_COMMAND");
    assertTrue(mockView.getLog().contains("showGuiMessage called"));
    assertTrue(mockView.getLastMessage().contains("Invalid command"));
  }

  /**
   * Tests for the MOVE command
   */
  @Test
  public void testMovePlayer() throws IOException {
    controller.executeCommand("MOVE,MockPlayer,1");

    assertTrue(mockModel.getLog().contains("movePlayer called"));
    assertTrue("Check isGameOver called",
        mockModel.getLog().contains("isGameOver called"));
  }

  /**
   * Tests for the MOVE command
   */
  @Test
  public void testMoveCommandValid() throws IOException {
    controller.executeCommand("MOVE,TestPlace1,1");

    assertTrue("Should move player", mockModel.getLog().contains("movePlayer called"));
    assertTrue("Player should move to place 1",
        mockModel.getLog().contains("Player 0 moved to place 1"));
    assertTrue("Check isGameOver called",
        mockModel.getLog().contains("isGameOver called"));
  }

  /**
   * Tests for the PICK command
   */
  @Test
  public void testPickUpItemCommand() throws IOException {
    // Execute pick up command
    controller.executeCommand("PICK");

    assertTrue("Should get current place items",
        mockModel.getLog().contains("getCurrentPlaceInfo called"));
    assertTrue(mockModel.getLog().contains("getCurrentPlayerIndex called"));
    assertTrue(mockModel.getLog().contains("showBasicLocationInfo called"));
    assertTrue("Check isGameOver called",
        mockModel.getLog().contains("isGameOver called"));
  }

  /**
   * Tests for the ATTACK command
   */
  @Test
  public void testAttackCommandValid() throws IOException {
    mockModel.setGameOver(false);

    // Create a mock Target and set it in the mock model
    Place mockPlace = new PlaceModel(0, 0, 1, 1, "MockPlace", "1");
    List<Place> mockPlaces = new ArrayList<>();
    mockPlaces.add(mockPlace);
    Target mockTarget = new TargetModel("MockTarget", 50, mockPlace, mockPlaces);
    mockModel.setTarget(mockTarget);

    // Execute attack command
    controller.executeCommand("ATTACK");

    assertTrue("Should check player visibility",
        mockModel.getLog().contains("isPlayerVisible called"));
    assertTrue("Attack target called",
        mockModel.getLog().contains("attackTarget called"));
    assertTrue("Get target information",
        mockModel.getLog().contains("getTarget called"));
    assertTrue("Check is any other player visible",
        mockModel.getLog().contains("getPlayers called"));
    assertTrue("Check isGameOver called",
        mockModel.getLog().contains("isGameOver called"));
  }

  /**
   * Tests for the LOOK command
   */
  @Test
  public void testLookAroundCommand() throws IOException {
    // Execute look command
    controller.executeCommand("LOOK");

    // Verify model interaction
    assertTrue("Should call lookAround", mockModel.getLog().contains("lookAround called"));
    assertTrue("Check isGameOver called",
        mockModel.getLog().contains("isGameOver called"));
  }
}