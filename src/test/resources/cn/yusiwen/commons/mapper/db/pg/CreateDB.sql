drop table users if exists;

create table users
(
    id   bigserial not null,
    name varchar(20),
    created_time timestamp default now() not null,
    created_by varchar(32) default 'unknown' not null,
    updated_time timestamp default now() not null,
    updated_by varchar(32) default 'unknown' not null,
    constraint users_key primary key (id)
);

insert into users(name) values ('User1');
insert into users(name) values ('User2');
insert into users(name) values ('User3');
insert into users(name) values ('User4');
insert into users(name) values ('User5');
