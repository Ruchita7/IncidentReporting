package com.example.incidentreport.integration;

import com.example.incidentreport.IncidentReportApplication;
import com.example.incidentreport.contract.IncidentReport;
import com.example.incidentreport.contract.User;
import com.example.incidentreport.model.IncidentStatus;
import com.example.incidentreport.repository.IncidentRepository;
import com.example.incidentreport.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpHeaders;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.annotation.AfterTestClass;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedHashMap;

import static com.example.incidentreport.util.TestConstants.INVALID_INCIDENT_ID;
import static com.example.incidentreport.util.TestConstants.INVALID_USERNAME;
import static com.example.incidentreport.util.TestConstants.VALID_FIRSTNAME;
import static com.example.incidentreport.util.TestConstants.VALID_FIRSTNAME_1;
import static com.example.incidentreport.util.TestConstants.VALID_INCIDENT_TITLE;
import static com.example.incidentreport.util.TestConstants.VALID_INCIDENT_TITLE_1;
import static com.example.incidentreport.util.TestConstants.VALID_LASTNAME;
import static com.example.incidentreport.util.TestConstants.VALID_LASTNAME_1;
import static com.example.incidentreport.util.TestConstants.VALID_USERNAME;
import static com.example.incidentreport.util.TestConstants.VALID_USERNAME_1;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_ACCEPTED;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {IncidentReportApplication.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IncidentIntegrationTest {

    @Autowired
    private IncidentRepository incidentRepository;
    @Autowired
    private UserRepository userRepository;

    @Before
    public void createUsers() {
        RestAssured.requestSpecification = new RequestSpecBuilder().setContentType(ContentType.JSON).build();
        //Create user 1
        createUser(VALID_USERNAME, VALID_FIRSTNAME, VALID_LASTNAME);
        //Create user 2
        createUser(VALID_USERNAME_1, VALID_FIRSTNAME_1, VALID_LASTNAME_1);
    }

    private void createUser(String userName, String firstName, String lastName) {
        User user = new User.Builder().userName(userName).
                firstName(firstName).lastName(lastName).build();
        getHeader().body(user)
                //When
                .when().post("/user");
    }

    private RequestSpecification getHeader() {
        return given().header(HttpHeaders.ACCEPT, APPLICATION_JSON_VALUE).
                header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE);
    }

    @Test
    public void givenIncidentWithNoStatusProvided_whenPostRequest_thenNewIncidentCreated() {
        //Given
        IncidentReport incidentReport = new IncidentReport.Builder().creator(VALID_USERNAME)
                .assignee(VALID_USERNAME).title(VALID_INCIDENT_TITLE).build();
        Response response = postIncidentRequest(incidentReport);
        //Then
        response.then().statusCode(SC_ACCEPTED).body("message", Matchers.is("Incident Created"));
    }

    @Test
    public void givenIncidentWithMissingFields_whenPostRequest_thenBadRequest() {
        //Given
        IncidentReport incidentReport = new IncidentReport.Builder().assignee(VALID_USERNAME).build();
        Response response = postIncidentRequest(incidentReport);
        //Then
        response.then().statusCode(SC_BAD_REQUEST).body("details", Matchers.contains("Missing mandatory fields"));
    }

    @Test
    public void givenIncidentWithTitleAlreadyExists_whenPostRequest_thenConflictException() {
        //Given
        IncidentReport incidentReport1 = new IncidentReport.Builder().creator(VALID_USERNAME)
                .assignee(VALID_USERNAME).title(VALID_INCIDENT_TITLE).build();
        postIncidentRequest(incidentReport1);
        IncidentReport incidentReport2 = new IncidentReport.Builder().creator(VALID_USERNAME_1)
                .assignee(VALID_USERNAME).title(VALID_INCIDENT_TITLE).build();
        Response response = postIncidentRequest(incidentReport2);
        //Then
        response.then().statusCode(SC_CONFLICT).body("details",
                Matchers.contains("Incident report by the same title already exists"));
    }


    @Test
    public void givenIncidentWithInvalidCreator_whenPostRequest_thenBadRequest() {
        //Given
        IncidentReport incidentReport = new IncidentReport.Builder().creator(INVALID_USERNAME)
                .assignee(VALID_USERNAME).title(VALID_INCIDENT_TITLE).build();
        Response response = postIncidentRequest(incidentReport);
        //Then
        response.then().statusCode(SC_BAD_REQUEST).body("details", Matchers.contains("Invalid creator provided"));
    }


    @Test
    public void givenIncidentWithInvalidAssignee_whenPostRequest_thenBadRequest() {
        //Given
        IncidentReport incidentReport = new IncidentReport.Builder().creator(VALID_USERNAME)
                .assignee(INVALID_USERNAME).title(VALID_INCIDENT_TITLE).build();
        Response response = postIncidentRequest(incidentReport);
        //Then
        response.then().statusCode(SC_BAD_REQUEST).body("details", Matchers.contains("Invalid assignee provided"));
    }

    @Test
    public void givenAssigneeHavingAlreadyAssignedIncidents_whenPostRequest_thenBadRequest() {
        //Given
        IncidentReport incidentReport1 = new IncidentReport.Builder().creator(VALID_USERNAME)
                .assignee(VALID_USERNAME).title(VALID_INCIDENT_TITLE).build();
        postIncidentRequest(incidentReport1);
        IncidentReport incidentReport2 = new IncidentReport.Builder().creator(VALID_USERNAME_1)
                .assignee(VALID_USERNAME).title(VALID_INCIDENT_TITLE_1).build();
        Response response = postIncidentRequest(incidentReport2);
        //Then
        response.then().statusCode(SC_CONFLICT).body("details",
                Matchers.contains("Assignee already has other tasks in assigned status"));
    }

    @Test
    public void givenIncidents_whenGetRequest_thenRetrieveAllIncidents() {
        //Given
        IncidentReport incidentReport1 = new IncidentReport.Builder().creator(VALID_USERNAME)
                .assignee(VALID_USERNAME).title(VALID_INCIDENT_TITLE).build();
        postIncidentRequest(incidentReport1);
        IncidentReport incidentReport2 = new IncidentReport.Builder().creator(VALID_USERNAME_1)
                .assignee(VALID_USERNAME_1).title(VALID_INCIDENT_TITLE_1).build();
        postIncidentRequest(incidentReport2);

        getHeader()
                //When
                .when().get("/incidents")
                //Then
                .then()
                .statusCode(SC_OK).body("size", greaterThan(0));
    }

    @Test
    public void givenStatus_whenGetRequest_thenRetrieveIncidentsForStatus() {
        //Given
        IncidentReport incidentReport1 = new IncidentReport.Builder().creator(VALID_USERNAME)
                .assignee(VALID_USERNAME).title(VALID_INCIDENT_TITLE).build();
        postIncidentRequest(incidentReport1);
        IncidentReport incidentReport2 = new IncidentReport.Builder().creator(VALID_USERNAME_1)
                .assignee(VALID_USERNAME_1).title(VALID_INCIDENT_TITLE_1).build();
        postIncidentRequest(incidentReport2);

        getHeader()
                //When
                .when().get("/incidents?status=assigned")
                //Then
                .then()
                .statusCode(SC_OK).body("size", greaterThan(2));
    }


    @Test
    public void givenIncidentWithInvalidAssignee_whenPatchRequest_thenBadRequest() {
        //Given
        createIncident();
        IncidentReport incidentReport = new IncidentReport.Builder()
                .assignee(INVALID_USERNAME).title(VALID_INCIDENT_TITLE).build();
        Response response = patchIncidentRequest(incidentReport,VALID_USERNAME,INVALID_INCIDENT_ID);
        //Then
        response.then().statusCode(SC_BAD_REQUEST).body("details", Matchers.contains("Invalid assignee provided"));
    }

    @Test
    public void givenIncidentWithInvalidUser_whenPatchRequest_thenBadRequest() {
        //Given
        createIncident();
        IncidentReport incidentReport = new IncidentReport.Builder()
                .assignee(VALID_USERNAME_1).title(VALID_INCIDENT_TITLE).build();
        Response response = patchIncidentRequest(incidentReport,INVALID_USERNAME,INVALID_INCIDENT_ID);
        //Then
        response.then().statusCode(SC_BAD_REQUEST).body("details", Matchers.contains("User provided does not exist"));
    }

    @Test
    public void givenIncidentWithInvalidIncidentId_whenPatchRequest_thenBadRequest() {

        //Given
        createIncident();
        IncidentReport incidentReport = new IncidentReport.Builder()
                .assignee(VALID_USERNAME_1).title(VALID_INCIDENT_TITLE).build();
        Response response = patchIncidentRequest(incidentReport,VALID_USERNAME,INVALID_INCIDENT_ID);
        //Then
        response.then().statusCode(SC_BAD_REQUEST).body("details", Matchers.contains("Incident provided does not exist"));
    }

    @Test
    public void givenIncidentWithInvalidPermissionToUpdate_whenPatchRequest_thenBadRequest() {

        //Given
        createIncident();
        int incidentId= (int)((LinkedHashMap<String,Object>)getHeader()
                //When
                .when().get("/incidents").getBody().jsonPath().getList("content").get(0)).get("incidentId");

        IncidentReport incidentReport = new IncidentReport.Builder()
                .assignee(VALID_USERNAME_1).title(VALID_INCIDENT_TITLE).build();
        Response response = patchIncidentRequest(incidentReport,VALID_USERNAME_1,incidentId);

        //Then
        response.then().statusCode(SC_BAD_REQUEST).body("details", Matchers.contains(String.format(
                "Current user %s does not have the required permission to update the report", VALID_USERNAME_1)));
    }

    @Test
    public void givenIncidentWithCreatorUpdatingReportStatus_whenPatchRequest_thenBadRequest() {

        //Given
        createIncidentWithDifferentCreatorAndAssignee();
        int incidentId= (int)((LinkedHashMap<String,Object>)getHeader()
                //When
                .when().get("/incidents").getBody().jsonPath().getList("content").get(0)).get("incidentId");

        IncidentReport incidentReport = new IncidentReport.Builder().status(IncidentStatus.CLOSED.getName()).build();

        Response response = patchIncidentRequest(incidentReport,VALID_USERNAME,incidentId);

        //Then
        response.then().statusCode(SC_BAD_REQUEST).body("details", Matchers.contains(String.format(
                "Current user %s does not have the required permission to update the report status", VALID_USERNAME)));
    }

    @Test
    public void givenIncidentWithAssigneeUpdatingReportStatus_whenPatchRequest_thenOK() {

        //Given
        createIncidentWithDifferentCreatorAndAssignee();
        int incidentId= (int)((LinkedHashMap<String,Object>)getHeader()
                //When
                .when().get("/incidents").getBody().jsonPath().getList("content").get(0)).get("incidentId");

        IncidentReport incidentReport = new IncidentReport.Builder().status(IncidentStatus.CLOSED.getName()).build();

        Response response = patchIncidentRequest(incidentReport,VALID_USERNAME_1,incidentId);

        //Then
        response.then().statusCode(SC_ACCEPTED).body("message", Matchers.is("Incident Report Updated"));
    }

    @Test
    public void givenIncidentWithValidPermissionToUpdate_whenPatchRequest_thenOk() {

        //Given
        createIncidentWithDifferentCreatorAndAssignee();
        int incidentId= (int)((LinkedHashMap<String,Object>)getHeader()
                //When
                .when().get("/incidents").getBody().jsonPath().getList("content").get(0)).get("incidentId");

        IncidentReport incidentReport = new IncidentReport.Builder()
                .assignee(VALID_USERNAME_1).title(VALID_INCIDENT_TITLE).build();
        Response response = patchIncidentRequest(incidentReport,VALID_USERNAME,incidentId);

        //Then
        response.then().statusCode(SC_ACCEPTED).body("message",Matchers.is("Incident Report Updated"));
    }



    @Test
    public void givenIncidentWithInvalidUser_whenDeleteRequest_thenBadRequest() {
        //Given
        createIncident();
        IncidentReport incidentReport = new IncidentReport.Builder()
                .assignee(VALID_USERNAME_1).title(VALID_INCIDENT_TITLE).build();
        Response response = deleteIncidentRequest(incidentReport,INVALID_USERNAME,INVALID_INCIDENT_ID);
        //Then
        response.then().statusCode(SC_BAD_REQUEST).body("details", Matchers.contains("User provided does not exist"));
    }

    @Test
    public void givenIncidentWithInvalidIncidentId_whenDeleteRequest_thenBadRequest() {

        //Given
        createIncident();
        IncidentReport incidentReport = new IncidentReport.Builder()
                .assignee(VALID_USERNAME_1).title(VALID_INCIDENT_TITLE).build();
        Response response = deleteIncidentRequest(incidentReport,VALID_USERNAME,INVALID_INCIDENT_ID);
        //Then
        response.then().statusCode(SC_BAD_REQUEST).body("details", Matchers.contains("Incident provided does not exist"));
    }

    @Test
    public void givenIncidentWithInvalidPermission_whenDeleteRequest_thenBadRequest() {

        //Given
        createIncident();
        int incidentId= (int)((LinkedHashMap<String,Object>)getHeader()
                //When
                .when().get("/incidents").getBody().jsonPath().getList("content").get(0)).get("incidentId");

        IncidentReport incidentReport = new IncidentReport.Builder()
                .assignee(VALID_USERNAME_1).title(VALID_INCIDENT_TITLE).build();
        Response response = deleteIncidentRequest(incidentReport,VALID_USERNAME_1,incidentId);

        //Then
        response.then().statusCode(SC_BAD_REQUEST).body("details", Matchers.contains(String.format(
                "Current user %s does not have the required permission to delete the report", VALID_USERNAME_1)));
    }

    @Test
    public void givenIncidentWithValidPermissionToDelete_whenDeleteRequest_thenOk() {

        //Given
        createIncidentWithDifferentCreatorAndAssignee();
        int incidentId= (int)((LinkedHashMap<String,Object>)getHeader()
                //When
                .when().get("/incidents").getBody().jsonPath().getList("content").get(0)).get("incidentId");

        IncidentReport incidentReport = new IncidentReport.Builder()
                .assignee(VALID_USERNAME_1).title(VALID_INCIDENT_TITLE).build();
        Response response = deleteIncidentRequest(incidentReport,VALID_USERNAME,incidentId);

        //Then
        response.then().statusCode(SC_ACCEPTED).body("message",Matchers.is("Incident Report Deleted"));
    }

    private Response patchIncidentRequest(IncidentReport incidentReport,String user,int incidentId) {
        return getHeader().body(incidentReport)
                //When
                .when().patch(String.format("/incident?incident_id=%d&loggedin_user=%s",incidentId,user));
    }

    private Response deleteIncidentRequest(IncidentReport incidentReport,String user,int incidentId) {
        return getHeader().body(incidentReport)
                //When
                .when().delete(String.format("/incident?incident_id=%d&loggedin_user=%s",incidentId,user));
    }

    private Response postIncidentRequest(IncidentReport incidentReport) {
        return getHeader().body(incidentReport)
                //When
                .when().post("/incident");
    }

    private void createIncident() {
        IncidentReport incidentReport = new IncidentReport.Builder().creator(VALID_USERNAME)
                .assignee(VALID_USERNAME).title(VALID_INCIDENT_TITLE).build();
        postIncidentRequest(incidentReport);
    }

    private void createIncidentWithDifferentCreatorAndAssignee() {
        IncidentReport incidentReport = new IncidentReport.Builder().creator(VALID_USERNAME)
                .assignee(VALID_USERNAME_1).title(VALID_INCIDENT_TITLE).build();
        postIncidentRequest(incidentReport);
    }

    @AfterTestClass
    public void cleanUsers() {
        userRepository.deleteAll();
    }

    @After
    public void cleanup() {
        incidentRepository.deleteAll();
    }
}
