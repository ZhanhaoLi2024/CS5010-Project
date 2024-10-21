package controller.command;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import model.place.Place;
import model.player.Player;
import model.player.PlayerModel;
import model.town.Town;

/**
 * Command to add a human player or computer player to the game.
 */
public class AddPlayerCommand implements Command {
  private final List<Player> players;
  private final Appendable output;
  private final Town town;
  private final Scanner scanner;

  /**
   * Constructs a new AddHumanPlayerCommand.
   *
   * @param players the list of players in the game
   * @param output  the output stream to write messages to
   * @param town    the town where the players are located
   * @param scanner the scanner to get user input
   */
  public AddPlayerCommand(List<Player> players, Appendable output, Town town,
                          Scanner scanner) {
    this.players = players;
    this.output = output;
    this.town = town;
    this.scanner = scanner;
  }

  @Override
  public void execute() throws IOException {
    addPlayerMenu();
  }

  /**
   * Adds a human player to the game.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  private void addPlayerMenu() throws IOException {
    boolean addPlayerContinue = true;
    addPlayer();
    while (addPlayerContinue) {
      output.append("Do you want to add another player? (yes/no)\n");
      String choice = scanner.nextLine();
      if ("yes".equalsIgnoreCase(choice)) {
        addPlayer();
      } else if ("no".equalsIgnoreCase(choice)) {
        addPlayerContinue = false;
      } else {
        output.append("Invalid input. Please enter 'yes' or 'no'.\n");
      }
    }
  }

  /**
   * Adds a human player to the game.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  private void addPlayer() throws IOException {
    output.append("Enter the player's name:\n");
    String playerName = scanner.nextLine();
    Random random = new Random();
    Place randomPlace = town.getPlaces().get(random.nextInt(town.getPlaces().size()));
    Player player = new PlayerModel(playerName, false, 3, randomPlace);
    output.append("Player name: ").append(player.getName()).append("\n");
    output.append(player.getName()).append(" current place: ")
        .append(player.getCurrentPlace().getName()).append("\n");
    output.append("Player added.\n");
    players.add(player);
  }

  /**
   * Adds a computer player to the game.
   *
   * @throws IOException if there is an issue with I/O operations.
   */
  public void addComputerPlayer() throws IOException {
    Random random = new Random();
    Place randomPlace = town.getPlaces().get(random.nextInt(town.getPlaces().size()));
    Player player = new PlayerModel("David(Computer)", true, 3, randomPlace);
    players.add(player);
    output.append("Computer player 'David' added.\n");
  }
}