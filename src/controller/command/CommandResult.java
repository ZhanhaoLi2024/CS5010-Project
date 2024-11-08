package controller.command;

import model.dto.GameStateDTO;

/**
 * Command result class to encapsulate command execution outcomes
 */
public class CommandResult {
  private final boolean success;
  private final String message;
  private final CommandType type;
  private final GameStateDTO gameState;

  public CommandResult(boolean success, String message, CommandType type, GameStateDTO gameState) {
    this.success = success;
    this.message = message;
    this.type = type;
    this.gameState = gameState;
  }

  public boolean isSuccess() {
    return success;
  }

  public String getMessage() {
    return message;
  }

  public CommandType getType() {
    return type;
  }

  public GameStateDTO getGameState() {
    return gameState;
  }
}