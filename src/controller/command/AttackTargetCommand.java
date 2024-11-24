package controller.command;

import java.io.IOException;
import model.player.Player;
import model.town.Town;
import view.GameView;

/**
 * Command to handle a player's attempt to attack the target character.
 * Implements the Command interface as part of the Command pattern.
 */
public class AttackTargetCommand implements Command {
  private final Town town;
  private final GameView view;

  /**
   * Constructs a new AttackTargetCommand.
   *
   * @param gameTown the town model
   * @param gameView the game view
   */
  public AttackTargetCommand(Town gameTown, GameView gameView) {
    this.town = gameTown;
    this.view = gameView;
  }

  @Override
  public void execute() throws IOException {
    Player currentPlayer = town.getPlayers().get(town.getCurrentPlayerIndex());

    if (currentPlayer.isComputerControlled()) {
      town.executeComputerAttack(currentPlayer);
    } else {
      town.handleHumanAttack(currentPlayer);
    }

    if (town.isGameOver()) {
      view.showMessage("Congratulations! You have defeated the target!");
    } else {
      town.switchToNextPlayer();
    }
  }
}