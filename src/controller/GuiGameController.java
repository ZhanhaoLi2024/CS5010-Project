package controller;

import controller.command.AddPlayerCommand;
import controller.support.CommandHandler;
import controller.support.EventHandler;
import controller.support.PlayerInfoDTO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import model.town.Town;
import view.GuiView;
import view.View;

/**
 * GUI implementation of the Controller interface.
 * Handles game logic and coordinates between model and view.
 */
public class GuiGameController implements Controller, GuiController {
  private final Town town;
  private final int maxTurns;
  //  private final GameStateManager stateManager;
  private final CommandHandler commandHandler;
  private final EventHandler eventHandler;
  private View view;
  private GuiView guiView;
  private boolean continueGame;

  /**
   * Constructs a new GUI controller.
   *
   * @param gameModel    the game model
   * @param gameMaxTurns maximum number of turns allowed
   */
  public GuiGameController(Town gameModel, int gameMaxTurns) {
    this.town = gameModel;
    this.maxTurns = gameMaxTurns;
//    this.stateManager = new GameStateManager(gameModel, gameMaxTurns);
    this.commandHandler = new CommandHandler();
    this.eventHandler = new EventHandler(this);
  }

  @Override
  public void setView(View view, boolean gui) {
    if (this.view != null) {
      throw new IllegalStateException("View already set");
    }
    this.view = view;
    if (gui) {
      this.guiView = (GuiView) view;
    }
  }

  @Override
  public Town getTown() {
    return this.town;
  }

  @Override
  public void startGame() throws IOException {
    view.initialize();
  }

//
//  private Boolean isValidateCommand(String commandName) {
//    if (!stateManager.isValidCommand(commandName)) {
//      try {
//        view.showMessage("Invalid command at this time: " + commandName);
//      } catch (IOException e) {
//        System.err.println("Error showing message: " + e.getMessage());
//      }
//      return false;
//    } else {
//      return true;
//    }
//  }

  @Override
  public PlayerInfoDTO getCurrentPlayerInfo() throws IOException {
    // Get current player information from town model
    String playerInfo = town.showBasicLocationInfo();

    // Parse the basic location info string
    String[] infoParts = playerInfo.substring(1, playerInfo.length() - 1).split("\\], \\[");

    // Extract player name from first part
    String[] playerPart = infoParts[0].split(",");
    String playerName = playerPart[0];

    // Extract items if they exist (they're in the last part)
    List<String> formattedItems = new ArrayList<>();
    if (infoParts.length > 4) {
      String[] itemsPart = infoParts[4].split(",");
      for (int i = 0; i < itemsPart.length; i += 2) {
        if (i + 1 < itemsPart.length) {
          String itemName = itemsPart[i].trim();
          String damage = itemsPart[i + 1].trim();
          formattedItems.add(itemName + " (Damage: " + damage + ")");
        }
      }
    }

    return new PlayerInfoDTO(
        playerName,
        formattedItems,
        town.getCurrentTurn()
    );
  }

  private void takeTurnForPlayer(String currentPlayerInfo) {
    boolean isComputerController = town.isComputerControllerPlayer();
    if (isComputerController) {
      handleComputerTurn();
    } else {
      handleHumanTurn(currentPlayerInfo);
    }
  }

  private void takeTurn() throws IOException {
    continueGame = true;
    while (continueGame) {
      String playerInfo = town.showBasicLocationInfo();
      takeTurnForPlayer(playerInfo);
      boolean isGameOver = town.isGameOver();
      if (isGameOver) {
        endGame();
      }
    }
  }

  private void endGame() throws IOException {
    continueGame = false;
    if (town.getTarget().isDefeated()) {
      String winner = town.getPlayers().get(town.getCurrentPlayerIndex()).getName();
      guiView.showGuiMessage("Game Over",
          winner + "has successfully eliminated the target and won the game!", "OK");
    } else if (town.getCurrentTurn() > town.getMaxTurns()) {
      guiView.showGuiMessage("Game Over",
          "The target has escaped and nobody wins!", "OK");
    }
    town.resetGameState();
  }

  private boolean handleStartGame() throws IOException {
    int currentPlayersSize = town.getPlayers().size();
    if (currentPlayersSize < 2) {
      guiView.showGuiMessage("Error", "Need at least 2 players to start the game.", "OK");
      return false;
    }
    String playersSize = String.valueOf(currentPlayersSize);
    String gameBasicInfo = "There are " + playersSize + " players in the game.";
    guiView.showGuiMessage("Are you ready", gameBasicInfo, "GO");
    return true;
  }

  @Override
  public boolean executeCommand(String commandName) throws IOException {
    if (commandName.startsWith("ADD_PLAYER")) {
      // Add player command
      String[] parts = commandName.split(" ");
      String commandStringName = parts[0];
      String name = parts[1];
      int place = Integer.parseInt(parts[2]);
      int limit = Integer.parseInt(parts[3]);

      // Execute add player command
      boolean addSuccess =
          new AddPlayerCommand(town, false, name, place, limit).execute();
      if (addSuccess) {
        String message = name + " player added successfully.";
        guiView.showGuiMessage("Result", message, "OK");
        return true;
      } else {
        guiView.showGuiMessage("Error", "Error adding player", "OK");

      }
    } else if (commandName.startsWith("ADD_COMPUTER")) {
      // Add computer player command
      String playersSize = String.valueOf((town.getPlayers().size() + 1));
      String computerName = "Computer" + playersSize;
      Random rand = new Random();
      int startingPlace = rand.nextInt(town.getPlaces().size());
      boolean addSuccess =
          new AddPlayerCommand(town, true, computerName, startingPlace, 5).execute();
      if (addSuccess) {
        String message = computerName + " player added successfully.";
        guiView.showGuiMessage("Result", message, "OK");
        return true;
      } else {
        guiView.showGuiMessage("Error", "Error adding computer player", "OK");
      }
    } else if (commandName.startsWith("START_TURNS")) {
      // Start game command
      boolean startSuccess = handleStartGame();
      if (startSuccess) {
        takeTurn();
        return true;
      }
    } else {
      guiView.showGuiMessage("Error", "Invalid command: " + commandName, "OK");
    }
    return false;
  }
}