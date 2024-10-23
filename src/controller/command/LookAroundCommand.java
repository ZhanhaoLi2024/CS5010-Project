package controller.command;

import model.player.Player;
import model.town.Town;

/**
 * Command to allow a player to look around in their current place.
 */
public class LookAroundCommand implements Command {
  private final Player player;

  public LookAroundCommand(Player player) {
    this.player = player;
  }

  @Override
  public void execute(Town town) {
    System.out.println(
        "Player: " + player.getName() + " is at " + player.getCurrentPlace().getName());
    System.out.println("Items here:");
    player.getCurrentPlace().getItems().forEach(item -> System.out.println("- " + item.getName()));
    System.out.println("Neighbors:");
    player.getCurrentPlace().getNeighbors()
        .forEach(place -> System.out.println("- " + place.getName()));
  }
}