
CREATE TABLE LAP (
  driver                    VARCHAR(255) NOT NULL,
  transponder               NUMERIC,
  lapNr                     NUMERIC,
  lapTime                   NUMERIC,
  ts                        DATE default now() NOT NULL
);

CREATE TABLE BESTNLAPS (
  driver                    VARCHAR(255) NOT NULL,
  transponder               NUMERIC,
  totalTime                 NUMERIC,
  totalTimePres             VARCHAR(25) NOT NUL,
  tsPres                    VARCHAR(25) NOT NUL,
  ts                        DATE default now() NOT NULL
);

CREATE TABLE BESTMINUTES (
  driver                    VARCHAR(255) NOT NULL,
  transponder               NUMERIC,
  laps                      NUMERIC,
  totalTime                 NUMERIC,
  result                    VARCHAR(25) NOT NUL,
  tsPres                    VARCHAR(25) NOT NUL,
  ts                        DATE default now() NOT NULL
);

CREATE TABLE CURRENTRACER (
  driver                    VARCHAR(255) NOT NULL,
  transponder               NUMERIC,
  lapNr                     NUMERIC,
  lapTime                   VARCHAR(25) NOT NUL,
  fastest                   VARCHAR(25) NOT NUL,
  bestN                     VARCHAR(25) NOT NUL,
  bestFive                  VARCHAR(25) NOT NUL,
  tsPres                    VARCHAR(25) NOT NUL,
  ts                        DATE default now() NOT NULL
);

DROP TABLE IF EXISTS LAP;
DROP TABLE IF EXISTS BESTNLAPS;
DROP TABLE IF EXISTS BESTMINUTES;
DROP TABLE IF EXISTS CURRENTRACER;