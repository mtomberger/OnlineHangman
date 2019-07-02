CREATE TABLE Game (
                    id SERIAL,
                    username varchar(255),
                    mistakes numeric(8),
                    timeNeeded numeric(8),
                    score numeric(8),
                    timestamp DATE
);