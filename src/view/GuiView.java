package view;

import java.io.IOException;

public interface GuiView extends View {

  void showGuiMessage(String title, String message, String buttonText);

  void updatePlayerInfoPanel() throws IOException;
}
