package controller.command;

import java.io.IOException;
import model.town.Town;

/**
 * Command to add a human player or computer player to the game.
 */
public class AddPlayerCommand implements Command {
  private final Town town;
  private final boolean isComputerPlayer;
  private final String playerName;
  private final int startingPlace;
  private final int carryLimit;


  public AddPlayerCommand(Town gameTown, boolean playerType,
                          String name, int place, int limit) {
    this.town = gameTown;
    this.isComputerPlayer = playerType;
    this.playerName = name;
    this.startingPlace = place;
    this.carryLimit = limit;
  }


  @Override
  public void execute() throws IOException {
    town.addPlayer(playerName, startingPlace, carryLimit, isComputerPlayer);
  }

}