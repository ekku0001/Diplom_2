package ru.yandex.praktikum.clients;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import static ru.yandex.praktikum.clients.base.StellarBurgersClient.BASE_URI;
import static ru.yandex.praktikum.clients.base.StellarBurgersClient.getBaseReqSpec;

public class IngredientsClient {
    private final String INGRIDIENTS_URI = BASE_URI + "/api/ingredients";

    @Step("Get available ingredients")
    public Response getIngredients(){
        return given()
                .spec(getBaseReqSpec())
                .when()
                .get(INGRIDIENTS_URI);
    }
}
