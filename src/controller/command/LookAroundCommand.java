package controller.command;

import java.io.IOException;
import java.util.List;
import model.item.Item;
import model.place.Place;
import model.player.Player;

/**
 * Command to allow a player to look around in their current place.
 */
public class LookAroundCommand implements Command {
  private final Player player;
  private final Appendable output;
  private List<Player> players;

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
    Place currentPlace = this.player.getCurrentPlace();
    List<Place> neighbors = currentPlace.getNeighbors();
    if (neighbors.isEmpty()) {
      output.append("No neighbors found.\n");
    } else {
      output.append("Neighbors of ").append(currentPlace.getName()).append(":\n");
      for (Place neighbor : neighbors) {
        output.append(neighbor.getName()).append("\n");
      }
    }
    List<Item> items = currentPlace.getItems();
    if (items.isEmpty()) {
      output.append("No items found.\n");
    } else {
      output.append("Items in ").append(currentPlace.getName()).append(":\n");
      for (Item item : items) {
        output.append(item.getName()).append(" (Damage: ").append(String.valueOf(item.getDamage()))
            .append(")\n");
      }
    }
    if (players.size() > 1) {
      for (Player player : players) {
        if (player.getCurrentPlace().equals(currentPlace)) {
          if (!player.equals(this.player)) {
            output.append(player.getName()).append(" is in this place.\n");
          }
        }
      }
    }
  }
}