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

public class TestChangeUserData {
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
    @DisplayName("Authorized user data is updated")
    @Description("Update data and check it valid to login")
    public void updateAuthorizedUser() {
        //login
        userClient.login(user);

        User newUserData = new User("newEmail@ya.ru", "validPassword", "NewName");
        userClient.update(newUserData)
                .then()
                .assertThat().statusCode(200)
                .and()
                .assertThat()
                .body("success", is(true));

        //logout and then login with new data
        userClient.logout();
        userClient.login(newUserData).then()
                .assertThat().statusCode(200);

    }

    @Test
    @DisplayName("Unauthorized user data is not updated")
    @Description("Try to update data of unauthorized user")
    public void updateUnauthorizedUser(){
        User newUserData = new User("newEmail@ya.ru", "validPassword", "NewName");
        userClient.updateWithoutAuthorization(newUserData)
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
