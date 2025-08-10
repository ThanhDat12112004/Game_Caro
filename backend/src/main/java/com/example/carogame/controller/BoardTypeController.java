package com.example.carogame.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.carogame.entity.BoardType;
import com.example.carogame.repository.BoardTypeRepository;

@RestController
@RequestMapping("/api/board-types")
@CrossOrigin(origins = "*")
public class BoardTypeController {

    @Autowired
    private BoardTypeRepository boardTypeRepository;

    @GetMapping
    public ResponseEntity<List<BoardType>> getAllBoardTypes() {
        List<BoardType> boardTypes = boardTypeRepository.findAllActiveOrderBySize();
        return ResponseEntity.ok(boardTypes);
    }

    @GetMapping("/default")
    public ResponseEntity<BoardType> getDefaultBoardType() {
        Optional<BoardType> defaultType = boardTypeRepository.findByIsDefaultTrue();
        if (defaultType.isPresent()) {
            return ResponseEntity.ok(defaultType.get());
        }

        // Fallback to first active board type if no default is set
        List<BoardType> activeTypes = boardTypeRepository.findByIsActiveTrue();
        if (!activeTypes.isEmpty()) {
            return ResponseEntity.ok(activeTypes.get(0));
        }

        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{name}")
    public ResponseEntity<BoardType> getBoardTypeByName(@PathVariable String name) {
        Optional<BoardType> boardType = boardTypeRepository.findByName(name);
        if (boardType.isPresent()) {
            return ResponseEntity.ok(boardType.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/size/{size}")
    public ResponseEntity<List<BoardType>> getBoardTypesBySize(@PathVariable Integer size) {
        List<BoardType> boardTypes = boardTypeRepository.findByBoardSizeAndIsActiveTrue(size);
        return ResponseEntity.ok(boardTypes);
    }
}
