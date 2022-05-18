package com.example.incidentreport.small;

import com.example.incidentreport.contract.IncidentReport;
import com.example.incidentreport.model.Incident;
import com.example.incidentreport.model.IncidentStatus;
import com.example.incidentreport.model.UserDetail;
import com.example.incidentreport.repository.IncidentRepository;
import com.example.incidentreport.repository.UserRepository;
import com.example.incidentreport.service.IncidentServiceImpl;
import com.example.incidentreport.utils.BadRequestException;
import com.example.incidentreport.utils.ConflictException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.incidentreport.util.TestConstants.DEFAULT_PAGE_NUMBER;
import static com.example.incidentreport.util.TestConstants.DEFAULT_PAGE_SIZE;
import static com.example.incidentreport.util.TestConstants.INVALID_INCIDENT_ID_LONG;
import static com.example.incidentreport.util.TestConstants.INVALID_STATUS;
import static com.example.incidentreport.util.TestConstants.INVALID_USERNAME;
import static com.example.incidentreport.util.TestConstants.VALID_FIRSTNAME;
import static com.example.incidentreport.util.TestConstants.VALID_INCIDENT_ID_LONG;
import static com.example.incidentreport.util.TestConstants.VALID_INCIDENT_TITLE;
import static com.example.incidentreport.util.TestConstants.VALID_INCIDENT_TITLE_1;
import static com.example.incidentreport.util.TestConstants.VALID_LASTNAME;
import static com.example.incidentreport.util.TestConstants.VALID_USERNAME;
import static com.example.incidentreport.util.TestConstants.VALID_USERNAME_1;
import static com.example.incidentreport.util.TestConstants.VALID_USER_ID;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IncidentServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private IncidentRepository incidentRepository;

    private IncidentServiceImpl incidentService;
    private IncidentReport incidentReport;
    private Incident incident;
    private UserDetail userDetail;


    @Before
    public void setup() {
        incidentService = new IncidentServiceImpl(incidentRepository,userRepository);
        incidentReport = new IncidentReport.Builder().creator(VALID_USERNAME).title(VALID_INCIDENT_TITLE).build();
        userDetail = new UserDetail.Builder().userId(VALID_USER_ID).userName(VALID_USERNAME).firstName(VALID_FIRSTNAME).
                lastName(VALID_LASTNAME).build();
        incident = new Incident.Builder().creator(userDetail).title(VALID_INCIDENT_TITLE)
                .statusId(IncidentStatus.ASSIGNED.getValue()).assignee(userDetail).build();
    }

    @Test(expected = BadRequestException.class)
    public void givenIncidentTitleEmpty_whenCreateIncident_throwBadRequestException() {
        IncidentReport incidentReport = new IncidentReport.Builder().creator(VALID_USERNAME).build();
        incidentService.createIncident(incidentReport);
    }

    @Test(expected = BadRequestException.class)
    public void givenIncidentCreatorEmpty_whenCreateIncident_throwBadRequestException() {
        IncidentReport incidentReport = new IncidentReport.Builder().title(VALID_INCIDENT_TITLE).build();
        incidentService.createIncident(incidentReport);
    }

    @Test(expected = ConflictException.class)
    public void givenIncidentTitleAlreadyExists_whenCreateIncident_throwConflict() {
        when(incidentRepository.findByTitle(any())).thenReturn(Optional.of(incident));
        incidentService.createIncident(incidentReport);
    }

    @Test(expected = BadRequestException.class)
    public void givenIncidentCreatorAssigneeInvalid_whenCreateIncident_throwBadRequestException() {
        IncidentReport incidentReport = new IncidentReport.Builder().title(VALID_INCIDENT_TITLE)
                .creator(INVALID_USERNAME).assignee(INVALID_USERNAME).build();
        when(incidentRepository.findByTitle(any())).thenReturn(Optional.empty());
        when(userRepository.findByUserName(any())).thenReturn(Optional.empty());
        incidentService.createIncident(incidentReport);
    }

    @Test(expected = BadRequestException.class)
    public void givenIncidentInvalidStatus_whenCreateIncident_throwBadRequestException() {
        IncidentReport incidentReport = new IncidentReport.Builder().title(VALID_INCIDENT_TITLE)
                .creator(VALID_USERNAME).assignee(VALID_USERNAME).status(INVALID_STATUS).build();
        when(incidentRepository.findByTitle(any())).thenReturn(Optional.empty());
        when(userRepository.findByUserName(any())).thenReturn(Optional.of(userDetail));
        incidentService.createIncident(incidentReport);
    }

    @Test(expected = ConflictException.class)
    public void givenAssigneeHasAssignedTasks_whenCreateIncident_throwConflictException() {
        IncidentReport incidentReport = new IncidentReport.Builder().title(VALID_INCIDENT_TITLE)
                .creator(VALID_USERNAME).assignee(VALID_USERNAME).status(IncidentStatus.ASSIGNED.getName()).build();
        when(incidentRepository.findByTitle(any())).thenReturn(Optional.empty());
        when(userRepository.findByUserName(any())).thenReturn(Optional.of(userDetail));
        List<Incident> incidentList = new ArrayList<>();
        incidentList.add(incident);
        when(incidentRepository.findByAssigneeAndStatusId(any(),any())).thenReturn(incidentList);
        incidentService.createIncident(incidentReport);
    }

    @Test
    public void givenIncident_whenCreateIncident_incidentCreated() {
        IncidentReport incidentReport = new IncidentReport.Builder().title(VALID_INCIDENT_TITLE)
                .creator(VALID_USERNAME).assignee(VALID_USERNAME).status(IncidentStatus.ASSIGNED.getName()).build();
        when(incidentRepository.findByTitle(any())).thenReturn(Optional.empty());
        when(userRepository.findByUserName(any())).thenReturn(Optional.of(userDetail));
        when(incidentRepository.findByAssigneeAndStatusId(any(),any())).thenReturn(emptyList());
        incidentService.createIncident(incidentReport);
        verify(incidentRepository,times(1)).save(any());
    }

    @Test
    public void givenIncidents_whenGet_incidentListReturned() {
        PageRequest pageRequest = PageRequest.of(DEFAULT_PAGE_NUMBER,DEFAULT_PAGE_SIZE);
        Incident incident2 = new Incident.Builder().creator(userDetail).title(VALID_INCIDENT_TITLE_1)
                .statusId(IncidentStatus.ASSIGNED.getValue()).assignee(userDetail).build();
        List<Incident> incidentList = new ArrayList<>();
        incidentList.add(incident);
        incidentList.add(incident2);
        when(incidentRepository.findIncidentsByStatusId(anyInt())).thenReturn(incidentList);
        Page<IncidentReport> incidents = incidentService.getIncidents(IncidentStatus.ASSIGNED.getName(), pageRequest);
        assertEquals(incidents.getContent().size(),2);
    }

    @Test(expected = BadRequestException.class)
    public void givenInvalidUser_whenUpdateIncident_throwBadRequestException() {
        when(userRepository.findByUserName(any())).thenReturn(Optional.empty());
        incidentService.updateIncident(VALID_INCIDENT_ID_LONG,VALID_USERNAME,incidentReport);
    }

    @Test(expected = BadRequestException.class)
    public void givenInvalidStatus_whenUpdateIncident_throwBadRequestException() {
        when(userRepository.findByUserName(any())).thenReturn(Optional.of(userDetail));
        IncidentReport incidentReport1 = new  IncidentReport.Builder().creator(VALID_USERNAME)
                .title(VALID_INCIDENT_TITLE).status(INVALID_STATUS).build();
        incidentService.updateIncident(VALID_INCIDENT_ID_LONG,VALID_USERNAME,incidentReport1);
    }

    @Test(expected = BadRequestException.class)
    public void givenIncidentAssigneeInvalid_whenUpdateIncident_throwBadRequestException() {
        when(userRepository.findByUserName(any())).thenReturn(Optional.of(userDetail));
        IncidentReport incidentReport = new IncidentReport.Builder().title(VALID_INCIDENT_TITLE)
                .assignee(INVALID_USERNAME).build();
        when(userRepository.findByUserName(any())).thenReturn(Optional.empty());
        incidentService.updateIncident(VALID_INCIDENT_ID_LONG,VALID_USERNAME, incidentReport);
    }

    @Test(expected = BadRequestException.class)
    public void givenInvalidIncidentId_whenUpdateIncident_throwBadRequestException() {
        when(userRepository.findByUserName(any())).thenReturn(Optional.of(userDetail));
        IncidentReport incidentReport = new IncidentReport.Builder().title(VALID_INCIDENT_TITLE)
               .assignee(VALID_USERNAME).build();
        when(incidentRepository.findIncidentsByIncidentId(anyLong())).thenReturn(Optional.empty());
        incidentService.updateIncident(INVALID_INCIDENT_ID_LONG,VALID_USERNAME, incidentReport);
    }

    @Test
    public void givenValidUserUpdatingStatus_whenUpdateIncident_throwBadRequestException() {
        when(userRepository.findByUserName(any())).thenReturn(Optional.of(userDetail));
        IncidentReport incidentReport = new IncidentReport.Builder().title(VALID_INCIDENT_TITLE)
                .assignee(VALID_USERNAME).build();
        when(incidentRepository.findIncidentsByIncidentId(anyLong())).thenReturn(Optional.of(incident));
        incidentService.updateIncident(VALID_INCIDENT_ID_LONG,VALID_USERNAME, incidentReport);
        verify(incidentRepository,times(1)).save(any());
    }

    @Test(expected = BadRequestException.class)
    public void givenInvalidUser_whenDeleteIncident_throwBadRequestException() {
        when(userRepository.findByUserName(any())).thenReturn(Optional.empty());
        incidentService.deleteIncident(VALID_INCIDENT_ID_LONG,VALID_USERNAME);
    }

    @Test(expected = BadRequestException.class)
    public void givenInvalidIncidentId_whenDeleteIncident_throwBadRequestException() {
        when(userRepository.findByUserName(any())).thenReturn(Optional.of(userDetail));
        when(incidentRepository.findIncidentsByIncidentId(anyLong())).thenReturn(Optional.empty());
        incidentService.deleteIncident(INVALID_INCIDENT_ID_LONG,VALID_USERNAME);
    }

    @Test(expected = BadRequestException.class)
    public void givenIncidentAssigneeInvalid_whenDeleteIncident_throwBadRequestException() {
        when(userRepository.findByUserName(any())).thenReturn(Optional.of(userDetail));
        incidentService.deleteIncident(VALID_INCIDENT_ID_LONG,VALID_USERNAME_1);
    }

    @Test
    public void givenIncident_whenDeleteIncident_thenOK() {
        when(userRepository.findByUserName(any())).thenReturn(Optional.of(userDetail));
        when(incidentRepository.findIncidentsByIncidentId(anyLong())).thenReturn(Optional.of(incident));
        incidentService.deleteIncident(VALID_INCIDENT_ID_LONG,VALID_USERNAME_1);
    }

}
