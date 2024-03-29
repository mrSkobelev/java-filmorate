package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    @NotNull
    private long id;
    @Email
    @NotBlank
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private final Set<Long> friendsId = new HashSet<>();

}
