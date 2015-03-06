# --- !Ups
ALTER TABLE `exceedvote`.`user` ADD UNIQUE `username` (`username`);
# --- !Downs
ALTER TABLE user DROP INDEX username;