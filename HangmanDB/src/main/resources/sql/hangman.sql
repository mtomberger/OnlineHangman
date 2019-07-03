CREATE TABLE Game (
                    id SERIAL PRIMARY KEY,
                    username varchar(255),
                    word varchar(15),
                    mistakes numeric(8),
                    timeNeeded numeric(8),
                    score numeric(8),
                    timestamp DATE
);