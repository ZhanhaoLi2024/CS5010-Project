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
   * @return true if the command was executed successfully, false otherwise
   * @throws IOException if there is an error during command execution
   */
  boolean execute() throws IOException;
}