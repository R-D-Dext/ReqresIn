package in.reqres.register;

import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserServiceRegister {
    public static Response registerUserRequest(RegisterUserRequest user) {
        return given()
                .spec(RegisterSpec.registerSpec())
                .body(user)
                .log().all()
                .when()
                .post()
                .then()
                .log().all()
                .extract()
                .response();
    }
}
