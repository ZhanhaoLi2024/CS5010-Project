package controller.command;

import java.io.IOException;

/**
 * The Command interface for executing actions in the game.
 */
public interface Command {

  void execute() throws IOException;
}