# Airport Automatic Landing System

[![Watch the video](src/main/resources/images/logo.png)](https://www.youtube.com/watch?v=NvXRgYPO4gE)


## 🛬 Project Overview

This project simulates an **automated airport control system** using a client-server architecture, collision detection, and real-time 3D visualization.

The system uses **multithreading** to handle multiple airplanes concurrently, prevent collisions, and manage runway allocation.
Airplanes act as clients and communicate with the control tower server using raw socket connections.


## ✨ Features

- **Collision Detection**: Prevents crashes by continuously monitoring airplane positions
- **Runway Assignment**: Dynamically allocates available runways for landing
- **Fuel Monitoring**: Real-time fuel level tracking
- **Client-Server Communication**: Planes (clients) communicate with the control tower (server) via sockets
- **Database Integration**: Logs registrations, landings, and collisions
- **3D Visualization**: JavaFX interface for visualizing airplane movements in real-time


## 🚀 Quick Start

### Prerequisites
- **Java 21** or higher
- **Docker** (for PostgreSQL database)

### Run

```bash
# 1. Clone
git clone https://github.com/jakubBone/Airport-Automatic-Landing-System.git
cd Airport-Automatic-Landing-System

# 2. Start database
docker-compose up -d

# 3. Initialize database schema (first time only)
./gradlew :db-init:initDB

# 4. Run simulation
./gradlew run
```

The 3D visualization window will open automatically.


## ⚙️ Configuration

All settings are in `src/main/resources/config.properties`.


## 🧰 Technologies

- **Java 21** - Core language
- **PostgreSQL** - Database
- **JOOQ** - Type-safe SQL
- **JavaFX** - 3D visualization
- **Docker** - Database containerization
- **Gradle** - Build tool
- **JUnit 5** - Testing
- **Log4j2** - Logging

**Note:** The project includes `libs/jim3dsModelImporterJFX/` for 3D model import (.obj files).
This library is not available in Maven Central and must be included locally.


## 🗂️ Project Structure

```
├── db-init/                        # Database schema initialization module
└── src/main/java/com/jakub/bone/
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


## 📌 Project Status

This project was created as an **educational project** to learn:
- Low-level threading and socket programming (without Spring)
- Client-server architecture
- Real-time 3D visualization with JavaFX

It is actively being refactored to improve code quality and architecture.


## 📬 Contact

- **Email**: [jakub.bone1990@gmail.com](mailto:jakub.bone1990@gmail.com)
- **Blog**: [javamPokaze.pl](https://javampokaze.pl)
- **LinkedIn**: [Jakub Bone](https://www.linkedin.com/in/jakub-bone)
