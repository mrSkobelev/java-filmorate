package ru.yandex.practicum.filmorate;

import java.time.LocalDate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

@SpringBootApplication
public class FilmorateApplication {

	public static void main(String[] args) {
		SpringApplication.run(FilmorateApplication.class, args);
	}

}
