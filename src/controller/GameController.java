package controller;

import java.io.IOException;
import model.place.Place;
import model.player.Player;
import model.town.Town;
import view.GameView;

/**
 * The game controller responsible for handling user input and game commands.
 */
public abstract class GameController implements Controller {
  protected Town model;
  protected GameView view;

  public GameController(Town model, GameView view) {
    this.model = model;
    this.view = view;
  }

  @Override
  public GameView getView() {
    return view;
  }

  @Override
  public void startGame() throws IOException {
    view.initialize();
    view.showWelcomeScreen();
  }

  @Override
  public void endGame() throws IOException {
    view.showMessage("Game Over!");
    view.close();
  }

  /**
   * Updates the view with current game state.
   */
  protected void updateView() throws IOException {
    Player currentPlayer = model.getPlayers().get(model.getCurrentPlayerIndex());
    Place currentPlace = model.getPlaceByNumber(currentPlayer.getPlayerCurrentPlaceNumber());

    view.updatePlayerInfo(currentPlayer, currentPlace);
  }

  /**
   * Handles any errors that occur during game operation.
   */
  protected void handleError(Exception e) throws IOException {
    view.showMessage("Error: " + e.getMessage());
  }
}