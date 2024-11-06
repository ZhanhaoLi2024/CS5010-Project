package controller.command;

import java.io.IOException;
import java.util.List;
import model.player.Player;

/**
 * Command to allow a player to look around in their current place.
 */
public class LookAroundCommand implements Command {
  private final Player player;
  private final Appendable output;
  private final List<Player> players;

  /**
   * Constructs a new LookAroundCommand.
   *
   * @param player the player who is looking around.
   * @param output the output stream to write messages to.
   */
  public LookAroundCommand(Player player, Appendable output, List<Player> players) {
    this.player = player;
    this.output = output;
    this.players = players;
  }

  @Override
  public void execute() throws IOException {
    player.getPlayerCurrentPlaceInfo();
  }
}