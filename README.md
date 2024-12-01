# Kill Doctor Lucky Game - Milestone 4

## Overview

This is the final milestone of the "Kill Doctor Lucky" game project, implementing a complete graphical user interface
while maintaining strict adherence to Object-Oriented Programming principles and MVC architecture. The game now supports
both text-based and GUI modes, allowing players to interact with the game world through an intuitive visual interface.

## New Features in Milestone 4

### Graphical User Interface

- Welcome screen with game credits and start options
- Interactive game map showing:
    - Visual representation of all spaces
    - Current locations of target character and players
    - Valid movement options highlighting
- Information panel displaying:
    - Current turn and player information
    - Player inventory
    - Game status updates
- Menu system for:
    - Adding players (human/computer)
    - Starting new games
    - Viewing player information
    - Quitting the game

### Enhanced User Interaction

- Mouse-based movement system
- Keyboard shortcuts for actions:
    - 'M' - Move player
    - 'L' - Look around
    - 'P' - Pick up item
    - 'E' - Move pet
    - 'A' - Attack target
- Visual feedback for:
    - Valid/invalid moves
    - Action results
    - Game state changes

### Dual Interface Support

- Maintains full functionality of text-based interface
- Seamless switching between GUI and text modes
- Consistent game logic across both interfaces

## Architecture

### Model (model/)

- Pure business logic with no UI dependencies
- Key components:
    - `TownModel`: Core game state and rules
    - `PlayerModel`: Player actions and inventory
    - `TargetModel`: Target movement and health
    - `PetModel`: Pet mechanics and visibility
    - `ItemModel`: Item properties and usage

### View (view/)

- Separated into GUI and text-based implementations
- Key components:
    - `GuiGameView`: Main GUI implementation
    - `TextGameView`: Text-based interface
    - `MapPanel`: Visual game board representation
    - Custom dialogs for user interaction:
        - `AddPlayerDialog`
        - `PlayerInfoDialog`
        - `MessageDialog`

### Controller (controller/)

- Handles game flow and user input
- Key components:
    - `GuiGameController`: GUI-specific control logic
    - `TextGameController`: Text interface control
    - Command pattern implementation:
        - `AddPlayerCommand`
        - `MovePlayerCommand`
        - `PickUpItemCommand`
        - `LookAroundCommand`
        - `MovePetCommand`

## How to Run

### Prerequisites

- Java 11 or higher
- Swing library (included in JDK)
- Screen resolution of at least 1024x768

### Using JAR File

```bash
# For GUI Mode
java -jar res/CS5010-Project.jar <world-file> <max-turns> --gui

# For Text Mode
java -jar res/CS5010-Project.jar <world-file> <max-turns>
```

Example:

```bash
# For GUI Mode
java -jar res/CS5010-Project.jar res/SmallTownWorld.txt 50 --gui

# For Text Mode
java -jar res/CS5010-Project.jar res/SmallTownWorld.txt 50
```

### Command Line Arguments

1. `world-file`: Path to world specification file
2. `max-turns`: Maximum number of game turns
3. `--gui` (optional): Launch in GUI mode

## Design Principles

### Object-Oriented Design

- **Encapsulation**: Each component maintains its own state
- **Inheritance**: View implementations extend common interfaces
- **Polymorphism**: Controllers handle different view types uniformly
- **Interface Segregation**: Clear separation between GUI and text interfaces

### MVC Implementation

- **Model**:
    - Pure game logic
    - No dependencies on view or controller
    - Observable state changes
- **View**:
    - Multiple implementations (GUI/Text)
    - No game logic
    - Pure presentation layer
- **Controller**:
    - Coordinates model and view
    - Handles user input
    - Manages game flow

### Design Patterns

- **Command Pattern**: Encapsulates all game actions
- **Observer Pattern**: Updates view on model changes
- **Strategy Pattern**: Different player control strategies
- **Factory Pattern**: Creates appropriate view components

## Testing Strategy

### Model Testing

- Unit tests for all game logic
- Mock objects for external dependencies
- Coverage of edge cases and error conditions

### Controller Testing

- Mock views and models for isolation
- Command execution verification
- Input validation testing

### View Testing

- No direct testing of GUI components
- Interface compliance verification
- Event handling validation

## Limitations and Future Improvements

### Current Limitations

- Fixed window size recommendations
- Limited animation support
- No game state persistence
- Basic computer player AI

### Potential Improvements

- Responsive design for different screen sizes
- Enhanced visual effects and animations
- Save/load game functionality
- Advanced AI strategies
- Network multiplayer support

## Author

Zhanhao Li

## License

This project is part of the CS5010 Object-Oriented Design course.