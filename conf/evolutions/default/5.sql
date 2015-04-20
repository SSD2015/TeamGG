# --- !Ups
CREATE TABLE `log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` integer NOT NULL,
  `ref` integer NOT NULL DEFAULT -1,
  `ref2` integer NOT NULL DEFAULT -1,
  `ref3` integer NOT NULL DEFAULT -1,
  `metadata` longtext NOT NULL DEFAULT "",
  constraint pk_log primary key (id)
) ENGINE=ARCHIVE DEFAULT CHARSET=utf8;
# --- !Downs
DROP TABLE `log`;