package controller;

import java.io.IOException;
import model.town.Town;
import view.View;

/**
 * The Controller interface defines the contract for managing game flow and user interactions.
 * It coordinates communication between the model and view components following the MVC pattern.
 */
public interface Controller {
  /**
   * Sets the view component for this controller. The view can only be set once.
   *
   * @param gameView the view to be used for user interaction
   * @param gui      boolean indicating if the view is a GUI
   * @throws IllegalStateException if the view has already been set
   */
  void setView(View gameView, boolean gui);

  /**
   * Starts the game by initializing the view and entering the main game loop.
   * Displays welcome message and maximum turns, then shows the main menu until quit.
   *
   * @throws IOException           if there is an error in input/output operations
   * @throws IllegalStateException if the view has not been set
   */
  void startGame() throws IOException;

  /**
   * Gets the town model associated with this controller.
   *
   * @return the Town model instance
   */
  Town getTown();

  /**
   * Executes a game command.
   *
   * @param command the command string to execute
   * @return boolean indicating if the command was executed successfully
   * @throws IOException if there is an error in input/output operations
   */

  boolean executeCommand(String command) throws IOException;
}