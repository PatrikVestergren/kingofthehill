# --- First database schema

# --- !Ups

CREATE TABLE LAP (
  name                      VARCHAR(255) NOT NULL,
  lapTime                   NUMERIC,
  ts                        DATE
);

# --- !Downs

DROP TABLE IF EXISTS LAP;