package ru.yandex.praktikum.model;

import java.util.List;

public class CreateOrder {
    public List<String> ingredients;

    @Override
    public String toString() {
        return String.format("CreateOrder{ingredients='%s' }", ingredients);
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public CreateOrder(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}
