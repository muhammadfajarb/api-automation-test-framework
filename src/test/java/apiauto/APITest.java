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
    @BeforeClass
    public void setup() {
        TestUtilities.setBaseURI( "https://reqres.in/api/");
    }

    @Test
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

    @Test
    public void getSingleUserTest() {
        int userId = 2;
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

    @Test
    public void createNewUserTest() {
        File createNewUserSchema = TestUtilities.getJsonSchemaFile("createNewUserSchema.json");

        String name = "Muhammad Fajar B";
        String job = "QA Engineer";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("job", job);

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(jsonObject.toString())
                .when()
                .post("users")
                .then().log().all()
                .assertThat().statusCode(201)
                .assertThat().body("name", Matchers.equalTo(name))
                .assertThat().body("job", Matchers.equalTo(job))
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(createNewUserSchema));
    }

    @Test
    public void testPutUser() {
        File putUserSchema = TestUtilities.getJsonSchemaFile("putUserSchema.json");
        int userId = 2;

        JsonPath jsonObj = given().when().get("users/" + userId).getBody().jsonPath();
        String fname = jsonObj.get("data.first_name");
        String lname = jsonObj.get("data.last_name");
        String fullname = fname + " " + lname;
        String newJob = "CEO";

        // pada PUT seluruh data diupdate
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", fullname);
        jsonObject.put("job", newJob);

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(jsonObject.toString())
                .when()
                .put("users/" + userId)
                .then().log().all()
                .assertThat().statusCode(200)
                .assertThat().body("name", Matchers.equalTo(fullname))
                .assertThat().body("job", Matchers.equalTo(newJob))
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(putUserSchema));
    }

    @Test
    public void testPatchUser() {
        File patchUserSchema = TestUtilities.getJsonSchemaFile("patchUserSchema.json");
        int userId = 2;

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

    @Test
    public void testDeleteUser() {
        int userId = 2;

        given()
                .header("Accept", "application/json")
                .when()
                .delete("users/" + userId)
                .then().log().all()
                .assertThat().statusCode(204);
    }

    @Test
    public void testLoginValid() {
        File loginValidSchema = TestUtilities.getJsonSchemaFile("loginValidSchema.json");

        String email = "eve.holt@reqres.in";
        String password = "cityslicka";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", email);
        jsonObject.put("password", password);

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(jsonObject.toString())
                .when()
                .post("login")
                .then()
                .log().all()
                .assertThat().statusCode(200)
                .assertThat().body("$", Matchers.hasKey("token"))
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(loginValidSchema));
    }

    @Test
    public void testLoginInvalidEmail() {
        File loginInvalidSchema = TestUtilities.getJsonSchemaFile("loginInvalidSchema.json");

        String email = "wrong.email@reqres.in";
        String password = "password";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", email);
        jsonObject.put("password", password);

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(jsonObject.toString())
                .when()
                .post("login")
                .then()
                .log().all()
                .assertThat().statusCode(400)
                .assertThat().body("$", Matchers.hasKey("error"))
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(loginInvalidSchema));
    }

    @Test
    public void testLoginInvalidPassword() {
        File loginInvalidSchema = TestUtilities.getJsonSchemaFile("loginInvalidSchema.json");

        String email = "peter@klaven";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", email);

        given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(jsonObject.toString())
                .when()
                .post("login")
                .then()
                .log().all()
                .assertThat().statusCode(400)
                .assertThat().body("$", Matchers.hasKey("error"))
                .assertThat().body(JsonSchemaValidator.matchesJsonSchema(loginInvalidSchema));
    }

    /* pada test boundaries ini yang diharapkan adalah seharusnya response api
       memberikan status code 400 jika batasan yang diset tidak sesuai */
    @Test
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

    @Test
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
