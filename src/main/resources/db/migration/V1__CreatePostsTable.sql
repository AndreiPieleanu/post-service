CREATE DATABASE IF NOT EXISTS `post-service`;
USE `post-service`;
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
CREATE INDEX idx_userId ON kwex_posts (userId);
CREATE INDEX idx_text on kwex_posts(text);
CREATE INDEX idx_createdAt ON kwex_posts (createdAt);