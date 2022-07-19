package ru.yandex.praktikum.model;

import org.apache.commons.lang3.RandomStringUtils;

public class AuthorizationClient {

    private String email;
    private String password;

    @Override
    public String toString() {
        return String.format("AuthorizationClient{email='%s', password='%s'}",email,password);
    }

    public String getPassword() {
        return password;
    }

    public AuthorizationClient(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static AuthorizationClient getRandomLogin() {
        String email = "mail" + RandomStringUtils.randomNumeric(10) + "@gmail.com";
        String password = RandomStringUtils.randomAlphabetic(10);
        return new AuthorizationClient(email, password);

    }
}
