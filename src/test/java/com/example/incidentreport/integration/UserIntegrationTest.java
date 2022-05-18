package com.example.incidentreport.integration;

import com.example.incidentreport.IncidentReportApplication;
import com.example.incidentreport.contract.User;
import com.example.incidentreport.repository.UserRepository;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpHeaders;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.example.incidentreport.util.TestConstants.INVALID_FIRSTNAME;
import static com.example.incidentreport.util.TestConstants.INVALID_LASTNAME;
import static com.example.incidentreport.util.TestConstants.INVALID_USERNAME;
import static com.example.incidentreport.util.TestConstants.VALID_FIRSTNAME;
import static com.example.incidentreport.util.TestConstants.VALID_FIRSTNAME_1;
import static com.example.incidentreport.util.TestConstants.VALID_LASTNAME;
import static com.example.incidentreport.util.TestConstants.VALID_LASTNAME_1;
import static com.example.incidentreport.util.TestConstants.VALID_USERNAME;
import static com.example.incidentreport.util.TestConstants.VALID_USERNAME_1;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_ACCEPTED;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {IncidentReportApplication.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void givenUser_whenPostRequest_thenUserCreated() {
        //Given
        User user = getUser(VALID_USERNAME, VALID_FIRSTNAME, VALID_LASTNAME);
        Response response = postUserRequest(user);
        //Then
        response.then().statusCode(SC_ACCEPTED).body("message", Matchers.is("User Created"));
    }

    private Response postUserRequest(User user) {
        return getHeader().body(user)
                //When
                .when().post("/user");
    }

    private User getUser(String userName, String firstName, String lastName) {
        User user = new User.Builder().userName(userName).
                firstName(firstName).lastName(lastName).build();
        return user;
    }

    @Test
    public void givenUserWithMissingUserName_whenPostRequest_thenBadRequest() {
        //Given
        User user = new User.Builder().
                firstName(VALID_FIRSTNAME).lastName(VALID_LASTNAME).build();
        Response response = postUserRequest(user);
        //Then
        response.then().statusCode(SC_BAD_REQUEST).body("details", Matchers.contains("Mandatory fields are missing"));
    }

    @Test
    public void givenUserAlreadyExists_whenPostRequest_thenBadRequest() {
        //Given
        createUser();
        User user2 = getUser(VALID_USERNAME, VALID_FIRSTNAME_1, VALID_LASTNAME_1);
        Response response = postUserRequest(user2);
        //Then
        response.then().statusCode(SC_CONFLICT).body("details", Matchers.contains("User name '" + VALID_USERNAME +
                "' already exists"));
    }

    @Test
    public void givenUserFirstNameUpdated_whenPutRequest_thenUserUpdated() {
        //Given
        createUser();

        User userPutRequest = getUser(VALID_USERNAME, VALID_FIRSTNAME_1,VALID_LASTNAME);
        Response response = putUserRequest(userPutRequest);
        //Then
        response.then().statusCode(SC_ACCEPTED).body("message", Matchers.is("User Updated"));
    }

    private void createUser() {
        User user = getUser(VALID_USERNAME, VALID_FIRSTNAME, VALID_LASTNAME);
        postUserRequest(user);
    }

    @Test
    public void givenUserDoesNotExist_whenPutRequest_thenBadRequest() {
        //Given
        createUser();

        User userPutRequest = getUser(INVALID_USERNAME, INVALID_FIRSTNAME,INVALID_LASTNAME);
        Response response = putUserRequest(userPutRequest);
        //Then
        response.then().statusCode(SC_BAD_REQUEST).body("details", Matchers.contains("User does not exists"));
    }

    @Test
    public void givenUserName_whenDeleteRequest_thenUserDeleted() {
        //Given
        createUser();
        Response response = deleteUserRequest(VALID_USERNAME);

        //Then
        response.then().statusCode(SC_ACCEPTED).body("message", Matchers.is("User Deleted"));
    }

    @Test
    public void givenUserNameNotExists_whenDeleteRequest_thenBadRequest() {
        //Given
        createUser();
        Response response = deleteUserRequest(INVALID_USERNAME);

        //Then
        response.then().statusCode(SC_BAD_REQUEST).body("details", Matchers.contains("User does not exists"));
    }

    @Test
    public void givenUserNameNotExists_whenGetRequest_thenBadRequest() {
        //Given
        Response response = getUser(INVALID_USERNAME);

        //Then
        response.then().statusCode(SC_BAD_REQUEST).body("details", Matchers.contains("User does not exist"));
    }

    @Test
    public void givenUserNameExists_whenGetRequest_thenReturnUser() {
        //Given
        createUser();
        Response response = getUser(VALID_USERNAME);

        //Then
        response.then().statusCode(SC_OK).body("userName", Matchers.is(VALID_USERNAME));
    }

    @Test
    public void givenUsers_whenGetRequest_thenReturnAllUsers() {
        //Given
        createUser();
        User user = getUser(VALID_USERNAME_1,VALID_FIRSTNAME_1,VALID_LASTNAME_1);
        postUserRequest(user);
        Response response = getAllUsers();

        //Then
        response.then().statusCode(SC_OK).body("results.size()", equalTo(2));
    }

    @Test
    public void givenNoUsers_whenGetRequest_thenReturnEmptyResponse() {
        //Given
        Response response = getAllUsers();

        //Then
        response.then().statusCode(SC_OK).body("results.size()", equalTo(0));
    }

    private Response getAllUsers() {
        return getHeader().
                //When
                        when().get("/users");
    }

    private Response putUserRequest(User user) {
        return getHeader().body(user)
                //When
                .when().put(String.format("/user/%s",user.getUserName()));
    }

    private Response deleteUserRequest(String userName) {
        return getHeader().
                //When
                when().delete(String.format("/user/%s",userName));
    }

    private Response getUser(String userName) {
        return getHeader().
                //When
                        when().get(String.format("/user/%s",userName));
    }

    private RequestSpecification getHeader() {
        return given().header(HttpHeaders.ACCEPT, APPLICATION_JSON_VALUE).
                header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE);
    }

    @After
    public void cleanup() {
        userRepository.deleteAll();
    }
}
