package controller.support;

import controller.command.Command;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CommandHandler {
  private Map<String, Command> commands;

  /**
   * Constructs a new CommandHandler.
   */
  public CommandHandler() {
    this.commands = new HashMap<>();
  }

  /**
   * Sets the command map.
   *
   * @param commandMap the map of command names to commands
   */
  public void setCommands(Map<String, Command> commandMap) {
    this.commands = new HashMap<>(commandMap);
  }

  /**
   * Registers a new command.
   *
   * @param commandName the name of the command
   * @param command     the command implementation
   */
  public void registerCommand(String commandName, Command command) {
    commands.put(commandName, command);
  }

  /**
   * Executes a command by its name.
   *
   * @param commandName the name of the command to execute
   */
  public void executeCommand(String commandName) throws IOException {
    Command command = commands.get(commandName);
    if (command != null) {
      System.out.println("Executing command-1: " + commandName);
      command.execute();
    }
  }

  /**
   * Checks if a command exists.
   *
   * @param commandName the name of the command
   * @return true if the command exists, false otherwise
   */
  public boolean hasCommand(String commandName) {
    return commands.containsKey(commandName);
  }

  /**
   * Removes a command.
   *
   * @param commandName the name of the command to remove
   */
  public void removeCommand(String commandName) {
    commands.remove(commandName);
  }

  /**
   * Clears all registered commands.
   */
  public void clearCommands() {
    commands.clear();
  }
}