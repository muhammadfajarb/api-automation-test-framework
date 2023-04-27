package apiauto;

import io.restassured.RestAssured;

import java.io.File;

public class TestUtilities {
    public static void setBaseURI(String baseURI) {
        RestAssured.baseURI = baseURI;
    }

    public static File getJsonSchemaFile(String jsonSchemaFilename) {
        String jsonSchemaDir = "src/test/resources/jsonSchema/";
        return new File(jsonSchemaDir + jsonSchemaFilename);
    }
}
