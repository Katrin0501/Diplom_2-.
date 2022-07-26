package ru.yandex.praktikum;


import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.model.AuthorizationClient;
import ru.yandex.praktikum.model.CreateOrder;
import ru.yandex.praktikum.model.UserCreation;

import java.util.*;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static ru.yandex.praktikum.ClientBurger.*;
import static ru.yandex.praktikum.model.UserCreation.getRandomUser;

public class CreateOrderTest {

    UserCreation userCreation;
    String token;
    List<String> ingredients;
    String fluorescentBun = "61c0c5a71d1f82001bdaaa6d";
    String bioCutlet = "61c0c5a71d1f82001bdaaa71";
    String sauceSpicy = "61c0c5a71d1f82001bdaaa72";


    @Before
    public void init() {
        userCreation = getRandomUser();
        sucUserReg(userCreation);
        AuthorizationClient authorizationClient = new AuthorizationClient(userCreation.getEmail(), userCreation.getPassword());
        Response responseAuth = authUserReg(authorizationClient);
        token = responseAuth.body().jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Создание заказа авторизованного пользователя") // имя теста
    @Description("Пользователь авторизован,имеет возможность создать заказ")
    public void getUserListOrdersTest() {
        List<String> ingredients = new ArrayList<>();
        ingredients.add(fluorescentBun);
        ingredients.add(bioCutlet);
        ingredients.add(sauceSpicy);
        CreateOrder createOrder = new CreateOrder(ingredients);
        Response orders = orderCreationAuth(createOrder, token);
        assertEquals(SC_OK, orders.statusCode());
    }

    @Test
    @DisplayName("Невалидный хэш ингредиента") // имя теста
    @Description("Пользователь авторизован,при создании заказа указан невалидный хэш ингредиента")
    public void invalidHashListOrdersTest() {
        List<String> ingredients = new ArrayList<>();
        ingredients.add("ничего не нашлось подходящего");
        ingredients.add(fluorescentBun);
        ingredients.add(bioCutlet);
        CreateOrder createOrder = new CreateOrder(ingredients);
        Response orders = orderCreationAuth(createOrder, token);
        assertEquals(SC_INTERNAL_SERVER_ERROR, orders.statusCode());
    }

    @Test
    @DisplayName("Некорректный хэш ингредиента") // имя теста
    @Description("Пользователь авторизован,в заказе указан несуществующий хэш")
    public void badHashListOrdersTest() {
        List<String> ingredients = new ArrayList<>();
        ingredients.add("55c1c5a71d5f82111bdaaa7a");
        CreateOrder createOrder = new CreateOrder(ingredients);
        Response orders = orderCreationAuth(createOrder, token);
        assertEquals(SC_BAD_REQUEST, orders.statusCode());
        assertEquals("false", orders.body().jsonPath().getString("success"));
        assertEquals("One or more ids provided are incorrect", orders.body().jsonPath().getString("message"));
    }

    @Test
    @DisplayName("В заказе отсутствуют ингредиенты") // имя теста
    @Description("Пользователь авторизован,в заказе отсутствуют ингредиенты")
    public void noIngredientsListOrdersTest() {
        CreateOrder createOrder = new CreateOrder(ingredients);
        Response orders = orderCreationAuth(createOrder, token);
        assertEquals(SC_BAD_REQUEST, orders.statusCode());
        assertEquals("false", orders.body().jsonPath().getString("success"));
        assertEquals("Ingredient ids must be provided", orders.body().jsonPath().getString("message"));
    }

    @Test
    @DisplayName("Получение номера заказа неавторизованного пользователя") // имя теста
    @Description("Пользователь не авторизован,но имеет возможность сделать заказа")
    public void noAuthUserListOrdersTest() {
        List<String> ingredients = new ArrayList<>();
        ingredients.add(fluorescentBun);
        ingredients.add(bioCutlet);
        CreateOrder createOrder = new CreateOrder(ingredients);
        Response ordersNoAuth = orderCreationNoAuth(createOrder);
        assertEquals(SC_OK, ordersNoAuth.statusCode());
        assertEquals("true", ordersNoAuth.body().jsonPath().getString("success"));
        MatcherAssert.assertThat(ordersNoAuth.body().jsonPath().getString("orders.number"), CoreMatchers.not(equalTo(0)));
    }

    @After
    public void clear() {
        if (token != null) {
            deleteUser(token);
        }
    }
}
