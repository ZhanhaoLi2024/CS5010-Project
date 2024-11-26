package controller.command;

import java.io.IOException;
import model.town.Town;

/**
 * Command to handle a player's attempt to attack the target character.
 * Implements the Command interface as part of the Command pattern.
 */
public class AttackTargetCommand implements Command {
  private final Town town;
  private final String attackItemName;

  /**
   * Constructs a new AttackTargetCommand.
   *
   * @param gameTown the town model
   * @param itemName the name of the item to use in the attack
   */
  public AttackTargetCommand(Town gameTown, String itemName) {
    this.town = gameTown;
    this.attackItemName = itemName;
  }

  @Override
  public void execute() throws IOException {
    boolean killSuccess = town.attackTarget(attackItemName);
    if (killSuccess) {

    } else {
      town.switchToNextPlayer();
    }
  }
}