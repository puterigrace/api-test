package stepdefinitions;


import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.path.json.JsonPath;
import io.cucumber.datatable.DataTable;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utility.TestContext;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;

public class AccountSteps {
    private static final Logger logger = LogManager.getLogger(AccountSteps.class);


    @Given("the API endpoint is available")
    public void theApiEndpointIsAvailable() {
        RestAssured.baseURI = "https://demoqa.com";
        TestContext.resetTestContext();
        TestContext.removeUserID();
        TestContext.removeToken();
        TestContext.removeMethod();
    }



    @Then("the response status should be {string}")
    public void theResponseStatusShouldBe(String statusCode) {
        TestContext.getResponse().then().statusCode(Integer.parseInt(statusCode));
    }

    @Then("the response should contain {string}")
    public void theResponseShouldContain(String message) {

        String responseBody = TestContext.getResponse().getBody().asString();
        JsonPath jsonPath = new JsonPath(responseBody);
        switch (TestContext.getEndPoint()){

            case "/Account/v1/User":
                if(TestContext.getResponse().getStatusCode() == 201){
                    String userID = jsonPath.getString("userID");
                    String username = jsonPath.getString("username");
                    if(message.equals("<username>")){
                        message = TestContext.getGeneratedUserName();
                    }
                    assertThat(username, equalTo(message));
                    assertThat(userID, notNullValue());
                    TestContext.setUserId(userID);

                } else if( TestContext.getResponse().getStatusCode() == 400){
                    String actualMessage = jsonPath.getString("message");
                    assertThat(actualMessage, equalTo(message));
                }

                break;
            case "/Account/v1/GenerateToken":
                String actualStatus = jsonPath.getString("status");
                String token = jsonPath.getString("token");
                TestContext.setToken(token);

                if(StringUtils.isBlank(message)){
                    assertThat(actualStatus, emptyOrNullString());
                }else{
                    assertThat(actualStatus, equalTo(message));
                }


                if ("Success".equals(message)) {

                    assertThat(token, org.hamcrest.Matchers.not(emptyOrNullString()));
                } else {
                    assertThat(token, emptyOrNullString());
                }
                break;
            case "/Account/v1/User/%s":
                switch (TestContext.getMethod()){
                    case "GET":
                        if(TestContext.getResponse().getStatusCode() == 401){
                            assertThat(jsonPath.getString("message"), equalTo(message));
                        }
                        break;
                    case "DELETE":
                        if(TestContext.getResponse().getStatusCode() == 200){
                            assertThat(jsonPath.getString("message"), equalTo(message));
                        }
                        break;
                }
                break;
            case "/Account/v1/Authorized":
                assertThat(responseBody, equalTo(message));
                break;
            case "/BookStore/v1/Books":


                if (TestContext.getResponse().getStatusCode() == 200){

                    List<Map<String, Object>> books = jsonPath.getList(message);
                    assertThat(books,notNullValue());

                    List<String> isbnList = books.stream()
                            .map(book -> (String) book.get("isbn"))
                            .collect(Collectors.toList());

                    TestContext.setIsbnList(isbnList);
                } else if (TestContext.getResponse().getStatusCode() == 201){

                    List<Map<String, Object>> books = jsonPath.getList(message);
                    assertThat(books,notNullValue());

                    Map<String,Object> book = books.get(0);
                    String actualIsbn = (String) book.get("isbn");

                    assertThat(actualIsbn,equalTo(TestContext.getIsbnList().get(0)));
                } else if (TestContext.getResponse().getStatusCode() == 401
                        || TestContext.getResponse().getStatusCode() == 400){
                    String actualMessage = jsonPath.getString("message");
                    assertThat(actualMessage,equalTo(message));
                }

                break;
            case "/BookStore/v1/Book":
                if(TestContext.getResponse().getStatusCode() == 200){
                    String actualIsbn = jsonPath.getString(message);
                    assertThat(actualIsbn, equalTo(TestContext.getIsbnList().get(0)));
                } else if(TestContext.getResponse().getStatusCode() == 400){
                    String actualMessage = jsonPath.getString("message");
                    assertThat(actualMessage, equalTo(message));
                }

                break;
            default:
                logger.error("invalid endpoint");
                break;
        }
        TestContext.removeEndpoint();

    }


    @When("I send a {string} request to {string} with the following data")
    public void iSendARequestToWithTheFollowingData(String method, String endpoint, DataTable dataTable) {
        Map<String, String> data = new HashMap<>(dataTable.asMap(String.class, String.class));

        TestContext.setGeneratedUserName();
        TestContext.setEndpoint(endpoint);
        TestContext.setMethod(method);


        logger.info("Request body: " + data);


        Map<String, Object> requestMap = composeRequest(method, endpoint, data);

        sendRequest(requestMap);
    }

    private Map<String, Object> composeRequest(String method, String endpoint, Map<String, String> data) {
        Map<String, Object> requestMap =  new HashMap<>();

        if (data.containsKey("userName") && data.get("userName").equals("<username>")) {
            data.put("userName", TestContext.getGeneratedUserName());
        }

        RequestSpecification request = RestAssured.given()
                .contentType("application/json")
                .accept("application/json");

        request.auth().none();

        switch (endpoint){
            case "/Account/v1/User/%s":
                if(data.containsKey("userId") && !StringUtils.isBlank(data.get("userId"))){
                    endpoint = String.format(endpoint, data.get("userId"));
                }else{
                    endpoint = String.format(endpoint,TestContext.getUserID());
                }
                request.auth().oauth2(TestContext.getToken());
                break;
            case "/BookStore/v1/Book":
                switch (method){
                    case "GET":
                        if(data.containsKey("ISBN") && StringUtils.isBlank(data.get("ISBN"))){
                            data.put("ISBN", TestContext.getIsbnList().get(0));
                        }
                        endpoint = String.format(endpoint + "?ISBN=%s",data.get("ISBN"));
                        break;

                    case "DELETE":
                        if(data.containsKey("isbn") && StringUtils.isBlank(data.get("isbn"))){
                            data.put("isbn", TestContext.getIsbnList().get(0));
                        }
                        if(data.containsKey("userId") && StringUtils.isBlank(data.get("userId"))){
                            data.put("userId", TestContext.getUserID());
                        }
                        if(data.containsKey("token") && StringUtils.isBlank(data.get("token"))){
                            data.put("token", TestContext.getToken());
                        }

                        Map<String, String> deleteBookObj = new HashMap<>();
                        deleteBookObj.put("isbn", data.get("isbn"));
                        deleteBookObj.put("userId", data.get("userId"));

                        request.body(data).auth().oauth2(data.get("token"));
                        break;

                    default:
                        break;
                }
                break;
            case "/BookStore/v1/Books":
                switch (method){
                    case "POST":
                    case "PUT" :


                        if(data.containsKey("userId") && StringUtils.isBlank(data.get("userId"))){
                            data.put("userId", TestContext.getUserID());
                        }

                        if(data.containsKey("token") && StringUtils.isBlank(data.get("token"))){
                            data.put("token", TestContext.getToken());
                        }

                        Map<String, Object> requestData =  new HashMap<>();

                        if(method.equals("POST")){

                            if(data.containsKey("isbn") && StringUtils.isBlank(data.get("isbn"))){
                                data.put("isbn", TestContext.getIsbnList().get(0));
                            }

                            Map<String, String> isbnObject = new HashMap<>();
                            isbnObject.put("isbn", data.get("isbn"));

                            // Creating the collection of ISBNs
                            List<Map<String, String>> collectionOfIsbns = List.of(isbnObject);

                            // Creating the final map
                            Map<String, Object> requestPostBooks = new HashMap<>();
                            requestPostBooks.put("userId", data.get("userId"));
                            requestPostBooks.put("collectionOfIsbns", collectionOfIsbns);
                            requestData = requestPostBooks;
                        } else {

                            if(data.containsKey("newIsbn") && StringUtils.isBlank(data.get("newIsbn"))){
                                data.put("newIsbn", TestContext.getIsbnList().get(1));
                            }
                            if(data.containsKey("existIsbn") && StringUtils.isBlank(data.get("existIsbn"))){
                                data.put("existIsbn", TestContext.getIsbnList().get(0));
                            }

                            endpoint = String.format(endpoint + "/%s",data.get("existIsbn"));

                            Map<String, Object> requestPutBooks = new HashMap<>();
                            requestPutBooks.put("userId", data.get("userId"));
                            requestPutBooks.put("isbn", data.get("newIsbn"));

                            requestData = requestPutBooks;
                        }


                        request.body(requestData).auth().oauth2(data.get("token"));

                        break;
                    case "DELETE":
                        if(data.containsKey("UserId") && StringUtils.isBlank(data.get("UserId"))){
                            data.put("UserId", TestContext.getUserID());
                        }
                        if(data.containsKey("token") && StringUtils.isBlank(data.get("token"))){
                            data.put("token", TestContext.getToken());
                        }
                        endpoint = String.format(endpoint + "?UserId=%s",data.get("UserId"));
                        request.auth().oauth2(data.get("token"));
                        break;
                    default:
                        break;
                }
                break;
            default:
                 request.body(data);
                 break;
        }

        requestMap.put("method", method);
        requestMap.put("endpoint", endpoint);
        requestMap.put("request", request);

        return requestMap;
    }

    private void sendRequest(Map<String, Object> requestMap){

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

        requestMap.clear();
        logger.info("Response status: " + response.getStatusCode());
        logger.info("Response body: " + response.getBody().asString());

    }

    @And("wait for {string} second\\(s)")
    public void waitForSecondS(String second) throws InterruptedException {
        long millis = Long.parseLong(second) * 1000;
        Thread.sleep(millis);
    }

}
