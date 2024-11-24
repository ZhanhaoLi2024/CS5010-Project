package controller.command;

import java.io.IOException;

/**
 * The Command interface defines the contract for actions that can be executed in the game.
 * It follows the Command pattern to encapsulate different game operations and allows for
 * uniform execution of varied game actions.
 */
public interface Command {
  /**
   * Executes the command's specific action in the game.
   *
   * @throws IOException if there is an error during command execution
   */
  void execute() throws IOException;
}