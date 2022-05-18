package com.example.incidentreport.contract;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.io.Serializable;

/**
 * Incident contract class for API request and response
 */
@JsonDeserialize(builder = IncidentReport.Builder.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IncidentReport implements Serializable {
    private final Long incidentId;
    private final String title;
    private final String status;
    private final String assignee;
    private final String creator;

    public IncidentReport(Builder builder) {
        this.incidentId=builder.incidentId;
        this.title=builder.title;
        this.status=builder.status;
        this.assignee=builder.assignee;
        this.creator=builder.creator;
    }

    public String getTitle() {
        return title;
    }

    public String getAssignee() {
        return assignee;
    }

    public String getStatus() {
        return status;
    }

    public String getCreator() {
        return creator;
    }

    public Long getIncidentId() {
        return incidentId;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static final class Builder {
        private  String title;
        private  String status;
        private  String assignee;
        private  String creator;

        private Long incidentId;

        public Builder title(String title) {
            this.title=title;
            return this;
        }

        public Builder status(String status) {
            this.status=status;
            return this;
        }

        public Builder assignee(String assignee) {
            this.assignee=assignee;
            return this;
        }

        public Builder creator(String creator) {
            this.creator=creator;
            return this;
        }

        public Builder incidentId(Long incidentId) {
            this.incidentId=incidentId;
            return this;
        }

        public IncidentReport build() {
            return new IncidentReport(this);
        }

    }
}
