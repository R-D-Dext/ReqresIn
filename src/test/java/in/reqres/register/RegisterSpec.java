package in.reqres.register;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class RegisterSpec {
    public static RequestSpecification registerSpec() {
        return new RequestSpecBuilder()
                .setBaseUri("https://reqres.in/api/register")
                .setContentType("application/json")
                .build();
    }
}
