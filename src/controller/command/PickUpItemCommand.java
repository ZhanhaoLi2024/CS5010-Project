package controller.command;

import java.io.IOException;
import java.util.Scanner;
import model.town.Town;

/**
 * Command to allow a player to pick up an item in their current place.
 */
public class PickUpItemCommand implements Command {
  private final Appendable output;
  private final Scanner scanner;
  private Town town;

  /**
   * Constructs a new PickUpItemCommand.
   *
   * @param town    the town where the player is located.
   * @param output  the output stream to write messages to.
   * @param scanner the scanner to get user input
   */
  public PickUpItemCommand(Town town, Appendable output, Scanner scanner) {
    this.output = output;
    this.scanner = scanner;
    this.town = town;
  }

  @Override
  public void execute() throws IOException {
    town.pickUpItem();
  }
}