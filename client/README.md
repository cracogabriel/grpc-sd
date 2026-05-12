# mflix Java Client

A Java Swing GUI client for the mflix movie management service.  
Communicates with a Python gRPC server using Protocol Buffers.

> **The server must be running before you start the client.**

---

## Project Structure

```
client/
├── pom.xml
├── movie.proto
└── src/
    └── main/
        └── java/
            ├── MovieClient.java
            ├── connection/
            │   └── ServerConnection.java
            └── gui/
                ├── MainWindow.java
                └── dialogs/
                    ├── AddMovieDialog.java
                    ├── GetMovieDialog.java
                    ├── UpdateMovieDialog.java
                    ├── DeleteMovieDialog.java
                    ├── SearchGenreDialog.java
                    └── SearchActorDialog.java
```

---

## Requirements

- Java 17 or higher
- Maven (to manage dependencies and compile the project)

Check your Java and Maven versions:

```bash
java -version
mvn -version
```

---

## How to Run

**1. Start the server first** (in a separate terminal):

```bash
cd server/
python server.py
```

**2. Compile the client and generate gRPC classes:**

```bash
mvn clean compile
```

Maven will automatically:
- Download the gRPC and Protobuf Java libraries
- Download the `protoc` compiler via the plugin
- Generate gRPC classes from `movie.proto`
- Compile all `.java` files

**3. Run the client:**

```bash
mvn exec:java -Dexec.mainClass="MovieClient"
```

---

## Features

| Button          | Operation                   |
| --------------- | --------------------------- |
| add movie       | Create a new movie          |
| get movie       | Fetch a movie by ID         |
| update movie    | Load and edit a movie by ID |
| delete movie    | Delete a movie by ID        |
| search by genre | List movies by genre        |
| search by actor | List movies by actor name   |

---

## Notes

- The client connects to `127.0.0.1:5000` by default. To change this, edit the `host` and `port` variables in `MovieClient.java`.
- The gRPC generated classes (`movies/`) are handled automatically by Maven in the `target/` directory. Do not edit them manually.
