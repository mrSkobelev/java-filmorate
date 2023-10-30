package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    @NotNull
    private int id;
    @Email
    @NotBlank
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

}
