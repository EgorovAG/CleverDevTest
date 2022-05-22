package com.github.egorovag.clevertest.clever.repository;

import com.github.egorovag.clevertest.clever.entities.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {
}
