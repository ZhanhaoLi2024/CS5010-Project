package view;

import java.io.IOException;
import java.util.List;

/**
 * The GameView interface defines all view-related operations for the game.
 * It handles all user interface aspects without directly depending on the model.
 */
public interface GameView {
  /**
   * Display the main menu of the game.
   */
  void displayMainMenu() throws IOException;

  /**
   * Display information about the world/map.
   *
   * @param worldName    the name of the world
   * @param targetName   the name of the target
   * @param targetHealth the health of the target
   * @param placesInfo   list of place information to display
   */
  void displayMapInfo(String worldName, String targetName, int targetHealth,
                      List<String> placesInfo) throws IOException;

  /**
   * Display information about all players or a specific player.
   *
   * @param playerInfo the player information to display
   */
  void displayPlayerInfo(List<String> playerInfo) throws IOException;

  /**
   * Display information about places in the game.
   *
   * @param placeInfo the place information to display
   */
  void displayPlaceInfo(List<String> placeInfo) throws IOException;

  /**
   * Display the current game state during a turn.
   *
   * @param currentTurn       the current turn number
   * @param maxTurns          the maximum number of turns
   * @param currentPlayerInfo information about the current player
   */
  void displayGameState(int currentTurn, int maxTurns,
                        List<String> currentPlayerInfo) throws IOException;

  /**
   * Display the movement options for a player.
   *
   * @param neighbors list of neighboring places that can be moved to
   */
  void displayMoveOptions(List<String> neighbors) throws IOException;

  /**
   * Display items that can be picked up in the current location.
   *
   * @param items list of available items
   */
  void displayItems(List<String> items) throws IOException;

  /**
   * Display the results of looking around the current location.
   *
   * @param lookAroundInfo information about what the player sees
   */
  void displayLookAroundInfo(List<String> lookAroundInfo) throws IOException;

  /**
   * Display attack options and results.
   *
   * @param attackInfo information about available attacks and results
   */
  void displayAttackInfo(List<String> attackInfo) throws IOException;

  /**
   * Display pet movement options and results.
   *
   * @param petInfo information about pet movement options
   */
  void displayPetInfo(List<String> petInfo) throws IOException;

  /**
   * Display a message to the user.
   *
   * @param message the message to display
   */
  void displayMessage(String message) throws IOException;

  /**
   * Get input from the user.
   *
   * @return the user's input as a String
   */
  String getInput() throws IOException;

  /**
   * Clear any resources used by the view.
   */
  void close() throws IOException;
}