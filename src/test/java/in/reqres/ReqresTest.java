package in.reqres;


import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ReqresTest {
    @Test
    void getListOfUsersCheckName() {
        RestAssured.baseURI = "https://reqres.in/";

        List<String> userNames =
            given()
                .contentType(ContentType.JSON)
            .when()
                .get("api/users?page=1")
            .then()
                    .statusCode(200)
                    .extract()
                    .jsonPath()
                    .getList("data.first_name", String.class);

        System.out.println(userNames);

        assertTrue(userNames.stream().anyMatch(name -> name.startsWith("T")));
    }
}
