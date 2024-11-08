package controller.command;

import java.io.IOException;
import model.town.Town;

/**
 * Refactored AttackTargetCommand implementing the Command interface
 */
public class AttackTargetCommand implements Command {
  private final Integer itemIndex; // null means poke attack

  public AttackTargetCommand(Integer itemIndex) {
    this.itemIndex = itemIndex;
  }

  @Override
  public void execute(Town town) throws IOException {
    town.attackTarget(itemIndex);
  }
}