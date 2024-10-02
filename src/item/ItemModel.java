package item;

public class ItemModel implements Item {

    private String name;
    private int damage;

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
