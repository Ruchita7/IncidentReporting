package com.example.incidentreport.repository;

import com.example.incidentreport.model.Incident;
import com.example.incidentreport.model.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IncidentRepository extends JpaRepository<Incident,Long> {

    public List<Incident> findByAssigneeAndStatusId(UserDetail assignee, Integer statusId);

    public Optional<Incident> findByTitle(String title);

    public List<Incident> findIncidentsByStatusId(int statusId);

    public Optional<Incident> findIncidentsByIncidentId(Long incidentId);
}
