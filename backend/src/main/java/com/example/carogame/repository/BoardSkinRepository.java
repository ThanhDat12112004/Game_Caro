package com.example.carogame.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.carogame.entity.BoardSkin;

@Repository
public interface BoardSkinRepository extends JpaRepository<BoardSkin, Long> {

    Optional<BoardSkin> findByName(String name);

    List<BoardSkin> findByIsActiveTrue();

    List<BoardSkin> findByIsPremiumFalse();

    List<BoardSkin> findByIsPremiumTrue();

    @Query("SELECT bs FROM BoardSkin bs WHERE bs.isActive = true ORDER BY bs.price ASC")
    List<BoardSkin> findAllActiveOrderByPrice();

    @Query("SELECT bs FROM BoardSkin bs WHERE bs.isActive = true AND bs.price <= :maxPrice ORDER BY bs.price ASC")
    List<BoardSkin> findByPriceLessThanEqualAndIsActiveTrue(Long maxPrice);
}
