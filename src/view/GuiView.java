package view;


import controller.support.PlayerInfoDTO;
import java.util.concurrent.CompletableFuture;

public interface GuiView extends View {

  void updatePlayerInfo(PlayerInfoDTO info);

  void showGuiMessage(String title, String message, String buttonText);

  CompletableFuture<Integer> showGuiNumberMessage(String title, String message, String buttonText,
                                                  int minValue, int maxValue);

  void showGuiMessage(String title, String message, String buttonText, Runnable onClose);

  void resetGame();
}
