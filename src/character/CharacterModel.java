package character;

import java.util.List;

import place.Place;

public class CharacterModel implements Character {

    private final String name;
    private final int health;
    private Place currentPlace;
    private final List<Place> places;

    public CharacterModel(String name, int health, Place startPlace, List<Place> places) {
        this.name = name;
        this.health = health;
        this.currentPlace = startPlace;
        this.places = places;
    }

    @Override
    public void moveToNextPlace() {
        int currentIndex = places.indexOf(currentPlace);
        if (currentIndex < places.size() - 1) {
            currentPlace = places.get(currentIndex + 1);
        } else {
            currentPlace = places.get(0);
        }
        System.out.println(name + " moved to " + currentPlace.getName());
    }

    @Override
    public Place getCurrentPlace() {
        return currentPlace;
    }
    
    @Override
    public int getHealth() {
    	return health;
    }

	@Override
	public String getName() {
		return name;
	}
    
}
