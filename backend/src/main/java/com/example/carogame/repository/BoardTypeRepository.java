package com.example.carogame.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.carogame.entity.BoardType;

@Repository
public interface BoardTypeRepository extends JpaRepository<BoardType, Long> {

    Optional<BoardType> findByName(String name);

    List<BoardType> findByIsActiveTrue();

    Optional<BoardType> findByIsDefaultTrue();

    @Query("SELECT bt FROM BoardType bt WHERE bt.isActive = true ORDER BY bt.boardSize ASC")
    List<BoardType> findAllActiveOrderBySize();

    List<BoardType> findByBoardSizeAndIsActiveTrue(Integer boardSize);
}
