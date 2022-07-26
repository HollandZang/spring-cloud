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
ALTER TABLE `spring-cloud_gateway`.`user`
    ADD INDEX `IDX_LOGIN_NAME`(`login_name`);
