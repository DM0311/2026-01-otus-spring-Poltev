insert into authors(full_name)
values ('Author_1'),
       ('Author_2'),
       ('Author_3');

insert into genres(name)
values ('Genre_1'),
       ('Genre_2'),
       ('Genre_3'),
       ('Genre_4'),
       ('Genre_5'),
       ('Genre_6');

insert into books(title, author_id)
values ('BookTitle_1', 1),
       ('BookTitle_2', 2),
       ('BookTitle_3', 3);

insert into books_genres(book_id, genre_id)
values (1, 1),
       (1, 2),
       (2, 3),
       (2, 4),
       (3, 5),
       (3, 6);

insert into comments(comment_text, book_id)
values ('Comment_1', 1),
       ('Comment_2', 2),
       ('Comment_3', 3),
       ('Comment_4', 1);

insert into users (username, password)
values
    ('user',  '$2a$05$KiyGJPRgnyc10qZWFGYUfeYfyEgqG3PdG2/7VFpid1IoSr7BEQ59.'),
    ( 'admin', '$2a$05$aoE/mwa503zWArXB1uwwU.DIPYhbqsquT44coqy.j8rL10L7yPmUW');

insert into user_roles (user_id, role_name)
values
    (1, 'USER'),
    (2, 'USER'),
    (2, 'ADMIN');