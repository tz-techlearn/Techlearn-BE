package com.techzen.techlearn.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.techzen.techlearn.enums.StudentCourseStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Where;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Where(clause = "is_deleted = false")
@Table(name = "tbl_student_course")
public class StudentCourseEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "id_course")
    Long idCourse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    UserEntity userEntity;

    @Column(name = "is_deleted")
    Boolean isDeleted;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    StudentCourseStatus  status;

}
