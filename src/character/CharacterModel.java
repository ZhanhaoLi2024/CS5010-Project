package character;

import java.util.List;

import place.Place;

/**
 * The CharacterModel class represents a character with a name, health status,
 * and a current location. It allows the character to move between different
 * places provided in a list and provides methods to retrieve the current place,
 * health, and name.
 */
public class CharacterModel implements Character {

	private final String name;
	private final int health;
	private Place currentPlace;
	private final List<Place> places;

	/**
	 * Constructs a new CharacterModel with the specified name, health,
	 * starting place, and a list of places the character can visit.
	 *
	 * @param name       the name of the character
	 * @param health     the health status of the character
	 * @param startPlace the place where the character starts
	 * @param places     the list of places the character can move to
	 */
	public CharacterModel(String name, int health, Place startPlace, List<Place> places) {
		this.name = name;
		this.health = health;
		this.currentPlace = startPlace;
		this.places = places;
	}

	/**
	 * Moves the character to the next place in the list of places. If the character
	 * is currently at the last place in the list, it will move back to the first
	 * place.
	 */
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

	/**
	 * Returns the current place where the character is located.
	 *
	 * @return the current place of the character
	 */
	@Override
	public Place getCurrentPlace() {
		return currentPlace;
	}

	/**
	 * Returns the health status of the character.
	 *
	 * @return the health value of the character
	 */
	@Override
	public int getHealth() {
		return health;
	}

	/**
	 * Returns the name of the character.
	 *
	 * @return the name of the character
	 */
	@Override
	public String getName() {
		return name;
	}

}
