package controller;

import controller.command.AddPlayerCommand;
import java.io.IOException;
import java.util.List;
import model.town.Town;
import view.GameView;

public class TextController implements Controller {
  private final Town town;
  private final int maxTurns;
  private GameView view;
  private int currentTurn;
  private boolean quitGame;
  private boolean continueGame;
  private boolean isGui;

  public TextController(Town gameTown, GameView gameView, int gameMaxTurns) {
    this.town = gameTown;
    this.view = gameView;
    this.maxTurns = gameMaxTurns;
    this.currentTurn = 1;
    this.quitGame = false;
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
  public void startGame() throws IOException {
    if (view == null) {
      throw new IllegalStateException("View is not set");
    }
    view.initialize();
    view.showMessage("Welcome to the game! You have " + maxTurns + " turns.");

    while (!quitGame) {
      displayMainMenu();
    }
  }

  private void displayMainMenu() throws IOException {
    view.showMessage("Please choose an option:");
    view.showMessage("1. Add Human-controller player");
    view.showMessage("2. Add Computer-controller player");
    view.showMessage("3. Display player information");
    view.showMessage("4. Start turn");
    view.showMessage("0. Quit game");

    int choice = view.getNumberInput();
    switch (choice) {
      case 1:
        addPlayer();
        break;
      case 2:
        addComputerPlayer();
        break;
      case 3:
        showPlayerInfo();
        break;
      case 4:
        break;
      case 0:
        quitGame = true;
        break;
      default:
        view.showMessage("Invalid choice. Please enter a number between 1 and 5.");
    }
  }

  private void showPlayerInfo() throws IOException {
    boolean showPlayerInfo = true;
    while (showPlayerInfo) {
      view.showMessage("Please choose an option:");
      view.showMessage("1. Show all players info");
      view.showMessage("2. Show specific player info");
      view.showMessage("0. Exit");

      int choice = view.getNumberInput();
      switch (choice) {
        case 1:
          showAllPlayersInfo();
          break;
        case 2:
          showSpecificPlayerInfo();
          break;
        case 0:
          showPlayerInfo = false;
          break;
        default:
          view.showMessage("Invalid choice. Please enter a number between 1 and 5.");
      }
    }
  }

  private void showAllPlayersInfo() throws IOException {
    List<String> players = town.getAllPlayersInfo();
    if (players.isEmpty()) {
      view.showMessage("There are no players in the town.");
    } else {
      view.showMessage("--------------------");
      view.showMessage("All players info:");
      for (String player : players) {
        String[] parts = player.split(",");
        view.showMessage(parts[0] + " is at " + parts[1] + " with carry limit " + parts[2]);
        view.showMessage("--------------------");
      }
    }
  }

  private void showSpecificPlayerInfo() throws IOException {
    view.showMessage("Enter the player's name:");
    String playerName = view.getStringInput();
    String playerInfo = town.getPlayerByName(playerName);
    String[] parts = playerInfo.split(",");
    view.showMessage(parts[0] + " is at " + parts[1] + " with carry limit " + parts[2]);
  }

  private void addComputerPlayer() throws IOException {
    new AddPlayerCommand(town, true, "Computer Player", 1, 1).execute();
  }

  private int showNewPlayerCarryLimit() throws IOException {
    while (true) {
      try {
        view.showMessage("Enter the carry limit (1-5):\n");
        int carryLimit = view.getNumberInput();

        if (carryLimit < 1 || carryLimit > 5) {
          view.showMessage("Carry limit must be between 1 and 5. Please try again.\n");
          continue;
        }

        return carryLimit;
      } catch (NumberFormatException e) {
        view.showMessage("Invalid input. Please enter a valid number.\n");
      }
    }
  }

  private void addPlayer() throws IOException {
    boolean addPlayerContinue = true;
    String firstPlayerName = showAddNewPlayerName();
    int firstPlaceNumber = showAddNewPlayerStartingPlaceNumber();
    int firstCarryLimit = showNewPlayerCarryLimit();
    try {
      new AddPlayerCommand(town, false, firstPlayerName, firstPlaceNumber,
          firstCarryLimit).execute();
    } catch (IOException e) {
      e.printStackTrace();
    }
    while (addPlayerContinue) {
      view.showMessage("Do you want to add another player? (yes/no)");
      String choice = view.getStringInput();
      if ("yes".equalsIgnoreCase(choice)) {
        String humanPlayerName = showAddNewPlayerName();
        int humanPlaceNumber = showAddNewPlayerStartingPlaceNumber();
        int humanCarryLimit = showNewPlayerCarryLimit();
        try {
          new AddPlayerCommand(town, false, humanPlayerName, humanPlaceNumber,
              humanCarryLimit).execute();
        } catch (IOException e) {
          e.printStackTrace();
        }
      } else if ("no".equalsIgnoreCase(choice)) {
        addPlayerContinue = false;
      } else {
        view.showMessage("Invalid input. Please enter 'yes' or 'no'.");
      }
    }
  }

  private String showAddNewPlayerName() throws IOException {
    while (true) {
      view.showMessage("Enter the player's name:\n");
      String playerName = view.getStringInput();
      if (playerName.isEmpty()) {
        view.showMessage("Name cannot be empty. Please try again.\n");
        continue;
      }
      return playerName;
    }
  }

  private int showAddNewPlayerStartingPlaceNumber() throws IOException {
    while (true) {
      try {
        view.showMessage("Enter the place number:\n");
        int placeNumber = view.getNumberInput();
        if (placeNumber < 0) {
          view.showMessage("Place number cannot be negative. Please try again.\n");
          continue;
        }
        if (placeNumber > 20) {
          view.showMessage("Place number cannot be greater than 20. Please try again.\n");
          continue;
        }
        return placeNumber;
      } catch (NumberFormatException e) {
        view.showMessage("Invalid input. Please enter a valid number.\n");
      }
    }
  }

  @Override
  public Town getTown() {
    return this.town;
  }

  @Override
  public void executeCommand(String command) throws IOException {
    // TODO Auto-generated method stub
  }
}
