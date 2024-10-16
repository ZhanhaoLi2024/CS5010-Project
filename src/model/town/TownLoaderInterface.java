package model.town;

import java.io.IOException;

/**
 * ITownLoader defines the contract for loading town data from a file.
 */
public interface TownLoaderInterface {
  /**
   * Loads the town data from the specified file.
   *
   * @param filename the name of the file to load the town data from
   * @return the town data loaded from the file
   * @throws IOException if an I/O error occurs
   */
  TownData loadTown(String filename) throws IOException;
}