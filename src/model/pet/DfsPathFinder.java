package model.pet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import model.place.Place;

/**
 * Implements depth-first search traversal algorithm for pet movement.
 * This class calculates the DFS path through all spaces in the world.
 */
public class DfsPathFinder implements PathFinder {
  private final List<Place> allPlaces;
  private final List<Integer> dfsPath;
  private int currentPathIndex;
  private boolean pathCalculated;

  /**
   * Constructs a DFS pathfinder with the given list of places.
   *
   * @param places the list of all places in the world
   * @throws IllegalArgumentException if places list is null or empty
   */
  public DfsPathFinder(List<Place> places) {
    if (places == null || places.isEmpty()) {
      throw new IllegalArgumentException("Places list cannot be null or empty");
    }
    this.allPlaces = new ArrayList<>(places);
    this.dfsPath = new ArrayList<>();
    this.currentPathIndex = 0;
    this.pathCalculated = false;
    calculateDfsPath();
  }

  /**
   * Calculates the complete DFS path through all places.
   */
  private void calculateDfsPath() {
    // Clear any existing path
    dfsPath.clear();

    // Use adjacency list representation for the graph
    Map<Integer, List<Integer>> adjacencyList = buildAdjacencyList();
    Set<Integer> visited = new HashSet<>();
    Stack<Integer> stack = new Stack<>();

    // Start DFS from the first place (index 1)
    int startNode = 1;
    stack.push(startNode);

    while (!stack.isEmpty()) {
      int currentNode = stack.pop();

      if (!visited.contains(currentNode)) {
        visited.add(currentNode);
        dfsPath.add(currentNode);

        // Get neighbors and push them to stack in reverse order
        List<Integer> neighbors = adjacencyList.get(currentNode);
        for (int i = neighbors.size() - 1; i >= 0; i--) {
          int neighbor = neighbors.get(i);
          if (!visited.contains(neighbor)) {
            stack.push(neighbor);
          }
        }
      }
    }

    this.pathCalculated = true;
  }

  /**
   * Builds adjacency list representation of the world graph.
   *
   * @return Map representing the adjacency list
   */
  private Map<Integer, List<Integer>> buildAdjacencyList() {
    Map<Integer, List<Integer>> adjacencyList = new HashMap<>();

    // Initialize adjacency lists for all places
    for (int i = 1; i <= allPlaces.size(); i++) {
      adjacencyList.put(i, new ArrayList<>());
    }

    // Build connections based on neighboring places
    for (Place place : allPlaces) {
      int placeNum = Integer.parseInt(place.getPlaceNumber());
      for (Place neighbor : place.getNeighbors()) {
        int neighborNum = Integer.parseInt(neighbor.getPlaceNumber());
        adjacencyList.get(placeNum).add(neighborNum);
      }
    }

    return adjacencyList;
  }

  @Override
  public int getNextPlaceNumber() {
    if (!pathCalculated || dfsPath.isEmpty()) {
      throw new IllegalStateException("Path has not been calculated yet");
    }

    int nextPlace = dfsPath.get(currentPathIndex);
    currentPathIndex = (currentPathIndex + 1) % dfsPath.size();
    return nextPlace;
  }

  @Override
  public boolean isPathReady() {
    return pathCalculated && !dfsPath.isEmpty();
  }

  @Override
  public void resetPath() {
    this.currentPathIndex = 0;
    this.pathCalculated = false;
    calculateDfsPath();
  }

  /**
   * Gets the complete path for testing purposes.
   *
   * @return List of place numbers in DFS order
   */
  protected List<Integer> getCompletePath() {
    return new ArrayList<>(dfsPath);
  }
}