create database game_room
create table room
(
    room_id            int auto_increment
        primary key,
    room_name          varchar(32) not null,
    room_capacity      int         not null,
    room_budget        double      not null,
    room_max_child_age int         not null,
    room_min_child_age int         not null
);

create table child
(
    child_id      int auto_increment
        primary key,
    child_name    varchar(64) not null,
    child_age     int         not null,
    child_sex     varchar(32) not null,
    child_room_id int         null,
    constraint child_room_id
        foreign key (child_room_id) references room (room_id)
            on delete set null
);

create table toy
(
    toy_id       int auto_increment
        primary key,
    toy_name     varchar(32) not null,
    toy_min_age  int         not null,
    toy_price    decimal     not null,
    toy_type     varchar(32) not null,
    toy_size     varchar(32) not null,
    toy_color    varchar(32) not null,
    toy_material varchar(32) not null,
    toy_room_id  int         null,
    constraint toy_room_id
        foreign key (toy_room_id) references room (room_id)
            on delete set null
);