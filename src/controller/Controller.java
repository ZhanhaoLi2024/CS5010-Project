package controller;

import java.io.IOException;
import view.GameView;

/**
 * The Controller interface defines the contract for managing game flow and user interactions.
 * It coordinates communication between the model and view components following the MVC pattern.
 */
public interface Controller {
  /**
   * Starts the game and initializes necessary components.
   *
   * @throws IOException if there is an error during game initialization
   */
  void startGame() throws IOException;

  /**
   * Processes a game turn for the current player.
   *
   * @throws IOException if there is an error during turn processing
   */
  void processTurn() throws IOException;

  /**
   * Displays the game map.
   *
   * @throws IOException if there is an error generating or displaying the map
   */
  void displayMap() throws IOException;

  /**
   * Gets the view component associated with this controller.
   *
   * @return the game view
   */
  GameView getView();

  /**
   * Ends the current game session.
   *
   * @throws IOException if there is an error during game termination
   */
  void endGame() throws IOException;
}