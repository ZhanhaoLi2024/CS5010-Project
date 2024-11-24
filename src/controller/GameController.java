package controller;

import controller.command.AddPlayerCommand;
import controller.command.AttackTargetCommand;
import controller.command.LookAroundCommand;
import controller.command.MovePetCommand;
import controller.command.MovePlayerCommand;
import controller.command.PickUpItemCommand;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import model.place.Place;
import model.player.Player;
import model.town.Town;
import view.GameView;

/**
 * The game controller responsible for handling user input and game commands.
 * Uses the command pattern for operations and coordinates between model and view.
 */
public class GameController implements Controller {
  private static final int CELL_SIZE = 90;
  private final Town town;
  private final int maxTurns;
  private GameView view;
  private int currentTurn;
  private boolean quitGame;
  private boolean continueGame;
  private boolean isGui;

  /**
   * Creates a new game controller with the given town, view and maximum turns.
   *
   * @param gameTown     The town where the game takes place
   * @param gameView     The view used to display game information
   * @param gameMaxTurns The maximum number of turns for the game
   */
  public GameController(Town gameTown, GameView gameView, int gameMaxTurns) {
    this.town = gameTown;
    this.view = gameView;
    this.maxTurns = gameMaxTurns;
    this.currentTurn = 1;
    this.quitGame = false;
  }

  @Override
  public void startGame() throws IOException {
    if (view == null) {
      throw new IllegalStateException("View is not set");
    }
    view.initialize();
    view.showMessage("Welcome to the game! You have " + maxTurns + " turns.");

    updateGameView();

    while (!quitGame) {
      displayMainMenu();
    }
  }

  private void updateGameView() throws IOException {
    view.updateMap(
        town.getPlaces(),
        town.getPlayers(),
        town.getTarget(),
        null  // 我们不再需要传递BufferedImage，因为MapPanel自己处理绘制
    );

    // 如果有当前玩家，也更新玩家信息
    if (!town.getPlayers().isEmpty()) {
      Player currentPlayer = town.getPlayers().get(town.getCurrentPlayerIndex());
      Place currentPlace = town.getPlaceByNumber(currentPlayer.getPlayerCurrentPlaceNumber());
      view.updatePlayerInfo(currentPlayer, currentPlace);
    }
  }

  /**
   * Displays the main menu and handles user input.
   */
  private void displayMainMenu() throws IOException {
    int choice = view.displayMainMenu();
    System.out.println("choice: " + choice);

    switch (choice) {
      case 1:
        view.showAddPlayerMessage();
        break;
      case 3:
        view.showPlayersInfo();
        break;
//      case 1:
//        displayMapInfo();
//        break;
//      case 2:
//        new AddPlayerCommand(town, view, false).execute();
//        break;
//      case 3:
//        new AddPlayerCommand(town, view, true).execute();
//        break;
//      case 4:
//        new DisplayPlayerInfoCommand(town, view).execute();
//        break;
//      case 5:
//        new DisplayPlaceInfoCommand(town, view).execute();
//        break;
//      case 6:
//        takeTurn();
//        break;
//      case 7:
//        printMap();
//        break;
      case 0:
        view.showMessage("Exiting...");
        this.quitGame = true;
        break;
      default:
        view.showMessage("Invalid choice, please try again.");
    }
  }

  @Override
  public void setView(GameView gameView, boolean gui) {
    if (this.view != null) {
      throw new IllegalStateException("View is already set");
    }
    this.view = gameView;
    this.isGui = gui;
  }

  @Override
  public void handleAddHumanPlayer(String name, int startingPlace, int carryLimit)
      throws IOException {
    new AddPlayerCommand(town, false, name, startingPlace, carryLimit).execute();
  }

  @Override
  public void handleAddComputerPlayer() throws IOException {
    new AddPlayerCommand(town, true, "Computer Player", 1, 1).execute();
  }

  @Override
  public void handleShowPlayersInfo(Boolean isAll) throws IOException {
    if (isAll) {
      view.showMessage("Showing all players info:");
    } else {
      view.showMessage("Showing specific player info:");
    }
  }

  @Override
  public Town getTown() {
    return this.town;
  }

  @Override
  public boolean hasEnoughPlayers() {
    return town.getPlayers().size() >= 2;
  }

  @Override
  public void handleDisplayPlayerInfo() throws IOException {
//    new DisplayPlayerInfoCommand(town, view).execute();
  }

  /**
   * Handles a turn for the current player.
   */
  private void takeTurnForPlayer() throws IOException {
    boolean isComputerControlled = town.isComputerControllerPlayer();
    if (!isComputerControlled) {
      handleHumanTurn();
    } else {
      handleComputerTurn();
    }
  }

  /**
   * Handles a turn for a human player.
   */
  private void handleHumanTurn() throws IOException {

    int choice = view.humanTurnChoice();
    switch (choice) {
      case 1:
        new MovePlayerCommand(town).execute();
        break;
      case 2:
        new PickUpItemCommand(town).execute();
        break;
      case 3:
        new LookAroundCommand(town).execute();
        break;
      case 4:
        new AttackTargetCommand(town, view).execute();
        break;
      case 5:
        new MovePetCommand(town, view).execute();
        break;
      default:
        view.showMessage("Invalid choice, please try again.");
    }
  }

  /**
   * Handles a turn for a computer-controlled player.
   */
  private void handleComputerTurn() throws IOException {
    view.showMessage("Computer player's turn.");
    Player computerPlayer = town.getPlayers().get(town.getCurrentPlayerIndex());
    Place currentPlace = town.getPlaceByNumber(computerPlayer.getPlayerCurrentPlaceNumber());

    if (currentPlace.getPlaceNumber().equals(
        town.getTarget().getCurrentPlace().getPlaceNumber())
        && !town.isPlayerVisible(computerPlayer)) {
      view.showMessage("Computer player attempts to attack the target.");
      new AttackTargetCommand(town, view).execute();
      return;
    }

    if (!currentPlace.getItems().isEmpty()
        && computerPlayer.getCurrentCarriedItems().size() < computerPlayer.getCarryLimit()) {
      view.showMessage("Computer player picks up an item.");
      new PickUpItemCommand(town).execute();
      return;
    }

    if (!computerPlayer.getCurrentCarriedItems().isEmpty()) {
      view.showMessage("Computer player moves to find the target.");
      new MovePlayerCommand(town).execute();
      return;
    }

    view.showMessage("Computer player looks around.");
    new LookAroundCommand(town).execute();
  }

  @Override
  public void takeTurn() throws IOException {
    if (town.getPlayers().size() < 2) {
      view.showMessage("You have to add at least two player");
      continueGame = false;
    } else {
      continueGame = true;
    }
    view.showMessage("Game started!");
    updateGameView();
    view.showMessage(String.format("Target: %s (Health: %d) in %s",
        town.getTargetName(),
        town.getTargetHealth(),
        town.getTarget().getCurrentPlace().getName()));

    town.showPetCurrentInfo();

    while (continueGame) {
      view.showMessage("\nTurn " + town.getCurrentTurn() + " of " + maxTurns);
      town.showBasicLocationInfo();
      takeTurnForPlayer();
      updateGameView();

      boolean isGameOver = town.isGameOver();
      if (isGameOver) {
        endGame();
      }
    }
  }

  @Override
  public void endGame() throws IOException {
    continueGame = false;
    if (town.getTarget().isDefeated()) {
      Player currentPlayer = town.getPlayers().get(town.getCurrentPlayerIndex());
      view.showMessage("Game Over! " + currentPlayer.getName()
          + " has successfully eliminated the target and won the game!");
    } else if (town.getCurrentTurn() > town.getMaxTurns()) {
      view.showMessage("Game Over! The target has escaped and nobody wins!");
    }
    view.showMessage("++++++++++++++++++++\n");
    this.currentTurn = 1;
    this.quitGame = false;
    town.resetGameState();
  }

  @Override
  public void displayMapInfo() {
    System.out.println("=== Map Information ===");
    System.out.println("Town: " + town.getTownName());
    System.out.println("Target name: " + town.getTargetName()
        + " (Health: " + town.getTargetHealth() + ")");
    System.out.println("Places in the town:");
    System.out.println("--------------------");
    int index = 1;
    for (Place place : town.getPlaces()) {
      System.out.println(index + " " + place.getName());
      place.getItems().forEach(item -> System.out.println(
          "Items in the place: " + item.getName()
              + " (Damage: " + item.getDamage() + ")"));
      index++;
      System.out.println("--------------------");
    }
  }

  @Override
  public void printMap() throws IOException {
    view.showMessage("Printing the map...");

    BufferedImage mapImage = new BufferedImage(
        11 * CELL_SIZE,
        12 * CELL_SIZE,
        BufferedImage.TYPE_INT_ARGB
    );

    Graphics2D g2d = mapImage.createGraphics();
    g2d.setColor(Color.WHITE);
    g2d.fillRect(0, 0, 11 * CELL_SIZE, 12 * CELL_SIZE);
    g2d.setColor(Color.BLACK);

    for (Place place : town.getPlaces()) {
      drawPlace(g2d, place.getName(), place.getRow1(), place.getCol1(),
          place.getRow2(), place.getCol2());
    }

    try {
      ImageIO.write(mapImage, "png", new File("res/map.png"));
      view.showMessage("Map saved as map.png");
    } catch (IOException e) {
      e.printStackTrace();
    }

    g2d.dispose();
  }

  /**
   * Draws a place on the map image.
   */
  private void drawPlace(Graphics2D g2d, String name, int row1, int col1, int row2, int col2) {
    int x = row1 * CELL_SIZE;
    int y = col1 * CELL_SIZE;
    int width = (row2 - row1) * CELL_SIZE;
    int height = (col2 - col1) * CELL_SIZE;
    g2d.drawRect(x, y, width, height);
    g2d.drawString(name, x + width / 4, y + height / 4);
  }
}