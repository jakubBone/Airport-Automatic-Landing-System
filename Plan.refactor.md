# Plan Refaktoryzacji - Airport Automatic Landing System

## Spis treści
1. [Podsumowanie stanu projektu](#1-podsumowanie-stanu-projektu)
2. [Zidentyfikowane problemy](#2-zidentyfikowane-problemy)
3. [Plan refaktoryzacji](#3-plan-refaktoryzacji)
4. [Analiza testów](#4-analiza-testów)
5. [Priorytetyzacja zadań](#5-priorytetyzacja-zadań)

---

## 1. Podsumowanie stanu projektu

### Struktura projektu
```
src/main/java/com/jakub/bone/
├── api/                    # REST API (Jetty servlets) - NIEPOTRZEBNE
├── application/            # PlaneHandler (wątki obsługi klientów)
├── client/                 # PlaneClient, komunikacja klient-serwer
├── config/                 # Constant.java, ConfigLoader.java
├── core/                   # SimulationLauncher (JavaFX entry point)
├── database/               # AirportDatabase (JOOQ + PostgreSQL)
├── domain/                 # Modele domenowe (Plane, Runway, Location)
├── generated/jooq/         # Wygenerowane klasy JOOQ
├── repository/             # PlaneRepository, CollisionRepository
├── server/                 # AirportServer (ServerSocket)
├── service/                # ControlTowerService, CollisionService, etc.
├── ui/                     # JavaFX wizualizacja 3D
└── utils/                  # Messenger, PlaneMapper, WaypointGenerator
```

### Co działa dobrze
- Modele domenowe (Plane, Runway, Location) - dobrze zdefiniowane
- Konfiguracja przez plik properties (config.properties)
- Użycie JOOQ dla type-safe SQL
- Log4j2 z thread context
- Częściowe zabezpieczenia wątkowe (CopyOnWriteArrayList, ReentrantLock)

---

## 2. Zidentyfikowane problemy

### 2.1 Hardcoded wartości

| Lokalizacja | Problem | Wartość |
|------------|---------|---------|
| `build.gradle` | Kredencjały DB w konfiguracji JOOQ | `airport` / `plane123` |
| `AirportDatabase.java` | Kredencjały i URL bazy | `localhost:5432`, `airport`, `plane123` |
| `AirportServer.java` | Port serwera | `5000` |
| `AirportStateService.java` | Host, port, liczba klientów | `localhost`, `5000`, `100` |
| `ApiServer.java` | Port REST API | `8080` |
| `PlaneClient.java` | Port serwera w main() | `5000` |
| `WaypointGenerator.java` | Parametry trajektorii | `radius=5000`, `waypoints=320`, etc. |

**Rozwiązanie:** Wynieść wszystkie wartości do `config.properties` lub zmiennych środowiskowych.

---

### 2.2 REST API - niepotrzebny komponent

**Obecne endpointy (Jetty servlets):**
- `/airport/start`, `/airport/stop`, `/airport/pause`, `/airport/resume`
- `/airport/uptime`, `/airport/planes/*`, `/airport/collisions`

**Problemy:**
1. API nie jest potrzebne do działania symulacji
2. Każdy servlet tworzy nową instancję `AirportStateService`
3. Brak walidacji i autentykacji
4. Servlety używają `System.err.println()` zamiast loggera
5. Tight coupling do implementacji serwera

**Rekomendacja:** Usunąć cały pakiet `api/` i klasę `ApiServer`. Jeśli API jest potrzebne w przyszłości - przepisać z użyciem Spring Web lub Javalin.

---

### 2.3 Wielowątkowość - nieprawidłowe użycie

#### Problem 1: Thread-per-connection model
```java
// AirportServer.java
while (true) {
    Socket clientSocket = serverSocket.accept();
    new PlaneHandler(clientSocket, ...).start();  // Nowy wątek dla każdego połączenia
}
```
**Problem:** Przy 100 klientach = 100 wątków. Brak thread poola.

**Rozwiązanie:** Użyć `ExecutorService` z fixed thread pool:
```java
ExecutorService executor = Executors.newFixedThreadPool(20);
executor.submit(new PlaneHandler(...));
```

#### Problem 2: Race conditions w CollisionService
```java
// CollisionService.java - brak synchronizacji
for (int i = 0; i < controlTowerService.getPlanes().size(); i++) {
    Plane plane1 = controlTowerService.getPlanes().get(i);  // Niebezpieczne!
```
**Problem:** Lista może się zmienić między wywołaniami `size()` i `get(i)`.

**Rozwiązanie:** Iterować po kopii listy lub użyć `ControlTowerService.executeWithLock()`.

#### Problem 3: Static mutable state
```java
// Airport.java
public static Runway runway1;  // Współdzielony mutowalny stan!
public static Runway runway2;
```
**Problem:** Dostęp z wielu wątków bez synchronizacji.

**Rozwiązanie:** Przenieść do `AirportConfiguration` jako instancję singleton z synchronizacją.

#### Problem 4: Thread.sleep() w logice biznesowej
```java
// Navigator.java - move()
Thread.sleep(Constant.UPDATE_DELAY);  // Blokuje wątek!
```
**Problem:** Blokujące operacje w logice domenowej.

**Rozwiązanie:** Użyć `ScheduledExecutorService` zamiast sleep.

#### Problem 5: Nieskończone pętle zamiast scheduled executors
```java
// CollisionService.java
while (true) {
    checkCollision();
    Thread.sleep(1000);  // Powinien być ScheduledExecutorService
}
```

---

### 2.4 Brak Separation of Concerns

#### SimulationLauncher (JavaFX)
- Inicjalizuje `AirportServer` (powinno być wstrzyknięte)
- Wywołuje `database.getSCHEMA().clearTables()` przy zamykaniu
- Łączy UI z lifecycle serwera

#### AirportServer
- Zarządza `ServerSocket` ORAZ logiką biznesową (running, paused)
- Zarządza wątkiem detekcji kolizji
- Przechowuje `startTime` do obliczania uptime
- Powinien tylko obsługiwać socket acceptance

#### PlaneHandler
- Obsługuje socket I/O ORAZ koordynację lotu
- Parsuje wiadomości ORAZ decyduje o fazach lotu
- Aktualizuje stan samolotu ORAZ bazę danych
- Tworzy nowy `FlightPhaseService` dla każdego połączenia

#### ControlTowerService
- Synchronizacja z `ReentrantLock`
- Dostęp do bazy danych (powinien przez repository)
- Logika biznesowa (alokacja pasów, collision risk)
- Mieszanie: State + Lock management

#### Plane (Domain Object)
- Zawiera `Navigator` (logika ruchu)
- `Navigator.move()` wywołuje `Thread.sleep()` (blokowanie!)
- Powinien być prostym data holderem

---

### 2.5 Brak Dependency Injection

**Obecny stan:**
```java
// Manualne tworzenie zależności wszędzie
public PlaneHandler(...) {
    this.messenger = new Messenger(socket);
    this.planeMapper = new PlaneMapper();
    this.flightService = new FlightPhaseService(controlTower, messenger);
}
```

**Problemy:**
1. Tight coupling - trudne do testowania
2. Brak możliwości mockowania
3. Każda klasa tworzy własne instancje

**Rozwiązanie:** Wprowadzić DI framework (Spring lub manual factory pattern).

---

### 2.6 Konfiguracja bazy danych (JOOQ)

**Problemy:**
1. Kredencjały hardcoded w `build.gradle` i `AirportDatabase.java`
2. Pojedyncze połączenie (brak connection pool)
3. Tworzenie tabel w runtime przez JOOQ (powinny być migracje)
4. Brak retry logic dla połączeń
5. Brak Dockera - manualna instalacja PostgreSQL

**build.gradle:**
```gradle
jooq {
    jdbc {
        url = 'jdbc:postgresql://localhost:5432/airport_system'
        user = 'airport'
        password = 'plane123'  // HARDCODED!
    }
}
```

**Rozwiązanie:**
1. Użyć zmiennych środowiskowych dla kredencjałów
2. Dodać HikariCP jako connection pool
3. Dodać Flyway/Liquibase dla migracji
4. Utworzyć `docker-compose.yml` z PostgreSQL

---

### 2.7 Architektura - brak wyraźnych warstw

**Obecny "warstwowy" chaos:**
```
UI (JavaFX) → Server → Service → Repository → Database
     ↓           ↓         ↓
   Domain ←←←←←←←←←←←←←←←←←
```

**Problemy:**
1. Brak interfejsów dla service layer
2. Brak abstrakcji dla Repository
3. Service classes mieszają I/O, logikę i stan
4. Monolityczna aplikacja (Server + API + UI w jednym procesie)

**Docelowa architektura:**
```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                    │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────┐  │
│  │  JavaFX UI  │  │ REST API    │  │ Socket Handler  │  │
│  │  (optional) │  │ (optional)  │  │                 │  │
│  └─────────────┘  └─────────────┘  └─────────────────┘  │
└─────────────────────────────────────────────────────────┘
                           │
┌─────────────────────────────────────────────────────────┐
│                    Application Layer                     │
│  ┌─────────────────────────────────────────────────┐    │
│  │ SimulationOrchestrator                          │    │
│  │ - Manages simulation lifecycle                  │    │
│  │ - Coordinates services                          │    │
│  └─────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────┘
                           │
┌─────────────────────────────────────────────────────────┐
│                      Domain Layer                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │
│  │ControlTower  │  │ FlightPhase  │  │  Collision   │   │
│  │  Service     │  │   Service    │  │   Detector   │   │
│  └──────────────┘  └──────────────┘  └──────────────┘   │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │
│  │    Plane     │  │   Runway     │  │   Airport    │   │
│  │   (Entity)   │  │   (Entity)   │  │    (Agg)     │   │
│  └──────────────┘  └──────────────┘  └──────────────┘   │
└─────────────────────────────────────────────────────────┘
                           │
┌─────────────────────────────────────────────────────────┐
│                  Infrastructure Layer                    │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐   │
│  │ PlaneRepo    │  │ CollisionRepo│  │  Socket I/O  │   │
│  │ (Interface)  │  │ (Interface)  │  │   Handler    │   │
│  └──────────────┘  └──────────────┘  └──────────────┘   │
│  ┌──────────────┐  ┌──────────────┐                     │
│  │ JOOQ Impl    │  │ Config       │                     │
│  │              │  │ Loader       │                     │
│  └──────────────┘  └──────────────┘                     │
└─────────────────────────────────────────────────────────┘
```

---

### 2.8 Elementy do usunięcia

| Element | Powód usunięcia |
|---------|-----------------|
| `api/` pakiet | Niepotrzebny, źle zaimplementowany |
| `ApiServer.java` | Jetty server niepotrzebny |
| Static fields w `Airport` | Zastąpić instancją singleton |
| `settings.gradle` linia `include 'org'` | Nieużywany moduł |
| Hardcoded main() w `PlaneClient` | Przenieść do testów/demo |

---

## 3. Plan refaktoryzacji

### Faza 1: Konfiguracja i Docker (Priorytet: WYSOKI)

#### 1.1 Utworzenie docker-compose.yml
```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: airport_system
      POSTGRES_USER: ${DB_USER:-airport}
      POSTGRES_PASSWORD: ${DB_PASSWORD:-plane123}
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql

volumes:
  postgres_data:
```

#### 1.2 Externalizacja konfiguracji
- Utworzyć `application.properties` z placeholderami dla zmiennych środowiskowych
- Zaktualizować `ConfigLoader` do obsługi env vars
- Usunąć hardcoded wartości z kodu

#### 1.3 Aktualizacja build.gradle
- Użyć zmiennych środowiskowych w konfiguracji JOOQ
- Naprawić `mainClass` w sekcji application

---

### Faza 2: Usunięcie niepotrzebnych komponentów (Priorytet: WYSOKI)

#### 2.1 Usunięcie REST API
- Usunąć pakiet `api/control/`
- Usunąć pakiet `api/monitoring/`
- Usunąć `ApiServer.java`
- Usunąć zależność Jetty z `build.gradle` (opcjonalnie - zostawić jeśli planowane do przyszłości)

#### 2.2 Czyszczenie
- Usunąć `include 'org'` z `settings.gradle`
- Usunąć main() z `PlaneClient.java`

---

### Faza 3: Architektura i Separation of Concerns (Priorytet: WYSOKI)

#### 3.1 Wprowadzenie interfejsów
```java
// Interfaces
public interface PlaneRepository {
    void save(Plane plane);
    Optional<Plane> findByFlightNumber(String flightNumber);
    List<Plane> findAllLanded();
}

public interface CollisionRepository {
    void save(Collision collision);
    List<Collision> findAll();
}

public interface ControlTowerService {
    boolean registerPlane(Plane plane);
    void removePlane(Plane plane);
    Optional<Runway> assignRunway(Plane plane);
    List<Plane> getPlanesInRiskZone(Plane plane);
}
```

#### 3.2 Rozdzielenie odpowiedzialności

**PlaneHandler** podzielić na:
- `PlaneConnectionHandler` - obsługa socket I/O
- `FlightCoordinator` - koordynacja faz lotu
- `PlaneMessageParser` - parsowanie wiadomości

**AirportServer** podzielić na:
- `SocketServer` - tylko akceptowanie połączeń
- `SimulationManager` - zarządzanie stanem symulacji

**Navigator** wyciągnąć z Plane:
- `Plane` - tylko dane (entity)
- `NavigationService` - logika ruchu

#### 3.3 Utworzenie Application Layer
```java
public class SimulationOrchestrator {
    private final SocketServer socketServer;
    private final CollisionDetector collisionDetector;
    private final ControlTowerService controlTower;

    public void start() { ... }
    public void stop() { ... }
    public void pause() { ... }
}
```

---

### Faza 4: Naprawa wielowątkowości (Priorytet: WYSOKI)

#### 4.1 Wprowadzenie ExecutorService
```java
public class SocketServer {
    private final ExecutorService connectionPool =
        Executors.newFixedThreadPool(20);

    public void acceptConnections() {
        while (running) {
            Socket socket = serverSocket.accept();
            connectionPool.submit(() -> handleConnection(socket));
        }
    }
}
```

#### 4.2 Naprawa CollisionService
```java
public class CollisionDetector {
    private final ScheduledExecutorService scheduler =
        Executors.newSingleThreadScheduledExecutor();

    public void start() {
        scheduler.scheduleAtFixedRate(
            this::detectCollisions,
            0, 1, TimeUnit.SECONDS
        );
    }

    private void detectCollisions() {
        List<Plane> snapshot = controlTower.getPlanesSnapshot();
        // Bezpieczna iteracja po kopii
    }
}
```

#### 4.3 Usunięcie static mutable state
```java
// Zamiast static fields w Airport:
public class AirportConfiguration {
    private final Runway runway1;
    private final Runway runway2;

    // Singleton lub DI bean
}
```

#### 4.4 Usunięcie Thread.sleep() z domeny
- Przenieść timing logic do `ScheduledExecutorService`
- `Navigator` zwraca tylko następny waypoint, nie czeka

---

### Faza 5: Baza danych (Priorytet: ŚREDNI)

#### 5.1 Connection Pool
```java
public class DatabaseConfig {
    public DataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(System.getenv("DB_URL"));
        config.setUsername(System.getenv("DB_USER"));
        config.setPassword(System.getenv("DB_PASSWORD"));
        config.setMaximumPoolSize(10);
        return new HikariDataSource(config);
    }
}
```

#### 5.2 Migracje (Flyway)
```
src/main/resources/db/migration/
├── V1__create_planes_table.sql
├── V2__create_collisions_table.sql
└── V3__add_indexes.sql
```

---

### Faza 6: Dependency Injection (Priorytet: ŚREDNI)

Opcja A: **Manual Factory Pattern** (prostsze)
```java
public class ApplicationFactory {
    public SimulationOrchestrator create() {
        DataSource ds = new DatabaseConfig().createDataSource();
        PlaneRepository planeRepo = new JooqPlaneRepository(ds);
        CollisionRepository collisionRepo = new JooqCollisionRepository(ds);
        ControlTowerService controlTower = new ControlTowerServiceImpl(planeRepo);
        // ...
        return new SimulationOrchestrator(...);
    }
}
```

Opcja B: **Spring Framework** (więcej boilerplate, ale standardowe)
- Dodać Spring Context
- Użyć `@Component`, `@Service`, `@Repository`
- Konfiguracja przez `@Configuration` classes

---

### Faza 7: Refaktoryzacja UI (Priorytet: NISKI)

- Oddzielić `SimulationLauncher` od logiki serwerowej
- UI powinno komunikować się z symulacją przez interfejs
- Rozważyć osobny proces dla UI (komunikacja przez socket/events)

---

## 4. Analiza testów

### Obecny stan testów

| Test | Typ | Problemy |
|------|-----|----------|
| `ControlTowerTest` | Unit | Deprecated assertions, słabe nazewnictwo |
| `DatabaseConnectionTest` | Integration | Wymaga żywej bazy, brak izolacji |
| `FlightPhaseTest` | Unit | Null dla messenger, słaba izolacja |
| `ClientServerConnectionTest` | Integration | Hardcoded sleep, brak timeout |
| `CollisionTest` | Unit | Nie sprawdzony |
| `RunwayTest` | Unit | Nie sprawdzony |
| `FuelManagerTest` | Unit | Nie sprawdzony |
| `LocationTest` | Unit | Nie sprawdzony |

### Brakujące testy

1. **Unit tests:**
   - PlaneHandler
   - Messenger
   - PlaneMapper
   - WaypointGenerator
   - AirportServer
   - CollisionService

2. **Integration tests:**
   - Pełny flow: connection → registration → landing
   - Collision detection end-to-end
   - Database operations

3. **Concurrency tests:**
   - Thread safety w ControlTowerService
   - Race conditions w CollisionService

### Plan naprawy testów

#### 4.1 Struktura testów
```
src/test/java/
├── unit/
│   ├── domain/
│   │   ├── PlaneTest.java
│   │   ├── RunwayTest.java
│   │   └── NavigatorTest.java
│   ├── service/
│   │   ├── ControlTowerServiceTest.java
│   │   ├── FlightPhaseServiceTest.java
│   │   └── CollisionDetectorTest.java
│   └── infrastructure/
│       ├── PlaneRepositoryTest.java
│       └── CollisionRepositoryTest.java
├── integration/
│   ├── DatabaseIntegrationTest.java
│   ├── ClientServerIntegrationTest.java
│   └── FullSimulationIntegrationTest.java
└── testcontainers/
    └── PostgresTestContainer.java  # Testcontainers dla bazy
```

#### 4.2 Testcontainers dla bazy danych
```java
@Testcontainers
class DatabaseIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres =
        new PostgreSQLContainer<>("postgres:15");

    @BeforeEach
    void setup() {
        // Użyj container.getJdbcUrl()
    }
}
```

#### 4.3 Naprawa istniejących testów
- Zamienić deprecated `assertEquals(boolean)` na `assertEquals(expected, actual)`
- Dodać `@Timeout` do testów wielowątkowych
- Mockować bazę danych zamiast używać żywej
- Użyć `CountDownLatch` zamiast `Thread.sleep()`

---

## 5. Priorytetyzacja zadań

### Poziom 1 - Krytyczne (Tydzień 1-2)
| # | Zadanie | Effort | Impact |
|---|---------|--------|--------|
| 1 | Utworzenie docker-compose.yml | Niski | Wysoki |
| 2 | Externalizacja konfiguracji (env vars) | Średni | Wysoki |
| 3 | Usunięcie REST API (pakiet api/) | Niski | Średni |
| 4 | Naprawa race conditions w CollisionService | Średni | Wysoki |

### Poziom 2 - Ważne (Tydzień 3-4)
| # | Zadanie | Effort | Impact |
|---|---------|--------|--------|
| 5 | Wprowadzenie ExecutorService | Średni | Wysoki |
| 6 | Usunięcie static mutable state | Średni | Wysoki |
| 7 | Wprowadzenie interfejsów Repository | Średni | Średni |
| 8 | Dodanie HikariCP connection pool | Niski | Średni |

### Poziom 3 - Poprawa jakości (Tydzień 5-6)
| # | Zadanie | Effort | Impact |
|---|---------|--------|--------|
| 9 | Separation of Concerns (podział klas) | Wysoki | Wysoki |
| 10 | Naprawa testów + Testcontainers | Średni | Średni |
| 11 | Usunięcie Thread.sleep() z domeny | Średni | Średni |
| 12 | Flyway migracje | Niski | Niski |

### Poziom 4 - Nice to have (Później)
| # | Zadanie | Effort | Impact |
|---|---------|--------|--------|
| 13 | Dependency Injection (Spring/manual) | Wysoki | Średni |
| 14 | Refaktoryzacja UI layer | Wysoki | Niski |
| 15 | Dodanie brakujących testów | Wysoki | Średni |

---

## Podsumowanie

Projekt wymaga istotnej refaktoryzacji, ale ma solidne fundamenty (dobre modele domenowe, konfiguracja przez properties). Kluczowe obszary do naprawy:

1. **Docker + konfiguracja** - umożliwi łatwe uruchamianie
2. **Wielowątkowość** - naprawa race conditions, wprowadzenie ExecutorService
3. **Architektura** - separation of concerns, interfejsy
4. **Testy** - izolacja od bazy, Testcontainers

Szacowany czas całej refaktoryzacji: **4-6 tygodni** przy pracy part-time.

Zalecam rozpocząć od Fazy 1 (Docker) i Fazy 4 (wielowątkowość), ponieważ te zmiany dadzą największy zwrot przy relatywnie niskim nakładzie pracy.
