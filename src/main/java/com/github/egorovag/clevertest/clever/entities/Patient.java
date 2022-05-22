package com.github.egorovag.clevertest.clever.entities;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "patient_profile")
public class Patient extends BaseEntity {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "old_client_guid")
    private String oldClientGuid;

    @Column(name = "status_id", nullable = false)
    private Long statusId;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Note> notes;
}
