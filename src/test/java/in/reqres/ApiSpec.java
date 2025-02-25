package in.reqres;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class ApiSpec {
    public static RequestSpecification getRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri("https://reqres.in/api")
                .setContentType("application/json")
                .build();
    }
}