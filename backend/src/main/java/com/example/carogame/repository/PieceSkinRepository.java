package com.example.carogame.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.carogame.entity.PieceSkin;

@Repository
public interface PieceSkinRepository extends JpaRepository<PieceSkin, Long> {

    Optional<PieceSkin> findByName(String name);

    List<PieceSkin> findByIsActiveTrue();

    List<PieceSkin> findByIsPremiumFalse();

    List<PieceSkin> findByIsPremiumTrue();

    @Query("SELECT ps FROM PieceSkin ps WHERE ps.isActive = true ORDER BY ps.price ASC")
    List<PieceSkin> findAllActiveOrderByPrice();

    @Query("SELECT ps FROM PieceSkin ps WHERE ps.isActive = true AND ps.price <= :maxPrice ORDER BY ps.price ASC")
    List<PieceSkin> findByPriceLessThanEqualAndIsActiveTrue(Long maxPrice);
}
