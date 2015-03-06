# --- !Ups
ALTER TABLE `user` ADD UNIQUE `username` (`username`);
# --- !Downs
ALTER TABLE user DROP INDEX username;