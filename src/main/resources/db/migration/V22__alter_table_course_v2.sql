ALTER TABLE tbl_course DROP COLUMN private_point;

ALTER TABLE tbl_course
    ADD COLUMN public_point INT NULL;

ALTER TABLE tbl_user
    ADD COLUMN public_point INT NULL;