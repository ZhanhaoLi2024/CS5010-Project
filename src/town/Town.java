package town;

import java.io.IOException;

import place.Place;

public interface Town {
    void loadTown(String filename) throws IOException;
    void displayPlaceInfo(Place place);
    void moveCharacter();
    character.Character getCharacter();
}
