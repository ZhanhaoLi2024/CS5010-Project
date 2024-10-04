package item;

/**
 * The ItemModel class implements the Item interface and represents an item in a
 * game. An item has a name and a damage value.
 */
public class ItemModel implements Item {

    private final String name;
    private final int damage;

    /**
     * Constructs a new ItemModel with the specified name and damage value.
     *
     * @param name   the name of the item
     * @param damage the damage value of the item
     */
    public ItemModel(String name, int damage) {
        this.name = name;
        this.damage = damage;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getDamage() {
		return damage;
	}

}
