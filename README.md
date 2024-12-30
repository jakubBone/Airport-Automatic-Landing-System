# Airport Automatic Landing System

Welcome to the Automatic Landing System with Java. This project is currently under development and demonstrates a client-server architecture
designed to manage the automatic landing of airplanes in a simulated airspace around an domain. The system ensures safe landings by controlling the movement
of airplanes in the airspace, managing fuel levels, and preventing collisions. I am continuously adding new features and improvements as the project evolves.


## Project Structure
The project now is divided into several key components:

PlaneClient (Client): Represents an airplane and handles the airplane's movement, utills with the server, and fuel management.

AirportServer (Server): Manages airplane connections, processes landing requests, and ensures safe landing by controlling runway availability.

AirSpace: Manages the airspace, including tracking airplanes, collision detection, and enforcing airspace capacity limits.

AirTrafficController: Manages runway assignments and landing procedures.

PlaneHandler: Handles utills between each PlaneClient and the AirportServer. 

Runway: Represents a physical runway with a specific location at the domain.

Location: Handles the coordinates (x, y, altitude) of the airplanes and runways within the airspace.


## Project Overview
The application will allow airplanes (clients) to enter the airspace around an domain and follow a holding pattern.

The server processes incoming requests from airplanes, guides them through the airspace, and ensures they land safely. 

Here are the main features:

Holding Pattern: Airplanes follow a circular holding pattern around the domain, gradually lowering their altitude while waiting for an available runway.

Collision Prevention: The server monitors the airspace to prevent collisions by managing the positions of airplanes and ensuring safe distances between them.

Landing Management: When a runway is available, the server guides an airplane out of its holding pattern and onto the runway for landing.

Fuel Management: The system tracks the fuel levels of each airplane. If a plane runs out of fuel before landing, an emergency is triggered.

Database Operations: The application uses a database to store records of successful landings, collisions, and other important events in the airspace.


## How to Run

To run the Automatic Landing System, ensure you have the Java Development Kit (JDK) installed on your system. 
Follow these steps:

### Clone this repository to your computer:
<https://github.com/jakubBone/Automatic-Landing-System>

### Navigate to the project directory:
cd AirportAutomatic-Landing-System

### Compile and run the server:
javac AirportServer.java
java AirportServer

### Compile and run the client:
javac PlaneClient.java
java PlaneClient


## Requirements
To compile and run the application, you'll need Java Development Kit (JDK) installed on your system.


## Future Developments
This project is a work in progress, with ongoing updates focused on enhancing functionality.


## Logging
The application uses Log4j2 for logging. The application generates detailed logs that are useful
for developers during troubleshooting. Access to the source code is essential for a full analysis and repair.