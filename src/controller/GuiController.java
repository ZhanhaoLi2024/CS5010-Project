package controller;

import controller.command.AddPlayerCommand;
import controller.command.Command;
import controller.support.CommandHandler;
import controller.support.EventHandler;
import controller.support.GameStateManager;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import model.player.Player;
import model.town.Town;
import view.GameView;

/**
 * GUI implementation of the Controller interface.
 * Handles game logic and coordinates between model and view.
 */
public class GuiController implements Controller {
  private final Town town;
  private final int maxTurns;
  private final GameStateManager stateManager;
  private final CommandHandler commandHandler;
  private final EventHandler eventHandler;
  private GameView view;

  /**
   * Constructs a new GUI controller.
   *
   * @param gameModel    the game model
   * @param gameMaxTurns maximum number of turns allowed
   */
  public GuiController(Town gameModel, int gameMaxTurns) {
    this.town = gameModel;
    this.maxTurns = gameMaxTurns;
    this.stateManager = new GameStateManager(gameModel, gameMaxTurns);
    this.commandHandler = new CommandHandler();
    this.eventHandler = new EventHandler(this);

    // Initialize game commands after construction
    initializeCommands();
  }

  private void initializeCommands() {
    Map<String, Command> commands = new HashMap<>();

    // Player management commands
    commands.put("ADD_PLAYER", () -> {
      try {
        handleAddHumanPlayer();
      } catch (IOException e) {
        handleError("Error adding player", e);
      }
    });

    commands.put("ADD_COMPUTER", () -> {
      try {
        handleAddComputerPlayer();
      } catch (IOException e) {
        handleError("Error adding computer player", e);
      }
    });

    // Game action commands
    commands.put("MOVE", () -> {
      try {
        handleMoveAction();
      } catch (IOException e) {
        handleError("Error moving player", e);
      }
    });

    commands.put("LOOK", () -> {
      try {
        handleLookAction();
      } catch (IOException e) {
        handleError("Error looking around", e);
      }
    });

    commands.put("PICK", () -> {
      try {
        handlePickupAction();
      } catch (IOException e) {
        handleError("Error picking up item", e);
      }
    });

    commands.put("ATTACK", () -> {
      try {
        handleAttackAction();
      } catch (IOException e) {
        handleError("Error attacking", e);
      }
    });

    commands.put("MOVE_PET", () -> {
      try {
        handlePetMoveAction();
      } catch (IOException e) {
        handleError("Error moving pet", e);
      }
    });

    // Game flow commands
    commands.put("START_TURN", () -> {
      try {
        handleStartTurn();
      } catch (IOException e) {
        handleError("Error starting turn", e);
      }
    });

    commands.put("SHOW_INFO", () -> {
      try {
        handleShowPlayerInfo();
      } catch (IOException e) {
        handleError("Error showing player info", e);
      }
    });

    commandHandler.setCommands(commands);
  }

  @Override
  public void setView(GameView gameView, boolean gui) {
    if (this.view != null) {
      throw new IllegalStateException("View already set");
    }
    this.view = gameView;
  }

  @Override
  public Town getTown() {
    return this.town;
  }

  @Override
  public void startGame() throws IOException {
    validateState();
    initializeGame();
    startGameLoop();
  }

  private void validateState() {
    if (view == null) {
      throw new IllegalStateException("View not set");
    }
  }

  private void initializeGame() throws IOException {
    view.initialize();
    view.showWelcomeScreen();
    stateManager.reset();
  }

  private void startGameLoop() throws IOException {
    while (!stateManager.isGameOver()) {
      processTurn();
    }
    handleGameOver();
  }

  private void processTurn() throws IOException {
    if (town.getPlayers().isEmpty()) {
      return; // Wait for player additions
    }

    Player currentPlayer = stateManager.getCurrentPlayer();
    updatePlayerDisplay(currentPlayer);

    if (currentPlayer.isComputerControlled()) {
      handleComputerTurn(currentPlayer);
    } else {
      handleHumanTurn(currentPlayer);
    }

    stateManager.nextTurn();
  }

  private void updatePlayerDisplay(Player currentPlayer) throws IOException {
    var currentPlace = stateManager.getCurrentPlayerPlace();
    view.updatePlayerInfo(currentPlayer, currentPlace);
  }

  private void handleComputerTurn(Player player) throws IOException {
    if (!player.getCurrentCarriedItems().isEmpty()) {
      town.executeComputerAttack(player);
    } else {
//      town.movePlayer();
    }
    view.showMessage("Computer player " + player.getName() + " completed their turn.");
  }

  private void handleHumanTurn(Player player) throws IOException {
    String message = String.format("Player %s's turn in %s",
        player.getName(),
        stateManager.getCurrentPlayerPlace().getName());
    view.showMessage(message);
//    town.showBasicLocationInfo();
  }

  // Player Action Handlers
  private void handleAddHumanPlayer() throws IOException {
    String[] inputs = getPlayerInputs();
    if (inputs == null) {
      return;
    }

    String name = inputs[0];
    int place = Integer.parseInt(inputs[1]);
    int limit = Integer.parseInt(inputs[2]);

//    town.addPlayer(name, place, limit);
    new AddPlayerCommand(town, false, name, place, limit).execute();
    view.showMessage("Added human player: " + name);
  }

  private void handleAddComputerPlayer() throws IOException {
    new AddPlayerCommand(town, true, "Computer", 0, 0);
    view.showMessage("Added computer player successfully");
  }

  private String[] getPlayerInputs() throws IOException {
    String name = view.getStringInput();
    if (name == null || name.trim().isEmpty()) {
      view.showMessage("Invalid name");
      return null;
    }

    try {
      int place = view.getNumberInput();
      int limit = view.getNumberInput();
      return new String[] {name, String.valueOf(place), String.valueOf(limit)};
    } catch (NumberFormatException e) {
      view.showMessage("Invalid number input");
      return null;
    }
  }

  private void handleMoveAction() throws IOException {
    if (!stateManager.canMove()) {
      view.showMessage("Cannot move at this time");
      return;
    }
//    town.movePlayer();
    view.showMessage("Move completed");
  }

  private void handleLookAction() throws IOException {
    if (!stateManager.canLook()) {
      view.showMessage("Cannot look at this time");
      return;
    }
    town.lookAround();
  }

  private void handlePickupAction() throws IOException {
    if (!stateManager.canPickup()) {
      view.showMessage("Cannot pick up at this time");
      return;
    }
//    town.pickUpItem();
  }

  private void handleAttackAction() throws IOException {
    if (!stateManager.canAttack()) {
      view.showMessage("Cannot attack at this time");
      return;
    }
    Player currentPlayer = stateManager.getCurrentPlayer();
    town.handleHumanAttack(currentPlayer);
  }

  private void handlePetMoveAction() throws IOException {
    if (!stateManager.canMovePet()) {
      view.showMessage("Cannot move pet at this time");
      return;
    }
    // Implement pet movement logic
  }

  private void handleStartTurn() throws IOException {
    if (!stateManager.canStartTurn()) {
      view.showMessage("Cannot start turn at this time");
      return;
    }
    town.startTurn();
  }

  private void handleShowPlayerInfo() throws IOException {
    Player currentPlayer = stateManager.getCurrentPlayer();
//    view.showMessage(town.getOnePlayerAndRoomInfo(currentPlayer.getName()));
  }

  private void handleGameOver() throws IOException {
    String message = stateManager.isMaxTurnsReached() ?
        "Game Over! Maximum turns reached." :
        "Game Over! Target has been defeated!";
    view.showMessage(message);
    view.close();
  }

  private void handleError(String message, Exception e) {
    try {
      view.showMessage(message + ": " + e.getMessage());
    } catch (IOException ex) {
      System.err.println("Error showing message: " + ex.getMessage());
    }
  }

  private Boolean isValidateCommand(String commandName) {
    if (!stateManager.isValidCommand(commandName)) {
      try {
        view.showMessage("Invalid command at this time: " + commandName);
      } catch (IOException e) {
        System.err.println("Error showing message: " + e.getMessage());
      }
      return false;
    } else {
      return true;
    }
  }

  @Override
  public void executeCommand(String commandName) throws IOException {
    System.out.println("Executing command-2: " + commandName);
    if (commandName.startsWith("ADD_PLAYER")) {
      // Parse command parameters
      String[] parts = commandName.split(" ");
      String commandStringName = parts[0];
      if (isValidateCommand(commandStringName)) {
        String name = parts[1];
        int place = Integer.parseInt(parts[2]);
        int limit = Integer.parseInt(parts[3]);

        System.out.println(
            "Adding player: " + name + " at place " + place + " with limit " + limit);
        // Execute add player command
        new AddPlayerCommand(town, false, name, place, limit).execute();
        view.showMessage("Player " + name + " added successfully");
      }
    } else if (commandName.startsWith("ADD_COMPUTER")) {
      if (isValidateCommand("ADD_COMPUTER")) {
        System.out.println("Adding computer player");
        new AddPlayerCommand(town, true, "Computer", 0, 0).execute();
        view.showMessage("Computer player added successfully");
      }
    }
  }
}