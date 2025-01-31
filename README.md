# ✈️ Airport Automatic Landing System

[![Watch the video](src/main/resources/images/logo.png)](https://www.youtube.com/watch?v=eqqYM1RD8ZI)

Welcome to the Airport Automatic Landing System! This project showcases a robust and safe solution for automating airplane landings, 
utilizing client-server architecture, collision detection, and real-time monitoring. The system is designed to handle multiple planes, 
prevent collisions, and ensure efficient usage of runways.


## 🎯 Features

- **Collision Detection**: Prevents crashes by continuously monitoring airplane positions

- **Runway Assignment**: Dynamically allocates available runways for landing

- **Fuel Monitoring**: Ensures planes are directed appropriately based on fuel levels

- **Client-Server Communication**: Planes (clients) communicate with the control tower (server) via sockets

- **Database Integration**: Logs airspace registrations, landings, collisions, and plane details for analysis

- **Visualization**: A 3D interface using JavaFX for visualizing airplane movements

- **REST API **: Exposes endpoints for controlling the airport and retrieving real-time data


## 🚀 Technologies Used

- **Java 21**: Core programming language for client-server logic

- **PostgreSQL**: Backend database for logging data

- **JOOQ**: SQL builder for database interactions

- **JavaFX**: 3D visualization of the airport and planes

- **JUnit**: Unit and integration testing

- **Log4j2**: Logging system for debugging and information tracking

- **Gradle**: Build automation and dependency management

- **Jetty**: Embedded HTTP server for API


## 📂 Project Structure

```
src
├── com.jakub.bone.api            # REST API endpoints
├── com.jakub.bone.application    # Application processes management
├── com.jakub.bone.client         # Client-side logic 
├── com.jakub.bone.config         # Configuration and constants 
├── com.jakub.bone.core           # Simulation entry point
├── com.jakub.bone.database       # Database connection
├── com.jakub.bone.domain         # Domain models for airport and airplane
├── com.jakub.bone.repository     # Data persistence layer
├── com.jakub.bone.server         # Server-side logic 
├── com.jakub.bone.service        # Business logic and processes 
├── com.jakub.bone.ui             # Visualization components
└── com.jakub.bone.utills         # Utilities and constants  
``` 


## 🚀 Getting Started

Follow these steps to set up and run the project:

### Prerequisites

Before you begin, ensure you have the following tools installed:
- **Java Development Kit (JDK)** 21 or higher
- **Gradle** for dependency management
- **PostgreSQL** database
- **JavaFX** library for visualization

### Setup Instructions

1. **Clone the Repository**  
   Download the project files to your local machine:
   ```bash
   git clone https://github.com/jakubBone/Airport-Automatic-Landing-System.git
   cd Airport-Automatic-Landing-System

2. **Configure the Database**  
   Set up a PostgreSQL database:
   - Create a database named airport_system
   - Update the database credentials in the `AirportDatabase.java` file located at:
     `src/com/jakub/bone/database/AirportDatabase.java`
     Replace the placeholders with your database credentials:
     ```java
     private final String USER = "your_user";
     private final String PASSWORD = "your_password";

3. **Build the Project**   
   Use Gradle to build the project:
   ```bash
   ./gradlew build

5. **Run the Server**   
   Start the server to manage plane communications:
   ```bash
   java -cp build/classes/java/main com.jakub.bone.core.AirportServer
   
   5. **Run the API Server**   
   The API will be available at: http://localhost:8080
   Start the REST API for managing and monitoring the system:
   ```bash
   java -cp build/classes/java/main com.jakub.bone.api.ApiServer

6. **Run the Clients**  
   Simulate planes connecting to the server:
   ```bash
   java -cp build/classes/java/main com.jakub.bone.core.PlaneClient

7. **Launch Visualization** (Optional)  
   Start the 3D visualization tool for real-time airplane monitoring:
   ```bash
   java -cp build/classes/java/main com.jakub.bone.core.SimulationLauncher
   
   
## 🌐 API Overview

The system provides a REST API for monitoring and controlling the airport. The available endpoints include:

### Control Endpoints

- `POST /airport/start` – Start the airport system
- `POST /airport/pause` – Pause the system
- `POST /airport/resume` – Resume the system
- `POST /airport/stop` – Stop the system

### Monitoring Endpoints

- `GET /airport/uptime` – Get the current uptime of the airport system
- `GET /airport/planes/count` – Get the number of planes in the air
- `GET /airport/planes/flightNumbers` – Get the flight numbers list of planes in the air
- `GET /airport/planes/landed` – Get a list of landed planes
- `GET /airport/collisions` – Get information about past collisions

For a detailed API specification, see [API Documentation](https://github.com/jakubBone/Airport-Automatic-Landing-System/blob/master/docs/API.md)   .


## 🎨 Interactive Visualization

Experience the airport operations in 3D! Planes move dynamically, and collisions or landings are visually represented.
Control the camera with keyboard inputs:
  
- **Z/X**: Zoom in/out
  
- **Arrow Keys**: Rotate the camera

## 📧 Contact

If you have any questions, feedback, or suggestions, feel free to reach out to me:

- **Email**: [jakub.bone1990@gmail.com](mailto:jakub.bone1990@gmail,com)
- **Blog**: [javamPokaze.pl](https://javampokaze.pl)  
- **LinkedIn**: [Jakub Bone](https://www.linkedin.com/in/jakub-bone)  

Let's connect and discuss this project further! 🚀
