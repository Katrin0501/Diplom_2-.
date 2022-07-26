package ru.yandex.praktikum.model;

public class User {
    public String email;
    public String name;

    @Override
    public String toString() {
        return String.format("User{'email='%s', name='%s'}", email, name);
    }

    public User(String name, String email) {
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
