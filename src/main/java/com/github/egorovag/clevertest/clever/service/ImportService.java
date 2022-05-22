package com.github.egorovag.clevertest.clever.service;

import com.github.egorovag.clevertest.clever.dto.RequestBodyNote;
import com.github.egorovag.clevertest.clever.dto.ResponseClient;
import com.github.egorovag.clevertest.clever.dto.ResponseNote;
import com.github.egorovag.clevertest.clever.entities.Patient;
import com.github.egorovag.clevertest.clever.entities.Note;
import com.github.egorovag.clevertest.clever.entities.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.*;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ImportService {

    private final PatientService patientService;
    private final NoteService noteService;
    private final UserService userService;
    private final WebClientService webClientService;

    @Scheduled(cron = "${interval-in-cron}")
    public void importDataFromOldToNewSystem() {
        Map<String, Patient> oldGuidActivePatientMap = patientService.getGuidActivePatients();
        if (oldGuidActivePatientMap.isEmpty()) {
            log.info("There are no active patient in the new system");
            return;
        }

        List<ResponseNote> oldSystemNotes = getNotesFromOldSystem(oldGuidActivePatientMap);
        if (oldSystemNotes.isEmpty()) {
            log.info("There are no notes in the old system");
            return;
        }

        List<Note> guidNewNotes = oldGuidActivePatientMap.values().stream()
                .map(Patient::getNotes)
                .flatMap(Collection::stream)
                .collect(toList());

        saveOrUpdateNote(oldGuidActivePatientMap, oldSystemNotes, guidNewNotes);
        log.info("Import data from old to new system done.");
    }

    private void saveOrUpdateNote(Map<String, Patient> oldGuidActivePatientMap, List<ResponseNote> oldSystemNotes,
                                  List<Note> guidNewNotes) {
        oldSystemNotes.forEach(oldNote -> {
            Patient patient = oldGuidActivePatientMap.get(oldNote.getClientGuid());
            Set<String> newNoteGuids = guidNewNotes.stream()
                    .map(Note::getOldNoteGuid)
                    .collect(toSet());
            if (newNoteGuids.contains(oldNote.getGuid())) {
                Note newNote = guidNewNotes.stream()
                        .filter(note -> note.getOldNoteGuid().equals(oldNote.getGuid()))
                        .findFirst().orElse(null);
                checkAndUpdateNote(oldNote, newNote, patient);
            } else {
                log.info("Note with note_guid: {} for patient_guid: {} saved", oldNote.getGuid(), patient.getOldClientGuid());
                save(oldNote, patient);
            }
        });
    }

    private void checkAndUpdateNote(ResponseNote oldNote, Note newNote, Patient patient) {
        if (nonNull(oldNote) && hasChanges(oldNote, newNote) && checkModifiedDate(oldNote, newNote)) {
            log.info("Note with note_guid: {} for patient_guid: {} updated ", oldNote.getGuid(), patient.getOldClientGuid());
            save(oldNote, patient);
        }
    }

    private boolean checkModifiedDate(ResponseNote oldNote, Note newNote) {
        return oldNote.getModifiedDateTime().isAfter(newNote.getLastModifiedDateTime());

    }

    private void save(ResponseNote oldNote, Patient patient) {
        User user = userService.findByLoginOrCreate(oldNote.getLoggedUser());
        Note note = Note.builder()
                .createdDateTime(oldNote.getCreatedDateTime())
                .createdUser(user)
                .lastModifiedDateTime(oldNote.getModifiedDateTime())
                .lastModifiedUser(user)
                .note(oldNote.getComments())
                .patient(patient)
                .oldNoteGuid(oldNote.getGuid())
                .build();
        noteService.save(note);
    }

    private boolean hasChanges(ResponseNote oldNote, Note newNote) {
        return !oldNote.getComments().equals(newNote.getNote())
                || !oldNote.getModifiedDateTime().equals(newNote.getLastModifiedDateTime())
                || !oldNote.getCreatedDateTime().equals(newNote.getCreatedDateTime())
                || !oldNote.getLoggedUser().equals(newNote.getCreatedUser().getLogin());
    }

    private List<ResponseNote> getNotesFromOldSystem(Map<String, Patient> guidActivePatients) {
        List<RequestBodyNote> requestBodyNote = createRequestBodyNote(guidActivePatients);
        return webClientService.getNotes(requestBodyNote);
    }

    private List<RequestBodyNote> createRequestBodyNote(Map<String, Patient> guidActivePatients) {
        List<ResponseClient> clients = webClientService.getClients();
        return clients.stream()
                .filter(client -> guidActivePatients.containsKey(client.getGuid()))
                .map(this::buildRequestBody)
                .collect(toList());
    }

    private RequestBodyNote buildRequestBody(ResponseClient client) {
        return RequestBodyNote.builder()
                .agency(client.getAgency())
                .dateFrom(client.getCreatedDateTime().toLocalDate())
                .dateTo(LocalDate.now())
                .clientGuid(client.getGuid())
                .build();
    }
}
