# --- !Ups
CREATE TABLE `screenshot` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` int(11) NOT NULL,
  `file` varchar(255) NULL,
  `position` int(11) NOT NULL DEFAULT '0',
  constraint pk_screenshot primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `screenshot` ADD CONSTRAINT `screenshot_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`);
# --- !Downs
DROP TABLE `screenshot`;