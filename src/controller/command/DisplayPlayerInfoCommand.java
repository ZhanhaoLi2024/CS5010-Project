package controller.command;

import java.io.IOException;
import java.util.Scanner;
import model.town.Town;

/**
 * Command to display information about all players or a specific player.
 */
public class DisplayPlayerInfoCommand implements Command {
  private final Appendable output;
  private final Scanner scanner;
  private Town town;

  /**
   * Constructs a new DisplayPlayerInfoCommand.
   *
   * @param town    the town where the players are located
   * @param output  the output stream to write messages to
   * @param scanner the scanner to get user input
   */
  public DisplayPlayerInfoCommand(Town town, Appendable output, Scanner scanner) {
    this.output = output;
    this.scanner = scanner;
    this.town = town;
  }

  @Override
  public void execute() throws IOException {
    showThePlayerInfo();
  }

  /**
   * Shows the player information.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  private void showThePlayerInfo() throws IOException {
    boolean showPlayerInfo = true;
    while (showPlayerInfo) {
      output.append("Please choose an option:\n");
      output.append("1. Show All Players Info\n");
      output.append("2. Show Specific Player Info\n");
      output.append("0. Exit\n");

      int choice = 0;
      try {
        choice = Integer.parseInt(scanner.nextLine());
      } catch (NumberFormatException e) {
        output.append("Invalid input. Please enter a number.\n");
      }

      switch (choice) {
        case 1:
          town.showAllPlayersInfo();
          break;
        case 2:
          showSpecificPlayerInfo();
          break;
        case 0:
          output.append("Exiting...\n");
          showPlayerInfo = false;
          break;
        default:
          output.append("Invalid choice, please try again.\n");
      }
    }
  }

  private void showSpecificPlayerInfo() throws IOException {
    output.append("Enter the player's name:\n");
    String playerName = scanner.nextLine();
    town.showSpecificPlayerInfo(playerName);
  }
}