package controller.command;

import java.io.IOException;
import java.util.Scanner;
import model.town.Town;

/**
 * Command to add a human player or computer player to the game.
 */
public class AddPlayerCommand implements Command {
  private final Appendable output;
  private final Town town;
  private final Scanner scanner;
  private final boolean isComputerPlayer;

  /**
   * Constructs a new AddHumanPlayerCommand.
   *
   * @param gameOutput  the output stream to write messages to
   * @param gameTown    the town where the players are located
   * @param gameScanner the scanner to get user input
   * @param playerType  whether the player is a computer player
   */
  public AddPlayerCommand(Town gameTown, Appendable gameOutput,
                          Scanner gameScanner, boolean playerType) {
    this.output = gameOutput;
    this.town = gameTown;
    this.scanner = gameScanner;
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

  /**
   * Adds a human player to the game.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  private void addPlayerMenu() throws IOException {
    boolean addPlayerContinue = true;
    town.addPlayer();
    while (addPlayerContinue) {
      output.append("Do you want to add another player? (yes/no)\n");
      String choice = scanner.nextLine();
      if ("yes".equalsIgnoreCase(choice)) {
        town.addPlayer();
      } else if ("no".equalsIgnoreCase(choice)) {
        addPlayerContinue = false;
      } else {
        output.append("Invalid input. Please enter 'yes' or 'no'.\n");
      }
    }
  }
}