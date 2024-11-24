package view;

import java.awt.Image;
import java.io.IOException;
import java.util.List;
import model.item.Item;
import model.place.Place;
import model.player.Player;
import model.target.Target;

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
   * Updates the game map display.
   *
   * @param places   List of places in the world
   * @param players  List of all players
   * @param target   Target character
   * @param mapImage Generated map image
   */
  void updateMap(List<Place> places, List<Player> players, Target target, Image mapImage);

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
   * Shows available move options and gets user's choice.
   *
   * @param neighbors Neighboring places
   * @return Selected place number
   */
  int getMoveInput(List<Place> neighbors) throws IOException;

  /**
   * Shows available items and gets user's pickup choice.
   *
   * @param items Available items
   * @return Selected item index
   */
  int getPickupInput(List<Item> items) throws IOException;

  /**
   * Gets user's attack input.
   *
   * @param carriedItems Items carried by player
   * @return Selected attack option (0 for poke, 1+ for items)
   */
  int getAttackInput(List<Item> carriedItems) throws IOException;

  /**
   * Displays the main menu and gets user selection.
   *
   * @return Selected menu option
   */
  int displayMainMenu() throws IOException;

  /**
   * Clean up and close the view.
   */
  void close() throws IOException;

  /**
   * Gets generic user input as string.
   *
   * @return user input string
   */
  String getUserInput() throws IOException;

  /**
   * Gets numeric input from user.
   *
   * @return numeric value entered by user
   */
  int getNumberInput() throws IOException;

  String getValidPlayerName() throws IOException;

  int getValidPlaceNumber() throws IOException;
}