package controller.command;

import java.io.IOException;
import java.util.Scanner;
import model.item.Item;
import model.player.Player;
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
          entryPlayerName();
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
//    if (players.isEmpty()) {
//      output.append("No players found.\n");
//      return;
//    }
//    output.append("All players info:\n");
//    int index = 1;
//    for (Player player : players) {
//      output.append(index + ". Player name: ").append(player.getName()).append("\n");
//      output.append("Player current place: ").append(player.getCurrentPlace().getName())
//          .append("\n");
//      output.append("--------------------\n");
//      index++;
//    }
    System.out.println("不能用了");
  }

  /**
   * Shows information about a specific player.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  private void entryPlayerName() throws IOException {
//    output.append("Enter the player's name:\n");
//    String playerName = scanner.nextLine();
//    Player player = players.stream()
//        .filter(p -> p.getName().equals(playerName))
//        .findFirst()
//        .orElse(null);
//    showSpecificPlayerInfo(player);
    System.out.println("不能用了");
  }

  /**
   * Shows information about a specific player.
   *
   * @param player the player to show information about
   * @throws IOException if there is an issue with I/O operations.
   */
  public void showSpecificPlayerInfo(Player player) throws IOException {
    if (player != null) {
      output.append("Player name: ").append(player.getName()).append("\n");
      output.append("Player current place: ").append(player.getCurrentPlace().getName())
          .append("\n");
      if (player.getCurrentCarriedItems().isEmpty()) {
        output.append("Player is not carrying any items.\n");
      } else {
        output.append("Player is carrying the following items:\n");
        for (Item item : player.getCurrentCarriedItems()) {
          output.append(item.getName()).append(" (Damage: ")
              .append(String.valueOf(item.getDamage()))
              .append(")\n");
        }
      }
    } else {
      output.append("Player not found.\n");
    }
  }
}