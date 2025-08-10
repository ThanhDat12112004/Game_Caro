package com.example.carogame.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.carogame.entity.User;
import com.example.carogame.entity.UserSkin;

@Repository
public interface UserSkinRepository extends JpaRepository<UserSkin, Long> {

    List<UserSkin> findByUser(User user);

    List<UserSkin> findByUserAndSkinType(User user, String skinType);

    Optional<UserSkin> findByUserAndSkinTypeAndSkinName(User user, String skinType, String skinName);

    @Query("SELECT us FROM UserSkin us WHERE us.user.id = :userId AND us.skinType = :skinType")
    List<UserSkin> findByUserIdAndSkinType(@Param("userId") Long userId, @Param("skinType") String skinType);

    @Query("SELECT COUNT(us) FROM UserSkin us WHERE us.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);

    boolean existsByUserAndSkinTypeAndSkinName(User user, String skinType, String skinName);
}
