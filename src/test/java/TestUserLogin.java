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
import ru.yandex.praktikum.clients.UserClient;
import ru.yandex.praktikum.models.User;

import static org.hamcrest.CoreMatchers.is;

public class TestUserLogin {
    private UserClient userClient;
    private User user;

    @BeforeClass
    public static void globalSetUp(){
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Before
    public void setUp(){
        user = new User("validEmail@yandex.ru","validPassword","validName");
        userClient = new UserClient();
       userClient.register(user);
    }

    @Test
    @DisplayName("User login with valid data")
    @Description("Login with existed user and valid data")
    public void loginExistedUser() {
        //login
        Response loginUserResp = userClient.login(user);
        loginUserResp.then()
                .assertThat().statusCode(200)
                .and()
                .assertThat().body("success", is(true));
    }

    @Test
    @DisplayName("User login with invalid data")
    @Description("Try to login with incorrect login")
    public void loginUserWithIncorrectLogin(){
        userClient.login(new User("incorrectEmail@yandex.ru","validPassword",""))
                .then()
                .assertThat().statusCode(401)
                .and()
                .assertThat()
                .body("message", is("email or password are incorrect"));
    }

    @Test
    @DisplayName("User login with invalid data")
    @Description("Try to login with incorrect password")
    public void loginUserWithIncorrectPassword(){
        userClient.login(new User("validEmail@yandex.ru","incorrectPassword",""))
                .then()
                .assertThat().statusCode(401)
                .and()
                .assertThat()
                .body("message", is("email or password are incorrect"));
    }

    @After
    public void cleanUp(){
        //delete registered user
        userClient.delete();
    }
}
