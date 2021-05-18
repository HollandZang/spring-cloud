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
