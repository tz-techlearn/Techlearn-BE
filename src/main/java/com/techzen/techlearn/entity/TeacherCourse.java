package com.techzen.techlearn.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "teacher_course")
public class TeacherCourse {

    @Id
    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    Teacher teacher;

    @Id
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    CourseEntity course;

}
