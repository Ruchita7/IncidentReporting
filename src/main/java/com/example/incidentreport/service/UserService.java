package com.example.incidentreport.service;

import com.example.incidentreport.contract.User;

import java.util.List;

public interface UserService {
    void createUser(User user);

    void updateUser(String userName, User user);

    void deleteUser(String userName);

    User getUserByUserName(String userName);

    List<User> getAllUsers();
}
