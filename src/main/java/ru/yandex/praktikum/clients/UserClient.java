package ru.yandex.praktikum.clients;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.yandex.praktikum.models.User;

import static io.restassured.RestAssured.given;
import static ru.yandex.praktikum.clients.base.StellarBurgersClient.BASE_URI;
import static ru.yandex.praktikum.clients.base.StellarBurgersClient.getBaseReqSpec;

public class UserClient {
    private final String AUTH_URI = BASE_URI + "/api/auth";
    private final String USER_URI = BASE_URI + "/api/auth/user";
    private String accessToken;
    private String refreshToken;

    public String getAccessToken() {
        return accessToken;
    }

    @Step("Register new user {user}")
    public Response register(User user){
        Response registerResp = given()
                .spec(getBaseReqSpec())
                .body(user)
                .when()
                .post(AUTH_URI + "/register");

        //save tokens
        if (registerResp.then().extract().path("success")) {
            accessToken = registerResp.then().extract().path("accessToken").toString().substring(7);
            refreshToken = registerResp.then().extract().path("refreshToken").toString();
        }

        return registerResp;
    }

    @Step("Login")
    public Response login(User user){
        Response loginResp = given()
                .spec(getBaseReqSpec())
                .body(user)
                .when()
                .post(AUTH_URI + "/login");

        //save tokens
        if (loginResp.then().extract().path("success")) {
            accessToken = loginResp.then().extract().path("accessToken").toString().substring(7);
            refreshToken = loginResp.then().extract().path("refreshToken").toString();
        }

        return loginResp;
    }

    @Step("Logout")
    public Response logout(){
        String jsonToken = String.format("{\"token\": \"%s\"}", refreshToken);
        return given()
                .spec(getBaseReqSpec())
                .body(jsonToken)
                .when()
                .post(AUTH_URI + "/logout");
    }

    @Step("Update user data")
    public Response update(User user){
        return given()
                .spec(getBaseReqSpec())
                .and()
                .auth().oauth2(accessToken)
                .body(user)
                .when()
                .patch(USER_URI);
    }
    @Step("Update unauthorized user data")
    public Response updateWithoutAuthorization(User user){
        return given()
                .spec(getBaseReqSpec())
                .and()
                .body(user)
                .when()
                .patch(USER_URI);
    }

    @Step("Get user data")
    public Response getUserData(){
        return given()
                .spec(getBaseReqSpec())
                .and()
                .auth().oauth2(accessToken)
                .when()
                .get(USER_URI);
    }

    @Step("Delete user")
    public Response delete(){
        return given()
                .spec(getBaseReqSpec())
                .and()
                .auth().oauth2(accessToken)
                .when()
                .delete(USER_URI);
    }
}