create table booking
(
    `id`            bigint AUTO_INCREMENT PRIMARY KEY,
    `uuid`          varchar(36)  NOT NULL UNIQUE,
    `email`         varchar(255) NOT NULL,
    `first_name`    varchar(255) NOT NULL,
    `last_name`     varchar(255) NOT NULL,
    `start_date`    DATETIME     NOT NULL,
    `end_date`      DATETIME     NOT NULL,
    `created_on`    DATETIME     NOT NULL,
    `last_modified` DATETIME     NOT NULL,
    `is_deleted`    TINYINT      NOT NULL DEFAULT 0,
    INDEX (`uuid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_unicode_ci;

