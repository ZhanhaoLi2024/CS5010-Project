package model.dto;

import java.util.List;

import model.item.Item;
import model.pet.Pet;
import model.place.Place;
import model.player.Player;
import model.target.Target;

/**
 * Interface for mapping between model objects and DTOs.
 * Provides clear separation between model and view layers.
 */
public interface DTOMapper {
  GameStateDTO toGameStateDto(List<Player> players, Target target, Pet pet,
                              int currentTurn, int maxTurns, boolean isGameOver,
                              int currentPlayerIndex);

  PlayerDTO toPlayerDto(Player player);

  PlaceDTO toPlaceDto(Place place);

  ItemDTO toItemDto(Item item);

  PetDTO toPetDto(Pet pet);

  TargetDTO toTargetDto(Target target);
}
