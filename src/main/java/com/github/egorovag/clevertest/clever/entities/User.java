package com.github.egorovag.clevertest.clever.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "company_user")
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String login;
}
