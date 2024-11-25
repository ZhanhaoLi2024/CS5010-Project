package controller.support;

import controller.GuiController;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EventHandler implements ActionListener, KeyListener {
  private final GuiController controller;
  private final Map<Integer, String> keyCommands;
  private final Map<String, String> actionCommands;

  /**
   * Constructs a new EventHandler.
   *
   * @param gameController the game controller
   */
  public EventHandler(GuiController gameController) {
    this.controller = gameController;
    this.keyCommands = new HashMap<>();
    this.actionCommands = new HashMap<>();
    initializeCommandMappings();
  }

  private void initializeCommandMappings() {
    // Initialize key command mappings
    keyCommands.put(KeyEvent.VK_M, "MOVE");
    keyCommands.put(KeyEvent.VK_L, "LOOK");
    keyCommands.put(KeyEvent.VK_P, "PICK");
    keyCommands.put(KeyEvent.VK_A, "ATTACK");
    keyCommands.put(KeyEvent.VK_T, "MOVE_PET");

    // Initialize action command mappings
    actionCommands.put("MOVE_BUTTON", "MOVE");
    actionCommands.put("LOOK_BUTTON", "LOOK");
    actionCommands.put("PICK_BUTTON", "PICK");
    actionCommands.put("ATTACK_BUTTON", "ATTACK");
    actionCommands.put("PET_BUTTON", "MOVE_PET");
    actionCommands.put("ADD_PLAYER_BUTTON", "ADD_PLAYER");
    actionCommands.put("ADD_COMPUTER_BUTTON", "ADD_COMPUTER");
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String actionCommand = e.getActionCommand();
    String gameCommand = actionCommands.get(actionCommand);

    if (gameCommand != null) {
      try {
        controller.executeCommand(gameCommand);
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  @Override
  public void keyPressed(KeyEvent e) {
    String command = keyCommands.get(e.getKeyCode());
    if (command != null) {
      try {
        controller.executeCommand(command);
      } catch (IOException ex) {
        throw new RuntimeException(ex);
      }
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
    // Not used
  }

  @Override
  public void keyTyped(KeyEvent e) {
    // Not used
  }

  /**
   * Registers a new key command.
   *
   * @param keyCode the key code to map
   * @param command the command to execute
   */
  public void registerKeyCommand(int keyCode, String command) {
    keyCommands.put(keyCode, command);
  }

  /**
   * Registers a new action command.
   *
   * @param actionCommand the action command to map
   * @param gameCommand   the game command to execute
   */
  public void registerActionCommand(String actionCommand, String gameCommand) {
    actionCommands.put(actionCommand, gameCommand);
  }
}