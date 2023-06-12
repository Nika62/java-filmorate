package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.lang.NonNull;
import ru.yandex.practicum.filmorate.customValidator.DateAfter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    @NonNull
    @EqualsAndHashCode.Exclude
    private HashMap<String, Object> mpa = new HashMap<>();
    @EqualsAndHashCode.Exclude
    private List<HashMap<String, Object>> genres = new ArrayList<>();

    public Film(String name, String description, LocalDate date, int duration, HashMap<String, Object> mpa) {
        this.name = name;
        this.description = description;
        this.releaseDate = date;
        this.duration = duration;
        this.mpa = mpa;
    }

    public Film(String name, String description, LocalDate date, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = date;
        this.duration = duration;
        this.mpa = mpa;
    }

    public Film(String name, HashSet<Long> list, String description, LocalDate date, int duration, HashMap<String, Object> mpa) {
        this.name = name;
        this.description = description;
        this.releaseDate = date;
        this.duration = duration;
        this.likes = list;
    }

    public Film(long id, String name, String description, LocalDate date, int duration, HashMap<String, Object> mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = date;
        this.duration = duration;
        this.mpa = mpa;
    }

    public Film(long id, String name, String description, LocalDate date, int duration, HashMap<String, Object> mpa, List<HashMap<String, Object>> genres) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = date;
        this.duration = duration;
        this.mpa = mpa;
        this.genres = genres;
    }


    public Film(long id, String name, String description, LocalDate date, int duration) {
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
        if (Objects.nonNull(likes)) {
            return likes.size();
        }
        return 0;
    }

    @Override
    public int compareTo(Film f) {
        if (f.getNumberOfLikes() <= 0) {
            return -1;
        }
        return (f.getNumberOfLikes()) - getNumberOfLikes();
    }

    @Override
    public String toString() {
        return "Film{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", releaseDate=" + releaseDate +
                ", duration=" + duration +
                ", mpa=" + mpa +
                ", genresList=" + genres +
                '}';
    }
}
