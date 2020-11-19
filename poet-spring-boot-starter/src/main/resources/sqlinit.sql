-- mysql
create table poet_annex
(
    `name`        varchar(56)  not null primary key,
    `real_name`   varchar(56)  null,
    `suffix`      varchar(12)  not null,
    `key`         varchar(512) not null,
    `length`      bigint       not null,
    `create_time` datetime     not null
) comment 'poet 附件支持' engine INNODB;


-- postgres
create table poet_annex
(
    name        varchar(56) not null primary key,
    real_name   varchar(56) null,
    suffix      varchar(12) not null,
    key varchar (512) not null,
    length      bigint      not null,
    create_time timestamp   not null
);

comment on table tb_user is 'poet 附件支持';