package com.example.incidentreport.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Incident entity to be persisted in MySQL DB
 */
@Entity
public class Incident {

    @Id
    @GeneratedValue
    private Long incidentId;
    private String title;
    private Integer statusId;

    @ManyToOne
    @JoinColumn(name="assignee_id")
    private UserDetail assignee;

    @ManyToOne
    @JoinColumn(name="user_id")
    private UserDetail creator;

    public Incident() {}

    public Long getIncidentId() {
        return incidentId;
    }

    public String getTitle() {
        return title;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public UserDetail getAssignee() {
        return assignee;
    }

    public UserDetail getCreator() {
        return creator;
    }

    public Incident(Builder builder) {
        this.incidentId=builder.incidentId;
        this.statusId=builder.statusId;
        this.title=builder.title;
        this.assignee=builder.assignee;
        this.creator=builder.creator;
    }

    public static final class Builder {
        private  Long incidentId;
        private  String title;
        private  Integer statusId;
        private UserDetail assignee;
        private UserDetail creator;

        public Builder incidentId(Long incidentId) {
            this.incidentId=incidentId;
            return this;
        }

        public Builder title(String title) {
            this.title=title;
            return this;
        }

        public Builder statusId(Integer statusId) {
            this.statusId=statusId;
            return this;
        }

        public Builder assignee(UserDetail assignee) {
            this.assignee=assignee;
            return this;
        }

        public Builder creator(UserDetail creator) {
            this.creator = creator;
            return this;
        }

        public Incident build()    {
            return new Incident(this);
        }
    }

}
