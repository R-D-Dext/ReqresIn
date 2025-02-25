package in.reqres.put;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class ApiSpecPut {
    public static RequestSpecification getRequestSpecPut() {
        return new RequestSpecBuilder()
                .setBaseUri("https://reqres.in/api")
                .setContentType("application/json")
                .build();
    }
}
