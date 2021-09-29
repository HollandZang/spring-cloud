create table `spring-cloud_gateway`.code
(
    type char(4) null,
    code char(4) null,
    name varchar(256) null
    );

create table `spring-cloud_gateway`.code_type
(
    id char(4) not null
    primary key,
    des varchar(256) null
    );

create table `spring-cloud_gateway`.log
(
    id int auto_increment
    primary key,
    operate_user varchar(16) null comment '操作人',
    operate_time timestamp null comment '操作时间',
    operate_type varchar(10) null comment '操作类型',
    operate_api varchar(128) null comment '操作api',
    ip varchar(30) null comment 'ip来源',
    param varchar(1024) null,
    result int null,
    response varchar(1024) null
    );

create table `spring-cloud_gateway`.log_login
(
    id int auto_increment
    primary key,
    operate_user varchar(16) null comment '操作人',
    operate_time timestamp null comment '操作时间',
    operate_type char null comment '操作类型 1: 登录  0: 登出',
    `from` varchar(256) null comment '指明通过什么软件、项目登录',
    ip varchar(30) null comment 'ip来源',
    result int null,
    response varchar(1024) null
    );

create table `spring-cloud_gateway`.user
(
    id int auto_increment
    primary key,
    login_name varchar(16) null,
    password char(60) null,
    create_time timestamp null,
    update_time timestamp null
    )
comment '用户信息表';

