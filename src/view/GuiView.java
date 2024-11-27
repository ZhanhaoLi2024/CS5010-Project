package view;


import controller.support.PlayerInfoDTO;

public interface GuiView extends View {

  void updatePlayerInfo(PlayerInfoDTO info);

  void showGuiMessage(String title, String message, String buttonText);

  void showGuiMessage(String title, String message, String buttonText, Runnable onClose);

  void resetGame();
}
