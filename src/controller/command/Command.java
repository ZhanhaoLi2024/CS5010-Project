package controller.command;

import java.io.IOException;

/**
 * The Command interface for executing actions in the game.
 */
public interface Command {

  /**
   * Executes the command.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  void execute() throws IOException;
}