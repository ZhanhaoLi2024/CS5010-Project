package controller;

import java.io.IOException;

/**
 * The Controller interface defines the contract for a game controller.
 * It handles the user input and controls the flow of the game.
 */
public interface Controller {

  /**
   * Starts the game and handles the main game loop.
   *
   * @throws IOException if there's an error with I/O operations
   */
  void startGame() throws IOException;
}