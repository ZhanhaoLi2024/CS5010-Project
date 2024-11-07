package controller;

import controller.command.AddPlayerCommand;
import controller.command.AttackTargetCommand;
import controller.command.Command;
import controller.command.DisplayPlaceInfoCommand;
import controller.command.DisplayPlayerInfoCommand;
import controller.command.LookAroundCommand;
import controller.command.MovePetCommand;
import controller.command.MovePlayerCommand;
import controller.command.PickUpItemCommand;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import model.dto.GameStateDTO;
import model.dto.ItemDTO;
import model.dto.PlaceDTO;
import model.dto.PlayerDTO;
import model.dto.TargetDTO;
import model.item.Item;
import model.observer.GameObserver;
import model.place.Place;
import model.town.Town;
import view.GameView;

/**
 * The GameController coordinates interactions between the Town model and GameView.
 * It handles user input and updates the view accordingly while keeping the model and view decoupled.
 */
public class GameController implements Controller, GameObserver {
  private final Town townModel;
  private final GameView view;
  private boolean isGameRunning;

  /**
   * Constructs a new GameController with the specified model and view.
   *
   * @param townModel the town model
   * @param view      the game view
   */
  public GameController(Town townModel, GameView view) {
    this.townModel = townModel;
    this.view = view;
    this.isGameRunning = true;
    townModel.addObserver(this);
  }

  /**
   * Starts the game and handles the main game loop.
   *
   * @throws IOException if there's an error with I/O operations
   */
  @Override
  public void startGame() throws IOException {
    view.displayMessage("\n++++++++++++++++++++");
    view.displayMessage("Welcome to the game! You have " + townModel.getMaxTurns() + " turns.");

    // Add initial computer player
    executeCommand(new AddPlayerCommand(townModel, true));

    while (isGameRunning) {
      handleMainMenu();
    }
  }

  /**
   * Handles the main menu interactions.
   *
   * @throws IOException if there's an error with I/O operations
   */
  private void handleMainMenu() throws IOException {
    view.displayMainMenu();
    String input = view.getInput();

    try {
      int choice = Integer.parseInt(input);
      switch (choice) {
        case 1:
          displayMapInfo();
          break;
        case 2:
          executeCommand(new AddPlayerCommand(townModel, false));
          break;
        case 3:
          executeCommand(new DisplayPlayerInfoCommand(townModel));
          break;
        case 4:
          executeCommand(new DisplayPlaceInfoCommand(townModel));
          break;
        case 5:
          handleGamePlay();
          break;
        case 6:
          displayMap();
          break;
        case 0:
          view.displayMessage("Exiting...");
          isGameRunning = false;
          break;
        default:
          view.displayMessage("Invalid choice, please try again.");
      }
    } catch (NumberFormatException e) {
      view.displayMessage("Invalid input. Please enter a number.");
    }
  }

  /**
   * Handles the gameplay loop when a game is in progress.
   *
   * @throws IOException if there's an error with I/O operations
   */
  private void handleGamePlay() throws IOException {
    if (townModel.getPlayers().size() < 2) {
      view.displayMessage("You need at least two players to start the game.");
      return;
    }

    boolean gameInProgress = true;
    while (gameInProgress && !townModel.isGameOver()) {
      // Display current game state
      GameStateDTO gameState = townModel.getGameState();
      view.displayGameState(gameState.getCurrentTurn(), gameState.getMaxTurns(),
          getPlayerInfoStrings(gameState.getCurrentPlayerIndex()));

      // Handle player turn
      if (townModel.isComputerControllerPlayer()) {
        handleComputerTurn();
      } else {
        handleHumanTurn();
      }

      if (townModel.isGameOver()) {
        handleGameEnd();
        gameInProgress = false;
      }
    }
  }

  /**
   * Handles a turn for a computer-controlled player.
   *
   * @throws IOException if there's an error with I/O operations
   */
  private void handleComputerTurn() throws IOException {
    view.displayMessage("Computer player's turn.");
    PlayerDTO player = townModel.getCurrentPlayerInfo();

    // Computer AI logic
    if (canAttackTarget(player)) {
      executeCommand(new AttackTargetCommand(townModel));
    } else if (canPickUpItem(player)) {
      executeCommand(new PickUpItemCommand(townModel));
    } else if (shouldMove(player)) {
      executeCommand(new MovePlayerCommand(townModel));
    } else {
      executeCommand(new LookAroundCommand(townModel));
    }
  }

  /**
   * Handles a turn for a human player.
   *
   * @throws IOException if there's an error with I/O operations
   */
  private void handleHumanTurn() throws IOException {
    view.displayMessage("Choose your action:");
    view.displayMessage("1. Move");
    view.displayMessage("2. Pick up item");
    view.displayMessage("3. Look around");
    view.displayMessage("4. Attack target");
    view.displayMessage("5. Move pet");

    String input = view.getInput();
    try {
      int choice = Integer.parseInt(input);
      switch (choice) {
        case 1:
          executeCommand(new MovePlayerCommand(townModel));
          break;
        case 2:
          executeCommand(new PickUpItemCommand(townModel));
          break;
        case 3:
          executeCommand(new LookAroundCommand(townModel));
          break;
        case 4:
          executeCommand(new AttackTargetCommand(townModel));
          break;
        case 5:
          executeCommand(new MovePetCommand(townModel));
          break;
        default:
          view.displayMessage("Invalid choice.");
      }
    } catch (NumberFormatException e) {
      view.displayMessage("Invalid input. Please enter a number.");
    }
  }

  /**
   * Executes a command and handles any exceptions.
   *
   * @param command the command to execute
   * @throws IOException if there's an error with I/O operations
   */
  private void executeCommand(Command command) throws IOException {
    try {
      command.execute();
    } catch (IllegalArgumentException | IllegalStateException e) {
      view.displayMessage("Error: " + e.getMessage());
    }
  }

  /**
   * Handles the end of the game.
   *
   * @throws IOException if there's an error with I/O operations
   */
  private void handleGameEnd() throws IOException {
    GameStateDTO gameState = townModel.getGameState();
    if (townModel.getTarget().isDefeated()) {
      PlayerDTO winner = gameState.getPlayers().get(gameState.getCurrentPlayerIndex());
      view.displayMessage("Game Over! " + winner.getName() + " has eliminated the target!");
    } else {
      view.displayMessage("Game Over! The target has escaped!");
    }
    view.displayMessage("++++++++++++++++++++\n");
  }

  // Helper methods for computer player decision making
  private boolean canAttackTarget(PlayerDTO player) {
    return String.valueOf(player.getCurrentPlaceNumber())
        .equals(townModel.getTarget().getCurrentPlace().getPlaceNumber())
        &&
        !townModel.isPlayerVisible(townModel.getPlayers().get(townModel.getCurrentPlayerIndex()));
  }

  private boolean canPickUpItem(PlayerDTO player) {
    return !townModel.getPlaceByNumber(player.getCurrentPlaceNumber()).getItems().isEmpty()
        && player.getItems().size() < player.getCarryLimit();
  }

  private boolean shouldMove(PlayerDTO player) {
    return !player.getItems().isEmpty();
  }

  private void displayMapInfo() throws IOException {
    GameStateDTO gameState = townModel.getGameState();
    view.displayMapInfo(
        townModel.getTownName(),
        gameState.getTarget().getName(),
        gameState.getTarget().getHealth(),
        getPlaceInfoStrings()
    );
  }

  private void displayMap() throws IOException {
    // Map display logic would go here
    view.displayMessage("Map functionality will be implemented in the view layer");
  }

  private List<String> getPlayerInfoStrings(int playerIndex) {
    PlayerDTO player = townModel.getCurrentPlayerInfo();
    List<String> info = new ArrayList<>();
    info.add("Current player: " + player.getName());
    info.add("Location: " + townModel.getPlaceByNumber(player.getCurrentPlaceNumber()).getName());
    info.add("Items: " + player.getItems().size() + "/" + player.getCarryLimit());
    return info;
  }

  private List<String> getPlaceInfoStrings() {
    List<String> placeInfo = new ArrayList<>();
    for (Place place : townModel.getPlaces()) {
      StringBuilder info = new StringBuilder(place.getName());
      if (!place.getItems().isEmpty()) {
        info.append("\n  Items:");
        for (Item item : place.getItems()) {
          info.append("\n    - ").append(item.getName())
              .append(" (Damage: ").append(item.getDamage()).append(")");
        }
      }
      placeInfo.add(info.toString());
    }
    return placeInfo;
  }

  @Override
  public void onGameStateChanged(GameStateDTO gameState) {
    try {
      // Update view with new game state
      view.displayGameState(
          gameState.getCurrentTurn(),
          gameState.getMaxTurns(),
          getPlayerInfoStrings(gameState.getCurrentPlayerIndex())
      );

      // Check game end conditions
      if (gameState.isGameOver()) {
        handleGameEnd();
      }
    } catch (IOException e) {
      // Handle exception
    }
  }

  @Override
  public void onPlaceStateChanged(PlaceDTO placeInfo) {
    try {
      // Update view with new place state
      List<String> placeInfoStrings = new ArrayList<>();
      placeInfoStrings.add(formatPlaceInfo(placeInfo));
      view.displayPlaceInfo(placeInfoStrings);
    } catch (IOException e) {
      // Handle exception
    }
  }

  @Override
  public void onPlayerStateChanged(PlayerDTO playerInfo) {
    try {
      // Update view with new player state
      List<String> playerInfoStrings = new ArrayList<>();
      playerInfoStrings.add(formatPlayerInfo(playerInfo));
      view.displayPlayerInfo(playerInfoStrings);
    } catch (IOException e) {
      // Handle exception
    }
  }

  @Override
  public void onTargetStateChanged(TargetDTO targetInfo) {
    try {
      // Update view with new target state
      view.displayMessage(String.format(
          "Target %s health: %d",
          targetInfo.getName(),
          targetInfo.getHealth()
      ));
    } catch (IOException e) {
      // Handle exception
    }
  }

  // Helper method to format place information
  private String formatPlaceInfo(PlaceDTO place) {
    StringBuilder info = new StringBuilder(place.getName());
    info.append("\nItems:");
    for (ItemDTO item : place.getItems()) {
      info.append("\n  - ").append(item.getName())
          .append(" (Damage: ").append(item.getDamage()).append(")");
    }
    info.append("\nPlayers here: ").append(String.join(", ", place.getPlayerNames()));
    return info.toString();
  }

  // Helper method to format player information
  private String formatPlayerInfo(PlayerDTO player) {
    return String.format(
        "%s at %s (Items: %d/%d)",
        player.getName(),
        townModel.getPlaceByNumber(player.getCurrentPlaceNumber()).getName(),
        player.getItems().size(),
        player.getCarryLimit()
    );
  }

  //  /**
//   * Prints the map of the town to a PNG file.
//   */
//  @Override
//  public void printMap() throws IOException {
//    output.append("Printing the map...\n");
//
//    int width = 11 * CELL_SIZE;
//    int height = 12 * CELL_SIZE;
//    BufferedImage mapImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//
//    Graphics2D g2d = mapImage.createGraphics();
//
//    g2d.setColor(Color.WHITE);
//    g2d.fillRect(0, 0, width, height);
//
//    g2d.setColor(Color.BLACK);
//
//    for (Place place : town.getPlaces()) {
//      int row1 = place.getRow1();
//      int col1 = place.getCol1();
//      int row2 = place.getRow2();
//      int col2 = place.getCol2();
//      drawPlace(g2d, place.getName(), row1, col1, row2, col2);
//    }
//
//    try {
//      ImageIO.write(mapImage, "png", new File("res/map.png"));
//      output.append("Map saved as map.png\n");
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//
//    g2d.dispose();
//  }
//
//  /**
//   * Draws a place on the map image.
//   */
//  private void drawPlace(Graphics2D g2d, String name, int row1, int col1, int row2, int col2) {
//    int y = col1 * CELL_SIZE;
//    int x = row1 * CELL_SIZE;
//    int width = (row2 - row1) * CELL_SIZE;
//    int height = (col2 - col1) * CELL_SIZE;
//    g2d.drawRect(x, y, width, height);
//    g2d.drawString(name, x + width / 4, y + height / 4);
//  }
}