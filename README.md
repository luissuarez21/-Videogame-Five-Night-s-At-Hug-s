# -Videogame-Five-Night-s-At-Hug-s
Procedurally generated survival game with A* pathfinding AI, seed-based world generation, and multi-agent coordination. Implemented collision detection, dynamic difficulty scaling, and game state serialization in Java.

📖 Overview
Five Nights At Hug's is a procedurally generated survival game where players must evade Professor Josh Hug for five minutes while navigating a dynamically created world. Built from scratch in Java, the game features sophisticated AI using A* pathfinding, procedural world generation, and escalating difficulty mechanics that spawn additional pursuers upon capture.
Win Condition: Survive for 5 minutes
Lose Condition: Get caught by 5 Josh Hugs

✨ Features
🗺️ Procedural World Generation

Deterministic seed-based generation for reproducible worlds
Room & hallway system with collision detection
Non-overlapping room placement algorithm
Connected pathfinding ensuring all rooms are reachable
2D tile grid system for efficient spatial queries

🤖 Advanced AI System

A pathfinding* with Manhattan distance heuristic
Real-time position tracking of player
Priority queue frontier management for optimal pathfinding
Multi-agent coordination - handles multiple concurrent AI pursuers
Dynamic difficulty scaling - each new Hug moves progressively faster

🎮 Core Gameplay

WASD movement controls with collision detection
Save/Load functionality with complete game state serialization
Heads-Up Display (HUD) showing timer, enemy count, and tile descriptions
Respawn mechanics with escalating challenge
Real-time countdown timer (5:00 to 0:00)

🎨 Visual & UI

Custom tile rendering system
Multi-layer rendering (walls, floors, entities)
Real-time HUD updates using StdDraw
Visual feedback for player and enemy positions


🛠️ Technical Stack
Languages & Frameworks

Java - Core game engine and logic
StdDraw - 2D rendering and graphics
JUnit - Unit testing framework

Data Structures & Algorithms
World Generation

HashSet<Room> - Stores non-overlapping room instances
ArrayList<Hallway> - Maintains hallway connections between rooms
2D TETile Array - Represents entire game world grid
Random (java.util) - Pseudorandom generation from seed

AI & Pathfinding

A Algorithm* - Optimal pathfinding for enemy AI
PriorityQueue<Node> - Manages A* open set (frontier)
HashMap<Node, Node> - Tracks came-from relationships for path reconstruction
HashSet<Node> - Tracks visited nodes to prevent cycles

Game State Management

Position Class - Encapsulates (x, y) coordinates
ArrayList<Hug> - Maintains all active AI pursuers
File Serialization - Saves game state (seed, positions, timer, enemy count)

Core Algorithms
1. Procedural World Generation
Input: Seed (long)
Process:
  1. Generate random rooms (x, y, width, height)
  2. Check overlap with existing rooms
  3. Create hallway paths between room centers
  4. Fill grid with floor/wall tiles
Output: TETile[][] world
Complexity: O(W × H) where W = width, H = height
2. A Pathfinding*
Input: Start position, Goal position, World grid
Process:
  1. Initialize open set (priority queue) with start
  2. While open set not empty:
     - Pop node with lowest f-score
     - If goal reached, reconstruct path
     - Explore valid neighbors (up, down, left, right)
     - Calculate g-score and h-score
     - Add to open set if better path found
Output: List<Position> path
Complexity: O(N + E) in practice, O(b^d) worst case
3. Collision Detection
Input: New position, World grid
Process:
  1. Check if position within bounds
  2. Check if tile at position is walkable (floor)
  3. Return boolean
Output: boolean isValid
Complexity: O(1)
4. Room Intersection Algorithm
Input: Room A, Room B
Process:
  1. Check if Room A's bounds overlap Room B's bounds
  2. Return true if any edge intersects
Output: boolean intersects
Complexity: O(1)

🏗️ Architecture
Class Structure
project/
├── WorldGenerator.java
│   ├── generateWorld()           # Main world creation
│   ├── generateRooms()            # Random room placement
│   ├── generateHallways()         # Connect rooms
│   ├── placePlayer()              # Initial player spawn
│   └── placeHug()                 # Initial enemy spawn
│
├── Room.java
│   ├── contains(Position)         # Check if position inside room
│   ├── center()                   # Return center coordinates
│   └── intersects(Room)           # Collision detection
│
├── Hallway.java
│   ├── generatePath(Room, Room)   # Create hallway tiles
│   └── tiles()                    # Return tile list
│
├── Player.java
│   ├── move(char, TETile[][])     # WASD movement with collision
│   ├── getPosition()              # Current player position
│   └── setPosition(Position)      # Update position (respawn/load)
│
├── Hug.java
│   ├── moveToward(Position, TETile[][])  # A* movement step
│   ├── getPosition()              # Current Hug position
│   └── setPosition(Position)      # Update position (load)
│
├── AStar.java
│   ├── findPath(start, goal, world)      # A* pathfinding
│   ├── getNeighbors(Position)            # Valid adjacent tiles
│   └── distanceTo(Position, Position)    # Manhattan distance
│
├── Engine.java
│   ├── keyBoard()                 # Input handling
│   ├── updateState()              # Game loop logic
│   ├── drawHUD()                  # Render HUD
│   ├── saveGame()                 # Serialize game state
│   ├── loadGame()                 # Deserialize game state
│   └── collisionHandler()         # Handle player-enemy collision
│
└── Position.java
    ├── equals(Position)           # Coordinate comparison
    ├── coordinates()              # Return (x, y) tuple
    └── neighbors()                # Return adjacent positions

🎮 Game Mechanics
Difficulty Scaling System
Initial State: 1 Hug chasing player
On Capture:
  - Player respawns at random valid room
  - New Hug spawns
  - Existing Hugs retain their positions
  - New Hug potentially moves faster (configurable)
Win: Survive 5 minutes
Lose: 5 Hugs spawn (5 captures)
Movement System
Player:
  - WASD keys for movement
  - Collision detection prevents wall walking
  - One tile per key press

Hug AI:
  - Recomputes path every frame (or every N frames)
  - Moves one tile closer per update cycle
  - Uses A* to navigate around walls
  - Handles dynamic obstacles
Save/Load System
Serialized Data:
{
  seed: long                    # World generation seed
  playerPosition: Position      # Current player location
  hugPositions: List<Position>  # All Hug locations
  timer: int                    # Remaining time (seconds)
  numHugs: int                  # Current number of Hugs
}

📊 Performance & Complexity
Time Complexity
OperationComplexityNotesWorld GenerationO(W × H)W = width, H = heightRoom Overlap CheckO(R²)R = number of roomsHallway CreationO(R)Connect all roomsA* Pathfinding per HugO(W × H)Worst case, typically much betterFrame UpdateO(H × A)H = # of Hugs, A = pathfinding costRenderingO(W × H)Redraw entire grid
Space Complexity
StructureComplexityPurposeWorld GridO(W × H)Store all tilesRoom SetO(R)Track roomsHallway ListO(R)Store connectionsHug ListO(H)Active enemiesA* Open SetO(W × H)Pathfinding frontierA* Visited SetO(W × H)Prevent cycles
Total: O(W × H) where W and H are world dimensions

🚀 Installation & Usage
Prerequisites

Java 11 or higher
StdDraw library (included in project)

Running the Game
bash# Compile
javac -cp ".;lib/*" *.java

# Run
java -cp ".;lib/*" Engine

# Run with specific seed
java -cp ".;lib/*" Engine --seed 12345
Controls
W - Move Up
A - Move Left  
S - Move Down
D - Move Right
Q - Save and Quit
L - Load Game

🎯 Key Technical Achievements
1. Efficient Pathfinding

A* with Manhattan heuristic for optimal paths
Handles multiple concurrent agents without performance degradation
Real-time recalculation every frame or N frames

2. Deterministic World Generation

Same seed always produces identical worlds
Enables save/load functionality
Supports world sharing between players

3. Scalable Difficulty

Dynamic enemy spawning
Configurable speed increases
Maintains 60 FPS with 5+ active AI agents

4. Robust State Management

Complete game state serialization
Saves seed for world reconstruction
Preserves all entity positions and game timer


🧪 Testing
Unit Tests

World generation determinism
Room intersection logic
A* pathfinding correctness
Collision detection accuracy
Save/load state integrity

Integration Tests

Full game loop execution
Multi-agent pathfinding
HUD rendering
Input handling


🔮 Future Enhancements

 Fog of war / limited vision system
 Power-ups (speed boost, temporary invisibility)
 Multiple difficulty levels
 Procedural tile textures
 Sound effects and background music
 Leaderboard system
 Multiplayer co-op mode
 Minimap display


📝 Design Decisions
Why A Over Dijkstra?*

Manhattan heuristic provides better performance
Guarantees optimal path for grid-based movement
Faster convergence in sparse spaces

Why Seed-Based Generation?

Enables deterministic testing
Allows world sharing
Supports save/load without storing entire world

Why One Tile Per Frame Movement?

Predictable collision detection
Prevents players from skipping through walls
Makes AI behavior more fair/readable


👥 Development Team
Luis Suarez - Lead Developer & Designer

Procedural generation system
A* pathfinding implementation
Game state management
Testing & debugging

Valerie Barajas - Co-Developer

UI/UX design
Game mechanics design
Testing & playtesting


📜 License
This project was developed as part of UC Berkeley's CS 61B course.

🎓 Learning Outcomes
This project demonstrates proficiency in:

Data Structures: HashSets, ArrayLists, HashMaps, PriorityQueues
Algorithms: A* pathfinding, procedural generation, collision detection
Object-Oriented Design: Separation of concerns, encapsulation
Software Engineering: Testing, debugging, version control
Game Development: Game loop, entity management, rendering pipeline
