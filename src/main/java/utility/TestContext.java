package utility;

import org.apache.commons.lang3.StringUtils;
import io.restassured.response.Response;

import java.util.List;

public class TestContext {
    private static String generatedUserName;
    private static String endPoint;
    private static String userID;
    private static String generatedToken;
    private static String method;
    private static List<String> isbnList;
    private static Response response;

    public static String getGeneratedUserName() {
        return generatedUserName;
    }

    public static void setGeneratedUserName() {
        if(StringUtils.isBlank(generatedUserName)){
            generatedUserName = RandomDataGenerator.generateRandomString(8);
        }
    }

    public static void setBlankGeneratedUsername(){
        generatedUserName = "";
    }
    public static void resetTestContext() {setBlankGeneratedUsername();}

    public static void setEndpoint(String endpoint) {endPoint = endpoint;}
    public static String getEndPoint(){return endPoint;}
    public static void removeEndpoint(){ endPoint ="";}

    public static void setUserId(String userId) {userID = userId;}
    public static String getUserID(){ return userID;}
    public static void removeUserID (){userID = "";}

    public static void setToken(String token) {generatedToken = token;}
    public static String getToken() {return  generatedToken;}
    public static void removeToken(){ generatedToken = "";}

    public static void setMethod(String givenMethod) {method = givenMethod;}
    public static String getMethod() {return  method;}
    public static void removeMethod() {method = "";}

    public static Response getResponse() {
        return response;
    }

    public static void setResponse(Response response) {
        TestContext.response = response;
    }

    public static void setIsbnList(List<String> isbnList) {
        TestContext.isbnList = isbnList;
    }
    public static List<String> getIsbnList(){return isbnList;}
}
