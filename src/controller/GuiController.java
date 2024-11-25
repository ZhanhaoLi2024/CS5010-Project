package controller;

import model.town.Town;
import view.GameView;

public class GuiController implements Controller {

  private static final int CELL_SIZE = 90;
  private Town town;
  private int maxTurns;
  private GameView view;
  private int currentTurn;
  private boolean quitGame;
  private boolean continueGame;
  private boolean isGui;

  public GuiController(Town gameTown, GameView gameView, int gameMaxTurns) {
    this.town = gameTown;
    this.view = gameView;
    this.maxTurns = gameMaxTurns;
    this.currentTurn = 1;
    this.quitGame = false;
  }

  @Override
  public void startGame() {

  }

  @Override
  public void setView(GameView gameView, boolean gui) {
    if (this.view != null) {
      throw new IllegalStateException("View is already set");
    }
    this.view = gameView;
    this.isGui = gui;
  }
}
