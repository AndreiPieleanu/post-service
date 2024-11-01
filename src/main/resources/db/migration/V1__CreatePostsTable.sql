CREATE TABLE kwex_posts
(
    id        int         NOT NULL AUTO_INCREMENT,
    text varchar(500) NOT NULL,
    createdAt DATE NOT NULL,
    userId int,
    isBlocked bool,
    PRIMARY KEY (id)
);
create table kwex_relationships
(
    id        int         NOT NULL AUTO_INCREMENT,
    senderId int not null,
    receiverId int not null,
    status varchar(20) not null,
    PRIMARY KEY (id)
);
create table kwex_users
(
  id int not null auto_increment,
  email varchar(100) NOT NULL,
  primary key (id)
);
