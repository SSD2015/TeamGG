# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table config (
  k                         varchar(255) not null,
  value                     longtext,
  constraint pk_config primary key (k))
;

create table groups (
  id                        integer auto_increment not null,
  name                      varchar(255),
  number                    integer,
  constraint pk_groups primary key (id))
;

create table project (
  id                        integer auto_increment not null,
  name                      varchar(255),
  group_id                  integer,
  description               longtext,
  logo                      varchar(255),
  constraint pk_project primary key (id))
;

create table user (
  id                        integer auto_increment not null,
  username                  varchar(255),
  password                  varchar(255),
  type                      integer,
  name                      varchar(255),
  organization              varchar(255),
  group_id                  integer,
  constraint ck_user_type check (type in (0,1,2)),
  constraint pk_user primary key (id))
;

create table vote (
  id                        integer auto_increment not null,
  category_id               integer,
  user_id                   integer,
  project_id                integer,
  score                     integer,
  date                      datetime,
  constraint pk_vote primary key (id))
;

create table vote_category (
  id                        integer auto_increment not null,
  name                      varchar(255),
  type                      integer,
  constraint ck_vote_category_type check (type in (0,1)),
  constraint pk_vote_category primary key (id))
;

alter table project add constraint fk_project_group_1 foreign key (group_id) references groups (id) on delete restrict on update restrict;
create index ix_project_group_1 on project (group_id);
alter table user add constraint fk_user_group_2 foreign key (group_id) references groups (id) on delete restrict on update restrict;
create index ix_user_group_2 on user (group_id);
alter table vote add constraint fk_vote_category_3 foreign key (category_id) references vote_category (id) on delete restrict on update restrict;
create index ix_vote_category_3 on vote (category_id);
alter table vote add constraint fk_vote_user_4 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_vote_user_4 on vote (user_id);
alter table vote add constraint fk_vote_project_5 foreign key (project_id) references project (id) on delete restrict on update restrict;
create index ix_vote_project_5 on vote (project_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table config;

drop table groups;

drop table project;

drop table user;

drop table vote;

drop table vote_category;

SET FOREIGN_KEY_CHECKS=1;

