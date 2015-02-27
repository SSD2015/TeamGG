# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table config (
  k                         varchar(255) not null,
  value                     clob,
  constraint pk_config primary key (k))
;

create table groups (
  id                        integer not null,
  name                      varchar(255),
  number                    integer,
  constraint pk_groups primary key (id))
;

create table project (
  id                        integer not null,
  name                      varchar(255),
  group_id                  integer,
  description               clob,
  logo                      varchar(255),
  constraint pk_project primary key (id))
;

create table user (
  id                        integer not null,
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
  id                        integer not null,
  category_id               integer,
  user_id                   integer,
  project_id                integer,
  score                     integer,
  date                      timestamp,
  constraint pk_vote primary key (id))
;

create table vote_category (
  id                        integer not null,
  name                      varchar(255),
  type                      integer,
  constraint ck_vote_category_type check (type in (0,1)),
  constraint pk_vote_category primary key (id))
;

create sequence config_seq;

create sequence groups_seq;

create sequence project_seq;

create sequence user_seq;

create sequence vote_seq;

create sequence vote_category_seq;

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

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists config;

drop table if exists groups;

drop table if exists project;

drop table if exists user;

drop table if exists vote;

drop table if exists vote_category;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists config_seq;

drop sequence if exists groups_seq;

drop sequence if exists project_seq;

drop sequence if exists user_seq;

drop sequence if exists vote_seq;

drop sequence if exists vote_category_seq;

