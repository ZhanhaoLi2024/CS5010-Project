package controller;

import controller.command.AddPlayerCommand;
import controller.command.MovePetCommand;
import controller.command.MovePlayerCommand;
import controller.command.PickUpItemCommand;
import controller.support.CommandHandler;
import controller.support.EventHandler;
import controller.support.PlayerInfoDTO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
public class GuiGameController implements Controller, GuiController {
  private final Town town;
  private final int maxTurns;
  //  private final GameStateManager stateManager;
  private final CommandHandler commandHandler;
  private final EventHandler eventHandler;
  private View view;
  private GuiView guiView;
  private boolean continueGame;
  private String newPlaceName;
  private int newPlaceNumber;

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

  private void handleComputerTurn() throws IOException {
    takeTurn();
  }

  private void takeTurnForPlayer() throws IOException {
    boolean isComputerController = town.isComputerControllerPlayer();
    if (isComputerController) {
      handleComputerTurn();
    }
  }

  private void updateCurrentPlayerInfo(String currentPlayerInfo) {
    currentPlayerInfo = currentPlayerInfo.replace("[[", "[").replace("]]", "]");
    String[] parts = currentPlayerInfo.split("], \\[");
    // current player info
    parts[0] = parts[0].replace("[", "");
    String[] playerInfoParts = parts[0].split(",");
    final String currentPlayerName = playerInfoParts[0];
    final String currentPlayerPlace = playerInfoParts[1];
    String currentPlayerMaxCarry = playerInfoParts[2];
    String playerItemList = playerInfoParts[3];
    if (playerItemList.endsWith("|")) {
      playerItemList = playerItemList.substring(0, playerItemList.length() - 1);
    }
    String[] list = playerItemList.split("\\|");
    final List<String> currentPlayerItems = Arrays.asList(list);

    // other players in the same place
    parts[1] = parts[1].replace("[", "").replace("]", "");
    String[] otherPlayers = parts[1].split(", ");
    final List<String> otherPlayersList = new ArrayList<>(Arrays.asList(otherPlayers));

    // target info
    parts[2] = parts[2].replace("[", "").replace("]", "");
    String[] targetInfo = parts[2].split(",");
    String targetName = targetInfo[0];
    String targetPlace = targetInfo[1];
    String targetHealth = targetInfo[2];
    final String targetInfoString = targetName + "," + targetPlace + "," + targetHealth;

    // current pet info
    parts[3] = parts[3].replace("[", "").replace("]", "");
    String[] currentPetInfo = parts[3].split(",");
    String petName = currentPetInfo[0];
    String petPlace = currentPetInfo[1];
    String petInfoString = petName + "," + petPlace;

    // current place items
    List<String> currentPlaceItemsList = new ArrayList<>();
    if (parts.length > 4) {
      parts[4] = parts[4].replace("[", "").replace("]", "");
      String[] currentPlaceItems = parts[4].split(",");
      String currentPlaceItemName = currentPlaceItems[0];
      String currentPlaceItemDemage = currentPlaceItems[1];
      currentPlaceItemsList.add(currentPlaceItemName + "(" + currentPlaceItemDemage + ")");
    }

    PlayerInfoDTO playerInfo =
        new PlayerInfoDTO(town.getCurrentTurn(), currentPlayerName, currentPlayerItems,
            currentPlayerPlace, targetInfoString, otherPlayersList, currentPlaceItemsList,
            petInfoString);
    guiView.updatePlayerInfo(playerInfo);
  }
//  }

  private void takeTurn() throws IOException {
    String playerInfo = town.showBasicLocationInfo();
    System.out.println("Current player: " + playerInfo);
    updateCurrentPlayerInfo(playerInfo);
    takeTurnForPlayer();
    boolean isGameOver = town.isGameOver();
    if (isGameOver) {
      endGame();
    }
  }

  private void endGame() throws IOException {
    continueGame = false;
    if (town.getTarget().isDefeated()) {
      String winner = town.getPlayers().get(town.getCurrentPlayerIndex()).getName();
      guiView.showGuiMessage("Game Over",
          winner + " has successfully eliminated the target and won the game!", "OK", () -> {
            guiView.resetGame();
          });
    } else if (town.getCurrentTurn() > town.getMaxTurns()) {
      guiView.showGuiMessage("Game Over",
          "The target has escaped and nobody wins!", "OK", () -> {
            guiView.resetGame();
          });
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
    guiView.showGuiMessage("Are you ready", gameBasicInfo, "GO", () -> {
      try {
        takeTurn();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    return true;
  }

  private void pickUpItem() throws IOException {
    String showItemInfo = "";
    int maxItemNumber = 0;
    int currentPlayerIndex = town.getCurrentPlayerIndex();
    int currentPlaceNumber = town.getPlayerCurrPlaceNumber(currentPlayerIndex);
    String currentPlace = town.getCurrentPlaceInfo(currentPlaceNumber);
    String[] parts = currentPlace.split(";");
    String itemName = "";
    List<String> items = convertStringToList(parts[1]);
    if (items.isEmpty()) {
      showItemInfo += "No item in this place." + "\n";
    } else {
      showItemInfo += "Items in this place:" + "\n";
      int i = 1;
      for (String item : items) {
        itemName = item.split("-")[0].trim();
        String itemDamage = item.split("-")[1].trim();
        showItemInfo += i + ". " + itemName + " (Damage: " + itemDamage + ")" + "\n";
      }
    }
    maxItemNumber = items.size();
    if (maxItemNumber == 0) {
      guiView.showGuiMessage("Pick Up Item", "No item in this place", "OK", () -> {
        try {
          takeTurn();
        } catch (IOException e) {
          e.printStackTrace();
        }
      });
    } else {
      guiView.showGuiNumberMessage("Pick Up Item", showItemInfo,
              "OK",
              1, maxItemNumber).thenAccept(itemNumber -> {
                System.out.println("Item number-1: " + itemNumber);
                String item = items.get(itemNumber - 1);
                String[] itemParts = item.split("-");
                String itemName1 = itemParts[0].trim();
                System.out.println("Item name: " + itemName1);
                try {
                  new PickUpItemCommand(town, itemName1).execute();
                  takeTurn();
                } catch (IOException e) {
                  e.printStackTrace();
                }
              }
          )
          .exceptionally(e -> {
            guiView.showGuiMessage("Error", "Invalid item number", "OK");
            return null;
          });
    }
  }


  private void lookAround() throws IOException {
    int currentPlayerIndex = town.getCurrentPlayerIndex();
    int currentPlayerPlaceNumber = town.getPlayerCurrPlaceNumber(currentPlayerIndex);
    String currentPlace = town.getCurrentPlaceInfo(currentPlayerPlaceNumber);
    String neighbors = town.getCurrentPlaceNeighborsInfo(currentPlayerPlaceNumber);

    String lookAroundInfo = "";
    // Show current place info: name, items, players
    String[] parts = currentPlace.split(";");
    String currentPlaceName = parts[0];
    lookAroundInfo = "Current place: " + currentPlaceName + "\n";
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
    String currentPlayers = "";
    if (parts[2].isEmpty()) {
      currentPlayers = "No other player in this place";
    } else {
      currentPlayers = parts[2].replace("[", "").replace("]", "").trim();
    }

    if (currentItem.isEmpty()) {
      lookAroundInfo += "No item in this place.\n";
    } else {
      lookAroundInfo += "Current place item: " + itemName + " (Damage: " + itemDamage + ")\n";
    }
    lookAroundInfo += "Current place players: " + currentPlayers + "\n";

    // Show neighbors info: name, items, players
    neighbors = neighbors.replace("[[", "").replace("]]", "");
    String[] neighborParts = neighbors.split("\\], \\[");
    for (String neighbor : neighborParts) {
      String[] neighborInfo = neighbor.split(";");

      String neighborName = neighborInfo[0].trim();
      String neighborNumber = neighborInfo[1].trim();
      String neighborItem = "";
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
      String neighborPlayers = "";
      List<String> players = convertStringToList(neighborInfo[3]);
      if (players.isEmpty()) {
        neighborPlayers = "No other player in this place";
      } else {
        neighborPlayers = neighborInfo[3].replace("[", "").replace("]", "").trim();
      }
      if (neighborInfo[5].equals("true")) {
        lookAroundInfo += "Neighboring place: " + neighborName + "\n";
        lookAroundInfo += "Pet is in this place.\n";
      } else {
        lookAroundInfo +=
            "Neighboring place: " + neighborName + " (Place NUmber: " + neighborNumber + ")\n";
        lookAroundInfo += "Item: " + neighborItem + "\n";
        lookAroundInfo += "Players: " + neighborPlayers + "\n";
        if (neighborInfo[4].equals("true")) {
          lookAroundInfo += "Target is in this place.\n";
        }
      }
    }
    guiView.showGuiMessage("Look Around", lookAroundInfo, "OK", () -> {
      try {
        town.lookAround();
        takeTurn();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
//    town.lookAround();
  }

  private void movePlayer() {
    int currentPlayerIndex = town.getCurrentPlayerIndex();
    String moveInfo = "You want to move to " + newPlaceName + "?" + "\n";
    guiView.showGuiMessage("Move Player", moveInfo, "OK", () -> {
      try {
        new MovePlayerCommand(town, currentPlayerIndex, newPlaceNumber).execute();
        takeTurn();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

  private void movePet() {
    guiView.showGuiNumberMessage("Move Pet", "Enter the place number to move the pet to", "OK", 1,
            20)
        .thenAccept(placeNumber -> {
          try {
            new MovePetCommand(town, placeNumber).execute();
            takeTurn();
          } catch (IOException e) {
            e.printStackTrace();
          }
        })
        .exceptionally(e -> {
          guiView.showGuiMessage("Error", "Invalid place number", "OK");
          return null;
        });
  }

  private void attackTarget() throws IOException {
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
    AtomicBoolean killSuccess = new AtomicBoolean(false);
    List<String> playerItems = convertStringToList(playerCurrentCarriedItems);
    playerItems.add("Poke Target-1");
    String showItemInfo = "";
    showItemInfo += "Items you have: " + "\n";
    int i = 1;
    for (String item : playerItems) {
      showItemInfo += i + ". " + item + "\n";
      i++;
    }
    showItemInfo += "Enter the item number you want to use to attack the target: \n";
    guiView.showGuiNumberMessage("Attack Target", showItemInfo, "OK", 1, playerItems.size())
        .thenAccept(itemNumber -> {
          String[] itemsParts = playerItems.get(itemNumber - 1).split("-");
          String itemName = itemsParts[0].trim();
          String itemDamage = itemsParts[1].trim();
          try {
            killSuccess.set(town.attackTarget(itemName));
            String showAttackResult = "";
            showAttackResult += "You used " + itemName + " to attack the target." + "\n";
            if (itemName.equals("Poke Target")) {
              showAttackResult += "You hit the target and caused 1 damage." + "\n";
            } else {
              showAttackResult += "You hit the target and caused " + itemDamage + " damage." + "\n";
            }
            System.out.println("Kill success: " + killSuccess);
            if (killSuccess.equals(true)) {
              endGame();
            } else {
              town.switchToNextPlayer();
              takeTurn();
            }
            guiView.showGuiMessage("Attack Result", showAttackResult, "OK");
          } catch (IOException e) {
            e.printStackTrace();
          }
        })
        .exceptionally(e -> {
          guiView.showGuiMessage("Error", "Invalid item number", "OK");
          return null;
        });

  }

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


  private void handleMovePlayer(String command) throws IOException {
    String[] parts = command.split(",");
    newPlaceName = parts[1];
    newPlaceNumber = Integer.parseInt(parts[2]);

//    System.out.println("New place name: " + newPlaceName);

    handlePlayerAction("MOVE");
  }

//        } else if (command.equals("MOVE")) {
////        controller.executeCommand("MOVE");
//      } else if (command.equals("LOOK")) {
//        System.out.println("Looking around...");
//        controller.executeCommand("LOOK");
//      } else if (command.equals("PICK")) {
////        controller.executeCommand("PICK");
//      } else if (command.equals("ATTACK")) {
////        controller.executeCommand("ATTACK");
//      } else if (command.equals("PETMOVE")) {
////        controller.executeCommand("PETMOVE");

  // 测试代码，最后删除
  private void testHere() {
    guiView.showGuiMessage("Error", "Test here", "OK");
  }
}