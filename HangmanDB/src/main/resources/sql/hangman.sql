CREATE TABLE Player (
  playerId varchar(255),
  username varchar(255)
);

ALTER TABLE Player ADD PRIMARY KEY(playerId);

CREATE TABLE Game (
  player1 varchar(255),
  player2 varchar(255),
  wordguess1 varchar(255),
  wordguess2 varchar(255),
  date DATE,
  winner varchar(255)
);

ALTER TABLE Game ADD PRIMARY KEY(player1, player2, wordguess1, wordguess2, date);
ALTER TABLE Game
  ADD CONSTRAINT FK_Player1
    FOREIGN KEY (player1) REFERENCES Player(playerId);

ALTER TABLE Game
  ADD CONSTRAINT FK_Player2
    FOREIGN KEY (player2) REFERENCES Player(playerId);

ALTER TABLE Game
  ADD CONSTRAINT FK_Winner
    FOREIGN KEY (winner) REFERENCES Player(playerId);