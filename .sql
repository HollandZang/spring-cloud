-- 路由白名单
create table route_whitelist
(
    id serial not null
        constraint route_whitelist_pk
            primary key,
    url varchar(256) not null,
    enabled boolean default true
);

comment on table route_whitelist is '路由白名单';

alter table route_whitelist owner to postgres;

create unique index route_whitelist_url_uindex
    on route_whitelist (url);

insert into route_whitelist(url) values ('/user/login');

-- 用户信息表
create table "user"
(
    id serial not null
        constraint user_pk
            primary key,
    login_name varchar(16) not null,
    password varchar(256) not null,
    create_time timestamp default now() not null,
    update_time timestamp default now() not null
);

comment on table "user" is '用户信息表';

alter table "user" owner to postgres;

create unique index user_login_name_uindex
    on "user" (login_name);

insert into "user"(login_name, password) values ('root', '$2a$08$t3mLhRGLQLUSMKcwdOy3ROBZNsM3t7poqEbiARIBQBjFAJLlLezUO');

-- 登录日志
CREATE TABLE log_login (
                     id serial NOT NULL,
                     operate_user varchar(16),
                     operate_time timestamp NOT NULL,
                     operate_type char(1) NOT NULL,
                     "from" varchar(256) NOT NULL,
                     ip varchar(30) DEFAULT NULL,
                     result int DEFAULT NULL,
                     response varchar(1024) DEFAULT NULL,
                     PRIMARY KEY (id)
);
comment on table log_login is '操作日志表';
comment on column log_login.operate_user is '操作人';
comment on column log_login.operate_time is '操作时间';
comment on column log_login.operate_type is '操作类型 1: 登录  0: 登出';
comment on column log_login."from" is '指明通过什么软件、项目登录';
comment on column log_login.ip is 'ip来源';

-- 操作日志
CREATE TABLE log (
                     id serial NOT NULL,
                     operate_user varchar(16),
                     operate_time timestamp NOT NULL,
                     operate_type varchar(10) NOT NULL,
                     operate_api varchar(128) NOT NULL,
                     ip varchar(30) DEFAULT NULL,
                     param varchar(1024) DEFAULT NULL,
                     result int DEFAULT NULL,
                     response varchar(1024) DEFAULT NULL,
                     PRIMARY KEY (id)
);
comment on table log is '操作日志表';
comment on column log.operate_user is '操作人';
comment on column log.operate_time is '操作时间';
comment on column log.operate_type is '操作类型';
comment on column log.operate_api is '操作api';
comment on column log.ip is 'ip来源';

-- code type
CREATE TABLE code_type (
                      id varchar(4) DEFAULT NULL,
                      des varchar(256) DEFAULT NULL
);

create unique index code_type_id_uindex
    on code_type (id);

-- code
create table code
(
    type char(4) default NULL::character varying,
    code char(4) default NULL::character varying,
    name varchar(256) default NULL::character varying
);

alter table code owner to postgres;

create unique index code_type_code_uindex
    on code (type, code);
