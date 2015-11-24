
CREATE TABLE BESTNLAPS (
  driver                    VARCHAR(255) NOT NULL,
  transponder               NUMERIC,
  totalTime                 NUMERIC,
  totalTimePres             VARCHAR(25) NOT NULL,
  tsPres                    VARCHAR(25) NOT NULL,
  ts                        DATE default now() NOT NULL
);

DROP TABLE IF EXISTS BESTNLAPS;