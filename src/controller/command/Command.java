package controller.command;

import model.town.Town;

/**
 * The Command interface for executing actions in the game.
 */
public interface Command {
  /**
   * Executes the command on the given town.
   *
   * @param town The town where the command is executed.
   */
  void execute(Town town);
}