package model.town;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import model.dto.DTOMapper;
import model.dto.DTOMapperImpl;
import model.dto.GameStateDTO;
import model.dto.PlaceDTO;
import model.dto.PlayerDTO;
import model.dto.TargetDTO;
import model.item.Item;
import model.pet.Pet;
import model.pet.PetModel;
import model.place.Place;
import model.player.Player;
import model.player.PlayerModel;
import model.target.Target;
import model.target.TargetModel;

/**
 * The TownModel class implements the Town interface and represents a town in a game. A town has a
 * name, a list of places, a list of items, and a target character that can move between different
 * places.
 */
public class TownModel implements Town {
  private final List<Place> places;
  private final List<Item> items;
  private final Target targetCharacter;
  private final List<Player> players;
  private final Appendable output;
  private final Scanner scanner;
  private final Pet pet;
  private final String townName;
  private final String targetName;
  private final int targetHealth;
  private final int maxTurns;
  private final DTOMapper dtoMapper;
  private int currentPlayerIndex;
  private int currentTurn;

  /**
   * Constructs a new TownModel with the specified town loader and filename.
   *
   * @param loader   the town loader to load the town data
   * @param filename the name of the file to load the town from
   * @throws IOException if an I/O error occurs
   */
  public TownModel(TownLoaderInterface loader, String filename, Readable scanner, Appendable output,
                   int maxTurns)
      throws IOException {
    TownData townData = loader.loadTown(filename);
    this.townName = townData.getTownName();
    this.targetName = townData.getTargetName();
    this.targetHealth = townData.getTargetHealth();
    this.pet = new PetModel(townData.getPetName());
    this.places = townData.getPlaces();
    this.items = townData.getItems();
    this.targetCharacter = new TargetModel(targetName, targetHealth, places.get(0), places);
    this.players = new ArrayList<>();
    this.currentPlayerIndex = 0;
    this.scanner = new Scanner(scanner);
    this.output = output;
    this.currentTurn = 1;
    this.maxTurns = maxTurns;
    this.dtoMapper = new DTOMapperImpl();
  }

  @Override
  public boolean isGameOver() {
    return currentTurn > maxTurns || targetCharacter.isDefeated();
  }

  @Override
  public Place getPlaceByNumber(int placeNumber) {
    return places.get(placeNumber - 1);
  }

  @Override
  public int getPlaceNumberByName(String placeName) throws IOException {
    for (int i = 0; i < places.size(); i++) {
      if (places.get(i).getName().equals(placeName)) {
        return i + 1;
      }
    }
    output.append("Place not found.\n");
    return -1;
  }

  @Override
  public void moveTarget() {
    targetCharacter.moveToNextPlace();
  }

  @Override
  public String getName() {
    return townName;
  }

  @Override
  public Target getTarget() {
    return targetCharacter;
  }

  @Override
  public Pet getPet() {
    return this.pet;
  }

  /**
   * Determines if a place is visible (not blocked by pet).
   *
   * @param place the place to check visibility for
   * @return true if the place is visible, false otherwise
   */
  @Override
  public boolean isPlaceVisible(Place place) {
    if (place == null) {
      throw new IllegalArgumentException("Place cannot be null");
    }
    return !place.getPlaceNumber().equals(String.valueOf(pet.getPetCurrentPlaceNumber()));
  }

  /**
   * Moves the pet to a new location.
   *
   * @param placeNumber the number of the place to move the pet to
   * @throws IllegalArgumentException if the place number is invalid
   */
  @Override
  public void movePet(int placeNumber) throws IOException {
    // Validate place number
    if (placeNumber <= 0 || placeNumber > places.size()) {
      throw new IllegalArgumentException("Invalid place number!");
    }

    // Move the pet
    pet.movePet(placeNumber);
    output.append(String.format("Pet %s moved to %s\n",
        pet.getName(),
        getPlaceByNumber(placeNumber).getName()));
  }

  public List<Place> getPlaces() {
    return places;
  }

  @Override
  public String getTargetName() {
    return targetName;
  }

  @Override
  public int getTargetHealth() {
    return targetHealth;
  }

  @Override
  public String getTownName() {
    return townName;
  }

  @Override
  public List<Item> getItems() {
    return items;
  }

  @Override
  public List<Player> getPlayers() {
    return players;
  }

  @Override
  public List<Place> getCurrentPlaceNeighbors(Place place) {
    List<Place> neighbors = new ArrayList<>();
    for (Place p : places) {
      if (!p.equals(place) && place.isNeighbor(p)) {
        neighbors.add(p);
      }
    }
    return neighbors;
  }

  @Override
  public void addComputerPlayer() throws IOException {
    Random random = new Random();
    Place randomPlace = getPlaces().get(random.nextInt(getPlaces().size()));
    Player player = new PlayerModel("David(Computer)", true, 3, 1, System.out, scanner);
    players.add(player);
    output.append("Computer player 'David' added.\n");
  }

  @Override
  public void addPlayer() throws IOException {
    output.append("Enter the player's name:\n");
    String playerName = scanner.nextLine();
    output.append("Enter your starting place number:\n");
    String placeIndex = scanner.nextLine();
    int placeNumber = Integer.parseInt(placeIndex);
    Place startingPlace = places.get(Integer.parseInt(placeIndex) - 1);
    output.append("Enter your limit of carrying items:\n");
    String carryLimit = scanner.nextLine();
    Player player = new PlayerModel(playerName, false, Integer.parseInt(carryLimit),
        placeNumber,
        output, scanner);
    output.append("Player name: ").append(player.getName()).append("\n");
    output.append(player.getName()).append("Current place: ").append(startingPlace.getName())
        .append("\n");
    output.append("You can carry up to ").append(String.valueOf(player.getCarryLimit()))
        .append(" items.\n");
    output.append("Player added.\n");
    players.add(player);
  }

  @Override
  public void movePlayer(int targetPlaceNumber) throws IOException {
    Player currentPlayer = players.get(currentPlayerIndex);
    Place currentPlace = getPlaceByNumber(currentPlayer.getPlayerCurrentPlaceNumber());
    Place targetPlace = getPlaceByNumber(targetPlaceNumber);

    if (!currentPlace.isNeighbor(targetPlace)) {
      throw new IllegalArgumentException("Invalid move: places must be neighbors");
    }

    currentPlace.removePlayer(currentPlayer);
    currentPlayer.moveToPlaceNumber(targetPlaceNumber);
    targetPlace.addPlayer(currentPlayer);
    switchToNextPlayer();
  }

  /**
   * Handles movement logic for human player.
   */
  private void handleHumanPlayerMove(Player player, Place currentPlace, List<Place> neighbors)
      throws IOException {
    output.append("Neighbors of ").append(currentPlace.getName())
        .append(":\n");
    for (int i = 0; i < neighbors.size(); i++) {
      output.append(String.valueOf(i + 1)).append(". ")
          .append(neighbors.get(i).getName())
          .append("\n");
    }
    output.append("Enter the number of the neighbor to move to:\n");

    try {
      int neighborNumber = Integer.parseInt(scanner.nextLine());
      if (neighborNumber < 1 || neighborNumber > neighbors.size()) {
        output.append("Invalid neighbor number.\n");
        return;
      }
      movePlayerToNeighbor(player, neighbors.get(neighborNumber - 1));
    } catch (NumberFormatException e) {
      output.append("Invalid input. Please enter a number.\n");
    }
  }

  /**
   * Moves a player to the specified neighboring place.
   */
  private void movePlayerToNeighbor(Player player, Place neighbor) throws IOException {
    int newPlaceNumber = getPlaceNumberByName(neighbor.getName());
    player.moveToPlaceNumber(newPlaceNumber);
    output.append("Moved to ").append(neighbor.getName()).append("\n");
  }

  @Override
  public void pickUpItem(int itemIndex) throws IOException {
    Player currentPlayer = players.get(currentPlayerIndex);
    Place currentPlace = getPlaceByNumber(currentPlayer.getPlayerCurrentPlaceNumber());
    List<Item> items = currentPlace.getItems();

    if (itemIndex < 0 || itemIndex >= items.size()) {
      throw new IllegalArgumentException("Invalid item index");
    }

    Item item = items.get(itemIndex);
    currentPlayer.pickUpItem(item);
    currentPlace.removeItem(item);
    switchToNextPlayer();
  }

  @Override
  public void switchToNextPlayer() throws IOException {
    if (players.size() <= 1) {
      return;
    }
    currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    if (currentPlayerIndex == 0) {
      currentTurn++;
    }
  }

  @Override
  public int getCurrentTurn() {
    return currentTurn;
  }

  @Override
  public Boolean isComputerControllerPlayer() {
    return players.get(currentPlayerIndex).isComputerControlled();
  }

  /**
   * Gets the index of the current player.
   *
   * @return the index of the current player
   */
  @Override
  public int getCurrentPlayerIndex() {
    return currentPlayerIndex;
  }

  /**
   * Checks if a player can be seen by other players.
   * A player is visible if another player is in the same room or a neighboring room.
   *
   * @param player the player to check visibility for
   * @return true if the player can be seen by others, false otherwise
   */
  @Override
  public boolean isPlayerVisible(Player player) {
    if (player == null) {
      throw new IllegalArgumentException("Player cannot be null");
    }
    Place playerPlace = getPlaceByNumber(player.getPlayerCurrentPlaceNumber());
    for (Player otherPlayer : players) {
      if (otherPlayer != player) {
        Place otherPlace = getPlaceByNumber(otherPlayer.getPlayerCurrentPlaceNumber());
        if (playerPlace.equals(otherPlace) || playerPlace.isNeighbor(otherPlace)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Executes an attack with a specific item.
   *
   * @param player the player performing the attack
   * @param item   the item being used
   * @throws IOException              if there is an error with output
   * @throws IllegalArgumentException if player or item is null
   */
  @Override
  public void executeItemAttack(Player player, Item item) throws IOException {
    if (player == null || item == null) {
      throw new IllegalArgumentException("Player and item cannot be null");
    }

    String playerPlaceNumber = String.valueOf(player.getPlayerCurrentPlaceNumber());
    String targetPlaceNumber = targetCharacter.getCurrentPlace().getPlaceNumber();

    // Validate player is in same room as target
    if (!playerPlaceNumber.equals(targetPlaceNumber)) {
      output.append("Attack failed: You must be in the same room as the target!\n");
      return;
    }

    // Check if player is visible to others
    if (isPlayerVisible(player)) {
      output.append("Attack failed: Other players have witnessed your attempt!\n");
      return;
    }

    // Execute attack (always successful if not seen)
    boolean targetDefeated = targetCharacter.takeDamage(item.getDamage());

    // Remove used item from player's inventory
    player.getCurrentCarriedItems().remove(item);
    output.append("Attack successful with ").append(item.getName())
        .append(" for ").append(String.valueOf(item.getDamage())).append(" damage!\n");

    if (targetDefeated) {
      output.append(player.getName()).append(" has eliminated the target!\n");
    }
  }

  /**
   * Checks if the player is in the same space as the target.
   *
   * @param player The player to check
   * @return true if the player is in the same space as the target
   */
  private boolean isPlayerWithTarget(Player player) {
    return String.valueOf(player.getPlayerCurrentPlaceNumber())
        .equals(targetCharacter.getCurrentPlace().getPlaceNumber());
  }

  @Override
  public AttackResult attackTarget(Integer itemIndex) throws IOException {
    Player currentPlayer = players.get(currentPlayerIndex);

    // Validate player is in same room as target
    if (!isPlayerWithTarget(currentPlayer)) {
      return new AttackResult("Player must be in same room as target");
    }

    // Check if attack is visible
    if (isPlayerVisible(currentPlayer)) {
      return new AttackResult("Attack was seen by other players");
    }

    int damage;
    String itemUsed;

    // Handle poke attack (itemIndex null) or item attack
    if (itemIndex == null) {
      damage = 1;
      itemUsed = "poke";
    } else {
      List<Item> items = currentPlayer.getCurrentCarriedItems();
      if (itemIndex < 0 || itemIndex >= items.size()) {
        return new AttackResult("Invalid item index");
      }
      Item item = items.get(itemIndex);
      damage = item.getDamage();
      itemUsed = item.getName();
      currentPlayer.getCurrentCarriedItems().remove(item);
    }

    boolean targetDefeated = targetCharacter.takeDamage(damage);
    switchToNextPlayer();

    return new AttackResult(
        true,
        String.format("Attack successful with %s for %d damage", itemUsed, damage),
        targetDefeated,
        damage
    );
  }

  @Override
  public int getMaxTurns() {
    return maxTurns;
  }

  @Override
  public void showPlayerCurrentInfo() throws IOException {
    PlayerDTO currentPlayer = getCurrentPlayerInfo();
    PlaceDTO currentPlace = getPlaceInfo(currentPlayer.getCurrentPlaceNumber());

    output.append("\nCurrent player: ").append(currentPlayer.getName())
        .append("\nLocation: ").append(currentPlace.getName())
        .append("\nItems carried: ")
        .append(String.valueOf(currentPlayer.getItems().size()))
        .append("/")
        .append(String.valueOf(currentPlayer.getCarryLimit()))
        .append("\n");
  }

  @Override
  public GameStateDTO getGameState() {
    return dtoMapper.toGameStateDto(
        players,
        targetCharacter,
        pet,
        currentTurn,
        maxTurns,
        isGameOver(),
        currentPlayerIndex
    );
  }

  @Override
  public PlaceDTO getPlaceInfo(int placeNumber) {
    Place place = getPlaceByNumber(placeNumber);
    return dtoMapper.toPlaceDto(place);
  }

  @Override
  public PlayerDTO getCurrentPlayerInfo() {
    return dtoMapper.toPlayerDto(players.get(currentPlayerIndex));
  }

  @Override
  public TargetDTO getTargetInfo() {
    return dtoMapper.toTargetDto(targetCharacter);
  }
}
