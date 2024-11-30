package view;


import controller.support.PlayerInfoDTO;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for a view that displays a graphical user interface.
 */
public interface GuiView extends View {

  /**
   * Updates the player's information displayed in the GUI.
   *
   * @param info the new player information
   */
  void updatePlayerInfo(PlayerInfoDTO info);

  /**
   * Shows a message to the user in the GUI.
   *
   * @param title      the title of the message
   * @param message    the message to display
   * @param buttonText the text to display on the button
   */
  void showGuiMessage(String title, String message, String buttonText);

  /**
   * Shows a message to the user in the GUI and calls a callback when the dialog closes.
   *
   * @param title      the title of the message
   * @param message    the message to display
   * @param buttonText the text to display on the button
   * @param onClose    the callback to call when the dialog closes
   */
  void showGuiMessage(String title, String message, String buttonText, Runnable onClose);


  /**
   * Shows a message to the user in the GUI and returns a future for the result.
   *
   * @param title      the title of the message
   * @param message    the message to display
   * @param buttonText the text to display on the button
   * @param minValue   the minimum value for the number input
   * @param maxValue   the maximum value for the number input
   * @return a future for the result
   */
  CompletableFuture<Integer> showGuiNumberMessage(String title, String message, String buttonText,
                                                  int minValue, int maxValue);

  /**
   * Resets the game state in the GUI.
   */
  void resetGame();
}
