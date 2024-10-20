# cs5010-project-kill-doctor-lucky

# Milestone02

## Running the Program using JAR File

Steps to Run:

1. Navigate to the `cs5010-project-kill-doctor-lucky` directory where the `res` file is located.
2. Use the following command to run the program:

```bash
java -jar res/cs5010-project-kill-doctor-lucky.jar <world_file> <max_turns>
```

3. The world_file is in the `res` directory called `SmallTownWorld.txt`:

```bash
java -jar res/cs5010-project-kill-doctor-lucky.jar res/SmallTownWorld.txt <max_turns>
```

## Example Run: World of Players - Milestone02

Example run simple text file:

1. Example Run1 - Adding a Human-Controlled Player: `res/01_add_human_player.txt`
2. Example Run2 - Adding a Computer-Controlled Player: `res/02_add_computer_player.txt`
3. Example Run3 - Player Moving Around the World: `res/03_player_movement.txt`
4. Example Run4 - Player Picking Up an Item: `res/04_player_pickup_item.txt`
5. Example Run5 - Player Looking Around: `res/05_player_look_around.txt`
6. Example Run6 - Taking Turns Between Multiple Players: `res/06_multiple_players_turns.txt`
7. Example Run7 - Displaying Description of a Specific Player: `res/07_player_description.txt`
8. Example Run8 - Displaying Information About a Specific Space: `res/08_space_description.txt`
9. Example Run9 - Creating and Saving a Graphical Representation of the World
   Map: `res/map.png`
10. Example Run10 - Game Ending After Reaching Maximum Number of
    Turns: `res/10_max_turns_game_end.txt`

# Milestone01

## Running the Program using JAR File

Steps to Run:

1. Navigate to the `cs5010-project-kill-doctor-lucky` directory where the `res` file is located.
2. Use the following command to run the program:

```bash
java -jar res/cs5010-project-kill-doctor-lucky.jar
```

## Example Run: Basic Game Functions - Milestone01

This example demonstrates:

1. Loading the world specification from `res/SmallTownWorld.txt`.
2. Displaying the initial place and items.
3. Moving the character between place and displaying place information.
4. Testing the loop movement when the character reaches the last room.

The output of this example can be found in the file `res/milestone01`.

Example run simple text file:

1. Example Run1 - Initial Place Information: `res/milestone01/ShowTownInfo.txt`
2. Example Run2 - Move Character to Next Position: `res/milestone01/MoveMayorToNextPosition.txt`
3. Example Run3 - Show Mayor's Current Space: `res/milestone01/ShowMayorCurrentSpace.txt`
4. Example Run4 - Show Current Space Neighbors: `res/milestone01/ShowCurrentSpaceNeighbors.txt`
5. Example Run5 - Show Space by Error Index: `res/milestone01/ShowSpaceByErrorIndex.txt`
6. Example Run6 - Show Space by Index: `res/milestone01/ShowSpaceByIndex.tet`
7. Example Run7 - Show Neighbors by Error Index: `res/milestone01/ShowNeighborsByErrorIndex.txt`
8. Example Run8 - Show Neighbors by Index: `res/milestone01/ShowNeighborsByIndex.txt`
9. Example Run9 - Print the World Map: `res/milestone01/map.png`
