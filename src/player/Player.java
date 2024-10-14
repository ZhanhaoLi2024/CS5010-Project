package player;

import item.Item;
import place.Place;

public interface Player {
  void moveToNeighbor(Place newPlace);
  void pickUpItem(Item item);
  void lookAround();
  String getDescription();
  String getName();
  Place getCurrentPlace();
}