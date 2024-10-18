CREATE TABLE `instagram-tracker`.profile
(
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    nickname   VARCHAR(40)  NOT NULL,
    link       VARCHAR(255) NOT NULL,
    timestamp  BIGINT       NOT NULL,
    createdAt  DATE         NOT NULL,
    modifiedAt DATE         NOT NULL,

    CONSTRAINT profile_id_pk PRIMARY KEY (id),
    UNIQUE (id)
);