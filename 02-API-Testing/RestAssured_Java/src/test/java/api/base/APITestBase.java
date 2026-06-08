package api.base;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeSuite;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static io.restassured.RestAssured.given;

/**
 * APITestBase - Base configuration for all RestAssured API tests.
 *
 * <p>Features:
 * <ul>
 *   <li>Base URI and path configuration</li>
 *   <li>Default request/response specs</li>
 *   <li>Authentication token management</li>
 *   <li>Allure reporting integration</li>
 *   <li>BDD-style given/when/then</li>
 * </ul>
 *
 * @author Deep Ghevariya
 */
public class APITestBase {

    private static final Logger logger = LogManager.getLogger(APITestBase.class);

    protected static final String BASE_URL;
    protected static final String USERNAME;
    protected static final String PASSWORD;

    protected static RequestSpecification requestSpec;
    protected static ResponseSpecification responseSpec;
    protected static String authToken;
    protected static String sessionCookies;

    static {
        Properties props = loadProperties();
        BASE_URL = props.getProperty("api.base.url", "https://opensource-demo.orangehrmlive.com");
        USERNAME = props.getProperty("admin.username", "Admin");
        PASSWORD = props.getProperty("admin.password", "admin123");
    }

    @BeforeSuite(alwaysRun = true)
    public void setupAPI() {
        logger.info("═══════════════════════════════════════════");
        logger.info(" API Test Suite Initialization");
        logger.info(" Base URL: {}", BASE_URL);
        logger.info("═══════════════════════════════════════════");

        RestAssured.baseURI = BASE_URL;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        // Build default request spec
        requestSpec = new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new AllureRestAssured())          // Allure logging
                .log(LogDetail.ALL)
                .build();

        // Build default response spec
        responseSpec = new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();

        logger.info("API base configuration complete");
    }

    // ════════════════════════════════════════════════
    //  AUTHENTICATION
    // ════════════════════════════════════════════════

    /**
     * Authenticate with OrangeHRM and retrieve session token.
     * OrangeHRM uses form-based auth with CSRF token.
     */
    protected String authenticate(String username, String password) {
        logger.info("Authenticating API user: {}", username);

        // Step 1: Get login page to extract CSRF token
        io.restassured.response.Response loginPageResponse = given()
                .relaxedHTTPSValidation()
                .when()
                .get("/web/index.php/auth/login")
                .then()
                .extract().response();

        // Extract CSRF token from HTML
        String html = loginPageResponse.getBody().asString();
        String csrfToken = extractCsrfToken(html);
        String cookies = loginPageResponse.getHeaders().getValue("Set-Cookie");

        logger.debug("CSRF Token extracted: {}...", csrfToken != null ? csrfToken.substring(0, Math.min(10, csrfToken.length())) : "null");

        // Step 2: POST login credentials
        io.restassured.response.Response authResponse = given()
                .relaxedHTTPSValidation()
                .contentType("application/x-www-form-urlencoded")
                .formParam("_token", csrfToken)
                .formParam("username", username)
                .formParam("password", password)
                .cookie("PHPSESSID", extractSessionId(cookies))
                .redirects().follow(false)
                .when()
                .post("/web/index.php/auth/validate")
                .then()
                .extract().response();

        // Extract session cookie
        String sessionCookie = authResponse.getCookie("PHPSESSID");
        authToken = sessionCookie;

        logger.info("Authentication successful | Session obtained: {}", sessionCookie != null);
        return sessionCookie;
    }

    protected RequestSpecification getAuthenticatedRequest() {
        if (authToken == null) {
            authToken = authenticate(USERNAME, PASSWORD);
        }
        return given()
                .spec(requestSpec)
                .relaxedHTTPSValidation()
                .cookie("PHPSESSID", authToken);
    }

    // ════════════════════════════════════════════════
    //  HELPERS
    // ════════════════════════════════════════════════

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream is = APITestBase.class.getClassLoader()
                .getResourceAsStream("api-config.properties")) {
            if (is != null) props.load(is);
        } catch (IOException e) {
            logger.warn("api-config.properties not found, using defaults");
        }
        return props;
    }

    private String extractCsrfToken(String html) {
        // Extract from: <input type="hidden" name="_token" value="...">
        String marker = "name=\"_token\" value=\"";
        int idx = html.indexOf(marker);
        if (idx == -1) return "";
        int start = idx + marker.length();
        int end = html.indexOf("\"", start);
        return html.substring(start, end);
    }

    private String extractSessionId(String cookieHeader) {
        if (cookieHeader == null) return "";
        String marker = "PHPSESSID=";
        int idx = cookieHeader.indexOf(marker);
        if (idx == -1) return "";
        int start = idx + marker.length();
        int end = cookieHeader.indexOf(";", start);
        return end == -1 ? cookieHeader.substring(start) : cookieHeader.substring(start, end);
    }
}
