package com.github.egorovag.clevertest.clever.repository;

import com.github.egorovag.clevertest.clever.entities.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    @Query("select p from Patient p where p.statusId in (200, 210, 230)")
    List<Patient> findActivePatients();
}
