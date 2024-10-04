package character;

import place.Place;

/**
 * The Character interface defines the basic behaviors and attributes of a game
 * character. A character can move between different places, has a name, and a
 * health value.
 */
public interface Character {
	/**
	 * Moves the character to the next place in the list of places. If the character
	 * is currently at the last place in the list, it will move back to the first
	 * place.
	 */
	void moveToNextPlace();

	/**
	 * Retrieves the current place where the character is located.
	 *
	 * @return the current place as a Place object.
	 */
	Place getCurrentPlace();

	/**
	 * Retrieves the character's current health value.
	 *
	 * @return the health value of the character as an integer.
	 */
	int getHealth();

	/**
	 * Retrieves the character's name.
	 *
	 * @return the name of the character as a String.
	 */
	String getName();
}
