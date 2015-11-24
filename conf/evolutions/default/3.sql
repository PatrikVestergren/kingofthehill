
CREATE TABLE BESTMINUTES (
  driver                    VARCHAR(255) NOT NULL,
  transponder               NUMERIC,
  laps                      NUMERIC,
  totalTime                 NUMERIC,
  result                    VARCHAR(25) NOT NULL,
  tsPres                    VARCHAR(25) NOT NULL,
  ts                        DATE default now() NOT NULL
);

DROP TABLE IF EXISTS BESTMINUTES;