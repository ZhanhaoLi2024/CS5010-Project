package controller;

import java.io.IOException;
import model.town.Town;
import view.GameView;

/**
 * The Controller interface defines the contract for managing game flow and user interactions.
 * It coordinates communication between the model and view components following the MVC pattern.
 */
public interface Controller {
  void setView(GameView gameView, boolean gui);

  /**
   * Initializes and starts the game, setting up initial game state and beginning gameplay.
   *
   * @throws IOException if there is an error during game initialization or I/O operations
   */
  void startGame() throws IOException;

  Town getTown();

  void executeCommand(String command) throws IOException;
}