package character;

import place.Place;

public interface Character {
    void moveToNextPlace();
    Place getCurrentPlace();
    int getHealth();
    String getName();
}
