ALTER TABLE teacher_calendar
    ADD COLUMN mentor_id BINARY(16);

ALTER TABLE teacher_calendar
    ADD CONSTRAINT FOREIGN KEY (mentor_id) REFERENCES mentor(id);