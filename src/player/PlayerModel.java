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
    this.name = name;
    this.currentPlace = startPlace;
    this.itemLimit = itemLimit;
    this.items = new ArrayList<>();
  }

  @Override
  public void moveToNeighbor(Place newPlace) {
    if (currentPlace.isNeighbor(newPlace)) {
      currentPlace.removePlayer(this);
      currentPlace = newPlace;
      currentPlace.addPlayer(this);
    }

  }

  @Override
  public void pickUpItem(Item item) {
    if (items.size() < itemLimit && currentPlace.getItems().contains(item)) {
      items.add(item);
      currentPlace.removeItem(item);
    }
  }

  @Override
  public void lookAround() {
    System.out.println("Looking around " + currentPlace.getName());
    System.out.println("Items here: " + currentPlace.getItems());
    System.out.println("Players here: ");
    for (Player player : currentPlace.getCurrentPlacePlayers()) {
      System.out.println(player.getName());
    }
  }

  @Override
  public String getDescription() {
    System.out.println("Looking around " + currentPlace.getName());
    System.out.println("Items here: " + currentPlace.getItems());
    System.out.println("Players here: ");
    for (Player player : currentPlace.getCurrentPlacePlayers()) {
      System.out.println(player.getName());
    }
    return null;
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
