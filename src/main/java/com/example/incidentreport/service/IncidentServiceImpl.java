package com.example.incidentreport.service;

import com.example.incidentreport.model.Incident;
import com.example.incidentreport.contract.IncidentReport;
import com.example.incidentreport.model.IncidentStatus;
import com.example.incidentreport.model.UserDetail;
import com.example.incidentreport.repository.IncidentRepository;
import com.example.incidentreport.repository.UserRepository;
import com.example.incidentreport.utils.BadRequestException;
import com.example.incidentreport.utils.ConflictException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Service
public class IncidentServiceImpl implements IncidentService {

    private final IncidentRepository incidentRepository;

    private final UserRepository userRepository;

    @Autowired
    public IncidentServiceImpl(IncidentRepository incidentRepository, UserRepository userRepository) {
        this.incidentRepository = incidentRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void createIncident(IncidentReport incidentReport) {
        validateMandatoryFields(incidentReport);
        validateTitle(incidentReport);
        UserDetail creator = validateCreator(incidentReport);
        UserDetail assignee = validateAssignee(incidentReport);
        validateStatus(incidentReport.getStatus());
        validateAssigneeStatus(assignee);

        IncidentStatus incidentStatus = !isEmpty(incidentReport.getAssignee()) ? IncidentStatus.ASSIGNED :
                IncidentStatus.NEW;
        incidentRepository.save(new Incident.Builder().creator(creator)
                .assignee(assignee).statusId(incidentStatus.getValue())
                .title(incidentReport.getTitle()).build());
    }

    private IncidentStatus validateStatus(String status) {
        if(isNotEmpty(status)) {
            IncidentStatus incidentStatus = IncidentStatus.getIncidentStatusByName(status);
            if (isNull(incidentStatus)) {
                throw new BadRequestException("Invalid Status Provided");
            }
            return incidentStatus;
        }
        return null;
    }

    //Check if assignee already has tasks in assigned status
    private void validateAssigneeStatus(UserDetail assignee) {
        List<Incident> assignedIncidentsList =
                incidentRepository.findByAssigneeAndStatusId(assignee, IncidentStatus.ASSIGNED.getValue());
        if (CollectionUtils.isNotEmpty(assignedIncidentsList)) {
            throw new ConflictException("Assignee already has other tasks in assigned status");
        }
    }

    private UserDetail validateAssignee(IncidentReport incidentReport) {
        if (!StringUtils.isEmpty(incidentReport.getAssignee())) {
            Optional<UserDetail> assignee = userRepository.findByUserName(incidentReport.getAssignee());
            if (!assignee.isPresent()) {
                throw new BadRequestException("Invalid assignee provided");
            }
            return assignee.get();
        }
        return null;
    }

    private UserDetail validateCreator(IncidentReport incidentReport) {
        Optional<UserDetail> creator = userRepository.findByUserName(incidentReport.getCreator());
        if (!creator.isPresent()) {
            throw new BadRequestException("Invalid creator provided");
        }
        return creator.get();
    }

    private void validateTitle(IncidentReport incidentReport) {
        if (incidentRepository.findByTitle(incidentReport.getTitle()).isPresent()) {
            throw new ConflictException("Incident report by the same title already exists");
        }
    }

    private void validateMandatoryFields(IncidentReport incidentReport) {
        if (isEmpty(incidentReport.getTitle()) || isEmpty(incidentReport.getCreator())) {
            throw new BadRequestException("Missing mandatory fields");
        }
    }

    private Page<IncidentReport> getIncidentReports(List<Incident> incidentList,Pageable pageable) {
        List<IncidentReport> incidentReports = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(incidentList)) {
            for (Incident incident : incidentList) {
                IncidentReport incidentReport = new IncidentReport.Builder()
                        .title(incident.getTitle())
                        .creator(incident.getCreator().getUserName())
                        .assignee(nonNull(incident.getAssignee()) ? incident.getAssignee().getUserName():
                                StringUtils.EMPTY)
                        .status(getStatus(incident))
                        .incidentId(incident.getIncidentId())
                        .build();
                incidentReports.add(incidentReport);
            }
        }
        return new PageImpl<>(incidentReports,pageable,incidentReports.size());
    }

    private String getStatus(Incident incident) {
        return nonNull(IncidentStatus.getIncidentStatusById(incident.getStatusId())) ?
                IncidentStatus.getIncidentStatusById(incident.getStatusId()).getName() : StringUtils.EMPTY;
    }


    @Override
    public Page<IncidentReport> getIncidents(String status, Pageable pageable) {
        if(nonNull(IncidentStatus.getIncidentStatusByName(status))) {
            List<Incident> incidentList = incidentRepository.findIncidentsByStatusId(getStatusId(status));
            return getIncidentReports(incidentList, pageable);
        }
        else {
            Page<Incident> incidentPage = incidentRepository.findAll(pageable);
            return getIncidentReports(incidentPage.getContent(),pageable);
        }
    }

    @Override
    public void updateIncident(Long incidentId, String currentUser, IncidentReport incidentReport) {
        Optional<UserDetail> userDetail = validateCurrentUser(currentUser);
        IncidentStatus status = validateStatus(incidentReport.getStatus());
        UserDetail assignee = validateAssignee(incidentReport);
        UserDetail currentUserDetail = userDetail.get();

        Optional<Incident> optionalIncident = validateIncidentId(incidentId);
        Incident incident = optionalIncident.get();

        validateUserForReportStatusUpdate(currentUser, incidentReport, currentUserDetail, incident);

        if (incident.getCreator().getUserId().equals(currentUserDetail.getUserId())
                || (nonNull(incident.getAssignee()) &&
                incident.getAssignee().getUserId().equals(currentUserDetail.getUserId()))) {
            updateReport(incidentId, incidentReport, status, assignee, incident);
        }
        else {
            throw new BadRequestException(String.format(
                    "Current user %s does not have the required permission to update the report",currentUser));
        }
    }

    private void updateReport(Long incidentId, IncidentReport incidentReport,
                              IncidentStatus status, UserDetail assignee, Incident incident) {
        Incident.Builder incidentBuilder = new Incident.Builder().incidentId(incidentId).creator(incident.getCreator());
        incidentBuilder.assignee(nonNull(incidentReport.getAssignee()) ? assignee : incident.getAssignee());
        incidentBuilder.title(isNotEmpty(incidentReport.getTitle())? incidentReport.getTitle() : incident.getTitle());
        incidentBuilder.creator(incident.getCreator());
        incidentBuilder.statusId(nonNull(status) ? status.getValue() : incident.getStatusId());
        incidentRepository.save(incidentBuilder.build());
    }

    //Only the assignee can update the report status
    private void validateUserForReportStatusUpdate(String currentUser, IncidentReport incidentReport,
                                                   UserDetail currentUserDetail, Incident incident) {
        if(nonNull(incident.getAssignee())) {
            if (isNotEmpty(incidentReport.getStatus()) && !currentUserDetail.getUserId().equals(incident.getAssignee().getUserId())) {
                throw new BadRequestException(String.format(
                        "Current user %s does not have the required permission to update the report status", currentUser));
            }
        }
    }

    private Optional<UserDetail> validateCurrentUser(String currentUser) {
        Optional<UserDetail> userDetail = userRepository.findByUserName(currentUser);
        if(!userDetail.isPresent()) {
            throw new BadRequestException("User provided does not exist");
        }
        return userDetail;
    }

    private Optional<Incident> validateIncidentId(Long incidentId) {
        Optional<Incident> optionalIncident = incidentRepository.findIncidentsByIncidentId(incidentId);
        if(!optionalIncident.isPresent()) {
            throw new BadRequestException("Incident provided does not exist");
        }
        return optionalIncident;
    }

    @Override
    public void deleteIncident(Long incidentId, String currentUser) {
        Optional<UserDetail> userDetail = validateCurrentUser(currentUser);
        Optional<Incident> optionalIncident = validateIncidentId(incidentId);
        UserDetail currentUserDetail = userDetail.get();
        Incident incident = optionalIncident.get();
        if (incident.getCreator().getUserId().equals(currentUserDetail.getUserId())
                || (nonNull(incident.getAssignee()) &&
                incident.getAssignee().getUserId().equals(currentUserDetail.getUserId()))) {
            incidentRepository.delete(incident);
        }
        else {
            throw new BadRequestException(String.format(
                    "Current user %s does not have the required permission to delete the report",currentUser));
        }
    }

    private int getStatusId(String status) {
        return nonNull(IncidentStatus.getIncidentStatusByName(status)) ?
                IncidentStatus.getIncidentStatusByName(status).getValue() : Integer.MIN_VALUE;
    }
}
