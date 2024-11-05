package controller.command;

import java.io.IOException;
import java.util.Scanner;
import model.town.Town;

/**
 * Command to move a player to a neighboring place.
 */
public class MovePlayerCommand implements Command {
  private final Town town;
  private final Appendable output;
  private final Scanner scanner;

  /**
   * Constructs a new MovePlayerCommand.
   *
   * @param town    the town where the player is located
   * @param output  the output stream to write messages to
   * @param scanner the scanner to get user input
   */
  public MovePlayerCommand(Town town, Appendable output, Scanner scanner) {
    this.town = town;
    this.output = output;
    this.scanner = scanner;
  }

  @Override
  public void execute() throws IOException {
    town.movePlayer();
  }
}