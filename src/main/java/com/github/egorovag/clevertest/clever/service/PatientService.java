package com.github.egorovag.clevertest.clever.service;

import com.github.egorovag.clevertest.clever.entities.Patient;
import com.github.egorovag.clevertest.clever.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientService {

    public static final String COMMA = ",";

    private final PatientRepository patientRepository;

    public Map<String, Patient> getGuidActivePatients() {
        Map<String, Patient> guidPatients = new HashMap<>();
        List<Patient> activePatients = findActivePatients();
        if (nonNull(activePatients)) {
            activePatients.stream()
                    .filter(patient -> nonNull(patient.getOldClientGuid()))
                    .forEach(patient -> populateGuidPatients(guidPatients, patient));
        }
        return guidPatients;
    }

    private List<Patient> findActivePatients() {
        return patientRepository.findActivePatients();
    }

    private void populateGuidPatients(Map<String, Patient> guidPatients, Patient patient) {
        String oldGuid = patient.getOldClientGuid();
        String[] split = oldGuid.split(COMMA);
        Arrays.stream(split)
                .forEach(guid -> guidPatients.put(guid, patient));
    }
}
