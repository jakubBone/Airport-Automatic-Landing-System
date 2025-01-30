# 🌍 API Documentation - Airport Automatic Landing System

This document describes the REST API endpoints available in the Airport Automatic Landing System.

📌 Base URL: `http://localhost:8080`


## 📌 Table of Contents

### 1️⃣  Control Endpoints 

- `POST /airport/start` – Start the airport system
- `POST /airport/pause` – Pause the system
- `POST /airport/resume` – Resume the system
- `POST /airport/stop` – Stop the system

### 2️⃣  Monitoring Endpoints

- `GET /airport/uptime` – Get the current uptime of the airport system
- `GET /airport/planes/count` – Get the number of planes in the air
- `GET /airport/planes/flightNumbers` – Get the flight numbers list of planes in the air
- `GET /airport/planes/landed` – Get a list of landed planes
- `GET /airport/collisions` – Get information about past collisions\


## 1️⃣  Control Endpoints 

These endpoints allow you to control the airport system.  

### `POST /airport/start`  
📌 **Description:** Starts the airport system. Creates the control tower and allows planes to land.  

🔹 **Request Example:**  
```sh
curl -X POST http://localhost:8080/airport/start
```
🔹 **Response Example:** 
```json
{
  "message": "airport started successfully"
}
```

### `POST /airport/pause`
📌 **Description:** Pauses the airport system, preventing new planes from landing. 

🔹 **Request Example:**  
```sh
curl -X POST http://localhost:8080/airport/pause
```

🔹 **Response Example:** 
```json
{
  "message": "airport paused successfully"
}
```

### `POST /airport/resume`
📌 **Description:** Resumes the airport system after being paused.

🔹 **Request Example:**  
```sh
curl -X POST http://localhost:8080/airport/resume
```

🔹 **Response Example:** 
```json
{
  "message": "airport resumed successfully"
}
```

### `POST /airport/stop`
📌 **Description:** Stops the airport system, preventing all further activity.

🔹 **Request Example:**  
```sh
curl -X POST http://localhost:8080/airport/stop
```

🔹 **Response Example:** 
```json
{
  "message": "airport stopped successfully"
}
```


## 1️⃣  Monitoring Endpoints

These endpoints provide real-time information about the airport system. 

### `GET /airport/uptime`  
📌 **Description:** Returns the uptime of the airport system.

🔹 **Request Example:**  
```sh
curl -X GET http://localhost:8080/airport/uptime
```

🔹 **Response Example:** 
```json
{
  "message": "00:10:23"
}
```

### `GET /airport/planes/count`
📌 **Description:** Returns the number of planes in the air.

🔹 **Request Example:**  
```sh
curl -X GET http://localhost:8080/airport/planes/count
```

🔹 **Response Example:** 
```json
{
  "count": 45
}
```

### `GET /airport/planes/flightNumbers`
📌 **Description:** Returns the flight numbers list of planes in the air.

🔹 **Request Example:**  
```sh
curl -X GET http://localhost:8080/airport/planes/flightNumbers
```

🔹 **Response Example:** 
```json
{
  "flight numbers": ["MH101", "LH202", "BA303"]
}
```

### `GET /airport/planes/landed`
📌 **Description:** Returns a list of landed planes.

🔹 **Request Example:**  
```sh
curl -X GET http://localhost:8080/airport/planes/landed
```

🔹 **Response Example:** 
{
  "landed planes": ["AA001", "DL305", "UA786"]
}

### `GET /airport/collisions`
📌 **Description:** Returns the flight numbers list of planes collided in the past.

🔹 **Request Example:**  
```sh
curl -X GET http://localhost:8080/airport/collisions
```

🔹 **Response Example:** 
```json
{
  "collided planes": ["MH101, LH202"]
}
```

## 📌 Notes
- API responses are in JSON format.
- The system must be started (`/airport/start`) before using monitoring endpoints.
- The `/airport/planes/*` endpoints provide dynamic information based on real-time simulation data.


## 📧 Contact

If you have any questions, feedback, or suggestions, feel free to reach out to me:

- **Email**: [jakub.bone1990@gmail.com](mailto:jakub.bone1990@gmail,com)
- **Blog**: [javamPokaze.pl](https://javampokaze.pl)  
- **LinkedIn**: [Jakub Bone](https://www.linkedin.com/in/jakub-bone)  

