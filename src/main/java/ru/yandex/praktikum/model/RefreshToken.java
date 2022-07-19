package ru.yandex.praktikum.model;

public class RefreshToken {
    private  String token;

    @Override
    public String toString() {
        return String.format("RefreshToken{'token=='%s' }",token);
    }

    public String getToken() {
        return token;
    }

    public RefreshToken(String token) {
        this.token = token;
    }
}
