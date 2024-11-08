package controller;

import controller.command.AddPlayerCommand;
import controller.command.AttackTargetCommand;
import controller.command.Command;
import controller.command.CommandResult;
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
import model.observer.GameObserver;
import model.town.Town;
import view.GameView;

/**
 * GameController coordinates interactions between the Town model and GameView.
 * It implements both Controller and GameObserver interfaces.
 */
public class GameController implements Controller, GameObserver {
  private final Town model;
  private final GameView view;
  private boolean isGameRunning;

  /**
   * Constructs a new GameController.
   *
   * @param model the town model
   * @param view  the game view
   */
  public GameController(Town model, GameView view) {
    this.model = model;
    this.view = view;
    this.isGameRunning = true;
    model.addObserver(this);
  }

  @Override
  public void startGame() throws IOException {
    view.displayMessage("\nWelcome to the game!");
    view.displayMessage("You have " + model.getMaxTurns() + " turns.");

    // Add initial computer player
    executeCommand(new AddPlayerCommand(true));

    while (isGameRunning && !model.isGameOver()) {
      handleMainMenu();
    }
  }

  /**
   * Handles main menu interactions
   */
  private void handleMainMenu() throws IOException {
    view.displayMainMenu();
    String input = view.getInput();

    try {
      int choice = Integer.parseInt(input);
      switch (choice) {
        case 1:
          handleDisplayMapInfo();
          break;
        case 2:
          executeCommand(new AddPlayerCommand(false));
          break;
        case 3:
          handleDisplayPlayerInfo();
          break;
        case 4:
          handleDisplayPlaceInfo();
          break;
        case 5:
          handleGamePlay();
          break;
        case 6:
          displayMap();
          break;
        case 0:
          isGameRunning = false;
          view.displayMessage("Exiting game...");
          break;
        default:
          view.displayMessage("Invalid choice. Please try again.");
      }
    } catch (NumberFormatException e) {
      view.displayMessage("Please enter a valid number.");
    }
  }

  private void handleGamePlay() throws IOException {
    if (model.getPlayers().size() < 2) {
      view.displayMessage("At least two players are required to start the game.");
      return;
    }

    boolean gameInProgress = true;
    while (gameInProgress && !model.isGameOver()) {
      PlayerDTO currentPlayer = model.getCurrentPlayerInfo();
      List<String> playerInfo = new ArrayList<>();
      playerInfo.add("Current Player: " + currentPlayer.getName());
      playerInfo.add("Location: Space " + currentPlayer.getCurrentPlaceNumber());
      playerInfo.add(
          "Items: " + currentPlayer.getItems().size() + "/" + currentPlayer.getCarryLimit());

      view.displayGameState(model.getCurrentTurn(), model.getMaxTurns(), playerInfo);

      if (currentPlayer.isComputerControlled()) {
        handleComputerTurn();
      } else {
        handleHumanTurn();
      }

      if (model.isGameOver()) {
        handleGameEnd();
        gameInProgress = false;
      }
    }
  }

  private void handleComputerTurn() throws IOException {
    view.displayMessage("Computer player's turn...");
    PlayerDTO player = model.getCurrentPlayerInfo();

    // Computer AI logic
    if (canAttackTarget(player)) {
      // Attack with best item
      List<ItemDTO> items = player.getItems();
      ItemDTO bestItem = null;
      int maxDamage = 0;
      for (int i = 0; i < items.size(); i++) {
        if (items.get(i).getDamage() > maxDamage) {
          maxDamage = items.get(i).getDamage();
          bestItem = items.get(i);
        }
      }
      executeCommand(new AttackTargetCommand(bestItem != null ? items.indexOf(bestItem) : null));
    } else if (shouldPickUpItem(player)) {
      PlaceDTO place = model.getPlaceInfo(player.getCurrentPlaceNumber());
      if (!place.getItems().isEmpty()) {
        executeCommand(new PickUpItemCommand(0)); // Pick up first available item
      }
    } else {
      // Move to neighboring space
      PlaceDTO currentPlace = model.getPlaceInfo(player.getCurrentPlaceNumber());
      List<Integer> neighbors = currentPlace.getNeighborNumbers();
      if (!neighbors.isEmpty()) {
        executeCommand(new MovePlayerCommand(neighbors.get(0)));
      }
    }
  }

  private void handleHumanTurn() throws IOException {
    view.displayMessage("\nChoose your action:");
    view.displayMessage("1. Move");
    view.displayMessage("2. Pick up item");
    view.displayMessage("3. Look around");
    view.displayMessage("4. Attack target");
    view.displayMessage("5. Move pet");

    String input = view.getInput();
    try {
      switch (Integer.parseInt(input)) {
        case 1:
          handlePlayerMove();
          break;
        case 2:
          handlePickUpItem();
          break;
        case 3:
          executeCommand(new LookAroundCommand());
          break;
        case 4:
          handleAttackTarget();
          break;
        case 5:
          handleMovePet();
          break;
        default:
          view.displayMessage("Invalid choice.");
      }
    } catch (NumberFormatException e) {
      view.displayMessage("Please enter a valid number.");
    }
  }

  private void handlePlayerMove() throws IOException {
    PlayerDTO player = model.getCurrentPlayerInfo();
    PlaceDTO currentPlace = model.getPlaceInfo(player.getCurrentPlaceNumber());
    List<String> neighborInfo = new ArrayList<>();

    for (Integer neighborNumber : currentPlace.getNeighborNumbers()) {
      PlaceDTO neighbor = model.getPlaceInfo(neighborNumber);
      neighborInfo.add(neighbor.getName());
    }

    view.displayMoveOptions(neighborInfo);

    try {
      int choice = Integer.parseInt(view.getInput()) - 1;
      if (choice >= 0 && choice < currentPlace.getNeighborNumbers().size()) {
        executeCommand(new MovePlayerCommand(currentPlace.getNeighborNumbers().get(choice)));
      } else {
        view.displayMessage("Invalid move choice.");
      }
    } catch (NumberFormatException e) {
      view.displayMessage("Please enter a valid number.");
    }
  }

  private void handlePickUpItem() throws IOException {
    PlayerDTO player = model.getCurrentPlayerInfo();
    PlaceDTO place = model.getPlaceInfo(player.getCurrentPlaceNumber());
    List<String> itemsInfo = new ArrayList<>();

    for (ItemDTO item : place.getItems()) {
      itemsInfo.add(item.getName() + " (Damage: " + item.getDamage() + ")");
    }

    if (itemsInfo.isEmpty()) {
      view.displayMessage("No items available to pick up.");
      return;
    }

    view.displayItems(itemsInfo);

    try {
      int choice = Integer.parseInt(view.getInput()) - 1;
      if (choice >= 0 && choice < place.getItems().size()) {
        executeCommand(new PickUpItemCommand(choice));
      } else {
        view.displayMessage("Invalid item choice.");
      }
    } catch (NumberFormatException e) {
      view.displayMessage("Please enter a valid number.");
    }
  }

  private void handleAttackTarget() throws IOException {
    PlayerDTO player = model.getCurrentPlayerInfo();
    List<String> attackOptions = new ArrayList<>();
    attackOptions.add("0. Poke in the eye (Damage: 1)");

    List<ItemDTO> items = player.getItems();
    for (int i = 0; i < items.size(); i++) {
      ItemDTO item = items.get(i);
      attackOptions.add(
          (i + 1) + ". Use " + item.getName() + " (Damage: " + item.getDamage() + ")");
    }

    view.displayAttackInfo(attackOptions);

    try {
      int choice = Integer.parseInt(view.getInput()) - 1;
      if (choice >= -1 && choice < items.size()) {
        executeCommand(new AttackTargetCommand(choice >= 0 ? choice : null));
      } else {
        view.displayMessage("Invalid attack choice.");
      }
    } catch (NumberFormatException e) {
      view.displayMessage("Please enter a valid number.");
    }
  }

  private void handleMovePet() throws IOException {
    List<String> placeInfo = new ArrayList<>();
    for (int i = 0; i < model.getPlaces().size(); i++) {
      placeInfo.add((i + 1) + ". " + model.getPlaces().get(i).getName());
    }

    view.displayPetInfo(placeInfo);

    try {
      int choice = Integer.parseInt(view.getInput());
      if (choice > 0 && choice <= model.getPlaces().size()) {
        executeCommand(new MovePetCommand(choice));
      } else {
        view.displayMessage("Invalid place choice.");
      }
    } catch (NumberFormatException e) {
      view.displayMessage("Please enter a valid number.");
    }
  }

  private void handleDisplayMapInfo() throws IOException {
    GameStateDTO gameState = model.getGameState();
    List<String> placesInfo = new ArrayList<>();
    for (int i = 1; i <= model.getPlaces().size(); i++) {
      PlaceDTO place = model.getPlaceInfo(i);
      StringBuilder placeInfo = new StringBuilder(place.getName() + ":");
      if (!place.getItems().isEmpty()) {
        placeInfo.append("\n  Items:");
        for (ItemDTO item : place.getItems()) {
          placeInfo.append("\n    - ").append(item.getName())
              .append(" (Damage: ").append(item.getDamage()).append(")");
        }
      }
      placesInfo.add(placeInfo.toString());
    }

    view.displayMapInfo(
        model.getTownName(),
        gameState.getTarget().getName(),
        gameState.getTarget().getHealth(),
        placesInfo
    );
  }

  private void handleDisplayPlayerInfo() throws IOException {
    List<String> playerInfo = new ArrayList<>();
    for (PlayerDTO player : model.getGameState().getPlayers()) {
      StringBuilder info = new StringBuilder();
      info.append("Player: ").append(player.getName())
          .append("\nLocation: ")
          .append(model.getPlaceInfo(player.getCurrentPlaceNumber()).getName())
          .append("\nItems: ").append(player.getItems().size())
          .append("/").append(player.getCarryLimit());
      playerInfo.add(info.toString());
    }
    view.displayPlayerInfo(playerInfo);
  }

  private void handleDisplayPlaceInfo() throws IOException {
    // Display all places first
    List<String> placeNames = new ArrayList<>();
    for (int i = 1; i <= model.getPlaces().size(); i++) {
      PlaceDTO place = model.getPlaceInfo(i);
      placeNames.add(place.getName());
    }
    view.displayPlaceInfo(placeNames);

    view.displayMessage("Enter place number to view details:");
    try {
      int placeNumber = Integer.parseInt(view.getInput());
      if (isValidPlaceNumber(placeNumber)) {
        PlaceDTO place = model.getPlaceInfo(placeNumber);
        List<String> placeInfo = new ArrayList<>();
        placeInfo.add("Name: " + place.getName());
        placeInfo.add("Items: " + place.getItems().size());
        placeInfo.add("Players here: " + place.getPlayerNames().size());
        view.displayPlaceInfo(placeInfo);
      } else {
        view.displayMessage("Invalid place number.");
      }
    } catch (NumberFormatException e) {
      view.displayMessage("Please enter a valid number.");
    }
  }

  private void displayMap() throws IOException {
    // This would integrate with the view's map display functionality
    view.displayMessage("Map display functionality will be implemented separately.");
  }

  private void handleGameEnd() throws IOException {
    GameStateDTO gameState = model.getGameState();
    if (gameState.getTarget().getHealth() <= 0) {
      PlayerDTO winner = gameState.getPlayers().get(gameState.getCurrentPlayerIndex());
      view.displayMessage(winner.getName() + " has won by eliminating the target!");
    } else {
      view.displayMessage("Game Over! The target has escaped!");
    }
    isGameRunning = false;
  }

  private void executeCommand(Command command) throws IOException {
    try {
      command.execute(model);
    } catch (IllegalArgumentException | IllegalStateException e) {
      view.displayMessage("Error: " + e.getMessage());
    }
  }

  // Helper methods for computer player decision making
  private boolean canAttackTarget(PlayerDTO player) {
    return
        player.getCurrentPlaceNumber() == model.getGameState().getTarget().getCurrentPlaceNumber()
            && !model.isPlayerVisible(model.getPlayers().get(model.getCurrentPlayerIndex()));
  }

  private boolean shouldPickUpItem(PlayerDTO player) {
    return !model.getPlaceInfo(player.getCurrentPlaceNumber()).getItems().isEmpty()
        && player.getItems().size() < player.getCarryLimit();
  }

  // GameObserver implementation
  @Override
  public void onGameStateChanged(GameStateDTO gameState) {
    try {
      List<String> playerInfo = new ArrayList<>();
      PlayerDTO currentPlayer = gameState.getPlayers().get(gameState.getCurrentPlayerIndex());
      playerInfo.add("Current Player: " + currentPlayer.getName());
      playerInfo.add("Turn: " + gameState.getCurrentTurn() + "/" + gameState.getMaxTurns());

      view.displayGameState(
          gameState.getCurrentTurn(),
          gameState.getMaxTurns(),
          playerInfo
      );
    } catch (IOException e) {
      // Handle exception appropriately
    }
  }

  @Override
  public void onPlaceStateChanged(PlaceDTO placeInfo) {
    try {
      List<String> info = new ArrayList<>();
      info.add("Place: " + placeInfo.getName());
      info.add("Items: " + placeInfo.getItems().size());
      info.add("Players: " + placeInfo.getPlayerNames().size());
      view.displayPlaceInfo(info);
    } catch (IOException e) {
      // Handle exception appropriately
    }
  }

  @Override
  public void onPlayerStateChanged(PlayerDTO playerInfo) {
    try {
      List<String> info = new ArrayList<>();
      info.add("Player: " + playerInfo.getName());
      info.add("Location: Space " + playerInfo.getCurrentPlaceNumber());
      info.add("Items: " + playerInfo.getItems().size() + "/" + playerInfo.getCarryLimit());
      view.displayPlayerInfo(info);
    } catch (IOException e) {
      handleIOException(e);
    }
  }

  @Override
  public void onTargetStateChanged(TargetDTO targetInfo) {
    try {
      view.displayMessage(String.format(
          "Target %s health: %d at %s",
          targetInfo.getName(),
          targetInfo.getHealth(),
          model.getPlaceInfo(targetInfo.getCurrentPlaceNumber()).getName()
      ));
    } catch (IOException e) {
      handleIOException(e);
    }
  }

  /**
   * Handles IO exceptions in a centralized way
   */
  private void handleIOException(IOException e) {
    try {
      view.displayMessage("Error occurred: " + e.getMessage());
    } catch (IOException ex) {
      // If we can't even display the error message, log it or handle it according to requirements
      System.err.println("Critical error: " + ex.getMessage());
    }
  }

  /**
   * Validates a place number is valid
   */
  private boolean isValidPlaceNumber(int placeNumber) {
    return placeNumber > 0 && placeNumber <= model.getPlaces().size();
  }

  /**
   * Gets a formatted description of items in a place
   */
  private String getItemsDescription(List<ItemDTO> items) {
    if (items.isEmpty()) {
      return "No items";
    }
    StringBuilder sb = new StringBuilder();
    for (ItemDTO item : items) {
      sb.append("\n  - ").append(item.getName())
          .append(" (Damage: ").append(item.getDamage()).append(")");
    }
    return sb.toString();
  }

  /**
   * Gets a formatted description of players in a place
   */
  private String getPlayersDescription(List<String> playerNames) {
    if (playerNames.isEmpty()) {
      return "No players";
    }
    return String.join(", ", playerNames);
  }

  /**
   * Formats a place description for display
   */
  private String formatPlaceDescription(PlaceDTO place) {
    return String.format("Space %d: %s\nItems: %s\nPlayers: %s",
        place.getPlaceNumber(),
        place.getName(),
        getItemsDescription(place.getItems()),
        getPlayersDescription(place.getPlayerNames())
    );
  }

  /**
   * Checks if the game should continue
   */
  private boolean shouldContinueGame() {
    return isGameRunning && !model.isGameOver() && model.getCurrentTurn() <= model.getMaxTurns();
  }

  /**
   * Gets the best attack item for computer player
   */
  private Integer getBestAttackItemIndex(List<ItemDTO> items) {
    if (items.isEmpty()) {
      return null; // Will result in a poke attack
    }

    int bestIndex = 0;
    int maxDamage = items.get(0).getDamage();

    for (int i = 1; i < items.size(); i++) {
      if (items.get(i).getDamage() > maxDamage) {
        maxDamage = items.get(i).getDamage();
        bestIndex = i;
      }
    }

    return bestIndex;
  }

  /**
   * Chooses best move for computer player
   */
  private Integer chooseBestMove(List<Integer> possibleMoves, PlayerDTO player) {
    // Simple strategy: Move towards target if we have items, otherwise explore
    if (!player.getItems().isEmpty()) {
      // Move towards target
      int targetLocation = model.getGameState().getTarget().getCurrentPlaceNumber();
      // Find neighbor closest to target
      for (Integer move : possibleMoves) {
        if (move == targetLocation) {
          return move;
        }
      }
    }

    // If no better strategy, just take first available move
    return possibleMoves.isEmpty() ? null : possibleMoves.get(0);
  }

  /**
   * Provides descriptive game state message
   */
  private String getGameStateMessage() {
    GameStateDTO state = model.getGameState();
    return String.format(
        "Turn %d/%d | Target Health: %d | Location: %s",
        state.getCurrentTurn(),
        state.getMaxTurns(),
        state.getTarget().getHealth(),
        model.getPlaceInfo(state.getTarget().getCurrentPlaceNumber()).getName()
    );
  }

  /**
   * Validates if an attack can be made
   */
  private boolean canMakeAttack(PlayerDTO player, TargetDTO target) {
    return player.getCurrentPlaceNumber() == target.getCurrentPlaceNumber()
        && !model.isPlayerVisible(model.getPlayers().get(model.getCurrentPlayerIndex()));
  }

  /**
   * Provides turn summary message
   */
  private String getTurnSummaryMessage(CommandResult result) {
    StringBuilder summary = new StringBuilder();
    summary.append("\n=== Turn Summary ===\n");
    summary.append("Action: ").append(result.getType()).append("\n");
    summary.append("Result: ").append(result.getMessage()).append("\n");
    if (result.getGameState() != null) {
      summary.append("Game State: ").append(getGameStateMessage()).append("\n");
    }
    return summary.toString();
  }
}