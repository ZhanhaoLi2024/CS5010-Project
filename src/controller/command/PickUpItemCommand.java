package controller.command;

import model.item.Item;
import model.player.Player;
import model.town.Town;

/**
 * Command to allow a player to pick up an item in their current place.
 */
public class PickUpItemCommand implements Command {
  private final Player player;
  private final Item item;

  public PickUpItemCommand(Player player, Item item) {
    this.player = player;
    this.item = item;
  }

  @Override
  public void execute(Town town) {
    player.pickUpItem(item);
    player.getCurrentPlace().removeItem(item);
    System.out.println(player.getName() + " picked up " + item.getName());
  }
}