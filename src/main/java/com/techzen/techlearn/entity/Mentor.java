package com.techzen.techlearn.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mentor")
public class Mentor extends BaseEntity {

    @Id
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(length = 7)
    private String color;

    @Column(columnDefinition = "TEXT")
    private String avatar;

    @OneToMany(mappedBy = "mentor")
    private List<TeacherCalendar> mentorCalendars;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "mentor_chapter",
            joinColumns = @JoinColumn(name = "mentor_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "chapter_id", referencedColumnName = "id")
    )
    private List<ChapterEntity> chapters;
}
