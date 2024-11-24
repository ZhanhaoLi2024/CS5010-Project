package controller.command;

import java.io.IOException;
import model.town.Town;
import view.GameView;

/**
 * Command to display information about all players or a specific player.
 */
public class DisplayPlayerInfoCommand implements Command {
  private final Town town;
  private final GameView view;

  /**
   * Constructs a new DisplayPlayerInfoCommand.
   *
   * @param gameTown the town model
   * @param gameView the game view
   */
  public DisplayPlayerInfoCommand(Town gameTown, GameView gameView) {
    this.town = gameTown;
    this.view = gameView;
  }

  @Override
  public void execute() throws IOException {
    showThePlayerInfo();
  }

  private void showThePlayerInfo() throws IOException {
    boolean showPlayerInfo = true;
    while (showPlayerInfo) {
      view.showMessage("Please choose an option:\n" +
          "1. Show All Players Info\n" +
          "2. Show Specific Player Info\n" +
          "0. Exit");

      int choice = view.displayMainMenu();

      switch (choice) {
        case 1:
          town.showAllPlayersInfo();
          break;
        case 2:
          showSpecificPlayerInfo();
          break;
        case 0:
          view.showMessage("Exiting...");
          showPlayerInfo = false;
          break;
        default:
          view.showMessage("Invalid choice, please try again.");
      }
    }
  }

  private void showSpecificPlayerInfo() throws IOException {
    view.showMessage("Enter the player's name:");
    String playerName = view.getUserInput();
    town.getPlayerByName(playerName);
  }
}