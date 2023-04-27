package apiauto;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;

import static io.restassured.RestAssured.given;

public class APITest {
    String userId;
    @BeforeClass
    public void setup() {
        TestUtilities.setBaseURI( "https://reqres.in/api/");
    }

    @Test(priority = 1)
    public void createNewUserTest() {
        File createNewUserSchema = TestUtilities.getJsonSchemaFile("createNewUserSchema.json");

        String name = "Muhammad Fajar B";
        String job = "QA Engineer";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("job", job);

        String response = given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(jsonObject.toString())
                .when()
                .post("users")
                .then().log().all()
                .assertThat().statusCode(201)
                .assertThat().body("name", Matchers.equalTo(name))
                .assertThat().body("job", Matchers.equalTo(job))
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(createNewUserSchema))
                .extract().asString();

        JSONObject jsonResponse = new JSONObject(response);
        userId = jsonResponse.getString("id");
    }

    @Test(priority = 2)
    public void getListUsersTest() {
        File usersSchema = TestUtilities.getJsonSchemaFile("getListUsersSchema.json");

        given()
                .header("Accept", "application/json")
                .when()
                .get("users?page=1")
                .then()
                .log().all()
                .assertThat().statusCode(200)
                .assertThat().body("page", Matchers.equalTo(1))
                .assertThat().body("data.id", Matchers.hasSize(6))
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(usersSchema));
    }

    @Test(priority = 3, dependsOnMethods = "createNewUserTest")
    public void getSingleUserTest() {
        File singleUserSchema = TestUtilities.getJsonSchemaFile("getSingleUserSchema.json");
        given()
                .header("Accept", "application/json")
                .when()
                .get("users/" + userId)
                .then()
                .log().all()
                .assertThat().statusCode(200)
                .assertThat().body("$", Matchers.hasKey("data"))
                .assertThat().body("data", Matchers.hasKey("id"))
                .assertThat().body("data", Matchers.hasKey("email"))
                .assertThat().body("data", Matchers.hasKey("first_name"))
                .assertThat().body("data", Matchers.hasKey("last_name"))
                .assertThat().body("data", Matchers.hasKey("avatar"))
                .assertThat().body("$", Matchers.hasKey("support"))
                .assertThat().body("support", Matchers.hasKey("url"))
                .assertThat().body("support", Matchers.hasKey("text"))
                .assertThat().body("data.id", Matchers.equalTo(2))
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(singleUserSchema));
    }

    @Test(priority = 4, dependsOnMethods = "createNewUserTest")
    public void testPutUser() {
        File putUserSchema = TestUtilities.getJsonSchemaFile("putUserSchema.json");

        String name = "Muhammad Fajar B";
        String newJob = "CEO";

        // pada PUT seluruh data diupdate
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("job", newJob);

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(jsonObject.toString())
                .when()
                .put("users/" + userId)
                .then().log().all()
                .assertThat().statusCode(200)
                .assertThat().body("name", Matchers.equalTo(name))
                .assertThat().body("job", Matchers.equalTo(newJob))
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(putUserSchema));
    }

    @Test(priority = 5, dependsOnMethods = "createNewUserTest")
    public void testPatchUser() {
        File patchUserSchema = TestUtilities.getJsonSchemaFile("patchUserSchema.json");

        String newJob = "CTO";

        // pada PATCH hanya sebagian data diupdate
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("job", newJob);

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(jsonObject.toString())
                .when()
                .put("users/" + userId)
                .then().log().all()
                .assertThat().statusCode(200)
                .assertThat().body("job", Matchers.equalTo(newJob))
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(patchUserSchema));
    }

    @Test(priority = 6, dependsOnMethods = "createNewUserTest")
    public void testDeleteUser() {
        given()
                .header("Accept", "application/json")
                .when()
                .delete("users/" + userId)
                .then().log().all()
                .assertThat().statusCode(204);
    }

    // method for login and register successful
    String email;
    String password;
    private static void testValid(String path, String email, String password, File jsonSchema) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", email);
        jsonObject.put("password", password);

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(jsonObject.toString())
                .when()
                .post(path)
                .then()
                .log().all()
                .assertThat().statusCode(200)
                .assertThat().body("$", Matchers.hasKey("token"))
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(jsonSchema));
    }

    // method for login and register unsuccessful
    private static void testInvalid(String path, JSONObject jsonObject, String errorMessage, File jsonSchema) {
        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(jsonObject.toString())
                .when()
                .post(path)
                .then()
                .log().all()
                .assertThat().statusCode(400)
                .assertThat().body("$", Matchers.hasKey("error"))
                .assertThat().body("error", Matchers.equalTo(errorMessage))
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(jsonSchema));
    }

    @Test(priority = 7)
    public void testRegisterSuccessful() {
        email = "eve.holt@reqres.in";
        password = "cityslicka";
        File loginValidSchema = TestUtilities.getJsonSchemaFile("registerLoginValidSchema.json");

        testValid("register", email, password, loginValidSchema);
    }

    @Test(priority = 8, dependsOnMethods = "testRegisterSuccessful")
    public void testLoginSuccessful() {
        File loginValidSchema = TestUtilities.getJsonSchemaFile("registerLoginValidSchema.json");

        testValid("login", email, password, loginValidSchema);
    }

    @Test(priority = 9)
    public void testRegisterUnsuccessful() {
        File loginInvalidSchema = TestUtilities.getJsonSchemaFile("registerLoginInvalidSchema.json");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", email);

        String errorMessage = "Missing password";

        testInvalid("register", jsonObject, errorMessage, loginInvalidSchema);
    }

    @Test(priority = 10)
    public void testLoginInvalidPassword() {
        File registerLoginInvalidSchema = TestUtilities.getJsonSchemaFile("registerLoginInvalidSchema.json");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", "eve.holt@reqres.in");

        String errorMessage = "Missing password";

        testInvalid("login", jsonObject, errorMessage, registerLoginInvalidSchema);
    }

    @Test(priority = 11)
    public void testLoginInvalidEmail() {
        File registerLoginInvalidSchema = TestUtilities.getJsonSchemaFile("registerLoginInvalidSchema.json");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", "wrong.email@reqres.in");
        jsonObject.put("password", "password");

        String errorMessage = "user not found";

        testInvalid("login", jsonObject, errorMessage, registerLoginInvalidSchema);
    }

    /* pada test boundaries ini yang diharapkan adalah seharusnya response api
       memberikan status code 400 jika batasan yang diset tidak sesuai */
    @Test(priority = 12)
    public void testBoundaryMinPage() {
        String page = "-1";
        given()
                .header("Accept", "application/json")
                .when()
                .get("users?page=" + page)
                .then()
                .log().all()
                .assertThat().statusCode(400);
    }

    @Test(priority = 13)
    public void testBoundaryMaxPage() {
        String page = "13";
        given()
                .header("Accept", "application/json")
                .when()
                .get("users?page=" + page)
                .then()
                .log().all()
                .assertThat().statusCode(400);
    }

}
