package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class Genre {
    private int id;
    private String name;

    public Genre(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Genre(String name) {
        this.name = name;
    }

    public Genre(int id) {
        this.id = id;
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> values = new HashMap<>();
        values.put("id", id);
        values.put("name", name);
        return values;
    }
}
