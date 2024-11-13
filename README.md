# Kill Doctor Lucky Game - Milestone 3

## Overview

This is milestone 3 of the "Kill Doctor Lucky" game project, focusing on implementing game mechanics and rules following
object-oriented programming principles and MVC architecture. The game is a text-based board game where players attempt
to eliminate a target character while avoiding detection by other players.

## New Features in Milestone 3

### Target's Pet Implementation

- Added Fortune the Cat as the target's pet
- Pet starts in the same space as the target character
- Pet's presence makes its location invisible to neighboring spaces
- Players can move the pet as an action (counts as a turn)
- Pet's location is included in space descriptions

### Combat System

- Players can attempt to kill the target character
- Attack success conditions:
    - Player must be in same room as target
    - Attack must not be visible to other players
    - Players can use items or perform a basic "poke" attack
- Items used in attacks are removed from play
- Computer players automatically use their highest damage item when attacking

### Game Victory Conditions

- Player wins by successfully killing the target
- Target escapes if maximum turns are reached
- Game ends immediately when either condition is met

## Architecture

### Model (model/)

- Implements core game logic and state
- Key classes:
    - `TownModel`: Manages game state and rules
    - `PlayerModel`: Handles player actions and inventory
    - `TargetModel`: Controls target movement and health
    - `PetModel`: Manages pet location and visibility effects

### Controller (controller/)

- Handles user input and game flow
- Key components:
    - `GameController`: Main game loop and turn management
    - Command pattern implementation for actions:
        - `AttackTargetCommand`
        - `MovePetCommand`
        - `MovePlayerCommand`
        - `LookAroundCommand`

## How to Run

### Prerequisites

- Java 11 or higher
- Command line interface

### Compilation

```bash
javac -d bin src/**/*.java
```

### Execution

```bash
java -cp bin Driver <world-file> <max-turns>
```

Example:

```bash
java -cp bin Driver res/SmallTownWorld.txt 50
```

### Command Line Arguments

1. `world-file`: Path to world specification file
2. `max-turns`: Maximum number of turns before target escapes

## Example Gameplay

The `res/` directory contains example runs demonstrating key features:

1. `pet_visibility_example.txt`: Shows pet's effect on space visibility
2. `pet_movement_example.txt`: Demonstrates moving the pet
3. `human_attack_item.txt`: Shows human player combat mechanics
4. `computer_attack_example.txt`: Demonstrates computer player combat
5. `human_victory_example.txt`: Human player winning scenario
6. `computer_victory_example.txt`: Computer player winning scenario
7. `target_escape_example.txt`: Target successfully escaping

## Design Principles

### Object-Oriented Programming

- **Encapsulation**: All game entities (Player, Target, Pet) encapsulate their state and behavior
- **Inheritance**: Common functionality shared through abstract classes
- **Polymorphism**: Different player types (human/computer) share common interface
- **Interface Segregation**: Clear separation of concerns through interfaces

### MVC Implementation

- **Model**: Pure business logic, no UI dependencies
- **View**: (Still in development)
- **Controller**: Coordinates model updates and view updates

### Command Pattern

- Encapsulates player actions as objects
- Enables easy addition of new commands
- Maintains clean separation between UI and game logic

## Testing

The project includes comprehensive unit tests for:

- Pet visibility mechanics
- Combat system
- Victory conditions
- Target movement
- Computer player AI

Run tests using:

```bash
java -cp bin org.junit.runner.JUnitCore <test-class>
```

## Future Improvements

- Graphical user interface
- Additional game modes
- Enhanced AI strategies
- Save/load game state

## Authors

Zhanhao Li

## License

This project is part of the CS5010 Object-Oriented Design course.