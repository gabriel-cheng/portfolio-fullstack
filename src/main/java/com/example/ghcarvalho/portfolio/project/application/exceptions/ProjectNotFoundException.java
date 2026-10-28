package com.example.ghcarvalho.portfolio.project.application.exceptions;

import java.util.UUID;

public class ProjectNotFoundException extends RuntimeException {

    public ProjectNotFoundException(UUID id) {
        super("Projeto não encontrado: " + id);
    }

    public ProjectNotFoundException(String message) {
        super(message);
    }
}