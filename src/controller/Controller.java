package controller;

public interface Controller {
  /**
   * Responsible for executing a player's turn in the game.
   */
  void takeTurn();

  /**
   * Displays the player's information.
   */
  void displayPlayerInfo();

  /**
   * Displays the place's information.
   */
  void displayPlaceInfo();
}
