package controller.command;

import java.io.IOException;
import model.player.Player;
import model.town.Town;

/**
 * Command to handle a player's attempt to attack the target character.
 * Implements the Command interface as part of the Command pattern.
 */
public class AttackTargetCommand implements Command {
  private final Town town;
  private final Appendable output;

  /**
   * Constructs a new AttackTargetCommand.
   *
   * @param gameTown   the town where the attack takes place
   * @param gameOutput the output stream to write messages to
   */
  public AttackTargetCommand(Town gameTown, Appendable gameOutput) {
    this.town = gameTown;
    this.output = gameOutput;
  }

  @Override
  public void execute() throws IOException {
    Player currentPlayer = town.getPlayers().get(town.getCurrentPlayerIndex());

    // Always try to make an attempt, regardless of success
    if (currentPlayer.isComputerControlled()) {
      town.executeComputerAttack(currentPlayer);
    } else {
      town.handleHumanAttack(currentPlayer);
    }

    // Always switch turns after an attempt
    if (town.isGameOver()) {
      output.append("Congratulations! You have defeated the target!\n");
    } else {

      town.switchToNextPlayer();
    }
  }
}