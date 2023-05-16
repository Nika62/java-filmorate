package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.customValidator.DateAfter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Film implements Comparable<Film> {
    @EqualsAndHashCode.Exclude
    private long id;
    @EqualsAndHashCode.Exclude
    private Set<Long> likes = new HashSet<>();
    @NotBlank(message = "Имя не может быть пустым")
    private String name;
    @Size(max = 200, message = "Длина поля description не должна превышать 200 символов")
    private String description;
    @DateAfter(message = "Дата релиза не может быть раньше 28 декабря 1895 года")
    private LocalDate releaseDate;
    @Min(value = 1, message = "Продолжительность фильма должна быть больше 0")
    private int duration;

    public Film(String name, String description, LocalDate date, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = date;
        this.duration = duration;
    }

    public Film(String name, HashSet<Long> list, String description, LocalDate date, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = date;
        this.duration = duration;
        this.likes = list;
    }

    public Film(int id, String name, String description, LocalDate date, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = date;
        this.duration = duration;
    }


    public boolean addLike(long id) {
        return likes.add(id);
    }

    public boolean deleteLike(long id) {
        return likes.remove(id);
    }

    public int getNumberOfLikes() {
        return likes.size();
    }

    @Override
    public int compareTo(Film f) {
        if (f.getNumberOfLikes() <= 0) {
            return -1;
        }
        return (f.getNumberOfLikes()) - getNumberOfLikes();
    }
}
