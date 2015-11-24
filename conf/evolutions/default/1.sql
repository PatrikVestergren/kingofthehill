
CREATE TABLE LAP (
  driver                    VARCHAR(255) NOT NULL,
  transponder               NUMERIC,
  lapNr                     NUMERIC,
  lapTime                   NUMERIC,
  ts                        DATE default now() NOT NULL
);

DROP TABLE IF EXISTS LAP;