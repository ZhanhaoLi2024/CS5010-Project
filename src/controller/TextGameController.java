package controller;

import controller.command.AddPlayerCommand;
import controller.command.LookAroundCommand;
import controller.command.MovePetCommand;
import controller.command.MovePlayerCommand;
import controller.command.PickUpItemCommand;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import model.town.Town;
import view.View;

/**
 * TextGameController implements the Controller interface for a text-based version of the game.
 * This controller manages game flow, user input/output, and coordinates between the model and view
 * following the MVC architecture pattern. It supports both human and computer-controlled players,
 * handles turn management, and processes various game commands.
 */
public class TextGameController implements Controller {
  private final Town town;
  private final int maxTurns;
  private View view;
  private boolean quitGame;
  private boolean continueGame;

  /**
   * Constructs a new TextGameController with specified model, view and game parameters.
   *
   * @param gameTown     the town model representing the game state
   * @param gameView     the view for user interaction
   * @param gameMaxTurns the maximum number of turns allowed in the game
   * @throws IllegalArgumentException if gameTown is null or gameMaxTurns is less than 1
   */
  public TextGameController(Town gameTown, View gameView, int gameMaxTurns) {
    this.town = gameTown;
    this.view = gameView;
    this.maxTurns = gameMaxTurns;
    this.quitGame = false;
  }

  /**
   * Converts a string representation of a list to an actual List object.
   *
   * @param input the string to convert, expected in format "[item1, item2, ...]"
   * @return List containing the parsed items,or empty list if input is null or empty
   */
  private static List<String> convertStringToList(String input) {
    if (input == null || input.equals("[]")) {
      return new ArrayList<>();
    }

    input = input.substring(1, input.length() - 1);

    String[] parts = input.split(",\\s*");

    List<String> resultList = new ArrayList<>();
    Collections.addAll(resultList, parts);

    return resultList;
  }

  @Override
  public void setView(View gameView, boolean gui) {
    if (this.view != null) {
      throw new IllegalStateException("View is already set");
    }
    this.view = gameView;
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

  /**
   * Displays the main menu and handles user input for menu options.
   *
   * @throws IOException if there is an error in input/output operations
   */
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
        addPlayer(false);
        break;
      case 2:
        addPlayer(true);
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

  /**
   * Manages the game turn logic, including displaying current game state,
   * processing player actions, and checking for game end conditions.
   *
   * @throws IOException if there is an error in input/output operations
   */
  private void takeTurn() throws IOException {
    if (town.getPlayers().isEmpty()) {
      view.showMessage("There are no players in the town. Please add a player first.");
      continueGame = false;
    } else if (town.getPlayers().size() < 2) {
      view.showMessage("You need at least two players to start the game.");
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
      view.showMessage("playerInfo: " + playerInfo);
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
          if (parts[1].isEmpty()) {
            itemName = "";
            itemDamage = "";
          } else {
            itemName = parts[0].trim();
            itemDamage = parts[1].trim();
          }

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
      if (itemName.isEmpty()) {
        view.showMessage("No item in this place.");
      } else {
        view.showMessage("Current place item: " + itemName + " (Damage: " + itemDamage + ")");
      }
      takeTurnForPlayer();
      boolean isGameOver = town.isGameOver();
      if (isGameOver) {
        endGame();
      }
    }
  }

  /**
   * Handles game end conditions, displays appropriate messages, and resets game state.
   *
   * @throws IOException if there is an error in input/output operations
   */

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
    this.quitGame = false;
    town.resetGameState();
  }

  /**
   * Processes a single player's turn based on whether they are human or computer-controlled.
   *
   * @throws IOException if there is an error in input/output operations
   */
  private void takeTurnForPlayer() throws IOException {
    boolean isComputerControlled = town.isComputerControllerPlayer();
    if (!isComputerControlled) {
      handleHumanTurn();
    } else {
      handleComputerTurn();
    }
    view.showMessage("Current Target Health: " + town.getTargetHealth());
  }

  /**
   * Handles turn logic for human-controlled players by displaying options
   * and processing their choice.
   *
   * @throws IOException if there is an error in input/output operations
   */
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
        pickUpItem();
        break;
      case 3:
        lookAround();
        break;
      case 4:
        attackTarget();
        break;
      case 5:
        movePet();
        break;
      default:
        view.showMessage("Invalid choice, please try again.");
    }
  }

  /**
   * Processes computer player's attack attempt.
   *
   * @param playerIndex index of the computer player
   * @return String representing the name of the item used for attack.
   * @throws IOException if there is an error in input/output operations
   */
  private String handleComputerAttack(int playerIndex) throws IOException {
    // handle computer attack
    String playerCurrentCarriedItems = town.getPlayerCurrentCarriedItems(playerIndex);
    String targetPlace = town.getTarget().getCurrentPlace().getPlaceNumber();
    String playerCurrentPlace = String.valueOf(town.getPlayerCurrPlaceNumber(playerIndex));
    if (!playerCurrentPlace.equals(targetPlace)) {
      view.showMessage("Computer player is not in the same place as the target. Cannot attack.");
      return "fail";
    }
    boolean isPlayerVisible = town.isPlayerVisible(town.getPlayers().get(playerIndex));
    if (isPlayerVisible) {
      view.showMessage("Computer player is visible to the other players. Cannot attack.");
      return "fail";
    }
    List<String> items = convertStringToList(playerCurrentCarriedItems);
    items.add("Poke Target-1");
    String maxDamageItemName = "";
    int maxDamageItem = 0;
    for (String item : items) {
      String[] chooseItem = item.split("-");
      String itemName = chooseItem[0].trim();
      String itemDamage = chooseItem[1].trim();
      if (Integer.parseInt(itemDamage) > maxDamageItem) {
        maxDamageItem = Integer.parseInt(itemDamage);
        maxDamageItemName = itemName;
      }
    }

    view.showMessage(
        "Computer player chooses to attack with " + maxDamageItemName + " for " + maxDamageItem
            + " damage.");
    return maxDamageItemName;
  }

  /**
   * Processes human player's attack attempt.
   *
   * @param playerIndex index of the human player
   * @return String representing the name of the item used for attack
   * @throws IOException if there is an error in input/output operations
   */
  private String handleHumanAttack(int playerIndex) throws IOException {
    // handle human attack
    String playerCurrentCarriedItems = town.getPlayerCurrentCarriedItems(playerIndex);
    String targetPlace = town.getTarget().getCurrentPlace().getPlaceNumber();
    String playerCurrentPlace = String.valueOf(town.getPlayerCurrPlaceNumber(playerIndex));
    if (!playerCurrentPlace.equals(targetPlace)) {
      view.showMessage("You are not in the same place as the target. You cannot attack.");
      return "fail";
    }
    boolean isPlayerVisible = town.isPlayerVisible(town.getPlayers().get(playerIndex));
    if (isPlayerVisible) {
      view.showMessage("You are visible to the other players. You cannot attack.");
      return "fail";
    }
    List<String> items = convertStringToList(playerCurrentCarriedItems);
    items.add("Poke Target-1");
    view.showMessage("Player's current carried items:");
    int i = 0;
    for (String item : items) {
      view.showMessage(i + ". " + item);
      i++;
    }
    view.showMessage("Enter the item number you want to use for attack:");
    int itemNumber = view.getNumberInput();
    boolean enterRightNumber = false;
    while (!enterRightNumber) {
      if (itemNumber < 0 || itemNumber > items.size()) {
        view.showMessage("Invalid item number. Please enter a valid item number: ");
        itemNumber = view.getNumberInput();
      } else {
        enterRightNumber = true;
      }
    }
    String[] chooseItem = items.get(itemNumber).split("-");
    String itemName = chooseItem[0].trim();
    String itemDamage = chooseItem[1].trim();
    view.showMessage("You choose to attack with " + itemName);
    if (itemName.equals("Poke Target")) {
      view.showMessage("Successfully poke the target in the eye for 1 damage.");
    } else {
      view.showMessage("Attack successfully with " + itemName + " for " + itemDamage + " damage.");
    }
    return itemName;
  }

  /**
   * Processes an attack attempt on the target by the current player.
   * Handles both computer and human player attacks, checking for success conditions
   * and updating game state accordingly.
   *
   * @throws IOException if there is an error in input/output operations
   */
  private void attackTarget() throws IOException {
    int currentPlayerIndex = town.getCurrentPlayerIndex();
    String itemName;
    boolean isComputerControlled = town.getPlayers().get(currentPlayerIndex).isComputerControlled();
    if (isComputerControlled) {
      itemName = handleComputerAttack(currentPlayerIndex);
    } else {
      itemName = handleHumanAttack(currentPlayerIndex);
      if (itemName.equals("fail")) {
        return;
      }
    }

    boolean killSuccess = town.attackTarget(itemName);
    if (killSuccess) {
      view.showMessage("You have successfully eliminated the target!");
      endGame();
    } else {
      int targetHealth = town.getTargetHealth();
      view.showMessage("The target's health is now " + targetHealth);
      town.switchToNextPlayer();
    }
  }

  /**
   * Handles the movement of the pet to a new location specified by the player.
   * Validates the input location and executes the move command.
   *
   * @throws IOException if there is an error in input/output operations
   */
  private void movePet() throws IOException {
    view.showMessage("Please enter the place number you want to move the pet to:");
    int placeNumber = view.getNumberInput();
    boolean enterRightNumber = false;
    while (!enterRightNumber) {
      if (placeNumber < 0 || placeNumber > 20) {
        view.showMessage("Invalid place number. Please enter a valid place number: ");
        placeNumber = view.getNumberInput();
      } else {
        enterRightNumber = true;
      }
    }
    new MovePetCommand(town, placeNumber).execute();
  }

  /**
   * Processes a computer-controlled player's attempt to pick up an item.
   * Automatically selects the item with the highest damage value if available.
   *
   * @throws IOException if there is an error in input/output operations
   */
  private void handleComputerPickUpItem() throws IOException {
    int currentPlayerIndex = town.getCurrentPlayerIndex();
    int currentPlaceNumber = town.getPlayerCurrPlaceNumber(currentPlayerIndex);
    String currentPlace = town.getCurrentPlaceInfo(currentPlaceNumber);
    String[] parts = currentPlace.split(";");
    List<String> items = convertStringToList(parts[1]);
    if (items.isEmpty()) {
      view.showMessage("No item in this place.");
    } else {
      String maxDamageItemName = "";
      int maxDamageItem = 0;
      for (String item : items) {
        String[] chooseItem = item.split("-");
        String itemName1 = chooseItem[0].trim();
        String itemDamage = chooseItem[1].trim();
        if (Integer.parseInt(itemDamage) > maxDamageItem) {
          maxDamageItem = Integer.parseInt(itemDamage);
          maxDamageItemName = itemName1;
        }
      }
      new PickUpItemCommand(town, maxDamageItemName).execute();
    }
  }

  /**
   * Handles a human player's attempt to pick up an item.
   * Displays available items and prompts for selection.
   *
   * @throws IOException if there is an error in input/output operations
   */
  private void handleHumanPickUpItem() throws IOException {
    int currentPlayerIndex = town.getCurrentPlayerIndex();
    int currentPlaceNumber = town.getPlayerCurrPlaceNumber(currentPlayerIndex);
    String currentPlace = town.getCurrentPlaceInfo(currentPlaceNumber);
    String[] parts = currentPlace.split(";");
    String itemName = "";
    view.showMessage("Current place: " + parts[1]);
    List<String> items = convertStringToList(parts[1]);
    if (items.isEmpty()) {
      view.showMessage("No item in this place.");
    } else {
      view.showMessage("Items in this place:");
      int i = 1;
      for (String item : items) {
        itemName = item.split("-")[0].trim();
        String itemDamage = item.split("-")[1].trim();
        view.showMessage(i + ". " + itemName + " (Damage: " + itemDamage + ")");
      }
      view.showMessage("Enter the number of the item you want to pick up:");
      int itemNumber = view.getNumberInput();
      boolean enterRightNumber = false;
      while (!enterRightNumber) {
        if (itemNumber < 1 || itemNumber > items.size()) {
          view.showMessage("Invalid item number. Please enter a valid item number: ");
          itemNumber = view.getNumberInput();
        } else {
          enterRightNumber = true;
        }
      }
      new PickUpItemCommand(town, itemName).execute();
    }
  }

  /**
   * Coordinates the item pickup action for both human and computer-controlled players.
   *
   * @throws IOException if there is an error in input/output operations
   */
  private void pickUpItem() throws IOException {
    int currentPlayerIndex = town.getCurrentPlayerIndex();
    boolean isComputerControlled = town.getPlayers().get(currentPlayerIndex).isComputerControlled();
    if (isComputerControlled) {
      view.showMessage("Computer player picks up an item.");
      handleComputerPickUpItem();
    } else {
      handleHumanPickUpItem();
    }

  }

  /**
   * Implements the look around action for a player, displaying information about
   * the current location and neighboring spaces including items, players, and special characters.
   *
   * @throws IOException if there is an error in input/output operations
   */
  private void lookAround() throws IOException {
    view.showMessage("Looking around...");
    int currentPlayerIndex = town.getCurrentPlayerIndex();
    int currentPlayerPlaceNumber = town.getPlayerCurrPlaceNumber(currentPlayerIndex);

    String currentPlace = town.getCurrentPlaceInfo(currentPlayerPlaceNumber);
    view.showMessage("Current place: "
        + currentPlace);
    String neighbors = town.getCurrentPlaceNeighborsInfo(
        currentPlayerPlaceNumber);
    view.showMessage("Neighbors: " + neighbors);

    // show the current place Info: name, items, players
    view.showMessage("----------");
    String[] parts = currentPlace.split(";");
    String currentPlaceName = parts[0];
    view.showMessage("Current place: " + currentPlaceName);
    String itemName = "";
    String itemDamage = "";
    String currentItem = "";
    List<String> items = convertStringToList(parts[1]);
    if (items.isEmpty()) {
      currentItem = "";
    } else {
      for (String item : items) {
        currentItem = item;
        itemName = currentItem.split("-")[0].trim();
        itemDamage = currentItem.split("-")[1].trim();
      }
    }
    String currentPlayers;
    if (parts[2].isEmpty()) {
      currentPlayers = "No other player in this place";
    } else {
      currentPlayers = parts[2].replace("[", "").replace("]", "").trim();
    }
    view.showMessage("Current place: " + currentPlaceName);
    if (currentItem.isEmpty()) {
      view.showMessage("No item in this place.");
    } else {
      view.showMessage("Current place item: " + itemName + " (Damage: " + itemDamage + ")");
    }
    view.showMessage("Current place players: " + currentPlayers);
    view.showMessage("----------");

    // show the neighbors Info: name, items, players
    neighbors = neighbors.replace("[[", "").replace("]]", "");
    String[] neighborParts = neighbors.split("\\], \\[");
    for (String neighbor : neighborParts) {
      String[] neighborInfo = neighbor.split(";");

      String neighborName = neighborInfo[0].trim();
      String neighborNumber = neighborInfo[1].trim();
      String neighborItem;
      String neighborItemName = "";
      String neighborItemDamage = "";
      List<String> currentItems = convertStringToList(neighborInfo[2]);
      if (currentItems.isEmpty()) {
        neighborItem = "No item in this place.";
      } else {
        for (String item : currentItems) {
          neighborItem = item;
          neighborItemName = neighborItem.split("-")[0].trim();
          neighborItemDamage = neighborItem.split("-")[1].trim();
        }
        neighborItem = neighborItemName + " (Damage: " + neighborItemDamage + ")";
      }
      String neighborPlayers;
      List<String> players = convertStringToList(neighborInfo[3]);
      if (players.isEmpty()) {
        neighborPlayers = "No other player in this place";
      } else {
        neighborPlayers = neighborInfo[3].replace("[", "").replace("]", "").trim();
      }
      if (neighborInfo[5].equals("true")) {
        view.showMessage("Neighboring place: " + neighborName);
        view.showMessage("Pet is in this place.");
      } else {
        view.showMessage("Neighbor: " + neighborName + " (Place Number: " + neighborNumber + ")");
        view.showMessage("Item: " + neighborItem);
        view.showMessage("Players: " + neighborPlayers);
        if (neighborInfo[4].equals("true")) {
          view.showMessage("Target is in this place.");
        }
      }
      view.showMessage("-----");
    }
    view.showMessage("----------");

    new LookAroundCommand(town).execute();
  }

  /**
   * Handles movement logic for computer-controlled players.
   * Prioritizes moving towards the target if visible, otherwise moves randomly.
   *
   * @throws IOException if there is an error in input/output operations
   */
  private void handleComputerMove() throws IOException {
    int currentPlayerIndex = town.getCurrentPlayerIndex();
    int currentPlaceNumber = town.getPlayerCurrPlaceNumber(currentPlayerIndex);

    // Get neighboring places info
    String currentPlaceNeighbour = town.getCurrentPlaceNeighborsInfo(currentPlaceNumber);
    currentPlaceNeighbour = currentPlaceNeighbour.substring(1, currentPlaceNeighbour.length() - 1);
    String[] places = currentPlaceNeighbour.split("\\], \\[");

    // Process neighbor information
    HashSet<Integer> neighborNumbers = new HashSet<>();
    int targetPlaceNumber = -1;

    for (String place : places) {
      place = place.replace("[", "").replace("]", "");
      String[] parts = place.split(";", 5);
      int placeNumber = Integer.parseInt(parts[1].trim());
      neighborNumbers.add(placeNumber);

      // Check if target is in this neighbor
      if (parts[4].equals("true")) {
        targetPlaceNumber = placeNumber;
      }
    }

    // Choose move location
    int newPlaceNumber;
    if (targetPlaceNumber != -1) {
      // Move to target's location if available
      newPlaceNumber = targetPlaceNumber;
    } else {
      // Move to random neighbor
      ArrayList<Integer> neighborsList = new ArrayList<>(neighborNumbers);
      Random random = new Random();
      newPlaceNumber = neighborsList.get(random.nextInt(neighborsList.size()));
    }

    // Execute move
    new MovePlayerCommand(town, currentPlayerIndex, newPlaceNumber).execute();
  }

  /**
   * Handles movement logic for human players.
   * Displays available destinations and processes player choice.
   *
   * @throws IOException if there is an error in input/output operations
   */
  private void handleHumanMove() throws IOException {
    int currentPlayerIndex = town.getCurrentPlayerIndex();
    int currentPlaceNumber = town.getPlayerCurrPlaceNumber(currentPlayerIndex);
    int newPlaceNumber = -1;

    // If the player is human controlled, ask the player where they want to move
    String currentPlaceNeighbour = town.getCurrentPlaceNeighborsInfo(currentPlaceNumber);
    currentPlaceNeighbour = currentPlaceNeighbour.substring(1, currentPlaceNeighbour.length() - 1);
    String[] places = currentPlaceNeighbour.split("\\], \\[");
    HashSet<Integer> neighborNumber = new HashSet<>();
    for (String place : places) {
      place = place.replace("[", "").replace("]", "");

      String[] parts = place.split(";", 5);
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

  /**
   * Coordinates the movement action for both human and computer-controlled players.
   *
   * @throws IOException if there is an error in input/output operations
   */
  private void movePlayer() throws IOException {
    int currentPlayerIndex = town.getCurrentPlayerIndex();
    boolean isComputerControlled = town.getPlayers().get(currentPlayerIndex).isComputerControlled();
    if (isComputerControlled) {
      handleComputerMove();
    } else {
      handleHumanMove();
    }
  }

  /**
   * Implements the decision-making logic for computer-controlled players.
   * Prioritizes actions in the following order:
   * 1. Attack target if possible
   * 2. Pick up items if available
   * 3. Move towards target if carrying items
   * 4. Look around for information
   *
   * @throws IOException if there is an error in input/output operations
   */
  private void handleComputerTurn() throws IOException {
    view.showMessage("Computer player's turn.");

    // First priority: Attack if in same room as target and not visible
    String computerPlayerPlaceNumber = String.valueOf(
        town.getPlayers().get(town.getCurrentPlayerIndex()).getPlayerCurrentPlaceNumber());
    String targetPlaceNumber = town.getTarget().getCurrentPlace().getPlaceNumber();
    if (computerPlayerPlaceNumber.equals(targetPlaceNumber)
        && !town.isPlayerVisible(town.getPlayers().get(town.getCurrentPlayerIndex()))) {
      view.showMessage("Computer player attempts to attack the target.");

      attackTarget();

      return;
    }

    // Second priority: Pick up items if available in the current place
    if (!town.getPlaceByNumber(
            town.getPlayers().get(town.getCurrentPlayerIndex()).getPlayerCurrentPlaceNumber())
        .getItems().isEmpty()
        && town.getPlayers().get(town.getCurrentPlayerIndex()).getCurrentCarriedItems().size()
        < town.getPlayers().get(town.getCurrentPlayerIndex()).getCarryLimit()) {
      view.showMessage("Computer player picks up an item.");

      pickUpItem();

      return;
    }

    // Third priority: Move towards target if carrying items
    if (!town.getPlayers().get(town.getCurrentPlayerIndex()).getCurrentCarriedItems().isEmpty()) {
      view.showMessage("Computer player moves to find the target.");

      movePlayer();

      return;
    }

    // Fourth priority: Look around to gather information
    view.showMessage("Computer player looks around.");

    lookAround();
  }

  /**
   * Displays and handles the player information menu options.
   *
   * @throws IOException if there is an error in input/output operations
   */
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

  /**
   * Displays information about all players currently in the game.
   *
   * @throws IOException if there is an error in input/output operations
   */
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

  /**
   * Displays information about a specific player based on user input.
   *
   * @throws IOException if there is an error in input/output operations
   */
  private void showSpecificPlayerInfo() throws IOException {
    view.showMessage("Enter the player's name:");
    String playerName = view.getStringInput();
    String playerInfo = town.getPlayerByName(playerName);
    String[] parts = playerInfo.split(",");
    view.showMessage(parts[0] + " is at " + parts[1] + " with carry limit " + parts[2]);
  }

  /**
   * Prompts for and validates the carry limit for a new player.
   *
   * @return the validated carry limit between 1 and 5
   * @throws IOException if there is an error in input/output operations
   */
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

  /**
   * Handles the creation and addition of a computer-controlled player.
   * Automatically assigns a name and random starting location.
   *
   * @throws IOException if there is an error in input/output operations
   */
  private void handleAddComputerPlayer() throws IOException {
    int currentPlayerSize = town.getPlayers().size() + 1;
    String computerPlayerName = "Computer-" + currentPlayerSize;
    Random random = new Random();
    int currentPlaceSize = town.getPlaces().size();
    int randomPlace = random.nextInt(currentPlaceSize);
    new AddPlayerCommand(town, true, computerPlayerName, randomPlace, 5).execute();
    view.showMessage(computerPlayerName + " player added successfully.");
  }

  /**
   * Handles the creation and addition of human-controlled players.
   * Prompts for player details and allows adding multiple players.
   *
   * @throws IOException if there is an error in input/output operations
   */
  private void handleAddHumanPlayer() throws IOException {
    boolean addPlayerContinue = true;
    String firstPlayerName = showAddNewPlayerName();
    int firstPlaceNumber = showAddNewPlayerStartingPlaceNumber();
    int firstCarryLimit = showNewPlayerCarryLimit();
    try {
      new AddPlayerCommand(town, false, firstPlayerName, firstPlaceNumber,
          firstCarryLimit).execute();
      view.showMessage(firstPlayerName + " player added successfully.");
      view.showMessage(firstPlayerName + " is at " + firstPlaceNumber + " with carry limit "
          + firstCarryLimit);
    } catch (IOException e) {
      throw new RuntimeException(e);
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
          view.showMessage(humanPlayerName + " player added successfully.");
          view.showMessage(humanPlayerName + " is at " + humanPlaceNumber + " with carry limit "
              + humanCarryLimit);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      } else if ("no".equalsIgnoreCase(choice)) {
        addPlayerContinue = false;
      } else {
        view.showMessage("Invalid input. Please enter 'yes' or 'no'.");
      }
    }
  }

  /**
   * Coordinates the addition of new players to the game.
   *
   * @param isComputer true if adding a computer-controlled player, false for human player
   * @throws IOException if there is an error in input/output operations
   */
  private void addPlayer(boolean isComputer) throws IOException {
    if (isComputer) {
      handleAddComputerPlayer();
    } else {
      handleAddHumanPlayer();
    }
  }

  /**
   * Prompts for and validates a new player's name.
   *
   * @return the validated player name
   * @throws IOException if there is an error in input/output operations
   */
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

  /**
   * Prompts for and validates a new player's starting place number.
   *
   * @return the validated place number between 0 and 20
   * @throws IOException if there is an error in input/output operations
   */
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
  public boolean executeCommand(String command) {
    // TODO Auto-generated method stub
    return false;
  }
}
