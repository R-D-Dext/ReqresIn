package in.reqres;


import in.reqres.delete.ApiSpecDelete;
import in.reqres.get.UserResponseGet;
import in.reqres.get.UsersDatum;
import in.reqres.get.UsersRoot;
import in.reqres.patch.ApiSpecPATCH;
import in.reqres.patch.RequestUserPATCH;
import in.reqres.patch.ResponseUserPATCH;
import in.reqres.post.UserRequest;
import in.reqres.post.UserResponsePost;
import in.reqres.put.ApiSpecPut;
import in.reqres.put.UserPutRequest;
import in.reqres.put.UserPutResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class ReqresTest {
    @Test
    public void getListOfUsersCheckName() {
        RestAssured.baseURI = "https://reqres.in/";

        UserResponseGet userResponse =
                given()
                        .contentType(ContentType.JSON)
                        .when()
                        .get("api/users?page=1")
                        .then()
                        .statusCode(200)
                        .log().all()
                        .extract()
                        .as(UserResponseGet.class);

//        List<UsersDatum> usersData = userResponse.getData();
//        usersData.stream().forEach(x -> System.out.println(x));
//        assertNotNull(userResponse.getData());

        System.out.println(userResponse);

//        UsersDatum userJanet = userResponse.getData().stream()
//                .filter(x -> x.getFirst_name().contains("Janet"))
//                .findFirst().orElseThrow(() -> new NoSuchElementException("User Janet not found"));
//        assertEquals(userJanet.getFirst_name(), "Janet", "Name not Janet");
    }

    @Test
    public void createUser() {
        //Настраиваем спецификацию
        RestAssured.requestSpecification = ApiSpec.getRequestSpec();
        //Создаем объект пользователя
        UserRequest requestUser = new UserRequest("John", "Developer");

        //Отправляем запрос
        UserResponsePost responseUser =
                given()
                        .body(requestUser) // Сериализация: объект -> JSON
                        .when()
                        .post("/users")
                        .then()
                        .statusCode(201)
                        .extract()
                        .as(UserResponsePost.class); // Десериализация: JSON -> объект

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

        UserRequest userRequest = new UserRequest("John Doe", "Software Engineer");

        UserResponsePost userResponse = given()
                .spec(ApiSpec.getRequestSpec())
                .body(userRequest)
                .log().all()
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .log().all()
                .extract()
                .as(UserResponsePost.class);
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

    @ParameterizedTest
    @CsvSource({"Denis, AutoTester", "Katyafynka, Karatishka"})
    public void checkStatusAndParamUser(String name, String job) {

        UserRequest requestUser = new UserRequest(name, job);

        UserResponsePost responseUser = given()
                .spec(ApiSpec.getRequestSpec())
                .body(requestUser)
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("user-schema.json"))
                .extract()
                .as(UserResponsePost.class);
        assertAll(
                () -> assertEquals(name, responseUser.getName()),
                () -> assertEquals(job, responseUser.getJob()),
                () ->  assertNotNull(responseUser.getId()),
                () ->  assertNotNull(responseUser.getCreatedAt())
        );
        System.out.println("Имя: " + name + " Работа: " + job);
    }

    @ParameterizedTest
    @CsvSource({"2", "3"})
    public void deleteUser(int id) {
        given()
                .spec(ApiSpecDelete.getRequestSpecDelete())
                .log().all()
                .when()
                .delete("" + id)
                .then()
                .statusCode(204)
                .log().all();

        UserResponseGet usersCheckDeleteUser = given()
                .spec(ApiSpec.getRequestSpec())
                .log().all()
                .when()
                .get("/users/" + id)
                .then()
                .log().all()
                .extract()
                .as(UserResponseGet.class);
        assertNull(usersCheckDeleteUser.getData());
    }

    @ParameterizedTest()
    @CsvSource("morpheus, zion resident, 2")
    public void checkUserPatch(String name, String job, int id) {
        RequestUserPATCH requestUserPATCH = new RequestUserPATCH(name, job);

        ResponseUserPATCH responseUserPATCH = given()
                .spec(ApiSpecPATCH.ApiSpecPatch())
                .body(requestUserPATCH)
                .when()
                .patch(""+id)
                .then()
                .statusCode(200)
                .extract()
                .as(ResponseUserPATCH.class);

        Map<String, Boolean> validationMap = Map.of(
                "Имя", responseUserPATCH.getName().equals(name),
                "Работа", responseUserPATCH.getJob().equals(job),
                "Время обновления", responseUserPATCH.getUpdatedAt() != null
        );

        assertAll(
                validationMap.entrySet().stream()
                        .map(entry -> () -> assertTrue(entry.getValue(), entry.getKey()+ " Не обновился"))
        );

    }
}
