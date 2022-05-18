package com.example.incidentreport.small;

import com.example.incidentreport.contract.IncidentReport;
import com.example.incidentreport.controller.IncidentController;
import com.example.incidentreport.model.IncidentStatus;
import com.example.incidentreport.service.IncidentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static com.example.incidentreport.util.TestConstants.DEFAULT_PAGE_NUMBER;
import static com.example.incidentreport.util.TestConstants.DEFAULT_PAGE_SIZE;
import static com.example.incidentreport.util.TestConstants.VALID_INCIDENT_ID;
import static com.example.incidentreport.util.TestConstants.VALID_INCIDENT_TITLE;
import static com.example.incidentreport.util.TestConstants.VALID_USERNAME;
import static com.example.incidentreport.util.TestConstants.VALID_USERNAME_1;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class IncidentControllerTest {



    private IncidentController incidentController;
    private MockMvc mockMvc;

    @MockBean
    private IncidentService incidentService;
    private IncidentReport incidentReport;


    @Before
    public void setup() {
        incidentController = new IncidentController(incidentService);
        mockMvc = MockMvcBuilders.standaloneSetup(incidentController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();
        incidentReport = new IncidentReport.Builder().title(VALID_INCIDENT_TITLE).creator(VALID_USERNAME).build();
    }

    @Test
    public void givenIncident_whenPostRequest_thenIncidentCreated() throws Exception {
        //When
        mockMvc.perform(MockMvcRequestBuilders.post("/incident").content(asJsonString(incidentReport))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                //Then
                .andExpect(status().isAccepted()).
                andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
    }

    @Test
    public void givenIncident_whenPatchRequest_thenIncidentUpdated() throws Exception {
        //When
        mockMvc.perform(MockMvcRequestBuilders.patch("/incident?incident_id="+VALID_INCIDENT_ID+
                                "&loggedin_user="+VALID_USERNAME)
                        .content(asJsonString(incidentReport))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                //Then
                .andExpect(status().isAccepted()).
                andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
    }

  @Test
    public void givenIncident_whenDeleteRequest_thenIncidenteleted() throws Exception {
        //When
        mockMvc.perform(MockMvcRequestBuilders.delete("/incident?incident_id="+VALID_INCIDENT_ID+
                        "&loggedin_user="+VALID_USERNAME))
                //Then
                .andExpect(status().isAccepted()).
                andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
    }



    @Test
    public void givenIncidents_whenGetRequest_thenReturnAllIncidents() throws Exception {
        //Given
       IncidentReport incidentReport2 = new IncidentReport.Builder().title(VALID_INCIDENT_TITLE)
               .creator(VALID_USERNAME).assignee(VALID_USERNAME_1).build();
        List<IncidentReport> incidentReports = new ArrayList<>();
        incidentReports.add(incidentReport);
        incidentReports.add(incidentReport2);
        PageRequest pageRequest = PageRequest.of(DEFAULT_PAGE_NUMBER,DEFAULT_PAGE_SIZE);
        //When
        Page<IncidentReport> incidentReportPage = new PageImpl<>(incidentReports,pageRequest,incidentReports.size());
        when(incidentService.getIncidents(IncidentStatus.ASSIGNED.getName(),pageRequest)).thenReturn(incidentReportPage);
        mockMvc.perform(MockMvcRequestBuilders.get("/incidents?page=0&size=10"))
                .andDo(print())
                //Then
                .andExpect(status().isOk());
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
