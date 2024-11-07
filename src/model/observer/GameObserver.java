package model.observer;

import model.dto.GameStateDTO;
import model.dto.PlaceDTO;
import model.dto.PlayerDTO;
import model.dto.TargetDTO;

/**
 * GameObserver interface defines methods that will be called when game state changes.
 * This is implemented by the Controller to receive notifications from the Model.
 */
public interface GameObserver {
  /**
   * Called when the overall game state changes
   *
   * @param gameState the new game state
   */
  void onGameStateChanged(GameStateDTO gameState);

  /**
   * Called when a specific place's state changes
   *
   * @param placeInfo updated place information
   */
  void onPlaceStateChanged(PlaceDTO placeInfo);

  /**
   * Called when a player's state changes
   *
   * @param playerInfo updated player information
   */
  void onPlayerStateChanged(PlayerDTO playerInfo);

  /**
   * Called when the target's state changes
   *
   * @param targetInfo updated target information
   */
  void onTargetStateChanged(TargetDTO targetInfo);
}