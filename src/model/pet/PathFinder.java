package model.pet;

/**
 * The PathFinder interface defines the contract for different path finding strategies
 * that can be used for pet movement in the game.
 */
public interface PathFinder {
  /**
   * Gets the next place number in the calculated path.
   *
   * @return the next place number in the sequence
   */
  int getNextPlaceNumber();

  /**
   * Checks if the path has been fully calculated.
   *
   * @return true if the path is ready to be traversed, false otherwise
   */
  boolean isPathReady();

  /**
   * Resets the path finding algorithm to its initial state.
   * This can be useful when restarting the game or recalculating paths.
   */
  void resetPath();
}