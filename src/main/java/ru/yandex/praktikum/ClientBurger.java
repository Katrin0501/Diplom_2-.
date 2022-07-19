package ru.yandex.praktikum;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.yandex.praktikum.model.*;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class ClientBurger extends BaseApiClient {

    @Step("Регистрация клиента")
    public static Response sucUserReg(UserCreation userCreation) {

        return (Response) given()
                .spec(getSeqSpec())
                .body(userCreation)
                .when()
                .post(BASE_URL + "/api/auth/register")
                .then().log().all().extract();

    }

    @Step("Авторизация Юзера")
    public static Response authUserReg(AuthorizationClient authorizationClient) {

        return (Response) given()
                .spec(getSeqSpec())
                .body(authorizationClient)
                .when()
                .post(BASE_URL + "/api/auth/login")
                .then().log().all().extract();
    }

    @Step("Удаление Юзера")
    public static Boolean deleteUser(String user) {

        return given()
                .spec(getSeqSpec())
                .when()
                .delete(BASE_URL + "/api/auth/user")
                .then()
                .assertThat().log().all()
                .statusCode(401)
                .extract()
                .path("ok");
    }



    @Step("Обновление данных Юзера с авторизацией")
    public static Response updateUserWithAuth(UserCreation userCreation, String token) {

        return (Response) given()
                .spec(getSeqSpec())
                .headers(Map.of("authorization", token))
                .body(userCreation)
                .when()
                .patch(BASE_URL + "/api/auth/user").then().log().all().extract();

    }

    @Step("Обновление данных Юзера без авторизаци")
    public static Response updateUserNoAuth(UserCreation userCreation) {

        return (Response) given()
                .spec(getSeqSpec())
                .body(userCreation)
                .when()
                .patch(BASE_URL + "/api/auth/user").then().log().all().extract();

    }

    @Step("Создание заказа с авторизацией")
    public static Response orderCreationAuth(CreateOrder createOrder, String token) {

        return (Response) given()
                .spec(getSeqSpec())
                .headers(Map.of("authorization", token))
                .body(createOrder)
                .when()
                .post(BASE_URL + "/api/orders").then().log().all().extract();

    }

    @Step("Создание заказа без авторизации")
    public static Response orderCreationNoAuth(CreateOrder createOrder) {

        return (Response) given()
                .spec(getSeqSpec())
                .body(createOrder)
                .when()
                .post(BASE_URL + "/api/orders").then().log().all().extract();

    }

    @Step("Получить список заказов c авторизацие")
    public static Response ordersListAuth(String token) {
        return (Response) given()
                .spec(getSeqSpec())
                .headers(Map.of("authorization", token))
                .when()
                .get(BASE_URL + "/api/orders").then().log().all().extract();

    }

    @Step("Получить список заказов без авторизации")
    public static Response ordersListNoAuth() {
        return (Response) given()
                .spec(getSeqSpec())
                .when()
                .get(BASE_URL + "/api/orders").then().log().all().extract();

    }


}
