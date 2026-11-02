package com.example.ghcarvalho.portfolio.project.application.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProjectNotFoundException extends RuntimeException {

    public ProjectNotFoundException(UUID id) {
        super("Projeto não encontrado: " + id);
    }

    public ProjectNotFoundException(String message) {
        super(message);
    }
}
