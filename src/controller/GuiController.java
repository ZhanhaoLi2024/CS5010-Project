package controller;

import controller.support.PlayerInfoDTO;
import java.io.IOException;

public interface GuiController extends Controller {
  /**
   * Gets the current player information in a format suitable for the GUI.
   *
   * @return PlayerInfoDTO containing current player's information
   */
  PlayerInfoDTO getCurrentPlayerInfo() throws IOException;
}
