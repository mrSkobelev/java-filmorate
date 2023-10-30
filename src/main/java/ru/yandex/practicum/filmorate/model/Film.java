package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Film {
    @NotNull
    private int id;
    @NotNull
    @NotBlank
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;

}