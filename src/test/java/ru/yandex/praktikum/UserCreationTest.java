package ru.yandex.praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.model.UserCreation;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.yandex.praktikum.ClientBurger.*;
import static ru.yandex.praktikum.model.UserCreation.getRandomUser;

public class UserCreationTest {

    UserCreation userCreation;
    String token;

    @Before
    public void init() {
        userCreation = getRandomUser();

    }


    @Test
    @DisplayName("Регистрация пользователя") // имя теста
    @Description("Базовый тест - успешного создания юзера и проверки его авторизации")
    public void successfulCreationTest() {
        Response responseCreate = sucUserReg(userCreation);
        token = responseCreate.body().jsonPath().getString("accessToken");
        //Проверка
        assertEquals(SC_OK, responseCreate.statusCode());
        assertTrue("true",responseCreate.body().jsonPath().getBoolean("success"));
        assertEquals(userCreation.getEmail(),responseCreate.body().jsonPath().getString("user.email"));
        assertEquals(userCreation.getName(),responseCreate.body().jsonPath().getString("user.name"));
        MatcherAssert.assertThat(responseCreate.body().jsonPath().getString( "accessToken"), CoreMatchers.not(equalTo(0)));
        MatcherAssert.assertThat(responseCreate.body().jsonPath().getString( "refreshToken"), CoreMatchers.not(equalTo(0)));

    }

    @Test
    @DisplayName("Регистрация пользователя, который уже зарегистрирован") // имя теста
    @Description("Повторное создание ранее зарегистрированного пользователя")
    public void regAnExistingUserTest() {
        Response responseCreate =sucUserReg(userCreation);
        token = responseCreate.body().jsonPath().getString("accessToken");
        new UserCreation(userCreation.getEmail(),userCreation.getPassword(),userCreation.getName());
        Response responseCreateTwo = sucUserReg(userCreation);
        assertEquals(SC_FORBIDDEN, responseCreateTwo.statusCode());
        assertEquals("User already exists",responseCreateTwo.body().jsonPath().getString("message"));

    }

    @Test
    @DisplayName("Регистрация пользователя, без заполнения одного из обязательных полей") // имя теста
    @Description("Проверка, что пользователь не может зарегистрироваться без заполнения одного из обязательных полей")
    public void regAnExistingUserNoEmailTest() {
        UserCreation userCreationNull = new UserCreation();
        Response responseCreateTwo = sucUserReg(userCreationNull);
        assertEquals(SC_FORBIDDEN, responseCreateTwo.statusCode());
        assertEquals("Email, password and name are required fields", responseCreateTwo.body().jsonPath().getString("message"));


    }

    @After
    public void clear() {
        if (token !=null) {

            deleteUser(token);
        }
    }

}
