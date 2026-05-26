# gRPC: mflix Movie Manager

A distributed movie management system built with Python and Java as part of the Distributed Systems course.  
The server exposes CRUD operations over **gRPC** using Protocol Buffers. The client is a Java Swing GUI that communicates with the server via gRPC stubs.

## Problem Statement

Proposed by Prof. Rodrigo Campiolo as part of the Distributed Systems course. The goal is to implement a remote service for managing movies from the MongoDB Atlas **sample_mflix** dataset. The server receives requests via gRPC, processes them against the database, and returns serialized responses. All communication uses **Protocol Buffers** as the interface definition and serialization format.

---

## Project Structure

```
red/
├── server/
│   ├── server.py           # Entry point: starts gRPC server
│   ├── service.py          # gRPC service implementation
│   ├── database.py         # MongoDB connection setup
│   ├── helpers.py          # Validation and formatting functions
│   ├── movie.proto         # Protobuf service definition shared between server and client
│   └── .env                # MongoDB credentials (needs to be created manually)
└── client/
    ├── pom.xml             # Maven configuration file for dependencies and gRPC generation
    ├── movie.proto         # Protobuf service definition (same as server)
    └── src/main/java/
        ├── MovieClient.java    # Entry point: connects to server and opens GUI
        ├── connection/
        │   └── ServerConnection.java
        └── gui/
            ├── MainWindow.java
            └── dialogs/        # One dialog per operation
```

---

## Architecture

```
┌─────────────────────┐       HTTP/2 :5000       ┌─────────────────────┐
│   Java Swing GUI    │  ────── gRPC Call ─────► │   Python Server     │
│   (client/)         │  ◄──── gRPC Reply ────── │   (server/)         │
└─────────────────────┘                          └────────┬────────────┘
                                                          │ PyMongo
                                                 ┌────────▼────────────┐
                                                 │  MongoDB Atlas      │
                                                 │  sample_mflix       │
                                                 └─────────────────────┘
```

---

## Supported Operations

The gRPC `MovieService` defines the following operations:

| RPC Method    | Description                                               |
| ------------- | --------------------------------------------------------- |
| `CreateMovie` | Inserts a new movie into the database                     |
| `GetMovie`    | Fetches a single movie by ID                              |
| `UpdateMovie` | Updates an existing movie by ID                           |
| `DeleteMovie` | Removes a movie by ID                                     |
| `ListByActor` | Returns movies featuring a given actor (case-insensitive) |
| `ListByGenre` | Returns movies from a given genre (case-insensitive)      |

---

## Requirements

- Python 3.10+ (server)
- Java 17+ (client)
- Maven (client)
- A MongoDB Atlas account with the **sample_mflix** dataset loaded

---

## How to Run

> **The server must be started before the client.**

**1. Start the server.** See [`server/README.md`](server/README.md) for setup instructions, including how to configure the `.env` file with your database credentials.

```bash
cd server/
python3 server.py
```

**2. Start the client.** See [`client/README.md`](client/README.md) for full instructions.

```bash
cd client/
mvn clean compile
mvn exec:java -Dexec.mainClass="MovieClient"
```

---

## gRPC Protocol

All messages between client and server are structured using **Protocol Buffers** (proto3) and sent over **gRPC**.  
The service is defined in `movie.proto` and shared between both sides.  
Each side generates its own language-specific classes (Python and Java) from the same `.proto` file using `protoc` (and Maven plugins).

Unlike raw sockets, gRPC provides strongly-typed methods (e.g. `CreateMovie(Movie) returns (MovieResponse)`), automatically handling serialization, deserialization, and network transport via HTTP/2.
