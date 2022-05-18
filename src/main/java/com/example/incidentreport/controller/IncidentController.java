package com.example.incidentreport.controller;

import com.example.incidentreport.contract.IncidentReport;
import com.example.incidentreport.contract.ResponseResult;
import com.example.incidentreport.service.IncidentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class IncidentController {

    private final IncidentService incidentService;

    @Autowired
    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }


    @Operation(summary = "Create an incident report")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Incident created"),
            @ApiResponse(responseCode = "400", description = "Missing mandatory fields," +
                    "invalid creator or assignee provided"),
            @ApiResponse(responseCode = "409", description = "Report with same title already exist, " +
                    "Assignee already having incidents in assigned state") })
    @PostMapping(value = "/incident", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseResult> createIncident(@RequestBody IncidentReport incidentReport) {
        incidentService.createIncident(incidentReport);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ResponseResult("Incident Created"));
    }

    @GetMapping(value = "/incidents")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Incident list is retrieved")})
    @Operation(summary = "Retrieve all incidents based on criteria like status")
    public Page<IncidentReport> getIncidentByStatus(@RequestParam(required=false)
                                                                        String status, Pageable pageable) {
        return incidentService.getIncidents(status,pageable);
    }

    //Assuming that title can contain spaces so providing update by id
    @PatchMapping(value = "/incident", produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Update incident based on incident id and logged in user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Incident updated"),
            @ApiResponse(responseCode = "400", description = "Invalid user provided,invalid assignee,"
                    + "invalid incident id, user not having the required permissions" ) })
    public ResponseEntity<ResponseResult> updateIncidentById(@RequestParam(name = "incident_id")
                                                              Long incidentId,
             @RequestParam(name = "loggedin_user") String currentUser,
                                                          @RequestBody IncidentReport incidentReport) {
        incidentService.updateIncident(incidentId,currentUser, incidentReport);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ResponseResult("Incident Report Updated"));
    }

    @DeleteMapping(value = "/incident", produces = APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete incident based on incident id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Incident Deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid user provided,"
                    + "invalid incident id, user not having the required permissions" ) })
    public ResponseEntity<ResponseResult> deleteIncidentById(@RequestParam(name = "incident_id")
                                                              Long incidentId,
                                                          @RequestParam(name = "loggedin_user") String currentUser) {
        incidentService.deleteIncident(incidentId,currentUser);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ResponseResult("Incident Report Deleted"));
    }

}
