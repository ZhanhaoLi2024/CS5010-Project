package controller.command;

import java.io.IOException;
import model.player.Player;
import model.town.Town;

/**
 * Command to allow a player to look around in their current place.
 */
public class LookAroundCommand implements Command {
  private final Player player;
  private final Appendable output;
  private final Town town;

  /**
   * Constructs a new LookAroundCommand.
   *
   * @param player the player who is looking around.
   * @param output the output stream to write messages to.
   */
  public LookAroundCommand(Player player, Appendable output, Town town) {
    this.player = player;
    this.output = output;
    this.town = town;
  }

  @Override
  public void execute() throws IOException {
//    player.getPlayerCurrentPlaceInfo();
    town.lookAround(player);
  }
}