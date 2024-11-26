package controller;

import controller.command.AddPlayerCommand;
import controller.command.MovePlayerCommand;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
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
        takeTurn();
        break;
      case 0:
        quitGame = true;
        break;
      default:
        view.showMessage("Invalid choice. Please enter a number between 1 and 5.");
    }
  }

  private void takeTurn() throws IOException {
    if (town.getPlayers().isEmpty()) {
      view.showMessage("There are no players in the town. Please add a player first.");
      continueGame = false;
    } else {
      continueGame = true;
    }
    view.showMessage("Starting turn");
    view.showMessage(
        "Target: " + town.getTargetName() + " (Health: " + town.getTargetHealth() + ") in "
            + town.getTarget().getCurrentPlace().getName());
    String petCurrentInfo = town.petCurrentInfo();
    String[] petInfo = petCurrentInfo.split(",");
    view.showMessage(petInfo[0] + "is in " + petInfo[1]);
    while (continueGame) {
      view.showMessage(town.getCurrentTurn() + " of " + maxTurns);
      String playerInfo = town.showBasicLocationInfo();
      playerInfo = playerInfo.substring(1, playerInfo.length() - 1);
      String[] items = playerInfo.split("\\], \\[");
      String playerName = "";
      String playerPlace = "";
      List<String> otherPlayers = new ArrayList<>();
      String targetPlace = "";
      String targetHealth = "";
      String petPlace = "";
      String itemName = "";
      String itemDamage = "";
      for (int i = 0; i < items.length; i++) {
        String item = items[i].replace("[", "").replace("]", "");
        String[] parts = item.split(",");
        view.showMessage(item);

        if (i == 0 && parts.length >= 2) {
          playerName = parts[0].trim();
          playerPlace = parts[1].trim();
        } else if (i == 1) {
          for (String name : parts) {
            otherPlayers.add(name.trim());
          }
        } else if (i == 2 && parts.length >= 2) {
          targetPlace = parts[1].trim();
          targetHealth = parts[2].trim();
        } else if (i == 3 && parts.length >= 2) {
          petPlace = parts[1].trim();
        } else if (i == 4 && parts.length == 2) {
          itemName = parts[0].trim();
          itemDamage = parts[1].trim();
        }
      }
      view.showMessage("Hi " + playerName + ", you Current place: " + playerPlace);
      if (otherPlayers.isEmpty()) {
        view.showMessage("No other players in this place.");
      } else {
        view.showMessage("Other players: " + otherPlayers);
      }
      view.showMessage("Target Current place: " + targetPlace + " (Health: " + targetHealth + ")");
      view.showMessage("Pet Current place: " + petPlace);
      view.showMessage("Current place item: " + itemName + " (Damage: " + itemDamage + ")");

      takeTurnForPlayer();
      boolean isGameOver = town.isGameOver();
      if (isGameOver) {
        endGame();
      }
    }
  }

  private void endGame() throws IOException {
    continueGame = false;
    if (town.getTarget().isDefeated()) {
      view.showMessage("Game Over! ");
      String winner = town.getPlayers().get(town.getCurrentPlayerIndex()).getName();
      view.showMessage(
          winner + " has successfully eliminated the target and won the game!");
    } else if (town.getCurrentTurn() > town.getMaxTurns()) {
      view.showMessage("Game Over! The target has escaped and nobody wins!");
    }
    view.showMessage("++++++++++++++++++++");
    this.currentTurn = 1;
    this.quitGame = false;
    town.resetGameState();
  }

  private void takeTurnForPlayer() throws IOException {
    boolean isComputerControlled = town.isComputerControllerPlayer();
    if (!isComputerControlled) {
      handleHumanTurn();
    } else {
      handleComputerTurn();
    }
  }

  private void handleHumanTurn() throws IOException {
    view.showMessage("Please choose an option:");
    view.showMessage("1. Move player");
    view.showMessage("2. Pick up item");
    view.showMessage("3. Look around");
    view.showMessage("4. Attack target");
    view.showMessage("5. Move pet");

    int choice = 0;
    try {
      choice = view.getNumberInput();
    } catch (NumberFormatException e) {
      view.showMessage("Invalid input. Please enter a number.");
    }

    switch (choice) {
      case 1:
        movePlayer();
        break;
      case 2:
//        new PickUpItemCommand(town).execute();
        break;
      case 3:
        lookAround();
        break;
      case 4:
//        new AttackTargetCommand(town, output).execute();
        break;
      case 5:
//        new MovePetCommand(town, output, scanner).execute();
        break;
      default:
        view.showMessage("Invalid choice, please try again.");
    }
  }

  private void lookAround() throws IOException {
    view.showMessage("Looking around...");
    int currentPlayerIndex = town.getCurrentPlayerIndex();

    new MovePlayerCommand(town, currentPlayerIndex,
        town.getPlayerCurrPlaceNumber(currentPlayerIndex)).execute();
  }

  private void handleComputerMove(int currentPlayerIndex, int currentPlaceNumber)
      throws IOException {
    int newPlaceNumber = -1;

    // If target is in a neighboring space, move there
    String currentPlaceNeighbour = town.getCurrentPlaceNeighborsInfo(currentPlaceNumber);
    currentPlaceNeighbour = currentPlaceNeighbour.substring(1, currentPlaceNeighbour.length() - 1);
    String[] places = currentPlaceNeighbour.split("\\], \\[");
    HashSet<Integer> neighborNumber = new HashSet<>();
    Boolean targetInNeighbor = false;
    for (String place : places) {
      place = place.replace("[", "").replace("]", "");

      String[] parts = place.split(",", 5);
      Integer placeNumber = Integer.parseInt(parts[1].trim());
      neighborNumber.add(placeNumber);
      if (parts[4].equals("true")) {
        targetInNeighbor = true;
        newPlaceNumber = placeNumber;
      }
    }
    if (targetInNeighbor) {
      new MovePlayerCommand(town, currentPlayerIndex, newPlaceNumber).execute();
    } else {
      // Move to a random neighboring space
      ArrayList<Integer> list = new ArrayList<>(neighborNumber);
      Random random = new Random();
      int randomIndex = random.nextInt(list.size());
      newPlaceNumber = list.get(randomIndex);
      new MovePlayerCommand(town, currentPlayerIndex, newPlaceNumber).execute();
    }
  }

  private void movePlayer() throws IOException {
    int currentPlayerIndex = town.getCurrentPlayerIndex();
    int currentPlaceNumber = town.getPlayerCurrPlaceNumber(currentPlayerIndex);
    int newPlaceNumber = -1;

    // If the player is computer controlled, move the player automatically
    if (town.getPlayers().get(currentPlayerIndex).isComputerControlled()) {
      handleComputerMove(currentPlayerIndex, currentPlaceNumber);
      return;
    }

    // If the player is human controlled, ask the player where they want to move
    String currentPlaceNeighbour = town.getCurrentPlaceNeighborsInfo(currentPlaceNumber);
    currentPlaceNeighbour = currentPlaceNeighbour.substring(1, currentPlaceNeighbour.length() - 1);
    String[] places = currentPlaceNeighbour.split("\\], \\[");
    view.showMessage("Current place's Neighbor:(PlaceName - PlaceNumber)");
    HashSet<Integer> neighborNumber = new HashSet<>();
    for (String place : places) {
      place = place.replace("[", "").replace("]", "");

      String[] parts = place.split(",", 5);
      String placeName = parts[0].trim();
      Integer placeNumber = Integer.parseInt(parts[1].trim());
      neighborNumber.add(placeNumber);

      view.showMessage(placeName + "-" + placeNumber);
    }
    boolean enterRightNumber = false;
    while (!enterRightNumber) {
      view.showMessage("Enter the place number you want to move to:");
      int useEnter = view.getNumberInput();
      if (neighborNumber.contains(useEnter)) {
        newPlaceNumber = useEnter;
        enterRightNumber = true;
      } else {
        view.showMessage("Invalid place number. Please enter a valid place number.");
      }
    }

    new MovePlayerCommand(town, currentPlayerIndex, newPlaceNumber).execute();

  }

  private void handleComputerTurn() throws IOException {
    view.showMessage("Computer player's turn.");
//    Player computerPlayer = town.getPlayers().get(town.getCurrentPlayerIndex());
//    Place currentPlace = town.getPlaceByNumber(
//        town.getPlayers().get(town.getCurrentPlayerIndex()).getPlayerCurrentPlaceNumber());

    // First priority: Attack if in same room as target and not visible
    String computerPlayerPlaceNumber = String.valueOf(
        town.getPlayers().get(town.getCurrentPlayerIndex()).getPlayerCurrentPlaceNumber());
    if (computerPlayerPlaceNumber.equals(
        town.getTarget().getCurrentPlace().getPlaceNumber())
        && !town.isPlayerVisible(town.getPlayers().get(town.getCurrentPlayerIndex()))) {
      view.showMessage("Computer player attempts to attack the target.");
//      new AttackTargetCommand(town, output).execute();
      return;
    }

    // Second priority: Pick up items if available and has space
    if (!town.getPlaceByNumber(
            town.getPlayers().get(town.getCurrentPlayerIndex()).getPlayerCurrentPlaceNumber())
        .getItems().isEmpty()
        && town.getPlayers().get(town.getCurrentPlayerIndex()).getCurrentCarriedItems().size()
        < town.getPlayers().get(town.getCurrentPlayerIndex()).getCarryLimit()) {
      view.showMessage("Computer player picks up an item.");
//      new PickUpItemCommand(town).execute();
      return;
    }

    // Third priority: Move towards target if carrying items
    if (!town.getPlayers().get(town.getCurrentPlayerIndex()).getCurrentCarriedItems().isEmpty()) {
      view.showMessage("Computer player moves to find the target.");
//      new MovePlayerCommand(town).execute();
      return;
    }

    // Fourth priority: Look around to gather information
    view.showMessage("Computer player looks around.");
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
