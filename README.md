# kalah

How to start the kalah application
---

1. Run `mvn clean install` to build your application
1. Start application with `java -jar target/kalah-1.0-SNAPSHOT.jar server config.yml`
1. To create a new game send a POST request to url `http://localhost:8080/games`
1. To make a move, send a PUT request to url `http://localhost:8080/games/{gameId}/pits/{pitId}`


