package com.example.incidentreport.contract;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * User request contract class
 */
@JsonDeserialize(builder = User.Builder.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    private Long userId;
    private String userName;
    private String firstName;
    private String lastName;

    public User() {
    }

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

    public User(User.Builder builder) {
        this.userId = builder.userId;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.userName = builder.userName;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static final class Builder {
        private Long userId;
        private String userName;
        private String firstName;
        private String lastName;

        public User.Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public User.Builder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public User.Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public User.Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

}
