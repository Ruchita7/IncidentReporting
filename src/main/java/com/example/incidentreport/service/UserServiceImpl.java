package com.example.incidentreport.service;

import com.example.incidentreport.contract.User;
import com.example.incidentreport.model.UserDetail;
import com.example.incidentreport.repository.UserRepository;
import com.example.incidentreport.utils.BadRequestException;
import com.example.incidentreport.utils.ConflictException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void createUser(User user) {
      if(StringUtils.isEmpty(user.getUserName())) {
            throw new BadRequestException("Mandatory fields are missing");
        }
        if(userRepository.existsByUserNameIgnoreCase(user.getUserName())) {
            throw new ConflictException(String.format("User name '%s' already exists",user.getUserName()));
        }
        userRepository.save(convertUserDetail(user));
    }

    @Override
    public void updateUser(String userName, User user) {
        Optional<UserDetail> optionalUser = userRepository.findByUserName(userName);
        if (optionalUser.isPresent()) {
            UserDetail existingUser = optionalUser.get();
            userRepository.save(new UserDetail.Builder()
                    .firstName(user.getFirstName())
                    .userId(existingUser.getUserId())
                    .lastName(user.getLastName())
                    .userName(user.getUserName()).build());
        } else {
            throw new BadRequestException("User does not exists");
        }
    }

    @Override
    public void deleteUser(String userName) {
        Optional<UserDetail> optionalUser = userRepository.findByUserName(userName);
        if (optionalUser.isPresent()) {
            UserDetail existingUser = optionalUser.get();
            userRepository.delete(existingUser);
        } else {
            throw new BadRequestException("User does not exists");
        }
    }

    @Override
    public User getUserByUserName(String userName) {
        Optional<UserDetail> optionalUser = userRepository.findByUserName(userName);
        if(optionalUser.isPresent()) {
            return convertUser(optionalUser.get());
        }
        throw  new BadRequestException("User does not exist");
    }



    @Override
    public List<User> getAllUsers() {
       List<UserDetail> userDetailsList = userRepository.findAll();
       List<User> userList = new ArrayList<>();
       for(UserDetail userDetail: userDetailsList) {
           userList.add(convertUser(userDetail));
       }
       return  userList;
    }

    private UserDetail convertUserDetail(User user) {
        return new UserDetail.Builder().userName(user.getUserName()).
                lastName(user.getLastName()).firstName(user.getFirstName()).build();
    }

    private User convertUser(UserDetail userDetail) {
        return new User.Builder().userId(userDetail.getUserId()).userName(userDetail.getUserName()).
                lastName(userDetail.getLastName()).firstName(userDetail.getFirstName()).build();
    }
}
