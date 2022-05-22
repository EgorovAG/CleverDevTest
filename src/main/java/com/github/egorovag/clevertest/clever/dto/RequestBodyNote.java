package com.github.egorovag.clevertest.clever.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestBodyNote {
    private String agency;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String clientGuid;
}
