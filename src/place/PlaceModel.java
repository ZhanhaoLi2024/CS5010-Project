package place;

import java.util.ArrayList;
import java.util.List;

import item.Item;

public class PlaceModel implements Place {
	private int row1, col1, row2, col2;
	private String name;
	private List<Item> items;
	private List<Place> neighbors;

	public PlaceModel(int row1, int col1, int row2, int col2, String name) {
		this.row1 = row1;
		this.col1 = col1;
		this.row2 = row2;
		this.col2 = col2;
		this.name = name;
		this.items = new ArrayList<>();
		this.neighbors = new ArrayList<>();
	}

	@Override
	public void addItem(Item item) {
		items.add(item);
	}

	@Override
	public void addNeighbor(Place place) {
		neighbors.add(place);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<Item> getItems() {
		return items;
	}

	@Override
	public List<Place> getNeighbors() {
		return neighbors;
	}

	@Override
	public boolean isNeighbor(Place other) {
		if (other instanceof PlaceModel) {
			PlaceModel otherPlace = (PlaceModel) other;

			// Check if they are neighbors horizontally or vertically
			boolean horizontallyAdjacent = (this.col1 == otherPlace.col2 || this.col2 == otherPlace.col1);
			boolean verticallyAdjacent = (this.row1 == otherPlace.row2 || this.row2 == otherPlace.row1);

			// Check if they overlap in the other dimension (row overlap for horizontal,
			// column overlap for vertical)
			boolean rowOverlap = this.row1 <= otherPlace.row2 && this.row2 >= otherPlace.row1;
			boolean colOverlap = this.col1 <= otherPlace.col2 && this.col2 >= otherPlace.col1;

			// They are neighbors if they are adjacent either horizontally or vertically and
			// there is overlap in the other dimension
			return (horizontallyAdjacent && rowOverlap) || (verticallyAdjacent && colOverlap);
		}
		return false;
	}
}
