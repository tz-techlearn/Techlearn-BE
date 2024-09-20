CREATE TABLE teacher
(
    id BINARY(16) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    color VARCHAR(7),
    avatar VARCHAR(255),
    created_by VARCHAR(255),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_by VARCHAR(255),
    modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE teacher_calendar
(
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    is_all_day BIT(1),
    guid VARCHAR(36),
    description VARCHAR(255),
    start_timezone VARCHAR(255),
    end_timezone VARCHAR(255),
    teacher_id BINARY(16),
    created_by VARCHAR(255),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_by VARCHAR(255),
    modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT FOREIGN KEY (teacher_id) REFERENCES teacher(id)
);

CREATE TABLE teacher_course
(
    teacher_id BINARY(16) NOT NULL,
    course_id  BIGINT NOT NULL,
    PRIMARY KEY (teacher_id, course_id),
    CONSTRAINT fk_teacher FOREIGN KEY (teacher_id) REFERENCES teacher(id),
    CONSTRAINT fk_course FOREIGN KEY (course_id) REFERENCES tbl_course(id)
);