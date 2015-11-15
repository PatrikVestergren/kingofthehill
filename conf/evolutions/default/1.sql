# --- First database schema

# --- !Ups

CREATE TABLE LAP (
  driver                    VARCHAR(255) NOT NULL,
  transponder               NUMERIC,
  lapNr                     NUMERIC,
  lapTime                   NUMERIC,
  ts                        NUMERIC
);

# --- !Downs

DROP TABLE IF EXISTS LAP;