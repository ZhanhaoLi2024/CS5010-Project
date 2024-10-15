package player;

import item.Item;
import java.util.ArrayList;
import java.util.List;
import place.Place;

/**
 * The PlayerModel class implements the Player interface and represents a player
 * in a game. A player has a name, a current place, a list of items, and an item
 * limit.
 */
public class PlayerModel implements Player {
  protected String name;
  protected Place currentPlace;
  protected List<Item> items;
  protected int itemLimit;

  /**
   * Constructs a new PlayerModel with the specified name, starting place, and item
   * limit.
   *
   * @param name       the name of the player
   * @param startPlace the place where the player starts
   * @param itemLimit  the maximum number of items the player can carry
   */
  public PlayerModel(String name, Place startPlace, int itemLimit) {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("Player name cannot be null or empty.");
    }
    if (startPlace == null) {
      throw new IllegalArgumentException("Starting place cannot be null.");
    }
    if (itemLimit < 0) {
      throw new IllegalArgumentException("Item limit cannot be negative.");
    }

    this.name = name;
    this.currentPlace = startPlace;
    this.itemLimit = itemLimit;
    this.items = new ArrayList<>();
    currentPlace.addPlayer(this); // Add the player to the starting place
  }

  /**
   * Moves the player to the specified place if it is a neighbor of the current
   * place.
   *
   * @param newPlace the place to move the player to
   */
  @Override
  public void moveToNeighbor(Place newPlace) {
    if (newPlace == null) {
      throw new IllegalArgumentException("Place cannot be null.");
    }
    if (currentPlace == null) {
      throw new IllegalStateException("Current place is null, the player cannot move.");
    }
    if (currentPlace.isNeighbor(newPlace)) {
      currentPlace.removePlayer(this);
      currentPlace = newPlace;
      currentPlace.addPlayer(this);
    }

  }

  /**
   * Picks up an item from the space they are currently occupying.
   *
   * @param item the item to pick up
   */
  @Override
  public void pickUpItem(Item item) {
    if (item == null) {
      throw new IllegalArgumentException("Item cannot be null.");
    }
    if (items.size() < itemLimit && currentPlace.getItems().contains(item)) {
      items.add(item);
      currentPlace.removeItem(item);
    }
  }

  @Override
  public void lookAround() {
    System.out.println("Looking around " + currentPlace.getName());
    if (currentPlace.getItems().isEmpty()) {
      System.out.println("No items here.");
    } else {
      System.out.println("Items here: ");
      currentPlace.getItems().forEach(
          item -> System.out.println(item.getName() + " (Damage: " + item.getDamage() + ")"));
    }
    if (currentPlace.getCurrentPlacePlayers().isEmpty()) {
      System.out.println("No players here.");
    } else {
      System.out.println("Players here: ");
      for (Player player : currentPlace.getCurrentPlacePlayers()) {
        System.out.println(player.getName());
      }
    }

    System.out.println("Neighboring places: ");
    for (Place neighbor : currentPlace.getNeighbors()) {
      System.out.println(neighbor.getName());
    }
  }

  @Override
  public String getDescription() {
    StringBuilder description = new StringBuilder();

    description.append("Looking around ").append(currentPlace.getName()).append("\n");

    if (currentPlace.getItems().isEmpty()) {
      description.append("No items here.\n");
    } else {
      description.append("Items here: \n");
      for (Item item : currentPlace.getItems()) {
        description.append(item.getName()).append(" (Damage: ").append(item.getDamage())
            .append(")\n");
      }
    }

    description.append("Players here: \n");
    for (Player player : currentPlace.getCurrentPlacePlayers()) {
      description.append(player.getName()).append("\n");
    }

    description.append("Neighboring places: \n");
    for (Place neighbor : currentPlace.getNeighbors()) {
      description.append(neighbor.getName()).append("\n");
    }

    return description.toString();
  }


  @Override
  public String getName() {
    return name;
  }

  @Override
  public Place getCurrentPlace() {
    return currentPlace;
  }

  @Override
  public List<Item> getItems() {
    return items;
  }

}
