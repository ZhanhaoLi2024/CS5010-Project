package model.dto;

import java.util.List;
import java.util.stream.Collectors;

import model.item.Item;
import model.pet.Pet;
import model.place.Place;
import model.player.Player;
import model.target.Target;

/**
 * Implementation of DTOMapper interface.
 * Handles all conversions between model objects and DTOs.
 */
public class DTOMapperImpl implements DTOMapper {

  @Override
  public GameStateDTO toGameStateDto(List<Player> players, Target target, Pet pet,
                                     int currentTurn, int maxTurns, boolean isGameOver,
                                     int currentPlayerIndex) {
    List<PlayerDTO> playerDtos = players.stream()
        .map(this::toPlayerDto)
        .collect(Collectors.toList());

    return new GameStateDTO(
        playerDtos,
        toTargetDto(target),
        toPetDto(pet),
        currentTurn,
        maxTurns,
        isGameOver,
        currentPlayerIndex
    );
  }

  @Override
  public PlayerDTO toPlayerDto(Player player) {
    List<ItemDTO> itemDtos = player.getCurrentCarriedItems().stream()
        .map(this::toItemDto)
        .collect(Collectors.toList());

    return new PlayerDTO(
        player.getName(),
        player.getPlayerCurrentPlaceNumber(),
        itemDtos,
        player.getCarryLimit(),
        player.isComputerControlled()
    );
  }

  @Override
  public PlaceDTO toPlaceDto(Place place) {
    List<ItemDTO> itemDtos = place.getItems().stream()
        .map(this::toItemDto)
        .collect(Collectors.toList());

    List<Integer> neighborNumbers = place.getNeighbors().stream()
        .map(p -> Integer.parseInt(p.getPlaceNumber()))
        .collect(Collectors.toList());

    List<String> playerNames = place.getCurrentPlacePlayers().stream()
        .map(Player::getName)
        .collect(Collectors.toList());

    return new PlaceDTO(
        place.getName(),
        Integer.parseInt(place.getPlaceNumber()),
        itemDtos,
        neighborNumbers,
        playerNames,
        place.getRow1(),
        place.getCol1(),
        place.getRow2(),
        place.getCol2()
    );
  }

  @Override
  public ItemDTO toItemDto(Item item) {
    return new ItemDTO(
        item.getName(),
        item.getDamage()
    );
  }

  @Override
  public PetDTO toPetDto(Pet pet) {
    return new PetDTO(
        pet.getName(),
        pet.getPetCurrentPlaceNumber()
    );
  }

  @Override
  public TargetDTO toTargetDto(Target target) {
    return new TargetDTO(
        target.getName(),
        target.getHealth(),
        Integer.parseInt(target.getCurrentPlace().getPlaceNumber())
    );
  }
}