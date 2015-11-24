
CREATE TABLE CURRENTRACER (
  driver                    VARCHAR(255) NOT NULL,
  transponder               NUMERIC,
  lapNr                     NUMERIC,
  lapTime                   VARCHAR(25) NOT NULL,
  fastest                   VARCHAR(25) NOT NULL,
  bestN                     VARCHAR(25) NOT NULL,
  bestFive                  VARCHAR(25) NOT NULL,
  tsPres                    VARCHAR(25) NOT NULL,
  ts                        DATE default now() NOT NULL
);

DROP TABLE IF EXISTS CURRENTRACER;