package controller.command;

import java.io.IOException;
import model.town.Town;
import view.GameView;

/**
 * Command to add a human player or computer player to the game.
 */
public class AddPlayerCommand implements Command {
  private final Town town;
  private final GameView view;
  private final boolean isComputerPlayer;

  /**
   * Constructs a new AddPlayerCommand.
   *
   * @param gameTown   the town model
   * @param gameView   the game view
   * @param playerType whether the player is a computer player
   */
  public AddPlayerCommand(Town gameTown, GameView gameView, boolean playerType) {
    this.town = gameTown;
    this.view = gameView;
    this.isComputerPlayer = playerType;
  }

  @Override
  public void execute() throws IOException {
    if (isComputerPlayer) {
      town.addComputerPlayer();
    } else {
      addPlayerMenu();
    }
  }

  private void addPlayerMenu() throws IOException {
    boolean addPlayerContinue = true;
    town.addPlayer();
    while (addPlayerContinue) {
      view.showMessage("Do you want to add another player? (yes/no)");
      String choice = view.getUserInput();
      if ("yes".equalsIgnoreCase(choice)) {
        town.addPlayer();
      } else if ("no".equalsIgnoreCase(choice)) {
        addPlayerContinue = false;
      } else {
        view.showMessage("Invalid input. Please enter 'yes' or 'no'.");
      }
    }
  }
}