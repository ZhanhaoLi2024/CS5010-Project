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

- Java 8 or higher
- Swing library (included in JDK)
- Screen resolution of at least 1024x768
- World specification file (example provided in `res/SmallTownWorld.txt`)

### Using JAR File

```bash
# For GUI Mode
java -jar res/CS5010-Project.jar <world-file> <max-turns> --gui

# For Text Mode
java -jar res/CS5010-Project.jar <world-file> <max-turns>
```

### Starting the Game

The game can be played in either GUI mode or text mode. The JAR file is located in the `res/` directory.

For first-time users, we recommend starting with GUI mode:

```bash
java -jar res/CS5010-Project.jar res/SmallTownWorld.txt 50 --gui
```

### Using the GUI Version

1. **Welcome Screen**
    - When you start the game, you'll see a welcome screen with game credits
    - Click "Start Game" to proceed to the main menu

2. **Main Menu**
    - Add Human Player: Create a new player by entering name, starting location, and item carry limit
    - Add Computer Player: Add an AI-controlled player to the game
    - Display Player Information: View details about all players
    - Start Game: Begin playing (requires at least 2 players)
    - Exit Game: Close the application

3. **During Gameplay**
    - The main game screen shows:
        - Left side: Interactive map with player and target locations
        - Right side: Player information and available actions

    - Use keyboard shortcuts for actions:
        - 'M': Move to adjacent room (click on highlighted rooms)
        - 'L': Look around current location
        - 'P': Pick up items in the room
        - 'E': Move the pet
        - 'A': Attempt to attack the target

### Using the Text Version

For players who prefer text-based interaction:

```bash
java -jar res/CS5010-Project.jar res/SmallTownWorld.txt 50
```

The text version provides the same functionality through a menu-driven interface:

1. Follow the on-screen prompts to add players
2. Choose actions from the numbered menu options
3. Enter commands and values as requested

### Example Gameplay Session

1. Start the game in GUI mode
2. Add more than 2 players
3. Start the game
4. Each player takes turns:
    - Move around the mansion
    - Collect items for attacking
    - Position strategically near the target
    - Attempt attacks when other players can't see you
5. Game ends when either:
    - A player successfully kills the target
    - Maximum turns are reached

### Command Line Arguments

- `world-file`: Path to the world specification file (e.g., `res/SmallTownWorld.txt`)
- `max-turns`: Maximum number of turns before the target escapes (e.g., 50)
- `--gui`: (Optional) Launch in GUI mode

### Important Notes

- The game must be run from the project's root directory
- Ensure the world specification file exists in the specified path
- The maximum turns must be a positive integer
- At least 2 players are required to start the game

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