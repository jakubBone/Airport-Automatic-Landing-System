# Airport Automatic Landing System

[![Watch the video](src/main/resources/images/logo.png)](https://www.youtube.com/watch?v=NvXRgYPO4gE)


## Project Overview

This project simulates an **automated airport landing control system** using a client-server architecture, collision detection, and real-time 3D visualization.

The system uses **multithreading** to handle multiple airplanes concurrently, prevent collisions, and manage runway allocation.
Airplanes act as clients and communicate with the control tower server using raw socket connections.


## Features

- **Collision Detection**: Prevents crashes by continuously monitoring airplane positions
- **Runway Assignment**: Dynamically allocates available runways for landing
- **Fuel Monitoring**: Real-time fuel level tracking
- **Client-Server Communication**: Planes (clients) communicate with the control tower (server) via sockets
- **Database Integration**: Logs registrations, landings, and collisions
- **3D Visualization**: JavaFX interface for visualizing airplane movements in real-time


## Quick Start

### Prerequisites
- **Java 21** or higher
- **Docker** (for PostgreSQL database)

### Run in 3 steps

```bash
# 1. Clone
git clone https://github.com/jakubBone/Airport-Automatic-Landing-System.git
cd Airport-Automatic-Landing-System

# 2. Start database
docker-compose up -d

# 3. Run simulation
./gradlew run
```

The 3D visualization window will open automatically.


## Configuration

The project uses **default development credentials** for quick start.

To customize, create a `.env` file (see `.env.example`):
```
DB_HOST=localhost
DB_PORT=5432
DB_NAME=airport_system
DB_USER=airport
DB_PASSWORD=your_password
```

Other settings: `src/main/resources/config.properties`


## Security Note

This project includes **default credentials for development purposes only**.

For production or shared environments:
1. Copy `.env.example` to `.env`
2. Set secure credentials in `.env`
3. The `.env` file is gitignored and won't be committed

Environment variables override default values in `docker-compose.yml` and `config.properties`.


## Visualization Controls

Control the camera with keyboard:
- **Z/X**: Zoom in/out
- **Arrow Keys**: Rotate the camera


## Technologies

- **Java 21** - Core language
- **PostgreSQL** - Database
- **JOOQ** - Type-safe SQL
- **JavaFX** - 3D visualization
- **Docker** - Database containerization
- **Gradle** - Build tool
- **JUnit 5** - Testing
- **Log4j2** - Logging


## Project Structure

```
src/main/java/com/jakub/bone/
├── application/    # Connection handlers
├── client/         # Plane client logic
├── config/         # Configuration
├── core/           # Entry point (SimulationLauncher)
├── database/       # Database connection
├── domain/         # Domain models (Plane, Runway, Airport)
├── repository/     # Data persistence
├── server/         # Airport server
├── service/        # Business logic (CollisionService, ControlTower)
├── ui/             # 3D visualization components
└── utils/          # Utilities
```


## Project Status

This project was created as an **educational project** to learn:
- Low-level threading and socket programming (without Spring)
- Client-server architecture
- Real-time 3D visualization with JavaFX

It is actively being refactored to improve code quality and architecture.


## Contact

- **Email**: [jakub.bone1990@gmail.com](mailto:jakub.bone1990@gmail.com)
- **Blog**: [javamPokaze.pl](https://javampokaze.pl)
- **LinkedIn**: [Jakub Bone](https://www.linkedin.com/in/jakub-bone)
