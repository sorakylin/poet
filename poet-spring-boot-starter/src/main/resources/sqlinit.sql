create table tb_poet_annex
(
    name      varchar(56)  not null primary key,
    real_name varchar(56)  null,
    suffix    varchar(12)  not null,
    `key`     varchar(512) not null,
    length    bigint       not null
) comment '诗人附件表' engine INNODB;