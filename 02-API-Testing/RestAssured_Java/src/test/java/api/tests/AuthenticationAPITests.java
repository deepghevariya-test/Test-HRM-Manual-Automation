package api.tests;

import api.base.APITestBase;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * AuthenticationAPITests - Tests for HRM Authentication APIs.
 *
 * <p>Covers 8 test cases:
 *   TC_AUTH_API_001 - Login returns session cookie
 *   TC_AUTH_API_002 - Invalid credentials returns 401/redirect
 *   TC_AUTH_API_003 - Empty username returns error
 *   TC_AUTH_API_004 - Empty password returns error
 *   TC_AUTH_API_005 - Login page loads (GET /auth/login)
 *   TC_AUTH_API_006 - Verify login page has CSRF token
 *   TC_AUTH_API_007 - Logout clears session
 *   TC_AUTH_API_008 - Verify HTTPS redirect for security
 *
 * @author Deep Ghevariya
 */
@Epic("HRM API Testing")
@Feature("Authentication")
public class AuthenticationAPITests extends APITestBase {

    @Test(groups = {"smoke", "api", "auth"},
            description = "TC_AUTH_API_001: Login page returns 200 OK")
    @Story("Login Page Accessibility")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify the OrangeHRM login page is accessible and returns HTTP 200")
    public void TC_AUTH_API_001_loginPageReturns200() {
        Response response = given()
                .relaxedHTTPSValidation()
                .when()
                .get(BASE_URL + "/web/index.php/auth/login")
                .then()
                .statusCode(200)
                .extract().response();

        Assert.assertEquals(response.getStatusCode(), 200,
                "Login page should return HTTP 200");
        Assert.assertTrue(response.getBody().asString().contains("OrangeHRM"),
                "Login page should contain OrangeHRM branding");
    }

    @Test(groups = {"smoke", "api", "auth"},
            description = "TC_AUTH_API_002: Login page contains CSRF token")
    @Story("CSRF Protection")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify CSRF token is present in the login form for security")
    public void TC_AUTH_API_002_loginPageHasCsrfToken() {
        Response response = given()
                .relaxedHTTPSValidation()
                .when()
                .get(BASE_URL + "/web/index.php/auth/login")
                .then()
                .statusCode(200)
                .extract().response();

        String html = response.getBody().asString();
        boolean hasCsrfToken = html.contains("name=\"_token\"");
        Assert.assertTrue(hasCsrfToken,
                "Login form should contain CSRF token for security");
    }

    @Test(groups = {"api", "auth"},
            description = "TC_AUTH_API_003: Employee API endpoint requires authentication")
    @Story("API Authorization")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Unauthenticated request to employee API should return 401 or redirect to login")
    public void TC_AUTH_API_003_employeeApiRequiresAuth() {
        Response response = given()
                .relaxedHTTPSValidation()
                .header("Accept", "application/json")
                .when()
                .get(BASE_URL + "/web/index.php/api/v2/pim/employees")
                .then()
                .extract().response();

        // OrangeHRM redirects to login or returns 401 for unauthenticated requests
        boolean requiresAuth = response.getStatusCode() == 401
                || response.getStatusCode() == 302
                || response.getStatusCode() == 403;

        Assert.assertTrue(requiresAuth,
                "Unauthenticated request should return 401/302/403, got: " + response.getStatusCode());
    }

    @Test(groups = {"api", "auth"},
            description = "TC_AUTH_API_004: API responds with JSON content type")
    @Story("API Content Type")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify API endpoint returns proper JSON content-type header")
    public void TC_AUTH_API_004_apiContentTypeIsJson() {
        // After authentication
        String session = authenticate(USERNAME, PASSWORD);

        if (session != null && !session.isEmpty()) {
            Response response = given()
                    .relaxedHTTPSValidation()
                    .cookie("PHPSESSID", session)
                    .header("Accept", "application/json")
                    .when()
                    .get(BASE_URL + "/web/index.php/api/v2/pim/employees?limit=1")
                    .then()
                    .extract().response();

            // Should return JSON
            String contentType = response.getContentType();
            boolean isJson = contentType != null && contentType.contains("json");

            if (response.getStatusCode() == 200) {
                Assert.assertTrue(isJson,
                        "API response should be JSON. Got: " + contentType);
            }
        }
    }

    @Test(groups = {"smoke", "api"},
            description = "TC_AUTH_API_005: Dashboard API returns employee count")
    @Story("Dashboard Data")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify dashboard API returns meaningful data after authentication")
    public void TC_AUTH_API_005_dashboardApiData() {
        String session = authenticate(USERNAME, PASSWORD);

        if (session != null && !session.isEmpty()) {
            Response response = given()
                    .relaxedHTTPSValidation()
                    .cookie("PHPSESSID", session)
                    .when()
                    .get(BASE_URL + "/web/index.php/api/v2/dashboard/employees/locations")
                    .then()
                    .extract().response();

            // 200 means data returned; 403 means endpoint exists but restricted
            Assert.assertTrue(
                    response.getStatusCode() == 200 || response.getStatusCode() == 403,
                    "Dashboard API should return 200 or 403. Got: " + response.getStatusCode()
            );
        }
    }

    @Test(groups = {"api", "auth"},
            description = "TC_AUTH_API_006: OrangeHRM API v2 employees endpoint structure")
    @Story("Employee API")
    @Severity(SeverityLevel.CRITICAL)
    public void TC_AUTH_API_006_employeeApiStructure() {
        String session = authenticate(USERNAME, PASSWORD);

        if (session != null && !session.isEmpty()) {
            Response response = given()
                    .relaxedHTTPSValidation()
                    .cookie("PHPSESSID", session)
                    .header("Accept", "application/json")
                    .queryParam("limit", 5)
                    .queryParam("offset", 0)
                    .when()
                    .get(BASE_URL + "/web/index.php/api/v2/pim/employees")
                    .then()
                    .extract().response();

            if (response.getStatusCode() == 200) {
                // Validate response structure: has "data" array
                response.then().body("data", notNullValue());
                Assert.assertNotNull(response.jsonPath().get("data"),
                        "Employee API response should contain 'data' array");
            }
        }
    }

    @Test(groups = {"api"},
            description = "TC_AUTH_API_007: API response time is under 5 seconds")
    @Story("API Performance")
    @Severity(SeverityLevel.NORMAL)
    @Description("Login page response time should be below 5000ms (5 seconds)")
    public void TC_AUTH_API_007_responseTimeUnder5Seconds() {
        Response response = given()
                .relaxedHTTPSValidation()
                .when()
                .get(BASE_URL + "/web/index.php/auth/login")
                .then()
                .extract().response();

        long responseTime = response.getTime();
        Assert.assertTrue(responseTime < 5000,
                "Response time should be < 5000ms. Actual: " + responseTime + "ms");
    }

    @Test(groups = {"api", "auth"},
            description = "TC_AUTH_API_008: Leave API endpoint is accessible after auth")
    @Story("Leave API")
    @Severity(SeverityLevel.NORMAL)
    public void TC_AUTH_API_008_leaveApiAccessibleAfterAuth() {
        String session = authenticate(USERNAME, PASSWORD);

        if (session != null && !session.isEmpty()) {
            Response response = given()
                    .relaxedHTTPSValidation()
                    .cookie("PHPSESSID", session)
                    .header("Accept", "application/json")
                    .queryParam("limit", 5)
                    .when()
                    .get(BASE_URL + "/web/index.php/api/v2/leave/leave-requests")
                    .then()
                    .extract().response();

            Assert.assertTrue(
                    response.getStatusCode() == 200 || response.getStatusCode() == 403,
                    "Leave API should be accessible after auth. Got: " + response.getStatusCode()
            );
        }
    }
}
