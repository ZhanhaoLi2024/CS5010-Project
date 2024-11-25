package view;

import java.io.IOException;
import model.place.Place;
import model.player.Player;

/**
 * The GameView interface defines the contract for game view implementations.
 * Supports both text-based and graphical user interfaces.
 */
public interface GameView {

  /**
   * Initializes the view components.
   */
  void initialize() throws IOException;

  /**
   * Shows the welcome/about screen.
   */
  void showWelcomeScreen() throws IOException;

  /**
   * Updates player information display.
   *
   * @param player       Current player
   * @param currentPlace Current player's location
   */
  void updatePlayerInfo(Player player, Place currentPlace) throws IOException;

  /**
   * Shows a message to the user.
   *
   * @param message Message to display
   */
  void showMessage(String message) throws IOException;

  /**
   * Clean up and close the view.
   */
  void close() throws IOException;

  /**
   * Gets generic user input as string.
   *
   * @return user input string
   */
  String getStringInput() throws IOException;

  /**
   * Gets numeric input from user.
   *
   * @return numeric value entered by user
   */
  int getNumberInput() throws IOException;
}