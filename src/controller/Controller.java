package controller;

import java.io.IOException;

/**
 * The Controller interface defines the contract for managing game flow and user interactions.
 * It coordinates communication between the model and view components following the MVC pattern.
 */
public interface Controller {
  /**
   * Initializes and starts the game, setting up initial game state and beginning gameplay.
   *
   * @throws IOException if there is an error during game initialization or I/O operations
   */
  void startGame() throws IOException;

  /**
   * Generates and displays a graphical representation of the game map.
   *
   * @throws IOException if there is an error during map generation or display
   */
  void printMap() throws IOException;

  /**
   * Displays detailed information about the current state of the game map.
   */
  void displayMapInfo();

  void handleAddHumanPlayer(String name, int startingPlace, int carryLimit)
      throws IOException;

  void handleAddComputerPlayer() throws IOException;

  /**
   * Processes a single turn in the game, handling player actions and game state updates.
   *
   * @throws IOException if there is an error during turn processing
   */
  void takeTurn() throws IOException;

  /**
   * Concludes the game session and performs necessary cleanup operations.
   *
   * @throws IOException if there is an error during game termination
   */
  void endGame() throws IOException;
}