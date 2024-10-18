package controller.command;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import model.player.Player;

/**
 * Command to display information about all players or a specific player.
 */
public class DisplayPlayerInfoCommand implements Command {
  private final List<Player> players;
  private final Appendable output;
  private final Scanner scanner;

  /**
   * Constructs a new DisplayPlayerInfoCommand.
   *
   * @param players the list of players in the game
   * @param output  the output stream to write messages to
   * @param scanner the scanner to get user input
   */
  public DisplayPlayerInfoCommand(List<Player> players, Appendable output, Scanner scanner) {
    this.players = players;
    this.output = output;
    this.scanner = scanner;
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
          showAllPlayersInfo();
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

  /**
   * Shows information about all players.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  private void showAllPlayersInfo() throws IOException {
    if (players.isEmpty()) {
      output.append("No players found.\n");
      return;
    }
    output.append("All players info:\n");
    int index = 1;
    for (Player player : players) {
      output.append(index + ". Player name: ").append(player.getName()).append("\n");
      output.append("Player current place: ").append(player.getCurrentPlace().getName())
          .append("\n");
      output.append("--------------------\n");
      index++;
    }
  }

  /**
   * Shows information about a specific player.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  private void showSpecificPlayerInfo() throws IOException {
    output.append("Enter the player's name:\n");
    String playerName = scanner.nextLine();
    Player player = players.stream()
        .filter(p -> p.getName().equals(playerName))
        .findFirst()
        .orElse(null);

    if (player != null) {
      output.append("Player name: ").append(player.getName()).append("\n");
      output.append("Player current place: ").append(player.getCurrentPlace().getName())
          .append("\n");
    } else {
      output.append("Player not found.\n");
    }
  }
}