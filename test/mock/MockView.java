package mock;

import controller.support.PlayerInfoDTO;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import view.GuiView;

/**
 * Mock implementation of the View interface for testing purposes.
 * Records all method calls and simulates user interactions.
 */
public class MockView implements GuiView {
  private final StringBuilder log;
  private String lastMessage;
  private int nextNumberInput;
  private String nextStringInput;
  private boolean initialized;
  private PlayerInfoDTO lastPlayerInfo;

  /**
   * Constructs a new MockView.
   */
  public MockView() {
    this.log = new StringBuilder();
    this.initialized = false;
    this.nextNumberInput = 1;
    this.nextStringInput = "";
  }

  /**
   * Gets the log of method calls.
   *
   * @return String containing all logged method calls
   */
  public String getLog() {
    return log.toString();
  }

  /**
   * Gets the last message displayed.
   *
   * @return last message displayed
   */
  public String getLastMessage() {
    return lastMessage;
  }

  /**
   * Gets whether the view has been initialized.
   *
   * @return true if initialized, false otherwise
   */
  public boolean isInitialized() {
    return initialized;
  }

  /**
   * Gets the last PlayerInfoDTO received.
   *
   * @return last PlayerInfoDTO received
   */
  public PlayerInfoDTO getLastPlayerInfo() {
    return lastPlayerInfo;
  }

  /**
   * Clears the log.
   */
  public void clearLog() {
    log.setLength(0);
    lastMessage = null;
  }

  /**
   * Sets the next number to return for number input.
   *
   * @param number the number to return
   */
  public void setNextNumberInput(int number) {
    this.nextNumberInput = number;
  }

  /**
   * Sets the next string to return for string input.
   *
   * @param input the string to return
   */
  public void setNextStringInput(String input) {
    this.nextStringInput = input;
  }

  private void logMethodCall(String methodName) {
    log.append(methodName).append(" called\n");
  }

  // View interface implementations
  @Override
  public void initialize() throws IOException {
    logMethodCall("initialize");
    initialized = true;
  }

  @Override
  public void showMessage(String message) throws IOException {
    logMethodCall("showMessage");
    log.append("Message: ").append(message).append("\n");
    lastMessage = message;
  }

  @Override
  public String getStringInput() throws IOException {
    logMethodCall("getStringInput");
    return nextStringInput;
  }

  @Override
  public int getNumberInput() throws IOException {
    logMethodCall("getNumberInput");
    return nextNumberInput;
  }

  @Override
  public void close() throws IOException {
    logMethodCall("close");
    initialized = false;
  }

  // GuiView interface implementations
  @Override
  public void updatePlayerInfo(PlayerInfoDTO info) {
    logMethodCall("updatePlayerInfo");
    lastPlayerInfo = info;
    log.append("Updated player info: ").append(info.getPlayerName()).append("\n");
  }

  @Override
  public void showGuiMessage(String title, String message, String buttonText) {
    logMethodCall("showGuiMessage");
    log.append("GUI Message - Title: ").append(title)
        .append(", Message: ").append(message)
        .append(", Button: ").append(buttonText).append("\n");
    lastMessage = message;
  }

  @Override
  public CompletableFuture<Integer> showGuiNumberMessage(
      String title, String message, String buttonText, int minValue, int maxValue) {
    logMethodCall("showGuiNumberMessage");
    log.append("GUI Number Message - Title: ").append(title)
        .append(", Min: ").append(minValue)
        .append(", Max: ").append(maxValue).append("\n");
    return CompletableFuture.completedFuture(nextNumberInput);
  }

  @Override
  public void showGuiMessage(String title, String message, String buttonText, Runnable onClose) {
    logMethodCall("showGuiMessage with callback");
    log.append("GUI Message with callback - Title: ").append(title)
        .append(", Message: ").append(message)
        .append(", Button: ").append(buttonText).append("\n");
    lastMessage = message;
    if (onClose != null) {
      onClose.run();
    }
  }

  @Override
  public void resetGame() {
    logMethodCall("resetGame");
    initialized = false;
  }
}