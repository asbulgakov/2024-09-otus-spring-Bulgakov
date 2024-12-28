package ru.otus.hw.domain;

import org.springframework.shell.standard.ShellOption;

public record Student(@ShellOption(help = "firstName")String firstName,
                      @ShellOption(help = "lastName")String lastName) {
    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }
}
