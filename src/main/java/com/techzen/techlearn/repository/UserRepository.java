package com.techzen.techlearn.repository;

import com.techzen.techlearn.dto.response.UserResponseDTO;
import com.techzen.techlearn.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    @Query("select u from UserEntity u where u.id =:id")
    Optional<UserEntity> findUserById(UUID id);

    Optional<UserEntity> findByEmail(String email);


    @Query("SELECT u from UserEntity u " +
            " join Token t on t.user.id = u.id" +
            " where t.token = :accessToken")
    Optional<UserEntity> findUserEntityByAccessToken(@Param("accessToken") String accessToken);

    @Query("SELECT ue.points from UserEntity ue WHERE ue.id =:idUser")
    Integer getAllPointsById (UUID idUser);

}
