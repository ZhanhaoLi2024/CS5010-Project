package controller;

import controller.command.AddPlayerCommand;
import controller.command.LookAroundCommand;
import controller.command.MovePetCommand;
import controller.command.MovePlayerCommand;
import controller.command.PickUpItemCommand;
import controller.support.PlayerInfoDto;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import model.town.Town;
import view.GuiView;
import view.View;

/**
 * GUI implementation of the Controller interface.
 * Handles game logic and coordinates between model and view.
 */
public class GuiGameController implements Controller {
  private final Town town;
  private View view;
  private GuiView guiView;
  private String newPlaceName;
  private int newPlaceNumber;

  /**
   * Constructs a new GUI controller.
   *
   * @param gameModel the game model
   */
  public GuiGameController(Town gameModel) {
    this.town = gameModel;
  }

  /**
   * Converts a string to a list of strings.
   *
   * @param input the input string
   * @return the list of strings
   */
  private static List<String> convertStringToList(String input) {
    if (input == null || "[]".equals(input)) {
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
      throw new IllegalStateException("View already set");
    }
    this.view = gameView;
    if (gui) {
      this.guiView = (GuiView) gameView;
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

  /**
   * Handles the computer player's turn.
   *
   * @throws IOException if an I/O error occurs
   */
  private void handleComputerTurn() throws IOException {
    // First priority: Attack the target if in the same place as target and not visible
    String computerPlayerPlaceNumber = String.valueOf(
        town.getPlayers().get(town.getCurrentPlayerIndex()).getPlayerCurrentPlaceNumber());
    String targetPlaceNumber = town.getTarget().getCurrentPlace().getPlaceNumber();

    System.out.println("Computer player place number: " + computerPlayerPlaceNumber);
    System.out.println("Target place number: " + targetPlaceNumber);
    if (computerPlayerPlaceNumber.equals(targetPlaceNumber)
        && !town.isPlayerVisible(town.getPlayers().get(town.getCurrentPlayerIndex()))) {
      // Computer player attempts to attack the target
      attackTarget();
      return;
    }

    // Second priority: Pick up an item if available in the current place
    if (!town.getPlaceByNumber(
            town.getPlayers().get(town.getCurrentPlayerIndex()).getPlayerCurrentPlaceNumber())
        .getItems().isEmpty()
        && town.getPlayers().get(town.getCurrentPlayerIndex()).getCurrentCarriedItems().size()
        < town.getPlayers().get(town.getCurrentPlayerIndex()).getCarryLimit()) {
      // Computer player picks up an item
      System.out.println("Computer player picks up an item");
      pickUpItem();
      return;
    }

    // Third priority: Move towards target if carrying items
    if (!town.getPlayers().get(town.getCurrentPlayerIndex()).getCurrentCarriedItems().isEmpty()) {
      movePlayer();
      return;
    }

    // Fourth priority: Look around to gather information
    lookAround();
  }

  /**
   * Handles the player's turn.
   *
   * @throws IOException if an I/O error occurs
   */
  private void takeTurnForPlayer() throws IOException {
    boolean isComputerController = town.isComputerControllerPlayer();
    if (isComputerController) {
      handleComputerTurn();
    }
  }

  /**
   * Updates the current player's information.
   *
   * @param currentPlayerInfo the current player's information
   */
  private void updateCurrentPlayerInfo(String currentPlayerInfo) {
    System.out.println("Current player info: " + currentPlayerInfo);
    currentPlayerInfo = currentPlayerInfo.replace("[[", "[").replace("]]", "]");
    String[] parts = currentPlayerInfo.split("], \\[");
    // current player info
    parts[0] = parts[0].replace("[", "");
    String[] playerInfoParts = parts[0].split(",");
    final String currentPlayerName = playerInfoParts[0];
    final String currentPlayerPlace = playerInfoParts[1];
    String playerItemList = playerInfoParts[3];
    if (playerItemList.endsWith("|")) {
      playerItemList = playerItemList.substring(0, playerItemList.length() - 1);
    }
    String[] list = playerItemList.split("\\|");
    final List<String> currentPlayerItems = Arrays.asList(list);

    // other players in the same place
    parts[1] = parts[1].replace("[", "").replace("]", "");

    // target info
    parts[2] = parts[2].replace("[", "").replace("]", "");
    String[] targetInfo = parts[2].split(",");
    String targetName = targetInfo[0];
    String targetPlace = targetInfo[1];
    String targetHealth = targetInfo[2];
    final String targetInfoString = targetName + "," + targetPlace + "," + targetHealth;

    // current pet info
    parts[3] = parts[3].replace("[", "").replace("]", "");

    // current place items
    if (parts.length > 4) {
      parts[4] = parts[4].replace("[", "").replace("]", "");
    }

    PlayerInfoDto playerInfo =
        new PlayerInfoDto(town.getCurrentTurn(), currentPlayerName, currentPlayerItems,
            currentPlayerPlace, targetInfoString
        );
    guiView.updatePlayerInfo(playerInfo);
  }

  /**
   * Handles the move player command.
   */
  private void takeTurn() throws IOException {
    String playerInfo = town.showBasicLocationInfo();
    System.out.println("Current player: " + playerInfo);
    updateCurrentPlayerInfo(playerInfo);
    takeTurnForPlayer();
    boolean isGameOver = town.isGameOver();
    System.out.println("Is game over: " + isGameOver);
    if (isGameOver) {
      endGame();
    }
  }

  /**
   * Ends the game.
   *
   * @throws IOException if an I/O error occurs
   */
  private void endGame() throws IOException {
    if (town.getTarget().isDefeated()) {
      String winner = town.getPlayers().get(town.getCurrentPlayerIndex()).getName();
      guiView.showGuiMessage("Game Over",
          winner + " has successfully eliminated the target and won the game!", "OK",
          () -> guiView.resetGame());
    } else if (town.getCurrentTurn() > town.getMaxTurns()) {
      guiView.showGuiMessage("Game Over",
          "The target has escaped and nobody wins!", "OK", () -> guiView.resetGame());
    }
    town.resetGameState();
  }

  /**
   * Handles the start game command.
   *
   * @return true if the game can start, false otherwise
   */
  private boolean handleStartGame() {
    int currentPlayersSize = town.getPlayers().size();
    if (currentPlayersSize < 2) {
      guiView.showGuiMessage("Error", "Need at least 2 players to start the game.", "OK");
      return false;
    }
    String playersSize = String.valueOf(currentPlayersSize);
    String gameBasicInfo = "There are " + playersSize + " players in the game.";
    guiView.showGuiMessage("Are you ready", gameBasicInfo, "GO", () -> {
      try {
        takeTurn();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
    return true;
  }

  /**
   * Handles the move player command.
   *
   * @throws IOException if an I/O error occurs
   */
  private void handleComputerPickUpItem() throws IOException {
    int currentPlayerIndex = town.getCurrentPlayerIndex();
    int currentPlaceNumber = town.getPlayerCurrPlaceNumber(currentPlayerIndex);
    String currentPlace = town.getCurrentPlaceInfo(currentPlaceNumber);
    String[] parts = currentPlace.split(";");
    List<String> items = convertStringToList(parts[1]);
    System.out.println("Items: " + items);
    if (items.isEmpty()) {
      view.showMessage("No item in this place.");
    } else {
      String maxDamageItemName = "";
      int maxDamageItem = 0;
      System.out.print("Items: " + items);
      for (String item : items) {
        System.out.println("Item: " + item);
        String[] chooseItem = item.split("-");
        String itemName1 = chooseItem[0].trim();
        String itemDamage = chooseItem[1].trim();
        if (Integer.parseInt(itemDamage) > maxDamageItem) {
          maxDamageItem = Integer.parseInt(itemDamage);
          maxDamageItemName = itemName1;
        }
      }
      System.out.println("Max damage item: " + maxDamageItemName);
      new PickUpItemCommand(town, maxDamageItemName).execute();
      boolean isGameOver = town.isGameOver();
      if (isGameOver) {
        endGame();
      } else {
        takeTurn();
      }
    }
  }

  /**
   * Handles the pickup item command.
   *
   * @throws IOException if an I/O error occurs
   */
  private void handleHumanPickUpItem() throws IOException {
    StringBuilder showItemInfo = new StringBuilder();
    int maxItemNumber;
    int currentPlayerIndex = town.getCurrentPlayerIndex();
    int currentPlaceNumber = town.getPlayerCurrPlaceNumber(currentPlayerIndex);
    String currentPlace = town.getCurrentPlaceInfo(currentPlaceNumber);
    String[] parts = currentPlace.split(";");
    String itemName;
    List<String> items = convertStringToList(parts[1]);
    if (items.isEmpty()) {
      showItemInfo.append("No item in this place." + "\n");
    } else {
      showItemInfo.append("Items in this place:" + "\n");
      int i = 1;
      for (String item : items) {
        itemName = item.split("-")[0].trim();
        String itemDamage = item.split("-")[1].trim();
        showItemInfo.append(i).append(". ").append(itemName).append(" (Damage: ").append(itemDamage)
            .append(")").append("\n");
      }
    }
    maxItemNumber = items.size();
    if (maxItemNumber == 0) {
      guiView.showGuiMessage("Pick Up Item", "No item in this place", "OK", () -> {
        try {
          takeTurn();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    } else if (town.getPlayers().get(town.getCurrentPlayerIndex()).getCurrentCarriedItems().size()
        >= town.getPlayers().get(town.getCurrentPlayerIndex()).getCarryLimit()) {
      guiView.showGuiMessage("Pick Up Item", "Your inventory is full", "OK");
    } else {
      guiView.showGuiNumberMessage("Pick Up Item", showItemInfo.toString(),
              "OK",
              1, maxItemNumber).thenAccept(itemNumber -> {
                String item = items.get(itemNumber - 1);
                String[] itemParts = item.split("-");
                String itemName1 = itemParts[0].trim();
                try {
                  new PickUpItemCommand(town, itemName1).execute();
                  takeTurn();
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
              }
          )
          .exceptionally(e -> {
            guiView.showGuiMessage("Error", "Invalid item number", "OK");
            return null;
          });
    }
  }

  /**
   * Handles the pickup item command.
   *
   * @throws IOException if an I/O error occurs
   */
  private void pickUpItem() throws IOException {
    int currentPlayerIndex = town.getCurrentPlayerIndex();
    boolean isComputerController = town.getPlayers().get(currentPlayerIndex).isComputerControlled();
    if (isComputerController) {
      handleComputerPickUpItem();
    } else {
      handleHumanPickUpItem();
    }
  }

  /**
   * Handles the computer look around command.
   *
   * @throws IOException if an I/O error occurs
   */
  private void handleComputerLookAround() throws IOException {
    new LookAroundCommand(town).execute();
    boolean isGameOver = town.isGameOver();
    if (isGameOver) {
      endGame();
    } else {
      takeTurn();
    }
  }

  /**
   * Handles the human look around command.
   *
   * @throws IOException if an I/O error occurs
   */
  private void handleHumanLookAround() throws IOException {
    int currentPlayerIndex = town.getCurrentPlayerIndex();
    int currentPlayerPlaceNumber = town.getPlayerCurrPlaceNumber(currentPlayerIndex);
    String currentPlace = town.getCurrentPlaceInfo(currentPlayerPlaceNumber);

    StringBuilder lookAroundInfo;
    // Show current place info: name, items, players
    String[] parts = currentPlace.split(";");
    String currentPlaceName = parts[0];
    lookAroundInfo = new StringBuilder("Current place: " + currentPlaceName + "\n");
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

    if (currentItem.isEmpty()) {
      lookAroundInfo.append("No item in this place.\n");
    } else {
      lookAroundInfo.append("Current place item: ").append(itemName).append(" (Damage: ")
          .append(itemDamage).append(")\n");
    }
    lookAroundInfo.append("Current place players: ").append(currentPlayers).append("\n");

    // Show neighbors info: name, items, players
    String neighbors = town.getCurrentPlaceNeighborsInfo(currentPlayerPlaceNumber);
    neighbors = neighbors.replace("[[", "").replace("]]", "");
    String[] neighborParts = neighbors.split("], \\[");
    System.out.println("Neighbor parts: " + Arrays.toString(neighborParts));
    for (String neighbor : neighborParts) {
      String[] neighborInfo = neighbor.split(";");

      String neighborName = neighborInfo[0].trim();
      final String neighborNumber = neighborInfo[1].trim();
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
      System.out.println("Neighbor name: " + neighborName);
      System.out.println("Neighbor pet: " + neighborInfo[5]);
      if (neighborInfo[5].equals("true")) {
        lookAroundInfo.append("Neighboring place: ").append(neighborName).append("\n");
        lookAroundInfo.append("Pet is in this place.\n");
      } else {
        lookAroundInfo.append("Neighboring place: ").append(neighborName).append(" (Place NUmber: ")
            .append(neighborNumber).append(")\n");
        lookAroundInfo.append("Item: ").append(neighborItem).append("\n");
        lookAroundInfo.append("Players: ").append(neighborPlayers).append("\n");
        if (neighborInfo[4].equals("true")) {
          lookAroundInfo.append("Target is in this place.\n");
        }
      }
    }
    guiView.showGuiMessage("Look Around", lookAroundInfo.toString(), "OK", () -> {
      try {
        town.lookAround();
        takeTurn();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }

  /**
   * Handles the look around command.
   *
   * @throws IOException if an I/O error occurs
   */
  private void lookAround() throws IOException {
    int currentPlayerIndex = town.getCurrentPlayerIndex();
    boolean isComputerControlled = town.getPlayers().get(currentPlayerIndex).isComputerControlled();
    if (isComputerControlled) {
      handleComputerLookAround();
    } else {
      handleHumanLookAround();
    }
  }

  /**
   * Handles the computer move command.
   *
   * @throws IOException if an I/O error occurs
   */
  private void handleComputerMove() throws IOException {
    int currentPlayerIndex = town.getCurrentPlayerIndex();
    int currentPlaceNumber = town.getPlayerCurrPlaceNumber(currentPlayerIndex);

    // Get neighboring places info
    String currentPlaceNeighbour = town.getCurrentPlaceNeighborsInfo(currentPlaceNumber);
    currentPlaceNeighbour = currentPlaceNeighbour.substring(1, currentPlaceNeighbour.length() - 1);
    String[] places = currentPlaceNeighbour.split("], \\[");

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
    int newPlaceNumberInfo;
    if (targetPlaceNumber != -1) {
      // Move to target's location if available
      newPlaceNumberInfo = targetPlaceNumber;
    } else {
      // Move to random neighbor
      ArrayList<Integer> neighborsList = new ArrayList<>(neighborNumbers);
      Random random = new Random();
      newPlaceNumberInfo = neighborsList.get(random.nextInt(neighborsList.size()));
    }

    // Execute move
    new MovePlayerCommand(town, currentPlayerIndex, newPlaceNumberInfo).execute();

    boolean isGameOver = town.isGameOver();
    if (isGameOver) {
      endGame();
    } else {
      takeTurn();
    }
  }

  /**
   * Handles the human move command.
   */
  private void handleHumanMove() {
    int currentPlayerIndex = town.getCurrentPlayerIndex();
    String moveInfo = "You want to move to " + newPlaceName + "?" + "\n";
    guiView.showGuiMessage("Move Player", moveInfo, "OK", () -> {
      try {
        new MovePlayerCommand(town, currentPlayerIndex, newPlaceNumber).execute();
        takeTurn();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }

  /**
   * Moves the player.
   *
   * @throws IOException if an I/O error occurs
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
   * Handles the move pet command.
   */
  private void movePet() {
    guiView.showGuiNumberMessage("Move Pet", "Enter the place number to move the pet to", "OK", 1,
            20)
        .thenAccept(placeNumber -> {
          try {
            new MovePetCommand(town, placeNumber).execute();
            takeTurn();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        })
        .exceptionally(e -> {
          guiView.showGuiMessage("Error", "Invalid place number", "OK");
          return null;
        });
  }

  /**
   * Handles the human attack target command.
   *
   * @throws IOException if an I/O error occurs
   */
  private void handleHumanAttackTarget() throws IOException {
    int currentPlayerIndex = town.getCurrentPlayerIndex();
    String playerCurrentCarriedItems = town.getPlayerCurrentCarriedItems(currentPlayerIndex);
    String targetPlaceNumber = town.getTarget().getCurrentPlace().getPlaceNumber();
    String playerCurrentPlaceNumber =
        String.valueOf(town.getPlayerCurrPlaceNumber(currentPlayerIndex));
    if (!playerCurrentPlaceNumber.equals(targetPlaceNumber)) {
      guiView.showGuiMessage("Error", "Target is not in the same place as you", "OK");
      return;
    }
    boolean isPlayerVisible = town.isPlayerVisible(town.getPlayers().get(currentPlayerIndex));
    if (isPlayerVisible) {
      guiView.showGuiMessage("Error", "You are visible to the other players. You cannot attack",
          "OK");
      return;
    }
    final AtomicBoolean killSuccess = new AtomicBoolean(false);
    List<String> playerItems = convertStringToList(playerCurrentCarriedItems);
    playerItems.add("Poke Target-1");
    StringBuilder showItemInfo = new StringBuilder();
    showItemInfo.append("Items you have: " + "\n");
    int i = 1;
    for (String item : playerItems) {
      showItemInfo.append(i).append(". ").append(item).append("\n");
      i++;
    }
    showItemInfo.append("Enter the item number you want to use to attack the target: \n");
    guiView.showGuiNumberMessage("Attack Target", showItemInfo.toString(), "OK", 1,
            playerItems.size())
        .thenAccept(itemNumber -> {
          String[] itemsParts = playerItems.get(itemNumber - 1).split("-");
          String itemName = itemsParts[0].trim();
          String itemDamage = itemsParts[1].trim();
          try {
            killSuccess.set(town.attackTarget(itemName));
            String showAttackResult = "";
            showAttackResult += "You used " + itemName + " to attack the target." + "\n";
            if ("Poke Target".equals(itemName)) {
              showAttackResult += "You hit the target and caused 1 damage." + "\n";
            } else {
              showAttackResult += "You hit the target and caused " + itemDamage + " damage." + "\n";
            }
            System.out.println("Kill success: " + killSuccess);
            if (killSuccess.get()) {
              endGame();
            } else {
              town.switchToNextPlayer();
              takeTurn();
            }
            guiView.showGuiMessage("Attack Result", showAttackResult, "OK");
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        })
        .exceptionally(e -> {
          guiView.showGuiMessage("Error", "Invalid item number", "OK");
          return null;
        });
  }

  /**
   * Handles the computer attack target command.
   *
   * @throws IOException if an I/O error occurs
   */
  private void handleComputerAttackTarget() throws IOException {
    int currentPlayerIndex = town.getCurrentPlayerIndex();
    String playerCurrentCarriedItems = town.getPlayerCurrentCarriedItems(currentPlayerIndex);
    final String currentPlayerName = town.getPlayers().get(currentPlayerIndex).getName();
    List<String> playerItems = convertStringToList(playerCurrentCarriedItems);
    playerItems.add("Poke Target-1");
    String maxDamageItemName = "";
    int maxDamage = 0;
    for (String item : playerItems) {
      String[] itemParts = item.split("-");
      String itemName = itemParts[0];
      int itemDamage = Integer.parseInt(itemParts[1]);
      if (itemDamage > maxDamage) {
        maxDamage = itemDamage;
        maxDamageItemName = itemName;
      }
    }
    view.showMessage(
        "Computer player chooses to attack with " + maxDamageItemName + " for " + maxDamage
            + " damage.");
    boolean killSuccess = town.attackTarget(maxDamageItemName);
    if (killSuccess) {
      guiView.showGuiMessage("Game Over",
          currentPlayerName + " player has successfully eliminated the target.",
          "OK", () -> {
            try {
              endGame();
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          });
    } else {
      town.switchToNextPlayer();
      boolean isGameOver = town.isGameOver();
      if (isGameOver) {
        endGame();
      } else {
        takeTurn();
      }
    }
  }

  /**
   * Handles the attack target command.
   *
   * @throws IOException if an I/O error occurs
   */
  private void attackTarget() throws IOException {
    int currentPlayerIndex = town.getCurrentPlayerIndex();
    boolean isComputerController = town.getPlayers().get(currentPlayerIndex).isComputerControlled();
    if (isComputerController) {
      handleComputerAttackTarget();
    } else {
      handleHumanAttackTarget();
    }
  }

  /**
   * Handles the player's action.
   *
   * @param action the player's action
   * @throws IOException if an I/O error occurs
   */
  private void handlePlayerAction(String action) throws IOException {
    switch (action) {
      case "MOVE":
        // Move command
        movePlayer();
        break;
      case "LOOK":
        // Look command
        lookAround();
        break;
      case "PICK":
        // Pick command
        pickUpItem();
        break;
      case "ATTACK":
        // Attack command
        attackTarget();
        break;
      case "PETMOVE":
        // Pet move command
        movePet();
        break;
      default:
        guiView.showGuiMessage("Error", "Invalid command: " + action, "OK");
    }
  }

  @Override
  public boolean executeCommand(String commandName) throws IOException {
    if (commandName == null) {
      throw new IllegalArgumentException("Command cannot be null");
    }

    if (town.isGameOver()
        && !commandName.startsWith("START_TURNS") // 允许重新开始游戏
        && !commandName.startsWith("ADD_PLAYER")  // 允许添加玩家
        && !commandName.startsWith("ADD_COMPUTER")) { // 允许添加电脑玩家
      guiView.showGuiMessage("Error", "Game is over. Start a new game to continue.", "OK");
      return false;
    }
    if (commandName.startsWith("ADD_PLAYER")) {
      // Add player command
      String[] parts = commandName.split(" ");
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
      String computerName;
      if (town.getPlayers().isEmpty()) {
        computerName = "Computer1";
      } else {
        String playersSize = String.valueOf((town.getPlayers().size() + 1));
        computerName = "Computer" + playersSize;
      }
      Random rand = new Random();
      int startingPlace = rand.nextInt(19) + 1;
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
      return handleStartGame();
    } else if (commandName.startsWith("MOVE")) {
      // Move command
      handleMovePlayer(commandName);
    } else if (commandName.startsWith("LOOK")) {
      // Look command
      handlePlayerAction("LOOK");
      return true;
    } else if (commandName.startsWith("PICK")) {
      // Pick command
      handlePlayerAction("PICK");
    } else if (commandName.startsWith("ATTACK")) {
      // Attack command
      handlePlayerAction("ATTACK");
    } else if (commandName.startsWith("PETMOVE")) {
      // Pet move command
      handlePlayerAction("PETMOVE");

    } else {
      guiView.showGuiMessage("Error", "Invalid command: " + commandName, "OK");
    }
    return false;
  }

  /**
   * Handles the move player command.
   *
   * @param command the player command
   * @throws IOException if an I/O error occurs
   */
  private void handleMovePlayer(String command) throws IOException {
    String[] parts = command.split(",");
    if (parts.length != 3) {
      throw new IllegalArgumentException("Invalid move command format");
    }
    int destinationNumber = Integer.parseInt(parts[2]);
    if (destinationNumber < 1 || destinationNumber > town.getPlaces().size()) {
      throw new IllegalArgumentException("Invalid place number");
    }
    newPlaceName = parts[1];
    newPlaceNumber = Integer.parseInt(parts[2]);

    handlePlayerAction("MOVE");
  }
}