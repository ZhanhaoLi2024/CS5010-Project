package controller.support;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import model.place.Place;
import model.player.Player;
import model.town.Town;

public class GameStateManager {
  private final Town town;
  private final int maxTurns;
  private final Set<String> validCommands;
  private int currentTurn;
  private GameState state;

  /**
   * Constructs a new GameStateManager.
   *
   * @param gameModel    the game model
   * @param maxGameTurns the maximum number of turns allowed
   */
  public GameStateManager(Town gameModel, int maxGameTurns) {
    this.town = gameModel;
    this.maxTurns = maxGameTurns;
    this.validCommands = new HashSet<>();
    initializeValidCommands();
    reset();
  }

  private void initializeValidCommands() {
    // Add player management commands
    validCommands.add("ADD_PLAYER");
    validCommands.add("ADD_COMPUTER");
    validCommands.add("SHOW_INFO");

    // Add game action commands
    validCommands.add("MOVE");
    validCommands.add("LOOK");
    validCommands.add("PICK");
    validCommands.add("ATTACK");
    validCommands.add("MOVE_PET");

    // Add game flow commands
    validCommands.add("START_TURN");
    validCommands.add("END_TURN");
  }

  /**
   * Resets the game state to initial conditions.
   */
  public void reset() {
    currentTurn = 1;
    state = GameState.WAITING_FOR_PLAYERS;
  }

  public int getMaxTurns() {
    return maxTurns;
  }

  /**
   * Checks if the game is over.
   *
   * @return true if the game is over, false otherwise
   */
  public boolean isGameOver() {
    return currentTurn > maxTurns || town.isGameOver();
  }

  /**
   * Checks if maximum turns have been reached.
   *
   * @return true if max turns reached, false otherwise
   */
  public boolean isMaxTurnsReached() {
    return currentTurn > maxTurns;
  }

  /**
   * Gets the current player.
   *
   * @return the current player
   */
  public Player getCurrentPlayer() {
    return town.getPlayers().get(town.getCurrentPlayerIndex());
  }

  /**
   * Gets the current player's place.
   *
   * @return the current place
   */
  public Place getCurrentPlayerPlace() {
    var player = getCurrentPlayer();
    return town.getPlaceByNumber(player.getPlayerCurrentPlaceNumber());
  }

  /**
   * Advances to the next turn.
   */
  public void nextTurn() throws IOException {
    currentTurn++;
    town.switchToNextPlayer();
  }

  /**
   * Checks if a move action is valid.
   *
   * @return true if move is allowed, false otherwise
   */
  public boolean canMove() {
    return state == GameState.PLAYER_TURN
        && !isGameOver()
        && !getCurrentPlayer().isComputerControlled();
  }

  /**
   * Checks if a look action is valid.
   *
   * @return true if look is allowed, false otherwise
   */
  public boolean canLook() {
    return state == GameState.PLAYER_TURN
        && !isGameOver()
        && !getCurrentPlayer().isComputerControlled();
  }

  /**
   * Checks if a pickup action is valid.
   *
   * @return true if pickup is allowed, false otherwise
   */
  public boolean canPickup() {
    return state == GameState.PLAYER_TURN
        && !isGameOver()
        && !getCurrentPlayer().isComputerControlled();
  }

  /**
   * Checks if an attack action is valid.
   *
   * @return true if attack is allowed, false otherwise
   */
  public boolean canAttack() {
    return state == GameState.PLAYER_TURN
        && !isGameOver()
        && !getCurrentPlayer().isComputerControlled();
//        && town.checkCurPlayerSameRoomWithDrLucky();
  }

  /**
   * Checks if pet movement is valid.
   *
   * @return true if pet movement is allowed, false otherwise
   */
  public boolean canMovePet() {
    return state == GameState.PLAYER_TURN
        && !isGameOver()
        && !getCurrentPlayer().isComputerControlled();
  }

  /**
   * Checks if a new turn can be started.
   *
   * @return true if turn can be started, false otherwise
   */
  public boolean canStartTurn() {
    return !isGameOver() && town.getPlayers().size() >= 2;
  }

  /**
   * Validates if a command is valid in the current state.
   *
   * @param command the command to validate
   * @return true if command is valid, false otherwise
   */
  public boolean isValidCommand(String command) {
    System.out.println("1" + command);
    if (!validCommands.contains(command)) {
      return false;
    }

    switch (command) {
      case "MOVE":
        return canMove();
      case "LOOK":
        return canLook();
      case "PICK":
        return canPickup();
      case "ATTACK":
        return canAttack();
      case "MOVE_PET":
        return canMovePet();
      case "START_TURN":
        return canStartTurn();
      default:
        return true;
    }
  }

  /**
   * Gets the current state of the game.
   *
   * @return the current game state
   */
  public GameState getState() {
    return state;
  }

  /**
   * Sets the current state of the game.
   *
   * @param newState the new game state
   */
  public void setState(GameState newState) {
    this.state = newState;
  }
}