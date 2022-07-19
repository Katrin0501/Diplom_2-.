package ru.yandex.praktikum;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.model.AuthorizationClient;
import ru.yandex.praktikum.model.UserCreation;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ru.yandex.praktikum.ClientBurger.*;
import static ru.yandex.praktikum.model.UserCreation.getRandomUser;

public class UpdateUserTest {

    UserCreation userCreation;
    String user;
    String token;

    @Before
    public void init() {
        userCreation = getRandomUser();
        sucUserReg(userCreation);
        AuthorizationClient authorizationClient = new AuthorizationClient(userCreation.getEmail(),userCreation.getPassword());
        Response responseAuth = authUserReg(authorizationClient);
        user = responseAuth.body().jsonPath().getString("user");
        token = responseAuth.body().jsonPath().getString("accessToken");


    }

    @Test
    @DisplayName("Изменение всех данных пользователя") // имя теста
    @Description("Успешное изменение всех полей зарегистрированного пользователя")
    public void updateAllFieldsUserTest(){
        UserCreation userUpdNew  =getRandomUser();
        Response resUpUser = updateUserWithAuth(userUpdNew,token);
        user = resUpUser.body().jsonPath().getString("user");
        assertEquals(SC_OK, resUpUser.statusCode());
        assertTrue("true",resUpUser.body().jsonPath().getBoolean("success"));
        assertEquals(userUpdNew.getEmail(),resUpUser.body().jsonPath().getString("user.email"));
        assertEquals(userUpdNew.getName(),resUpUser.body().jsonPath().getString("user.name"));

    }

    @Test
    @DisplayName("Изменение Email авторизованного пользователя") // имя теста
    @Description("Автоизованный пользователь имеет возможность измененить Email ")
    public void updateEmailUserTest(){
        UserCreation userUpdNew  = new UserCreation(getRandomUser().getEmail(), userCreation.getPassword(), userCreation.getName());
        Response resUpUser = updateUserWithAuth(userUpdNew,token);
        user = resUpUser.body().jsonPath().getString("user");
        assertEquals(SC_OK, resUpUser.statusCode());
        assertTrue("true",resUpUser.body().jsonPath().getBoolean("success"));
        assertEquals(userUpdNew.getEmail(),resUpUser.body().jsonPath().getString("user.email"));

    }

    @Test
    @DisplayName("Изменение Имени авторизованного пользователя") // имя теста
    @Description("Автоизованный пользователь имеет возможность измененить Имя ")
    public void updateNamelUserTest(){
        UserCreation userUpdNew  = new UserCreation(userCreation.getEmail(), userCreation.getPassword(), getRandomUser().getName());
        Response resUpUser = updateUserWithAuth(userUpdNew,token);
        user = resUpUser.body().jsonPath().getString("user");
        assertEquals(SC_OK, resUpUser.statusCode());
        assertTrue("true",resUpUser.body().jsonPath().getBoolean("success"));
        assertEquals(userUpdNew.getEmail(),resUpUser.body().jsonPath().getString("user.email"));

    }
    @Test
    @DisplayName("Email пользователя уже используется") // имя теста
    @Description("Пользователь с таким адресом электронной почты уже существует")
    public void updateUserMailAlreadyExistsTest(){
        UserCreation userCreationNew  = getRandomUser();
        sucUserReg(userCreationNew);
        AuthorizationClient authorizationClient = new AuthorizationClient(userCreationNew.getEmail(),userCreationNew.getPassword());
        Response responseAuthNew = authUserReg(authorizationClient);
        user = responseAuthNew.body().jsonPath().getString("user");
        token = responseAuthNew.body().jsonPath().getString("accessToken");
        UserCreation userUpdNew  = new UserCreation(userCreation.getEmail(), "usPas", "usN");
        Response resUpUser = updateUserWithAuth(userUpdNew,token);
        assertEquals(SC_FORBIDDEN, resUpUser.statusCode());
        assertEquals("false",resUpUser.body().jsonPath().getString("success"));
        assertEquals("User with such email already exists",resUpUser.body().jsonPath().getString("message"));

    }

    @Test
    @DisplayName("Изменение данных без авторизации") // имя теста
    @Description("Чтобы изменить данные, нужно быть авторизованным")
    public void updateUserNoAuthTest(){
        UserCreation userUpdNew  = getRandomUser();
        Response resUpUser = updateUserNoAuth(userUpdNew);
        assertEquals(SC_UNAUTHORIZED, resUpUser.statusCode());
        assertEquals("false",resUpUser.body().jsonPath().getString("success"));
        assertEquals("You should be authorised",resUpUser.body().jsonPath().getString("message"));

    }



    @After
    public void clear() {
        if (user !=null) {

            deleteUser(user);
        }
    }

}
