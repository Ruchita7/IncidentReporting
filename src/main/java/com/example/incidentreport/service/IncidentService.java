package com.example.incidentreport.service;

import com.example.incidentreport.contract.IncidentReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IncidentService {

    void createIncident(IncidentReport incidentReport);

    Page<IncidentReport> getIncidents(String status, Pageable pageable);

    void updateIncident(Long incidentId, String currentUser, IncidentReport incidentReport);

    void deleteIncident(Long incidentId, String currentUser);
}
