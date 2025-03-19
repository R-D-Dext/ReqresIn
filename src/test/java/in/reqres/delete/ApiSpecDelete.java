package in.reqres.delete;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class ApiSpecDelete {
    public static RequestSpecification getRequestSpecDelete() {
        return new RequestSpecBuilder()
                .setBaseUri("https://reqres.in/api/users/")
                .setContentType("application/json")
                .build();
    }
}
