--changeset asbulgakov:1
create table authors (
    id bigserial,
    full_name varchar(255),
    primary key (id)
);

--changeset asbulgakov:2
create table genres (
    id bigserial,
    name varchar(255),
    primary key (id)
);

--changeset asbulgakov:3
create table books (
    id bigserial,
    title varchar(255),
    author_id bigint references authors (id) on delete cascade,
    primary key (id)
);

--changeset asbulgakov:4
create table books_genres (
    book_id bigint references books (id) on delete cascade,
    genre_id bigint references genres (id) on delete cascade,
    primary key (book_id, genre_id)
);