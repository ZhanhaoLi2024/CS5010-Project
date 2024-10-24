package controller;

import java.io.IOException;
import model.item.Item;
import model.place.Place;
import model.player.Player;

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
   * Allows a player to show their current information.
   *
   * @param player the player who is showing their information.
   * @throws IOException if there is an issue with I/O operations.
   */
  void showPlayerCurrentInfo(Player player) throws IOException;

  /**
   * Finds a player by their name.
   *
   * @param name the name of the player to find.
   * @return the player with the specified name.
   */
  Player findPlayerByName(String name);

  /**
   * Finds a place by its name.
   *
   * @param name the name of the place to find.
   * @return the place with the specified name.
   */
  Place findPlaceByName(String name);

  /**
   * Finds an item by its name.
   *
   * @param name the name of the item to find.
   * @return the item with the specified name.
   */
  Item findItemByName(String name, Place place);
}