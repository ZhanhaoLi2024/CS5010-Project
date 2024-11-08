package controller.command;

import java.io.IOException;
import model.town.Town;

/**
 * Refactored AddPlayerCommand implementing the Command interface
 */
public class AddPlayerCommand implements Command {
  private final boolean isComputerPlayer;

  public AddPlayerCommand(boolean isComputerPlayer) {
    this.isComputerPlayer = isComputerPlayer;
  }

  @Override
  public void execute(Town town) throws IOException {
    if (isComputerPlayer) {
      town.addComputerPlayer();
    } else {
      town.addPlayer();
    }
  }
}