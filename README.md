# OnlineHangman - University Project
__Course:__ Distributed Computing  
__Year:__ 2019  
__Projectteam:__  

## Ausgangssituation
Es soll ein Projekt erstellt werden, das die Möglichkeiten und Herrausforderungen von Distributed Computing zeigt.
Dazu müssen verschiedene unabhängige Teile in einem System zusammenarbeiten. 
## Übersicht
Das Projekt besteht aus zwei Anwendungen. Die erste Anwendung ist eine mit dem Spring-Boot Framework implementierte Webapplikation für die Abwicklung der Hangman-Spiele. Diese stellt die Benutzeroberfläche für die Spieler zur Verfügung und benachrichtigt diese über eine WebSocket-Anbindung. Sie wird mit einer externen Wörterbuch-API und mit der zweiten Anwendung mithilfe REST-Schnittstelle verbunden. Über diese werden erspielte Ergebnisse gespeichert.    

Die zweite Anwendung ist ebenso mit dem Spring-Boot Framework implementiert und benutzt Hibernate zum Aufbau der Datenbank für Spielergebnisse. Diese Anwendung ist für die Speicherung von Scores aus der ersten Anwendung zuständig, muss aber nicht am selben Server wie die erste Anwendung laufen. Außerdem bietet sie für externe Benutzer einen REST-Service mit dem Score-Daten abgefragt werden können.
## Themen

__1. Bidirectional Communication (WebSocket)__   
Ein Websocket dient dazu den jeweils anderen Spieler im Raum den Fortschritt seines gegenübers live anzuzeigen. Außerdem werden Websocket-Nachrichten dazu verwendet, alle Daten vom Client zum Server zu übertragen. Dazu zählen der Spielername und das Wort, das der andere Spieler eraten muss sowie eine Spieler-Id und diverse Score-Daten. Sobald ein Spieler einen Raum beitritt wird eine Verbindung hergestellt über die das gesamte Spiel bis zum Speichern des Scores abgewickelt wird.   

__2. Background Services__  
Die Applikation, die die Spiele und Spieler verwaltet loggt alle Ereignisse mit verschiedenen Log-Levels in eine Datei. Dabei wird erfasst:
  * Ein falsches Wort wurde eingegeben
  * Spieler eröffnet Spiel
  * Spieler betritt Spiel von anderen
  * Ein Buchstabe wird richtig/falsch geraten
  * Ein Spieler gewinnt/verliert
  * Ein Spiel ist abgeschlossen
  * Ein Spieler erhält einen Score
  * Allgemeine Fehler  
  
__3. Message Queuing__   
Die gesammte Kommunikation zwischen Spielern und Hangman-Server wird mt Websockets abgewickelt.
Für die vereinzelten Aufrufe des Hangman-API-Servers (HangmanDB) stellt dieser eine REST-API zur Verfügung. 
Weil die ausgetauschten Daten keine riesigen Ausmase annehmen und keine paralelle Bearbeitung nötig ist, ist der einsatz einer Message-Queue hier nicht notwendig. Das Senden von gleichen Nachrichten an mehrere Empfänger (Spieler) wird von Websockets besser unterstützt. 
Message-Queue bieten weitere möglichkeiten wie Prioritäten, Authentifizierung oder Transaktionen, die in deisem Fall aber alle nicht benötigt werden. Daher sind Websockets für diese Art der Kommunikation die bessere Wahl.

__4. Different Programming Languages__   
Am Client wird JavaScript für die Kommunikation über Websockets eingesetzt. Der gesammte Server-Teil besteht aus zwei Java-Applikationen. Dabei wird das Spring Boot Framework eingesetzt. Zur Anbindung der Datenbank wird der OR-Mapper Hibernate eingesetzt.  

__5. Distributed Demonstration__  
Es wird versucht den Hangman-API-Server (HangmanDB) und die Spiel-Applikation auf verschiedenen Geräten zu hosten und diese zu verbinden.  

__6. Integration of different Data Sources__   
Für die Speicherung der Scores wird eine Postgres Datenbank verwendet. Diese ist mit dem Hangman-API-Server (HangmanDB) verbunden.
Um zu überprüfen, ob eingegebene Wörter richtige englische Nomen sind, wird die externe REST-API von https://www.datamuse.com/api/ verwendet.  

__7. Client UI__   
Damit der Spielablauf ansprechend und intuitiv ist, gibt es eine mit HTML/CSS/JS gebaute Benutzeroberfläche, über die Hangman-Spiele abgewickelt und Scores angezeigt werden. 

## Ziele
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
__Vorraussetzungen__  
1. Apache Maven: https://maven.apache.org/download.cgi  
2. PostgreSQL Datenbank: https://www.postgresql.org/
    * Username: postgres
    * Passwort (für Testzwecke): postgres  
    
__Setup__  
1. Den Sourcecode als .zip oder über GIT herunterladen
2. Postgres Datenbank "hangmandb" anlegen
3. SQL in \HangmanDB\src\main\resources\sql\hangman.sql auf "hangmandb" ausführen  

__Starten__
1. Hangman (Applikation) starten: \HangmanGame\src\main\java\at\hangman\hangman\HangmanApplication    
   Commandline: Im HangmanGame-Ordner 
   ``` mvn org.springframework.boot:spring-boot-maven-plugin:run ``` ausführen
2. HangmanDB (API) starten: \HangmanDB\src\main\java\hangmandb\hangmandb\HangmandbApplication    
   Commandline: Im HangmanDB-Ordner 
   ``` mvn org.springframework.boot:spring-boot-maven-plugin:run ``` ausführen
3. Ports: Applikation: 8080, API: 8081  

## Anleitung
### Als Spieler
Zu Beginn muss man ein Wort, das der Gegenspieler erraten muss eingeben. Es muss ein englisches Nomen mit mindestens 3 Buchstaben sein.
Außerdem muss man den Namen eingeben, unter welchem man spielen möchte. Nach dem Klick auf den Start-Button muss gewartet werden, bis ein zweiter Spieler das Spiel betritt.

Nun bekommt man das Wort, das erraten werden muss in Form von Strichen angezeigt. Im rechten Bereich ist der Status des Gegenübers zu finden, mit dem vorher gewählten Wort und den bereits verbrauchten Versuchen in Form der Hangman-Zeichnung. Um einen Buchstaben zu Raten, wird dieser einfach auf der Tastatur eingegeben. Mit Enter-Taste wird der geratene Buchstabe bestätigt. Dann sieht man ob man richtig gelegen hat und ein oder mehrere der Striche mit einem Buchstaben befüllt werden oder aber sich die Hangman-Zeichnung langsam vervollständigt. 

Für das Raten muss man nicht auf den Gegenspieler werten. Das Spiel ist zu Ende wen beide Spieler das Wort erraten haben oder alle Versuche aufgebraucht haben. Dann wird der Score der Spieler angezeigt. Man sieht ob man besser wwar wie der Gegenspieler und bekommt die Chance seinen Score zu speichern.    

__Wann habe ich gewonnen/verloren?__        

Gewonnen hat man, wenn man im Score-Bildschirm über dem Gegenüber an erster Stelle steht. Der eiigene Spieler ist mit einem Spieler-Symbol markiert. Verloren hat man wenn man zweiter im Score-Bildschirm wird oder alle Versuch aufgebraucht hat.

__Wie rate ich einen Buchstaben?__  

Wenn man in einem Spiel einen Gegenspieler gefunden hat (der Name erscheint rechts) muss einfach der Buchstabe auf der Tastatur eingegeben und mit Enter bestätigt werden.

__Wie sehe ich meine Score?__  

Am Ende eines Spiels wird der Score für dieses Spiel angezeigt. Dort sieht man wer gewonnen hat und bekommt die Chance seinen Score zu speichern.

__Wie starte ich ein Spiel?__  

Auf der Startseite muss ein Name und ein englisches Namenword in die Felder eingegeben werden. Mit dem "Start Game"-Button geht es los. Es kann aber sein dass man auf einen Gegenspieler warten muss.

__Wie sehe ich Scores von Anderen?__  

Die besten Scores können über den "Scores"-Button auf der Startseite angesehen werden.

### Als Benutzer der API

Um die Hangman-Scores API zu benutzen, müssen HTTP-GET-Requests auf die URL der API abgesetzt werden.

``` /score?size=5``` gibt die ersten 5 Spieler des Scoreboards zurück   

## Architektur
![Architektur-Bild][architecture]
## Technologien
* Spring Boot - Java Applikationen
* STOMP.js - Websockets
* Hibernate - Java OR-Mapper
* PostgreSQL - einfache Scoredatenbank
* HTML/JS/CSS - Benutzeroberfläche
* GIT - Sourcecodeverwaltung
## Zusammenfassung
### Ergebnis
Man kann gegeneinander Hangman spielen, die Wörter werden vorher auf Richtigkeit überprüft. Die gesamte Kommunikation zwischen Spelern und dem Server erfolgt über Websockets. Alle Informationen wie Fehler, geratene und eratene Buchtaben werden zwischen den Spielern in einem Raum (immer 2) synchronisiert. Haben beide Spieler das Spiel abgeschlossen, wird für jeden Spieler ein Score und der Gewinner angezeigt. Dann kann ein Spieler seinen Score speichern. Die 10 besten gespeicherten Scores können angesehen werden. Alle Scores werden in einer Datenbank abgelegt und sind über eine REST-API (gereiht nach Score) verfügbar.  
### Bekannte Probleme
* Die Spiele werden in Räumen verwaltet. Sollten zu viele Räume gleichzeitig eröffnet/bespielt werden, kann es zu Speicherknappheit kommen. 
* Die API für die Scores kann ohne Authentifizierung verwendet werden. Das birgt Gefahren für den Datenbestand und macht die API angreifbar für DDoS und andere Attacken
* Jeder Spieler kann jeden Namen eingeben. Es gibt keine Anmeldefunktion, deshalb ist nicht wirklich nachvollziebar wer welchen Score erspielt hat.
* Socketnachrichten werden immer an alle Spieler geschickt. Erst diese "hören" nur auf die Nachrichten, die Sie betreffen. Dadurch werden viele Daten an viele Spieler verschickt. Es gäbe die Möglichkeit pro Raum einen eigenen Nachrichtenservice zu erstellen.
* Es kommt bei langen Wartezeiten zwischen Eingaben von Spielern zu Verbindungsabbrüchen beim Socket (>2 min).
### Weitere Möglichkeiten
* Ausbau der API mit Authentifizeirung und umfangreicheren Statistiken.
* Einbau einer Anmeldefunktion für User
* Hangman-Spiele mit mehr als zwei Spieler in einem Raum. Der Hangman-Server besitzt diese Funktion, sie ist allerdinbgs nicht in die Benutzeroberfläche integriert.
* Mögliche Übersetzung der Applikation in mehrere Sprachen. Zu ratente Wörter in mehreren Sprachen möglich.
* Automatische Auswertung der Logs (z.B.: Logstash)
### Persönliche Menung / Erkenntnisse
An diesem Projekt, bestehent aus zwei Applikationen und einer Datenbank erkennt man die Herausforderungen, die das Aufteilen von Anwendungsteilen über mehrere Server bringen. Die Übertragung der Scores zwischen Spiel und Score-Datenbank ist komplexer als in monolithischen Architekturen. Der Einsatz der WebSocket-Verbindung bringt die nötige Funktion Nachrichten vom Server zum Client zu schicken. Ohne diese wäre das Spielen nicht möglich.
## Anhang
### Sourcecode
https://github.com/mtomberger/OnlineHangman/
### Links
Spring Boot Framework: https://spring.io/projects/spring-boot  
Spring Boot/ STOMP Tutorial: https://www.baeldung.com/websockets-api-java-spring-client  
STOMP js: https://github.com/stomp-js/stompjs  


[architecture]: https://github.com/mtomberger/OnlineHangman/blob/master/architecture.png "Architektur"




