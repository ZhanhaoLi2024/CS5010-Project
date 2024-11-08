package controller.command;

import java.io.IOException;
import model.town.Town;

/**
 * Refactored LookAroundCommand implementing the Command interface
 */
public class LookAroundCommand implements Command {
  @Override
  public void execute(Town town) throws IOException {
    town.lookAround();
  }
}