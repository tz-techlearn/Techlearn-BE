package com.techzen.techlearn.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Where(clause = "is_deleted = false")
@Table(name = "tbl_course")
public class CourseEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "image")
    private String image;

    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    private String description;

    @Column(name = "time")
    private String time;

    @Column(name = "points")
    private Integer points;

    @Column(name = "public_point")
    private Integer publicPoint;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @OneToMany(mappedBy = "course")
    @JsonIgnore
    private List<ChapterEntity> listChapter;

    @JsonIgnore
    @ManyToMany()
    @JoinTable(name = "tbl_user_course", joinColumns = @JoinColumn(name = "id_course"),
            inverseJoinColumns = @JoinColumn(name = "id_user"))
    List<UserEntity> userEntities = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "courses")
    List<Teacher> teachers = new ArrayList<>();
}