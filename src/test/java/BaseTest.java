import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HeaderConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

public class BaseTest {
    private final String userNamePrefix = "testapiuser";
    private final String password = "testapipass";
    private String userName;
    private String sessionToken;

    @BeforeTest(groups = {"Student", "Group", "Assignment"})
    public void setUp() {
        RestAssured.baseURI = "http://www.robotdreams.karpenko.cc";
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }

    @BeforeMethod(groups = {"Student", "Group", "Assignment"})
    public void setUserToken() {
        userName = userNamePrefix + randomAlphanumeric(5);
        JSONObject jo = new JSONObject();
        jo.put("username", userName);
        jo.put("password", password);

        RestAssured.given()
                .body(jo.toString())
                .when()
                .post("/user").then().statusCode(200);
        Response sessionResponse = RestAssured.given()
                .queryParam("username", userName)
                .queryParam("password", password)
                .when()
                .get("/login");
        sessionResponse.then().statusCode(200);
        JSONObject joTokenResponse = new JSONObject(sessionResponse.asString());
        sessionToken = joTokenResponse.getString("session_token");
        //System.out.println("Session Token : " + sessionToken);

        RestAssured.config = RestAssured.config.headerConfig(HeaderConfig.headerConfig().overwriteHeadersWithName("user-token"));

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addHeader("user-token", sessionToken)
                .build();
    }
}