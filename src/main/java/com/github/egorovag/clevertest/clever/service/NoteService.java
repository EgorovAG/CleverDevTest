package com.github.egorovag.clevertest.clever.service;

import com.github.egorovag.clevertest.clever.entities.Note;
import com.github.egorovag.clevertest.clever.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NoteService {

    private final NoteRepository noteRepository;

    public void save(Note note) {
        try {
            noteRepository.save(note);
        } catch (HibernateException e) {
            log.error("Fail to save note with note_guid {}", note.getOldNoteGuid());
        }
    }
}
