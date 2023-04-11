package ru.yandex.praktikum.clients.base;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class StellarBurgersClient {
    public static final String BASE_URI = "https://stellarburgers.nomoreparties.site";

    public static RequestSpecification getBaseReqSpec(){
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setBaseUri(BASE_URI)
                .build();
    }

}
