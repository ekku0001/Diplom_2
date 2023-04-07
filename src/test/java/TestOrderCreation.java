import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.yandex.praktikum.clients.IngredientsClient;
import ru.yandex.praktikum.clients.OrderClient;
import ru.yandex.praktikum.clients.UserClient;
import ru.yandex.praktikum.models.Order;
import ru.yandex.praktikum.models.User;

import static org.hamcrest.CoreMatchers.is;

public class TestOrderCreation {
    private UserClient userClient;
    private Response ingredientsResp;

    @BeforeClass
    public static void globalSetUp(){
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Before
    public void setUp(){
        User user = new User("validEmail@yandex.ru","validPassword","validName");
        userClient = new UserClient();
        userClient.register(user);
        userClient.login(user);

        IngredientsClient ingredientsClient = new IngredientsClient();
        ingredientsResp = ingredientsClient.getIngredients();
    }

    @Test
    @DisplayName("Create order by authorised user")
    @Description("Create order by authorised user with valid ingredients check if order is created")
    public void createOrderAuthUser() {
        String[] ingredients = new String[2];
        for (int i = 0; i < ingredients.length; i++){
            ingredients[i] = ingredientsResp.then().extract().path(String.format("data[%d]._id", i*2)).toString();
        }

        Order order = new Order(ingredients);
        OrderClient orderClient = new OrderClient();
        orderClient.create(order, userClient.getAccessToken())
                .then()
                .assertThat().statusCode(200);

        orderClient.getUserOrders(userClient.getAccessToken())
                .then().assertThat().statusCode(200)
                .and().assertThat().body("orders.ingredients[0][0]", is(ingredients[0]))
                .and().assertThat().body("orders.ingredients[0][1]", is(ingredients[1]));

    }

    @Test
    @DisplayName("Create order without authorisation")
    @Description("Create order without authorisation and valid ingredients")
    public void createOrderUnauthUser() {
        String[] ingredients = new String[2];
        for (int i = 0; i < ingredients.length; i++){
            ingredients[i] = ingredientsResp.then().extract().path(String.format("data[%d]._id", i)).toString();
        }

        Order order = new Order(ingredients);
        OrderClient orderClient = new OrderClient();
        orderClient.create(order, null).then()
                .assertThat().statusCode(200)
                .and()
                .assertThat()
                .body("success", is(true));
    }

    @Test
    @DisplayName("Create order without ingridients")
    @Description("Try to create order without ingridients and check error")
    public void createOrderWithoutIngridients(){
        OrderClient orderClient = new OrderClient();
        orderClient.createWithoutIngredients(userClient.getAccessToken())
                .then()
                .assertThat().statusCode(400)
                .and()
                .assertThat()
                .body("message", is("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Create order with invalid data")
    @Description("Try to create order with invalid data and check error")
    public void createOrderWithInvalidData() {
        String[] ingredients = {"invalidIngridient"};

        Order order = new Order(ingredients);
        OrderClient orderClient = new OrderClient();
        orderClient.create(order, userClient.getAccessToken())
                .then()
                .assertThat().statusCode(500);
    }

    @After
    public void cleanUp(){
        //delete registered user
        userClient.delete();
    }
}
