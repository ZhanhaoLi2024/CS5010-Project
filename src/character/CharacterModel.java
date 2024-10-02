package character;

import java.util.List;

import place.Place;

public class CharacterModel implements Character {

    private String name;
    private int health;
    private Place currentPlace;
    private List<Place> places;

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
            System.out.println(name + " moved to " + currentPlace.getName());
        } else {
            System.out.println(name + " is already at the last place.");
        }
    }

    @Override
    public Place getCurrentPlace() {
        return currentPlace;
    }
}
