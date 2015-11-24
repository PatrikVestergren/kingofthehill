# --- !Ups
CREATE TABLE LAP (
  driver                    VARCHAR(255) NOT NULL,
  transponder               NUMERIC,
  lapNr                     NUMERIC,
  lapTime                   NUMERIC,
  ts                        DATE default now() NOT NULL
);
# --- !Ups
CREATE TABLE BESTNLAPS (
  driver                    VARCHAR(255) NOT NULL,
  transponder               NUMERIC,
  totalTime                 NUMERIC,
  totalTimePres             VARCHAR(25) NOT NULL,
  tsPres                    VARCHAR(25) NOT NULL,
  ts                        DATE default now() NOT NULL
);
# --- !Ups
CREATE TABLE BESTMINUTES (
  driver                    VARCHAR(255) NOT NULL,
  transponder               NUMERIC,
  laps                      NUMERIC,
  totalTime                 NUMERIC,
  result                    VARCHAR(25) NOT NULL,
  tsPres                    VARCHAR(25) NOT NULL,
  ts                        DATE default now() NOT NULL
);
# --- !Ups
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
# --- !Downs
DROP TABLE IF EXISTS LAP;
# --- !Downs
DROP TABLE IF EXISTS BESTNLAPS;
# --- !Downs
DROP TABLE IF EXISTS BESTMINUTES;
# --- !Downs
DROP TABLE IF EXISTS CURRENTRACER;