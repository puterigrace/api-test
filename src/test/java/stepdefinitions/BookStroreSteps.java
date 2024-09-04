package stepdefinitions;

import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utility.TestContext;

import java.util.HashMap;
import java.util.Map;

public class BookStroreSteps {

    private static final Logger logger = LogManager.getLogger(BookStroreSteps.class);

    @When("I send a {string} request to {string}")
    public void iSendARequestTo(String method, String endpoint) {

        TestContext.setEndpoint(endpoint);
        TestContext.setMethod(method);

        Map<String, Object> requestMap = composeRequest(method, endpoint);

        sendRequest(requestMap);
    }

    private Map<String, Object> composeRequest(String method, String endpoint) {
        Map<String, Object> requestMap =  new HashMap<>();

        RequestSpecification request = RestAssured.given()
                .contentType("application/json")
                .accept("application/json");

        requestMap.put("method", method);
        requestMap.put("endpoint", endpoint);
        requestMap.put("request", request);


        return requestMap;
    }

    private void sendRequest(Map<String, Object> requestMap) {

        RequestSpecification request = (RequestSpecification) requestMap.get("request");
        String endpoint = String.valueOf(requestMap.get("endpoint"));
        String method = String.valueOf(requestMap.get("method"));

        logger.info("Sending request to: " + endpoint);

        Response response;
        switch (method) {
            case "POST":
                response = request.post(endpoint);
                TestContext.setResponse(response);
                break;
            case "GET":
                response = request.get(endpoint);
                TestContext.setResponse(response);
                break;
            case "PUT":
                response = request.put(endpoint);
                TestContext.setResponse(response);
                break;
            case "DELETE":
                response = request.delete(endpoint);
                TestContext.setResponse(response);
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }

        response.then();
        logger.info("Response status: " + response.getStatusCode());
        logger.info("Response body: " + response.getBody().asString());
        
    }
}
