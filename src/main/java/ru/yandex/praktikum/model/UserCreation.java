package ru.yandex.praktikum.model;

import org.apache.commons.lang3.RandomStringUtils;

public class UserCreation {
    private String email;
    private String password;
    private String name;

    @Override
    public String toString() {
        return String.format("UserCreation{email='%s', password='%s', name='%s'}", email, password, name);
    }

    public static UserCreation getRandomUser() {
        String email = "mail" + RandomStringUtils.randomNumeric(10) + "@gmail.com";
        String password = RandomStringUtils.randomAlphabetic(10);
        String name = RandomStringUtils.randomAlphabetic(10);
        return new UserCreation(email, password, name);

    }

    public UserCreation(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public UserCreation() {
    }


    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
