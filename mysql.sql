CREATE TABLE `spring-cloud_gateway`.code
(
    `id`           int auto_increment primary key,
    `code_type_id` char(4)      NOT NULL,
    `des`          varchar(256) NULL DEFAULT NULL,
    `val`          varchar(256) NOT NULL,
    `val1`          varchar(1024) NOT NULL,
    INDEX          `IDX_TYPE_ID`(`code_type_id`) USING BTREE
);

create table `spring-cloud_gateway`.code_type
(
    id  char(4) not null primary key,
    des varchar(256) null
);

create table `spring-cloud_gateway`.user
(
    id          int auto_increment,
    login_name  varchar(16) null,
    password    char(60) null,
    create_time timestamp null,
    update_time timestamp null,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX       `IDX_LOGIN_NAME`(`login_name`) USING BTREE
) COMMENT = '用户信息表';

create table `spring-cloud_gateway`.user_role
(
    id          int auto_increment,
    login_name  varchar(16)  not null,
    roles       varchar(256) not null,
    update_time timestamp null,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX       `IDX_LOGIN_NAME`(`login_name`) USING BTREE
);

insert into `spring-cloud_gateway`.user(id, login_name, password, create_time, update_time) values (-1,'hollandX','$2a$08$D718MtMskaWMFopyWi/ZO.HRVS4hZyk1R3Xu1.Mzs14bocOYuzzS.',now(),now());
insert into `spring-cloud_gateway`.code_type(id) values ('role');
insert into `spring-cloud_gateway`.code(id, val, code_type_id) values (-1,'admin','role'),(-2,'guest','role');
insert into `spring-cloud_gateway`.user_role(id, login_name, roles, update_time) values (-1,'hollandX','admin',now());