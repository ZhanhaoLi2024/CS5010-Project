package controller;

import controller.command.AddPlayerCommand;
import controller.command.Command;
import controller.support.CommandHandler;
import controller.support.EventHandler;
import controller.support.GameStateManager;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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

  private void handleGameOver() {
  }

  private void processTurn() throws IOException {
    //
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
    // Move player to a new location
  }

  private void handleLookAction() throws IOException {
    // Look around the current location
  }

  private void handlePickupAction() throws IOException {
    // Pick up an item from the current location
  }

  private void handleAttackAction() throws IOException {
    // Attack the target character
  }

  private void handlePetMoveAction() throws IOException {
    // Move the pet to a new location
  }

  private void handleStartTurn() throws IOException {
    // Start the turn
  }

  private void handleShowPlayerInfo() throws IOException {
    // Show player information
  }

  private void handleError(String message, Exception e) {
    // Handle error
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