package controller.command;

import java.io.IOException;
import java.util.Scanner;
import model.player.Player;
import model.town.Town;

/**
 * Command to handle a player's attempt to attack the target character.
 * Implements the Command interface as part of the Command pattern.
 */
public class AttackTargetCommand implements Command {
  private final Town town;
  private final Appendable output;
  private final Scanner scanner;

  /**
   * Constructs a new AttackTargetCommand.
   *
   * @param town    the town where the attack takes place
   * @param output  the output stream to write messages to
   * @param scanner the scanner to get user input for human players
   */
  public AttackTargetCommand(Town town, Appendable output, Scanner scanner) {
    this.town = town;
    this.output = output;
    this.scanner = scanner;
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
    town.switchToNextPlayer();

    // Check both game ending conditions
    if (town.getTarget().isDefeated()) {
      output.append("Game Over! ").append(currentPlayer.getName())
          .append(" has successfully eliminated the target and won the game!\n");
    } else if (town.getCurrentTurn() > town.getMaxTurns()) {
      output.append("Game Over! The target has escaped and nobody wins!\n");
    }
  }
}