package controller.command;

import model.place.Place;
import model.player.Player;
import model.town.Town;

/**
 * Command to move a player to a neighboring place.
 */
public class MovePlayerCommand implements Command {
  private final Player player;
  private final Place roomName;

  /**
   * Creates a new move player command with the given player and destination.
   *
   * @param player   The player to move.
   * @param roomName The room name to move the player to.
   */
  public MovePlayerCommand(Player player, Place roomName) {
    this.player = player;
    this.roomName = roomName;
  }

  @Override
  public void execute(Town town) {
    if (!player.getCurrentPlace().getNeighbors().contains(roomName)) {
      throw new IllegalArgumentException("Invalid move: destination is not a neighbor.");
    }
    player.moveTo(roomName);
    System.out.println(player.getName() + " moved to " + roomName.getName());
  }
}