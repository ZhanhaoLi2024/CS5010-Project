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

    /**
     * Retrieves the name of the item.
     *
     * @return the name of the item as a String.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Retrieves the damage value of the item.
     *
     * @return the damage value of the item as an integer.
     */
    @Override
    public int getDamage() {
        return damage;
    }

}
