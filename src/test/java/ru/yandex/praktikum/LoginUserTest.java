package ru.yandex.praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.model.AuthorizationClient;
import ru.yandex.praktikum.model.UserCreation;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.yandex.praktikum.ClientBurger.*;
import static ru.yandex.praktikum.model.AuthorizationClient.getRandomLogin;
import static ru.yandex.praktikum.model.UserCreation.getRandomUser;

public class LoginUserTest {
    UserCreation userCreation;
    String token;
    String user;


    @Before
    public void init() {
        userCreation = getRandomUser();
        Response responseCreate = sucUserReg(userCreation);
        assertEquals(SC_OK, responseCreate.statusCode());
        user = responseCreate.body().jsonPath().getString("user");
        token = responseCreate.body().jsonPath().getString("accessToken");
        assertTrue("true", responseCreate.body().jsonPath().getBoolean("success"));

    }

    @Test
    @DisplayName("Авторизация пользователя") // имя теста
    @Description("Базовый тест - успешная авторизации юзера")
    public void successfulAuthTest() {
        AuthorizationClient authorizationClient = new AuthorizationClient(userCreation.getEmail(), userCreation.getPassword());
        Response responseAuth = authUserReg(authorizationClient);
        assertEquals(SC_OK, responseAuth.statusCode());
        MatcherAssert.assertThat(responseAuth.body().jsonPath().getString("accessToken"), CoreMatchers.not(equalTo(0)));
        MatcherAssert.assertThat(responseAuth.body().jsonPath().getString("refreshToken"), CoreMatchers.not(equalTo(0)));
        assertEquals(userCreation.getEmail(), responseAuth.body().jsonPath().getString("user.email"));
        assertEquals(userCreation.getName(), responseAuth.body().jsonPath().getString("user.name"));

    }

    @Test
    @DisplayName("Логин под существующим пользователем") // имя теста
    @Description("Нельзя авторизоваться, если использовать несуществующий логин")
    public void failedAuthNoLoginTest() {
        AuthorizationClient authorizationClient = getRandomLogin();
        Response responseAuth = authUserReg(authorizationClient);
        assertEquals(SC_UNAUTHORIZED, responseAuth.statusCode());
        assertEquals("email or password are incorrect", responseAuth.body().jsonPath().getString("message"));
    }

    @Test
    @DisplayName("Авторизация c пустыми полями") // имя теста
    @Description("Нельзя авторизоваться, если обязательные поля не заполнены")
    public void failedAuthEmptyLoginTest() {
        AuthorizationClient authorizationClient = new AuthorizationClient(null, null);
        Response responseAuth = authUserReg(authorizationClient);
        assertEquals(SC_UNAUTHORIZED, responseAuth.statusCode());
        assertEquals("email or password are incorrect", responseAuth.body().jsonPath().getString("message"));
    }

    @Test
    @DisplayName("Логин c пустым Email") // имя теста
    @Description("Нельзя авторизоваться с незаполненным Email")
    public void failedAuthEmptyEmailTest() {
        AuthorizationClient authorizationClient = new AuthorizationClient(user, userCreation.getPassword());
        Response responseAuth = authUserReg(authorizationClient);
        assertEquals(SC_UNAUTHORIZED, responseAuth.statusCode());
        assertEquals("email or password are incorrect", responseAuth.body().jsonPath().getString("message"));
    }

    @Test
    @DisplayName("Логин c пустым Password") // имя теста
    @Description("Нельзя авторизоваться с незаполненным паролем")
    public void failedAuthEmptyPasswordTest() {
        AuthorizationClient authorizationClient = new AuthorizationClient(userCreation.getEmail(), user);
        Response responseAuth = authUserReg(authorizationClient);
        assertEquals(SC_UNAUTHORIZED, responseAuth.statusCode());
        assertEquals("email or password are incorrect", responseAuth.body().jsonPath().getString("message"));
    }


    @After
    public void clear() {
        if (token != null) {

            deleteUser(token);

        }

    }
}
