# OnlineHangman - University Project
__Course:__ Distributed Computing  
__Year:__ 2019  
__Projectteam:__  

## Ausgangssituation
## Übersicht
## Aufgabenstellung
* Zwei Spieler können gegeneinander das Spiel Hangman spielen
* Ein Spieler kann sich selbst einen Namen geben
* Ein Spieler kann sich das Wort, das der jeweils Andere erraten muss selbst aussuchen
* Die eingegebenen Wörter werden auf Korrektheit überprüft (Nomen,>3 Buchstaben,...)
* Jeder Spieler kann unabhägig von anderen Buchstaben (A-Z) raten
* Die Spieler, die gegeneinander spielen sehen den Fortschritt des jeweils anderen
* Der Gewinner wird nach dem Fertigraten beider Spieler ermittelt
* Für jedes erratene Wort gibt es einen Score in Punkten
* Jeder Spieler kann seinen Score speichern
* Die 15 besten Scores können angezeigt werden
* Es gibt ein API zur Abfrage aller Scores
## Installation
## Anleitung
## Architektur
![Architektur-Bild][architecture]
## Technologien
* Spring Boot - Java Applikationen
* SockJs - Websockets
* Hibernate - Java OR-Mapper
* PostgreSQL - einfache Scoredatenbank
* HTML/JS/CSS - Benutzeroberfläche
* GIT - Sourcecodeverwaltung
## Zusammenfassung
## Anhang
### Sourcecode
https://github.com/mtomberger/OnlineHangman/
### Links
Spring Boot Framework: https://spring.io/projects/spring-boot
Spring Boot/ STOMP Tutorial: https://www.baeldung.com/websockets-api-java-spring-client
STOMP js: https://github.com/stomp-js/stompjs


[architecture]: https://github.com/mtomberger/OnlineHangman/blob/master/architecture.png "Architektur"




