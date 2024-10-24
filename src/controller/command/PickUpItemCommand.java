package controller.command;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import model.item.Item;
import model.place.Place;
import model.player.Player;

/**
 * Command to allow a player to pick up an item in their current place.
 */
public class PickUpItemCommand implements Command {
  private final Player player;
  private final Appendable output;
  private final Scanner scanner;

  /**
   * Constructs a new PickUpItemCommand.
   *
   * @param player the player who is picking up the item.
   * @param output the output stream to write messages to.
   */
  public PickUpItemCommand(Player player, Appendable output, Scanner scanner) {
    this.player = player;
    this.output = output;
    this.scanner = scanner;
  }

  @Override
  public void execute() throws IOException {
    Place currentPlace = player.getCurrentPlace();
    List<Item> items = currentPlace.getItems();
    if (items.isEmpty()) {
      output.append("No items found.\n");
      return;
    } else {
      if (player.isComputerControlled()) {
        Item item = items.get(new Random().nextInt(items.size()));
        player.pickUpItem(item);
        currentPlace.removeItem(item);
        output.append("Picked up ").append(item.getName()).append(".\n");
      } else {
        output.append("Items in ").append(currentPlace.getName()).append(":\n");
        for (int i = 0; i < items.size(); i++) {
          int currentIndex = i + 1;
          output.append(String.valueOf(currentIndex)).append(". ").append(items.get(i).getName())
              .append(" (Damage: ").append(String.valueOf(items.get(i).getDamage())).append(")\n");
        }
        output.append("Enter the item number to pick up:\n");
        int itemNumber = Integer.parseInt(scanner.nextLine());
        if (itemNumber < 1 || itemNumber > items.size()) {
          output.append("Invalid item number.\n");
        } else {
          Item item = items.get(itemNumber - 1);
          player.pickUpItem(item);
          currentPlace.removeItem(item);
          output.append("Picked up ").append(item.getName()).append(".\n");
        }
      }
    }
  }
}