import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.yandex.praktikum.clients.UserClient;
import ru.yandex.praktikum.models.User;

import static org.hamcrest.CoreMatchers.is;

public class TestCreateUser {
    private UserClient userClient;

    @BeforeClass
    public static void globalSetUp(){
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Before
    public void setUp(){
        userClient = new UserClient();
    }

    @Test
    @DisplayName("User registration with valid data")
    @Description("register user with valid data and check if it can login")
    public void userIsCreatedWithValidData() {
        User user = new User("validEmail@yandex.ru","validPassword","validName");

        userClient.register(user)
                .then()
                .assertThat().statusCode(200)
                .and()
                .assertThat()
                .body("success", is(true));

        //login to check user creation
        userClient.login(user)
                .then()
                .assertThat().statusCode(200);
    }

    @Test
    @DisplayName("User registration with invalid login")
    @Description("Try to register user with empty login")
    public void registerUserWithEmptyLogin(){
        User user = new User("","passwordWithEmptyLogin","name");

        userClient.register(user)
                .then()
                .assertThat().statusCode(403)
                .and()
                .assertThat()
                .body("message", is("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Double User registration")
    @Description("Try to register two users with the same data")
    public void registerUsersWithSameData(){
        User user = new User("doubleLogin@ya.ru","doublePassword","doubleName");

        //check if user is exist, login and delete
        if (userClient.login(user).then().extract().path("success")) userClient.delete();

        userClient.register(user);
        userClient.register(user)
                .then()
                .assertThat().statusCode(403)
                .and()
                .assertThat()
                .body("message", is("User already exists"));
    }

    @After
    public void cleanUp(){
        //delete registered user
        userClient.delete();
    }

}
