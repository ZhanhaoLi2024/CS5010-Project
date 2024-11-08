package controller.command;

import java.io.IOException;
import model.town.Town;

/**
 * Command interface - represents game actions
 */
public interface Command {
  /**
   * Executes the command
   *
   * @param town the town to execute the command on
   * @throws IOException if an IO error occurs
   */
  void execute(Town town) throws IOException;
}