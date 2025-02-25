package in.reqres;


import in.reqres.put.ApiSpecPut;
import in.reqres.put.UserPutRequest;
import in.reqres.put.UserPutResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class ReqresTest {
    @Test
    public void getListOfUsersCheckName() {
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

    @Test
    public void createUser() {
        //Настраиваем спецификацию
        RestAssured.requestSpecification = ApiSpec.getRequestSpec();
        //Создаем объект пользователя
        User requestUser = new User("John", "Developer");

        //Отправляем запрос
        User responseUser =
                given()
                        .body(requestUser) // Сериализация: объект -> JSON
                        .when()
                        .post("/users")
                        .then()
                        .statusCode(201)
                        .extract()
                        .as(User.class); // Десериализация: JSON -> объект

        assertNotNull(responseUser.getId(), "ID пользователя не должен быть NULL");
        assertNotNull(responseUser.getCreatedAt(), "Дата создания не должна быть NULL");
        assertEquals(requestUser.getName(), responseUser.getName());
        assertEquals(requestUser.getJob(), responseUser.getJob());
    }

    @Test
    public void checkListUsers() {
        RestAssured.requestSpecification = ApiSpec.getRequestSpec();

        UsersRoot usersRoot =
                given()
                        .contentType(ContentType.JSON)
                        .when()
                        .get("/users?page=2")
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(UsersRoot.class);

        assertEquals(usersRoot.getPer_page(), usersRoot.getData().size(), "Количество записей не равно");

        usersRoot.getData().stream().forEach(user -> {
            assertNotNull(user.getId(), "Id равен Null");
            assertNotNull(user.getEmail(), "Email равен Null");
            assertNotNull(user.getFirst_name(), "First Name равен Null");
            assertNotNull(user.getLast_name(), "Last Name равен Null");
        });

        List<String> filterNames = usersRoot.getData().stream()
                .filter(user -> user.getEmail().endsWith("reqres.in"))
                .map(UsersDatum::getFirst_name)
                .collect(Collectors.toList());

        System.out.println(filterNames);

        assertFalse(filterNames.isEmpty(), "Список имен пустой");
    }

    @Test
    public void createAndValidationUser() {
        //RestAssured.requestSpecification = ApiSpec.getRequestSpec();

        User userRequest = new User("John Doe", "Software Engineer");

        User userResponse = given()
                .spec(ApiSpec.getRequestSpec())
                .body(userRequest)
                .log().all()
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .log().all()
                .extract()
                .as(User.class);
        assertEquals(userRequest.getName(), userResponse.getName(), "Имена не равны");
        assertEquals(userRequest.getJob(), userResponse.getJob(), "Работы не равны");
        assertTrue(userResponse.getCreatedAt().contains("T"));
        assertFalse(userResponse.getCreatedAt().isEmpty());
        assertFalse(userResponse.getId().isEmpty());
    }

    @DisplayName("Проверка обновления Имени и работы, и валидация")
    @Test
    public void putUserInformation() {
        UserPutRequest userPutRequest = new UserPutRequest("Denis", "Engineer");

        UserPutResponse userPutResponse = given()
                .spec(ApiSpecPut.getRequestSpecPut())
                .body(userPutRequest)
                .when()
                .put("/api/users/2")
                .then()
                .log().all()
                .extract()
                .as(UserPutResponse.class);

        assertAll(
                () -> assertEquals(userPutRequest.getName(), userPutResponse.getName(), "Имена не равны"),
                () -> assertEquals(userPutRequest.getJob(), userPutResponse.getJob(), "Должности не равны"),
                () -> {
                    boolean isNameValid = userPutResponse.getName().chars()
                            .filter(Character::isLetterOrDigit)
                            .count() >= 3;
                    assertTrue(isNameValid, "Имя содержит меньше 3 символов");
                },
                () -> {
                    boolean isJobValid = userPutResponse.getJob().chars()
                            .filter(Character::isLetterOrDigit)
                            .count() >= 5;
                    assertTrue(isJobValid, "Работа содержит меньше 5 символов");
                }
        );
    }
}
