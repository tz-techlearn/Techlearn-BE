package com.techzen.techlearn.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "is_deleted = false")
@Table(name = "tbl_user")
public class UserEntity extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "age")
    private Integer age;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "points")
    private Integer points;

    @Column(name = "public_point")
    private Integer publicPoint;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "is_mentor")
    private boolean isMentor;

    @Column(name = "is_teacher")
    private boolean isTeacher;

    @ManyToMany(mappedBy = "userEntities")
    @JsonIgnore
    private List<CourseEntity> courseEntities;

    @ManyToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinTable(name = "tbl_user_roles",
            joinColumns=@JoinColumn(name="user", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name="role", referencedColumnName = "id")
    )
    private Set<Role> roles= new HashSet<>();

    @OneToMany(mappedBy = "user")
    private List<Token> tokens;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(
                        (role) -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
