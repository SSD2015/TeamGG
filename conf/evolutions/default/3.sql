# --- !Ups
ALTER TABLE `vote` DROP FOREIGN KEY `fk_vote_category_3`;
ALTER TABLE `vote` ADD CONSTRAINT `fk_vote_category_3` FOREIGN KEY (`category_id`) REFERENCES `vote_category`(`id`) ON DELETE CASCADE ON UPDATE RESTRICT;

ALTER TABLE `vote` DROP FOREIGN KEY `fk_vote_project_5`;
ALTER TABLE `vote` ADD CONSTRAINT `fk_vote_project_5` FOREIGN KEY (`project_id`) REFERENCES `project`(`id`) ON DELETE CASCADE ON UPDATE RESTRICT;

ALTER TABLE `vote` DROP FOREIGN KEY `fk_vote_user_4`;
ALTER TABLE `vote` ADD CONSTRAINT `fk_vote_user_4` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE ON UPDATE RESTRICT;

ALTER TABLE `project` DROP FOREIGN KEY `fk_project_group_1`;
ALTER TABLE `project` ADD CONSTRAINT `fk_project_group_1` FOREIGN KEY (`group_id`) REFERENCES `groups`(`id`) ON DELETE SET NULL ON UPDATE RESTRICT;

ALTER TABLE `user` DROP FOREIGN KEY `fk_user_group_2`;
ALTER TABLE `user` ADD CONSTRAINT `fk_user_group_2` FOREIGN KEY (`group_id`) REFERENCES `groups`(`id`) ON DELETE SET NULL ON UPDATE RESTRICT;

# --- !Downsp