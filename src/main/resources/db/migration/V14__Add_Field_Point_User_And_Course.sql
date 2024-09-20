ALTER TABLE tbl_user
    ADD COLUMN points INT DEFAULT 0;

ALTER TABLE tbl_course
    ADD COLUMN points INT CHECK ( points > 0 );

DELIMITER //

CREATE TRIGGER after_insert_user_course
    AFTER INSERT ON tbl_user_course
    FOR EACH ROW
BEGIN
    UPDATE tbl_user u
    SET u.points = u.points + (
        SELECT c.points
        FROM tbl_course c
        WHERE c.id = NEW.id_course
    )
    WHERE u.id = NEW.id_user;
END;

//

DELIMITER ;

UPDATE tbl_course
SET points = 30
WHERE id = 1;

UPDATE tbl_course
SET points = 35
WHERE id = 2;


INSERT INTO tbl_user (id, created_by, created_date, modified_date, modified_by, full_name, age, email, password, is_deleted)
VALUES
    (UNHEX('6a1b4ebafbc6412b82192a1f84eba568'), 'admin', NOW(), NOW(), 'system', 'Trần Võ', 25, 'vo@gmail.com', '$2a$12$k5fluGyp4vSRccRd2Q1Btemdc/nvvds5z1qsyiljve0iETHFccXLq', 0),
    (UNHEX('6a1b4ebafbc6412b82192a1f84eba569'), 'admin', NOW(), NOW(), 'system', 'Nguyễn Kim Tuyết', 20, 'nktuyet1@gmail.com', '$2a$12$k5fluGyp4vSRccRd2Q1Btemdc/nvvds5z1qsyiljve0iETHFccXLq', 0);

INSERT INTO tbl_user_course (id_course, id_user)
VALUES
    (1, UNHEX('6a1b4ebafbc6412b82192a1f84eba568')),
    (2, UNHEX('6a1b4ebafbc6412b82192a1f84eba568'));

