# --- !Ups
CREATE TABLE log_tmp LIKE log;
ALTER TABLE log_tmp ADD COLUMN `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP AFTER `type`;
ALTER TABLE log_tmp ENGINE=archive;
INSERT INTO log_tmp SELECT *, 0 AS `time` FROM log;
DROP TABLE log;
RENAME TABLE log_tmp TO log;
# --- !Downs