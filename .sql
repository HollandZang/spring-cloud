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

