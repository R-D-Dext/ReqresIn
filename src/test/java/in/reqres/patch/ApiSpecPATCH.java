package in.reqres.patch;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class ApiSpecPATCH {
    public static RequestSpecification ApiSpecPatch() {
        return new RequestSpecBuilder()
                .setBaseUri("https://reqres.in/api/users/")
                .setContentType("application/json")
                .build();
    }
}
