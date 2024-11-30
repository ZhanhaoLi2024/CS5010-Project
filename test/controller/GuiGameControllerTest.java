package controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import controller.support.PlayerInfoDTO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import mock.MockTownModel;
import mock.MockView;
import model.item.Item;
import model.item.ItemModel;
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
  private MockTownModel mockTown;
  private GuiGameController mockController;

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

    mockTown = new MockTownModel();
    mockController = new GuiGameController(mockTown, 50);
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

  /**
   * Tests the Invalid Place Number exception for the MOVE command
   */
  @Test
  public void testMoveCommandInvalidDestination() {
    try {
      controller.executeCommand("MOVE,InvalidPlace,999");
      fail("Expected IllegalArgumentException for invalid place");
    } catch (IllegalArgumentException | IOException e) {
      assertEquals("Invalid place number", e.getMessage());
    }
  }

  @Test
  public void testPickUpItemWhenInventoryFull() throws IOException {
    // Create places list with an item
    List<Place> mockPlaces = new ArrayList<>();
    Place mockPlace = new PlaceModel(0, 0, 1, 1, "TestPlace", "1");
    mockPlace.addItem(new ItemModel("PickableItem", 10));
    mockPlaces.add(mockPlace);
    mockModel.setPlaces(mockPlaces);

    // Create player with full inventory
    Player mockPlayer = new PlayerModel("TestPlayer", false, 3, 1);
    for (int i = 1; i <= 3; i++) {
      mockPlayer.pickUpItem(new ItemModel("Item" + i, 5));
    }
    List<Player> players = new ArrayList<>();
    players.add(mockPlayer);
    mockModel.setPlayers(players);

    // Set current state
    mockModel.setPlayerCurrPlaceNumber(0, 1);

    String placeInfo = "TestPlace;[PickableItem-10];[]";
    mockModel.placeInfoToReturn = placeInfo;

    // Execute pick up command
    controller.executeCommand("PICK");

    // Verify error message was shown
    assertTrue("Should show inventory full message",
        mockView.getLastMessage().contains("inventory is full"));
  }

  @Test
  public void testAttackCommandTargetNotInRange() throws IOException {
    // Set up player
    Player mockPlayer = new PlayerModel("TestPlayer", false, 3, 1);
    mockModel.setPlayers(Arrays.asList(mockPlayer));
    mockModel.setPlayerCurrPlaceNumber(0, 1);

    // Set up places
    List<Place> mockPlaces = new ArrayList<>();
    Place playerPlace = new PlaceModel(0, 0, 1, 1, "PlayerPlace", "1");
    Place targetPlace = new PlaceModel(1, 1, 2, 2, "TargetPlace", "2");
    mockPlaces.add(playerPlace);
    mockPlaces.add(targetPlace);
    mockModel.setPlaces(mockPlaces);

    // Set up target in a different place
    Target mockTarget = new TargetModel("TestTarget", 50, targetPlace, mockPlaces);
    mockModel.setTarget(mockTarget);

    // Execute attack command
    controller.executeCommand("ATTACK");

    // Verify error message
    assertTrue("Should show target not in range message",
        mockView.getLog().contains("showGuiMessage called"));
    assertEquals("Target is not in the same place as you",
        mockView.getLastMessage());
  }

  // Game Start Tests
  @Test
  public void testGameStartWithInsufficientPlayers() throws IOException {
    // Set up town with less than 2 players
    mockTown.setPlayers(new ArrayList<>());

    // Try to start game
    boolean result = controller.executeCommand("START_TURNS");

    // Verify results
    assertFalse("Game should not start with insufficient players", result);
    assertTrue("Error message should be shown",
        mockView.getLastMessage().contains("Need at least 2 players"));
  }

  @Test
  public void testGameStartWithSufficientPlayers() throws IOException {
    // Set up mock players
    List<Player> players = new ArrayList<>();
    players.add(new MockPlayer("Player1", false));
    players.add(new MockPlayer("Player2", false));
    mockModel.setPlayers(players);

    // Start game
    boolean result = controller.executeCommand("START_TURNS");

    assertTrue("Game should start successfully", result);
    assertTrue("Game start message should be shown",
        mockView.getLastMessage().contains("There are 2 players"));
  }

  // Turn Management Tests
  @Test
  public void testPlayerTurnAndRoundProgression() throws IOException {
    // 设置三个测试玩家
    List<Player> players = new ArrayList<>();
    players.add(new MockPlayer("Player1", false));
    players.add(new MockPlayer("Player2", false));
    players.add(new MockPlayer("Player3", false));
    mockTown.setPlayers(players);

    // 设置初始状态
    mockTown.setCurrentTurn(1);
    mockTown.setCurrentPlayerIndex(0);
    mockTown.setGameOver(false);

    // 创建并设置View
    MockView mockGuiView = new MockView();
    mockController.setView(mockGuiView, true);

    // 为MockTownModel设置必要的返回数据
    mockTown.placeInfoToReturn = "TestPlace;[TestItem-10];[TestPlayer]";

    // 第一个玩家执行LOOK
    mockController.executeCommand("LOOK");

    // 验证第一个玩家的操作结果
    String modelLog = mockTown.getLog();
    assertTrue("Should call lookAround", modelLog.contains("lookAround called"));
    assertTrue("Should switch to next player", modelLog.contains("switchToNextPlayer called"));
    assertEquals("Should be player 2's turn", 1, mockTown.getCurrentPlayerIndex());
    assertEquals("Should still be turn 1", 1, mockTown.getCurrentTurn());

    mockTown.clearLog();

    // 第二个玩家执行LOOK
    mockController.executeCommand("LOOK");
    modelLog = mockTown.getLog();
    assertTrue("Should call lookAround", modelLog.contains("lookAround called"));
    assertTrue("Should switch to next player", modelLog.contains("switchToNextPlayer called"));
    assertEquals("Should be player 3's turn", 2, mockTown.getCurrentPlayerIndex());
    assertEquals("Should still be turn 1", 1, mockTown.getCurrentTurn());

    // 清除日志
    mockTown.clearLog();

    // 第三个玩家执行LOOK
    mockController.executeCommand("LOOK");
    modelLog = mockTown.getLog();
    assertTrue("Should call lookAround", modelLog.contains("lookAround called"));
    assertTrue("Should switch to next player", modelLog.contains("switchToNextPlayer called"));
    assertEquals("Should return to player 1", 0, mockTown.getCurrentPlayerIndex());
    assertEquals("Should advance to turn 2", 2, mockTown.getCurrentTurn());
  }


  @Test
  public void testTurnUpdateWithPlayerInfo() throws IOException {
    // 设置测试玩家
    List<Player> players = new ArrayList<>();
    players.add(new MockPlayer("Player1", false));
    players.add(new MockPlayer("Player2", false));
    mockTown.setPlayers(players);

    // 设置初始状态
    mockTown.setCurrentTurn(1);
    mockTown.setCurrentPlayerIndex(0);
    mockTown.setGameOver(false);

    // 创建并设置View
    MockView mockGuiView = new MockView();
    mockController.setView(mockGuiView, true);

    // 设置模拟数据返回
    mockTown.placeInfoToReturn = "TestPlace;[TestSword-10];[Player1]";

    // 执行指令
    mockController.executeCommand("LOOK");

    // 验证玩家信息更新
    PlayerInfoDTO lastInfo = mockGuiView.getLastPlayerInfo();
    assertNotNull("Player info should be updated", lastInfo);
    assertEquals("Current turn should be correct", 1, lastInfo.getCurrentTurn());
    assertEquals("Current player name should be correct", "MockPlayer", lastInfo.getPlayerName());

    // 验证Model方法调用
    String modelLog = mockTown.getLog();
    assertTrue("Should call showBasicLocationInfo",
        modelLog.contains("showBasicLocationInfo called"));
    assertTrue("Should call getCurrentPlaceInfo",
        modelLog.contains("getCurrentPlaceInfo called"));
  }

  // Game End Tests
  @Test
  public void testGameEndByTargetDefeat() throws IOException {
    List<Player> players = new ArrayList<>();
    MockPlayer player = new MockPlayer("Player1", false);
    players.add(player);
    mockTown.setPlayers(players);

    mockTown.setCurrentTurn(1);
    mockTown.setCurrentPlayerIndex(0);
    mockTown.setGameOver(true);

    MockView mockGuiView = new MockView();
    mockController.setView(mockGuiView, true);

    Place mockPlace = new PlaceModel(0, 0, 1, 1, "TestPlace", "1");
    List<Place> mockPlaces = new ArrayList<>();
    mockPlaces.add(mockPlace);
    mockTown.setPlaces(mockPlaces);

    Target mockTarget = new MockTarget(true) {
      @Override
      public Place getCurrentPlace() {
        return mockPlace;
      }

      @Override
      public boolean isDefeated() {
        return true;
      }
    };
    mockTown.setTarget(mockTarget);

    mockTown.setPlayerCurrPlaceNumber(0, 1);

    mockTown.placeInfoToReturn = "TestPlace;[Sword-10];[Player1]";
    mockTown.setPlayerVisible(false);
    mockGuiView.setNextNumberInput(1);

    String mockItemsStr = "[Sword-10]";
    mockTown.placeInfoToReturn = String.format("TestPlace;%s;[Player1]", mockItemsStr);

    mockTown.clearLog();
    mockGuiView.clearLog();

    mockController.executeCommand("ATTACK");

    String viewLog = mockGuiView.getLog();
    assertTrue("Should mention successful elimination",
        viewLog.contains("successfully eliminated the target"));

    String modelLog = mockTown.getLog();
    assertTrue("Should check player visibility",
        modelLog.contains("isPlayerVisible called"));
    assertTrue("Should get player items",
        modelLog.contains("getPlayerCurrentCarriedItems called"));
    assertTrue("Should reset game state",
        modelLog.contains("resetGameState called"));
  }


  @Test
  public void testGameEndByMaxTurns() throws IOException {
    // 设置测试玩家
    List<Player> players = new ArrayList<>();
    players.add(new MockPlayer("Player1", false));
    mockTown.setPlayers(players);

    // 设置初始状态 - 已达到最大回合数
    mockTown.setMaxTurns(50);
    mockTown.setCurrentTurn(51);
    mockTown.setGameOver(true);

    // 创建并设置View
    MockView mockGuiView = new MockView();
    mockController.setView(mockGuiView, true);

    // 设置模拟目标（未被击败）
    Place mockPlace = new PlaceModel(0, 0, 1, 1, "TestPlace", "1");
    List<Place> mockPlaces = new ArrayList<>();
    mockPlaces.add(mockPlace);
    Target mockTarget = new MockTarget(false); // 未被击败的目标
    mockTown.setTarget(mockTarget);
    mockTown.setPlaces(mockPlaces);

    // 执行一个动作触发游戏结束检查
    mockController.executeCommand("LOOK");

    // 验证游戏结束消息
    String viewLog = mockGuiView.getLog();
    assertTrue("Should show game over message",
        viewLog.contains("Game Over"));
    assertTrue("Should show target escaped message",
        mockGuiView.getLastMessage().contains("target has escaped"));

    // 验证游戏状态重置
    assertTrue("Should reset game state",
        mockTown.getLog().contains("resetGameState called"));

    // 验证视图重置
    assertTrue("Should reset view",
        viewLog.contains("resetGame called"));
  }

  // Helper mock classes
  private static class MockPlayer implements Player {
    private final String name;
    private final boolean isComputer;

    MockPlayer(String name, boolean isComputer) {
      this.name = name;
      this.isComputer = isComputer;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public boolean isComputerControlled() {
      return isComputer;
    }

    @Override
    public int getPlayerCurrentPlaceNumber() {
      return 0;
    }

    @Override
    public void moveToPlaceNumber(int placeNumber) throws IllegalArgumentException {

    }

    @Override
    public void pickUpItem(Item item) throws IllegalStateException {

    }

    @Override
    public List<Item> getCurrentCarriedItems() {
      return List.of();
    }

    @Override
    public int getCarryLimit() {
      return 0;
    }

  }

  private static class MockTarget implements Target {
    private final boolean isDefeated;

    MockTarget(boolean isDefeated) {
      this.isDefeated = isDefeated;
    }

    @Override
    public void moveToNextPlace() {

    }

    @Override
    public Place getCurrentPlace() {
      return null;
    }

    @Override
    public int getHealth() {
      return 0;
    }

    @Override
    public String getName() {
      return "";
    }

    @Override
    public boolean takeDamage(int damage) {
      return false;
    }

    @Override
    public boolean isDefeated() {
      return isDefeated;
    }
  }
}