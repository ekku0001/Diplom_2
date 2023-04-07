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

import static org.hamcrest.CoreMatchers.*;

public class TestGetUserOrders {
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
    @DisplayName("Get authorised user orders")
    @Description("Create order and get it from user orders")
    public void getUserOrders() {
        String[] ingredients = new String[2];
        for (int i = 0; i < ingredients.length; i++){
            ingredients[i] = ingredientsResp.then().extract().path(String.format("data[%d]._id", i*2)).toString();
        }

        Order order = new Order(ingredients);
        OrderClient orderClient = new OrderClient();
        orderClient.create(order, userClient.getAccessToken());

        orderClient.getUserOrders(userClient.getAccessToken())
               .then().assertThat().statusCode(200)
               .and().assertThat().body("orders.ingredients[0][0]", is(ingredients[0]))
               .and().assertThat().body("orders.ingredients[0][1]", is(ingredients[1]));

    }

    @Test
    @DisplayName("Get unauthorised user orders")
    @Description("Create order and get it from unauthorized user ")
    public void getUnauthUserOrders(){
        String[] ingredients = {"61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f"};
        Order order = new Order(ingredients);
        OrderClient orderClient = new OrderClient();
        orderClient.create(order, userClient.getAccessToken());

        orderClient.getUserOrders(null)
                .then()
                .assertThat().statusCode(401)
                .and()
                .assertThat()
                .body("message", is("You should be authorised"));
    }

    @After
    public void cleanUp(){
        //delete registered user
        userClient.delete();
    }
}
