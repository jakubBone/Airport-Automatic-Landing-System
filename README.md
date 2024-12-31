# ✈️ Airport Automatic Landing System

![Airport Automatic Landing System](src/main/resources/images/airport_automatic_landing_system.png)

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


## 🚀 Technologies Used

- **Java 21**: Core programming language for client-server logic

- **PostgreSQL**: Backend database for logging data

- **JOOQ**: SQL builder for database interactions

- **JavaFX**: 3D visualization of the airport and planes

- **Log4j2**: Logging system for debugging and information tracking

- **Gradle**: Build automation and dependency management


## 📂 Project Structure

```
src
├── com.jakub.bone.application    # Core application logic
├── com.jakub.bone.client         # Client-side logic 
├── com.jakub.bone.core           # Simulation entry point
├── com.jakub.bone.database       # Database integration using JOOQ
├── com.jakub.bone.domain         # Domain models for airport and airplane
├── com.jakub.bone.server         # Server-side logic 
├── com.jakub.bone.ui             # Visualization components
└── com.jakub.bone.utills          # Utilities and constants  
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
   - Create a database named airport_system.
   - Update the database credentials in the `AirportDatabase.java` file located at:
     `src/com/jakub/bone/database/AirportDatabase.java`.
     Replace the placeholders with your database credentials:
     ```java
     private final String USER = "your_user";
     private final String PASSWORD = "your_password";

3. **Build the Project**
   Use Gradle to build the project:
   ```bash
   ./gradlew build

4. **Run the Server** 
   Start the server to manage plane communications:
   ```bash
   java -cp build/classes/java/main com.jakub.bone.core.AirportServer

5. **Run the Clients**
   Simulate planes connecting to the server:
   ```bash
   java -cp build/classes/java/main com.jakub.bone.core.PlaneClient

6. **Launch Visualization** (Optional)
   Start the 3D visualization tool for real-time airplane monitoring:
   ```bash
   java -cp build/classes/java/main com.jakub.bone.core.SimulationLauncher


## 🎨 Interactive Visualization

Experience the airport operations in 3D! Planes move dynamically, and collisions or landings are visually represented. 
Control the camera with keyboard inputs:

- **W/A/S/D**: Move the camera
  
- **Z/X**: Zoom in/out
  
- **Arrow Keys**: Rotate the camera

## 📧 Contact

If you have any questions, feedback, or suggestions, feel free to reach out to me:

- **Email**: [jakub.bone1990@gmail.com](mailto:jakub.bone1990@gmail,com)
- **Blog**: [javamPokaze.pl](https://javampokaze.pl)  
- **LinkedIn**: [Jakub Bone](https://www.linkedin.com/in/jakub-bone)  

Let's connect and discuss this project further! 🚀
