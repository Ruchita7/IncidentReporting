package com.example.incidentreport.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Entity class for storing user detail in MYSQL DB
 */
@Entity
public class UserDetail {
    @Id
    @GeneratedValue
    private Long userId;

    private String userName;

    private String firstName;
    private String lastName;

    public UserDetail() {}

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public UserDetail(Builder builder) {
        this.userId=builder.userId;
        this.firstName=builder.firstName;
        this.lastName=builder.lastName;
        this.userName = builder.userName;
    }

    public static final class Builder {
        private Long userId;
        private String userName;
        private String firstName;
        private String lastName;

        public Builder userId(Long userId) {
            this.userId=userId;
            return this;
        }

        public Builder userName(String userName) {
            this.userName=userName;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName=firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName=lastName;
            return this;
        }

        public UserDetail build() {
            return new UserDetail(this);
        }
    }

}
