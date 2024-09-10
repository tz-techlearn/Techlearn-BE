CREATE TABLE teacher (
     id BINARY(16) PRIMARY KEY,
     name VARCHAR(50) NOT NULL,
     avatar VARCHAR(250) NOT NULL,
     color VARCHAR(250) NOT NULL
);

CREATE TABLE technical_teacher (
     id INTEGER AUTO_INCREMENT PRIMARY KEY,
     name VARCHAR(50) NOT NULL,
     id_teacher BINARY(16) NOT NULL,
     FOREIGN KEY (id_teacher) REFERENCES teacher(id)
);

CREATE TABLE teacher_calendar (
     id  INTEGER AUTO_INCREMENT PRIMARY KEY,
     id_teacher BINARY(16) NOT NULL,
     date_appointment DATE NOT NULL,
     time_start TIME NOT NULL,
     time_end TIME NOT NULL,
     status VARCHAR(50) NOT NULL,
     id_user BINARY(16),
     note VARCHAR(255),
     is_all_day BIT(1)       NULL,
     FOREIGN KEY (id_teacher) REFERENCES teacher(id),
     FOREIGN KEY (id_user) REFERENCES tbl_user(id)
);