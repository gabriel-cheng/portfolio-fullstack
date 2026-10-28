package com.example.ghcarvalho.portfolio.project.application.exceptions;

public class ProjectSlugAlreadyExistsException extends RuntimeException {

    public ProjectSlugAlreadyExistsException(String slug) {
        super("Já existe um projeto com o slug: " + slug);
    }
}