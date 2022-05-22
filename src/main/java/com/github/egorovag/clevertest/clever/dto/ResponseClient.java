package com.github.egorovag.clevertest.clever.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseClient {

    private String agency;
    private String guid;
    private String firstName;
    private String lastName;
    private String status;
    private LocalDate dob;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDateTime;
}
