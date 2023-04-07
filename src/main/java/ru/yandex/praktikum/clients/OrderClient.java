package ru.yandex.praktikum.clients;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.yandex.praktikum.models.Order;
import static io.restassured.RestAssured.given;
import static ru.yandex.praktikum.clients.base.StellarBurgersClient.BASE_URI;
import static ru.yandex.praktikum.clients.base.StellarBurgersClient.getBaseReqSpec;

public class OrderClient {
    private final String ORDER_URI = BASE_URI + "/api/orders";


    @Step("Create order {order}")
    public Response create(Order order, String accessToken){
        Response createOrdersResp;
        if (accessToken != null) {
            createOrdersResp = given()
                    .spec(getBaseReqSpec())
                    .and()
                    .auth().oauth2(accessToken)
                    .body(order)
                    .when()
                    .post(ORDER_URI);
        } else{
            createOrdersResp = given()
                    .spec(getBaseReqSpec())
                    .body(order)
                    .when()
                    .post(ORDER_URI);
        }
        return createOrdersResp;
    }

    public Response createWithoutIngredients(String accessToken) {
        return given()
                .spec(getBaseReqSpec())
                .and()
                .auth().oauth2(accessToken)
                .when()
                .post(ORDER_URI);
    }

    @Step("Get user orders")
    public Response getUserOrders(String accessToken){
        Response getUserOrdersResp;
        if (accessToken != null) {
            getUserOrdersResp = given()
                .spec(getBaseReqSpec())
                .and()
                .auth().oauth2(accessToken)
                .when()
                .get(ORDER_URI);
        } else{
            getUserOrdersResp = given()
                    .spec(getBaseReqSpec())
                    .when()
                    .get(ORDER_URI);
        }
        return getUserOrdersResp;
    }

    @Step("Get all orders")
    public Response getAllOrders(){
        return given()
                .spec(getBaseReqSpec())
                .when()
                .get(ORDER_URI + "/all");
    }
}
