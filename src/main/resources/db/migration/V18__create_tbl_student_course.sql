CREATE TABLE tbl_student_course
(
    id            BIGINT AUTO_INCREMENT NOT NULL,
    created_by    VARCHAR(255) NULL,
    created_date  datetime NULL,
    modified_date datetime NULL,
    modified_by   VARCHAR(255) NULL,
    id_course     BIGINT NULL,
    id_user       BINARY(16) NOT NULL,
    is_deleted    BIT(1) NULL,
    CONSTRAINT pk_tbl_student_course PRIMARY KEY (id)
);

ALTER TABLE tbl_student_course
    ADD CONSTRAINT FK_TBL_STUDENT_COURSE_ON_ID_USER FOREIGN KEY (id_user) REFERENCES tbl_user (id);