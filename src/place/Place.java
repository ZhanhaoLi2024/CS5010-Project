package place;

import java.util.List;

import item.Item;

public interface Place {
    void addItem(Item item);
    void addNeighbor(Place place);
    String getName();
    List<Item> getItems();
    List<Place> getNeighbors();
    boolean isNeighbor(Place other);
}