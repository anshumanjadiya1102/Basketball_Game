# JavaFX Football Multiplayer
If you want to play this game, please ensure that you have:
- Java 17 (or newer)
- Maven (to build)
- JDK (Java Development Kit)
- (Optional) JavaFX SDK (only if your JDK doesn’t bundle it)

## Prereqs
- Java 17+
- Maven 3.9+

## Build
```
mvn -q -DskipTests package
```

## Run Server
```
java -cp target/football-multiplayer-java-1.0.0.jar net.footy.server.ServerMain 5555
```

## Run Client (two instances)
```
# If JavaFX is provided by the plugin, this works:
mvn javafx:run -Pclient -Dexec.mainClass=net.footy.client.ClientMain -Dargs="localhost 5555"

# Or run the jar directly (ensure JavaFX modules are available on classpath)
java -cp target/football-multiplayer-java-1.0.0.jar net.footy.client.ClientMain localhost 5555
```

## Controls
- Player 1: WASD + Space
- Player 2: Arrow keys + Enter

## Notes
- The server is authoritative; clients only send inputs and render received state.
- Physics/logic are simplified for learning purposes.
- Tweak constants in `GameConstants` to change feel (speed, friction, kick power, sizes).
