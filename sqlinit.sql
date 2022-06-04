-- mysql
create table poet_annex
(
    `name`        varchar(56)  not null primary key,
    `real_name`   varchar(56)  null,
    `suffix`      varchar(12)  not null,
    `key`         varchar(512) not null,
    `length`      bigint       not null,
    `create_time` datetime     not null
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='poet 附件支持';


-- postgres
create table poet_annex
(
    name            varchar(56)  not null
        constraint tb_poet_annex_pkey
            primary key,
    real_name       varchar(128),
    suffix          varchar(12)  not null,
    key             varchar(512) not null,
    length          bigint       not null,
    create_time     timestamp    not null,
    main_category   varchar(56),
    instance_id     bigint,
    instance_module varchar(56),
    expire_time     timestamp
);

comment on column poet_annex.main_category is '附件所属类别';
comment on column poet_annex.instance_id is '附件所属的实例ID';
comment on column poet_annex.instance_module is '附件所属的是这个实例的哪个模块';
comment on column poet_annex.expire_time is '过期时间，null为不过期';
comment on table tb_user is 'poet 附件支持';

