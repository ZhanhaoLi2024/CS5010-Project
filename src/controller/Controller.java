package controller;

import java.io.IOException;

/**
 * The Controller interface defines the contract for a game controller.
 * It handles the user input and controls the flow of the game.
 */
public interface Controller {

  /**
   * Starts the game loop, where the controller receives input, processes it, and interacts with the model.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  void startGame() throws IOException;

  /**
   * Prints the map of the town.
   */
  void printMap() throws IOException;

  /**
   * Displays the map information.
   */
  void displayMapInfo();

  /**
   * Allows a player to take a turn.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  void takeTurn() throws IOException;

  /**
   * Ends the game.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  void endGame() throws IOException;
}