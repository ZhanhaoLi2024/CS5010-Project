package controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import controller.support.PlayerInfoDTO;
import java.io.IOException;
import java.util.ArrayList;
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
    mockModel = new MockTownModel();

    List<Place> mockPlaces = new ArrayList<>();
    for (int i = 1; i <= 20; i++) {
      Place mockPlace = new PlaceModel(0, 0, 1, 1, "TestPlace" + i, String.valueOf(i));
      mockPlaces.add(mockPlace);
    }
    mockModel.setPlaces(mockPlaces);

    Player mockPlayer = new PlayerModel("TestPlayer", false, 5, 1);
    mockModel.setPlayers(List.of(mockPlayer));

    controller = new GuiGameController(mockModel);
    mockView = new MockView();
    controller.setView(mockView, true);

    mockTown = new MockTownModel();
    mockController = new GuiGameController(mockTown);
  }


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
    Controller newController = new GuiGameController(mockModel);
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
//    mockModel.setPlayerCurrPlaceNumber(0, 1);

    mockModel.placeInfoToReturn = "TestPlace;[PickableItem-10];[]";

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
    mockModel.setPlayers(List.of(mockPlayer));
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

  /**
   * Tests starting the game with sufficient players.
   *
   * @throws IOException if an error occurs during command execution
   */
  @Test
  public void testGameStartWithSufficientPlayers() throws IOException {
    // Set up mock players
    List<Player> players = new ArrayList<>();
    players.add(new PlayerModel("Player1", false, 5, 1));
    players.add(new PlayerModel("Player2", false, 5, 2));
    mockModel.setPlayers(players);

    // Start game
    boolean result = controller.executeCommand("START_TURNS");

    assertTrue("Game should start successfully", result);
    assertTrue("Game start message should be shown",
        mockView.getLastMessage().contains("There are 2 players"));
  }

  /**
   * Tests turn progression mechanics for multiple players.
   */
  @Test
  public void testPlayerTurnAndRoundProgression() throws IOException {
    // 设置场景:两个玩家和初始空间
    List<Place> mockPlaces = new ArrayList<>();
    Place testPlace = new PlaceModel(0, 0, 1, 1, "TestPlace", "1");
    mockPlaces.add(testPlace);
    mockTown.setPlaces(mockPlaces);

    // 创建两个玩家
    List<Player> players = new ArrayList<>();
    Player player1 = new PlayerModel("Player1", false, 5, 1);
    Player player2 = new PlayerModel("Player2", false, 5, 1);
    players.add(player1);
    players.add(player2);
    mockTown.setPlayers(players);

    // 设置初始状态
    mockTown.setCurrentPlayerIndex(0);
    mockTown.setCurrentTurn(1);
    mockTown.setPlayerCurrPlaceNumber(0, 1); // 设置两个玩家都在位置1
    mockTown.setPlayerCurrPlaceNumber(1, 1);
    mockTown.setPlayerVisible(false); // 设置玩家不可见以允许attack

    // 设置目标角色
    Target mockTarget = new MockTarget(false) {
      @Override
      public Place getCurrentPlace() {
        return testPlace; // 目标也在同一位置
      }
    };
    mockTown.setTarget(mockTarget);

    // 创建并设置View
    MockView mockGuiView = new MockView();
    mockController.setView(mockGuiView, true);

    // 设置第一个玩家(Player1)的位置信息
    mockTown.setCurrentPlayerInfo(
        "[[Player1,TestPlace,5,None], [], [MockTarget,TestPlace,50], [MockPet,TestPlace]]"
    );

    // Player1执行lookAround操作
    mockController.executeCommand("LOOK");

    // 验证Player1的lookAround结果
    String modelLog = mockTown.getLog();
    assertTrue("Should call lookAround", modelLog.contains("lookAround called"));
    assertTrue("Should switch to player 2",
        modelLog.contains("Switched to player 1 on turn 1"));
    assertEquals("Should be on turn 1", 1, mockTown.getCurrentTurn());
    assertEquals("Should be player 2's turn", 1, mockTown.getCurrentPlayerIndex());

    // 清除日志准备验证下一个玩家
    mockTown.clearLog();

    // 设置第二个玩家(Player2)的信息
    mockTown.setCurrentPlayerInfo(
        "[[Player2,TestPlace,5,Sword-10], [], [MockTarget,TestPlace,50], [MockPet,TestPlace]]"
    );

    // Player2执行attack操作
    mockController.executeCommand("ATTACK");

    // 验证Player2的attack结果
    modelLog = mockTown.getLog();
    assertTrue("Should attempt attack", modelLog.contains("attackTarget called"));
    assertEquals("Should be back to first player", 0, mockTown.getCurrentPlayerIndex());
  }


  @Test
  public void testTurnUpdateWithPlayerInfo() throws IOException {
    // 设置测试玩家
    List<Player> players = new ArrayList<>();
    players.add(new PlayerModel("Player1", false, 5, 1));
    players.add(new PlayerModel("Player2", false, 5, 2));
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
    assertEquals("Current player name should be correct", "Player2", lastInfo.getPlayerName());

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
    PlayerModel player = new PlayerModel("Player1", false, 5, 1);
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
    players.add(new PlayerModel("Player1", false, 5, 1));
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

  /**
   * Tests computer player AI decision-making and priorities:
   * 1. Attack target if in same place and not visible
   * 2. Pick up items if available and inventory not full
   * 3. Move towards target if carrying items
   * 4. Look around to gather information
   */
  @Test
  public void testComputerPlayerAIDecisionMaking() throws IOException {
    // Set up two players: one computer player and one human player (need minimum 2 players)
    List<Player> players = new ArrayList<>();
    Player computerPlayer = new PlayerModel("Computer1", true, 5, 1);
    players.add(computerPlayer);
    Player humanPlayer = new PlayerModel("Human1", false, 5, 2);
    players.add(humanPlayer);
    computerPlayer.moveToPlaceNumber(1);
    mockTown.setPlayers(players);
    mockTown.setCurrentPlayerIndex(0); // Set to computer player's turn

    // Create and set View
    MockView mockGuiView = new MockView();
    mockController.setView(mockGuiView, true);

    // Test Case 1: Computer player finds target in same place and is not visible
    // Setup conditions for attack
    mockTown.setPlayerCurrPlaceNumber(0, 1);
    Place mockPlace = new PlaceModel(0, 0, 1, 1, "TestPlace1", "1");
    List<Place> mockPlaces = new ArrayList<>();
    mockPlaces.add(mockPlace);
    mockTown.setPlaces(mockPlaces);

    Target mockTarget = new MockTarget(false) {
      @Override
      public Place getCurrentPlace() {
        return mockPlace;
      }
    };

    mockTown.setTarget(mockTarget);
    mockTown.setPlayerVisible(false);

    mockTown.placeInfoToReturn = "TestPlace;[];[]";

    // Mock isComputerControllerPlayer return value
    mockTown.setIsComputerPlayer(true);

    // Execute turn and verify attack attempt
    mockController.executeCommand("START_TURNS");
    String modelLog = mockTown.getLog();
    assertTrue("Computer should attempt to attack when conditions are right",
        modelLog.contains("attackTarget called"));

    mockTown.clearLog();
    mockGuiView.clearLog();
  }

  /**
   * Tests computer player AI's item pickup behavior.
   * Second priority: Pick up items if available and inventory not full
   */
  @Test
  public void testComputerPlayerPickupBehavior() throws IOException {
    mockTown = new MockTownModel();

    // Set up places with item
    Place place1 = new PlaceModel(0, 0, 2, 3, "TestPlace1", "1");
    Place place2 = new PlaceModel(2, 0, 4, 1, "TestPlace2", "2");
    Item mockItem = new ItemModel("TestItem", 10);
    place1.addItem(mockItem);
    place2.addItem(mockItem);
    List<Place> mockPlaces = new ArrayList<>();
    mockPlaces.add(place1);
    mockPlaces.add(place2);
    mockTown.setPlaces(mockPlaces);

    // Set up computer player
    List<Player> players = new ArrayList<>();
    Player computerPlayer = new PlayerModel("Computer1", true, 5, 2);
    players.add(computerPlayer);
    players.add(new PlayerModel("Human1", false, 5, 1));
    mockTown.setPlayers(players);
    mockTown.setCurrentPlayerIndex(0);

    // Set up target
    Target mockTarget = new TargetModel("MockTarget", 50, place1, mockPlaces);
    mockTown.setTarget(mockTarget);

    // Set computer player state
    mockTown.setPlayerCurrPlaceNumber(0, 2);
    mockTown.setIsComputerPlayer(true);
    mockTown.setPlayerVisible(false);

    // Set up view and controller
    mockController = new GuiGameController(mockTown);
    MockView mockGuiView = new MockView();
    mockController.setView(mockGuiView, true);

    // Execute command
    mockController.executeCommand("START_TURNS");

    // Verify behavior
    String modelLog = mockTown.getLog();
    assertTrue("Should check computer player status",
        modelLog.contains("isComputerControllerPlayer called"));
    assertTrue("Should get current place info",
        modelLog.contains("getCurrentPlaceInfo called"));
    assertTrue("Should attempt to pick up item",
        modelLog.contains("pickUpItem called"));
  }

  /**
   * Test computer player's movement decision when:
   * - Carrying items
   * - Target is in different place
   */
  @Test
  public void testComputerPlayerMovementWithItemsTowardsTarget() throws IOException {
    mockTown = new MockTownModel();
    Place place1 = new PlaceModel(0, 0, 2, 3, "TestPlace1", "1");
    Place place2 = new PlaceModel(2, 0, 4, 1, "TestPlace2", "2");
    Place place3 = new PlaceModel(4, 0, 6, 2, "TestPlace3", "3");
    List<Place> mockPlaces = new ArrayList<>();
    mockPlaces.add(place1);
    mockPlaces.add(place2);
    mockPlaces.add(place3);
    mockTown.setPlaces(mockPlaces);

    Player computerPlayer = new PlayerModel("Computer1", true, 5, 2);
    computerPlayer.pickUpItem(new ItemModel("TestItem", 10));
    List<Player> players = new ArrayList<>();
    players.add(computerPlayer);
    players.add(new PlayerModel("Human1", false, 5, 1));
    mockTown.setPlayers(players);
    mockTown.setCurrentPlayerIndex(0);

    Target mockTarget = new TargetModel("MockTarget", 50, place3, mockPlaces);
    mockTown.setTarget(mockTarget);

    mockTown.setPlayerCurrPlaceNumber(0, 2); // 电脑在place2
    mockTown.setIsComputerPlayer(true);
    mockTown.setPlayerVisible(false);

    mockTown.setNeighborInfoForPlace(2, true);

    mockTown.setBasicLocationInfo(
    );

    mockController = new GuiGameController(mockTown);
    MockView mockGuiView = new MockView();
    mockController.setView(mockGuiView, true);

    mockController.executeCommand("START_TURNS");

    String modelLog = mockTown.getLog();

    assertTrue("Should check computer player status",
        modelLog.contains("isComputerControllerPlayer called"));
    assertTrue("Should get current place neighbors info",
        modelLog.contains("getCurrentPlaceNeighborsInfo called"));
    assertTrue("Should attempt to move player",
        modelLog.contains("movePlayer called"));
  }

  /**
   * Test computer player's look around behavior when no other actions are available.
   *
   * @throws IOException if an error occurs during command execution
   */
  @Test
  public void testComputerPlayerLookAround() throws IOException {

    // Set up two players: one computer player and one human player (need minimum 2 players)
    List<Player> players = new ArrayList<>();
    Player computerPlayer = new PlayerModel("Computer1", true, 5, 1);
    players.add(computerPlayer);
    Player humanPlayer = new PlayerModel("Human1", false, 5, 2);
    players.add(humanPlayer);
    computerPlayer.moveToPlaceNumber(1);
    mockTown.setPlayers(players);
    mockTown.setCurrentPlayerIndex(0);

    // Create and set View
    MockView mockGuiView = new MockView();
    mockController.setView(mockGuiView, true);

    mockTown.setPlayerCurrPlaceNumber(0, 1);
    Place mockPlace = new PlaceModel(0, 0, 1, 1, "TestPlace1", "1");
    List<Place> mockPlaces = new ArrayList<>();
    mockPlaces.add(mockPlace);
    mockTown.setPlaces(mockPlaces);

    Target mockTarget = new MockTarget(false) {
      @Override
      public Place getCurrentPlace() {
        return mockPlace;
      }
    };

    mockTown.setTarget(mockTarget);
    mockTown.setPlayerVisible(false);

    // Mock isComputerControllerPlayer return value
    mockTown.setIsComputerPlayer(true);

    mockTown.placeInfoToReturn = "TestPlace;[];[]";
    mockTown.setPlayerVisible(true); // Make player visible so they can't attack

    // Execute turn and verify look around attempt
    mockController.executeCommand("START_TURNS");
    String modelLog;
    modelLog = mockTown.getLog();
    assertTrue("Computer should look around when no other actions are available",
        modelLog.contains("lookAround called"));

    mockTown.clearLog();
    mockGuiView.clearLog();
  }

  // Move Pet Tests
  @Test
  public void testMovePetValidCommand() throws IOException {
    mockTown.setPlayers(List.of(
        new PlayerModel("Player1", false, 5, 1),
        new PlayerModel("Player2", false, 5, 2)
    ));
    mockTown.setCurrentPlayerIndex(0);

    MockView mockGuiView = new MockView();
    mockController.setView(mockGuiView, true);

    mockGuiView.setNextNumberInput(5);

    mockController.executeCommand("PETMOVE");

    String modelLog = mockTown.getLog();
    assertTrue("Should call movePet", modelLog.contains("movePet called"));
  }

  /**
   * Test for invalid move pet command
   */
  @Test
  public void testMovePetInvalidLocation() {
    mockTown.setPlayers(List.of(
        new PlayerModel("Player1", false, 5, 1),
        new PlayerModel("Player2", false, 5, 2)
    ));
    mockTown.setCurrentPlayerIndex(0);

    MockView mockGuiView = new MockView();
    mockController.setView(mockGuiView, true);

    try {
      mockTown.movePet(25);
      fail("Should throw exception for invalid place number");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("must be less than or equal to 20"));
    }

    String modelLog = mockTown.getLog();
    assertTrue("Should call movePet", modelLog.contains("movePet called"));
  }

  /**
   * Test for negative move pet command
   */
  @Test
  public void testMovePetNegativeLocation() {
    mockTown.setPlayers(List.of(
        new PlayerModel("Player1", false, 5, 1),
        new PlayerModel("Player2", false, 5, 2)
    ));
    mockTown.setCurrentPlayerIndex(0);

    MockView mockGuiView = new MockView();
    mockController.setView(mockGuiView, true);

    try {
      mockTown.movePet(-1);
      fail("Should throw exception for negative place number");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("must be positive"));
    }

    // 验证错误处理
    String modelLog = mockTown.getLog();
    assertTrue("Should call movePet", modelLog.contains("movePet called"));
  }

  /**
   * Test for empty command
   *
   * @throws IOException if an error occurs during command execution
   */
  @Test
  public void testEmptyCommand() throws IOException {
    controller.executeCommand("");
    assertTrue("Should show invalid command message",
        mockView.getLastMessage().contains("Invalid command"));
  }

  /**
   * Test for unrecognized command
   *
   * @throws IOException if an error occurs during command execution
   */
  @Test
  public void testUnrecognizedCommand() throws IOException {
    // 测试未定义的命令
    controller.executeCommand("JUMP");
    assertTrue("Should show unrecognized command message",
        mockView.getLastMessage().contains("Invalid command"));
  }

  /**
   * Test for case-insensitive command handling
   *
   * @throws IOException if an error occurs during command execution
   */
  @Test
  public void testCommandCaseSensitivity() throws IOException {
    controller.executeCommand("move");
    assertTrue("Should handle case-insensitive commands",
        mockView.getLastMessage().contains("Invalid command"));
  }

  /**
   * Test pick up item when no items are available
   *
   * @throws IOException if an error occurs during command execution
   */
  @Test
  public void testPickupItemWhenNoItemsAvailable() throws IOException {
    mockModel.placeInfoToReturn = "TestPlace;[];[]";

    controller.executeCommand("PICK");
    assertTrue("Should show no items message",
        mockView.getLastMessage().contains("No item in this place"));
  }

  /**
   * Test attack target when player is not in the same place
   *
   * @throws IOException if an error occurs during command execution
   */
  @Test
  public void testAttackCommandWithoutItems() throws IOException {
    mockModel.setPlayerVisible(false);
    Place playerPlace = new PlaceModel(0, 0, 1, 1, "Place1", "1");
    Place targetPlace = new PlaceModel(1, 1, 2, 2, "Place2", "2");

    List<Place> places = new ArrayList<>();
    places.add(playerPlace);
    places.add(targetPlace);
    mockModel.setPlaces(places);
    mockModel.setPlayerCurrPlaceNumber(0, 1);

    Target mockTarget = new MockTarget(false) {
      @Override
      public Place getCurrentPlace() {
        return targetPlace;
      }
    };
    mockModel.setTarget(mockTarget);

    controller.executeCommand("ATTACK");
    assertTrue("Should show not in range message",
        mockView.getLastMessage().contains("Target is not in the same place as you"));
  }

  @Test
  public void testMovePetOutOfBounds() {
    try {
      mockModel.movePet(100);
      fail("Should throw exception for out of bounds place number");
    } catch (IllegalArgumentException e) {
      assertTrue(e.getMessage().contains("must be less than or equal to 20"));
    }

    String modelLog = mockModel.getLog();
    assertTrue("Should call movePet", modelLog.contains("movePet called"));
  }

  /**
   * Test for invalid command in game over state
   *
   * @throws IOException if an error occurs during command execution
   */
  @Test
  public void testInvalidCommandsInGameOver() throws IOException {
    mockModel.setGameOver(true);

    List<Player> players = new ArrayList<>();
    players.add(new PlayerModel("TestPlayer", false, 5, 1));
    mockModel.setPlayers(players);

    Place mockPlace = new PlaceModel(0, 0, 1, 1, "TestPlace", "1");
    List<Place> mockPlaces = new ArrayList<>();
    mockPlaces.add(mockPlace);
    mockModel.setPlaces(mockPlaces);

    controller.executeCommand("MOVE,Place1,1");
    assertTrue("Move should not be allowed in game over state",
        mockView.getLog().contains("Game is over"));
    mockView.clearLog();

    controller.executeCommand("PICK");
    assertTrue("Pick up should not be allowed in game over state",
        mockView.getLog().contains("Game is over"));
    mockView.clearLog();

    controller.executeCommand("ATTACK");
    assertTrue("Attack should not be allowed in game over state",
        mockView.getLog().contains("Game is over"));
    mockView.clearLog();

    controller.executeCommand("LOOK");
    assertTrue("Look should not be allowed in game over state",
        mockView.getLog().contains("Game is over"));
    mockView.clearLog();

    controller.executeCommand("PETMOVE");
    assertTrue("Pet move should not be allowed in game over state",
        mockView.getLog().contains("Game is over"));
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