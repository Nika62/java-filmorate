package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @EqualsAndHashCode.Exclude
    private long id;

    private Set<Long> userFriends = new HashSet<>();
    @Email(message = "Некорректный формат адреса электронной почты")
    @NotBlank(message = "Электронная почта не может быть пустой")
    private String email;
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^\\S*$", message = "Логин не должен содержать пробелы")
    private String login;
    private String name;

    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public boolean addFriend(Long id) {
        return userFriends.add(id);
    }

    public boolean deleteFriend(Long id) {
        return userFriends.remove(id);
    }
}
