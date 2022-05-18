package com.example.incidentreport.small;

import com.example.incidentreport.contract.User;
import com.example.incidentreport.model.UserDetail;
import com.example.incidentreport.repository.UserRepository;
import com.example.incidentreport.service.UserServiceImpl;
import com.example.incidentreport.utils.BadRequestException;
import com.example.incidentreport.utils.ConflictException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.incidentreport.util.TestConstants.VALID_FIRSTNAME;
import static com.example.incidentreport.util.TestConstants.VALID_FIRSTNAME_1;
import static com.example.incidentreport.util.TestConstants.VALID_LASTNAME;
import static com.example.incidentreport.util.TestConstants.VALID_LASTNAME_1;
import static com.example.incidentreport.util.TestConstants.VALID_USERNAME;
import static com.example.incidentreport.util.TestConstants.VALID_USERNAME_1;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    private UserServiceImpl userServiceImpl;
    private User user;
    private UserDetail userDetail;

    @Before
    public void setup() {
        userServiceImpl = new UserServiceImpl(userRepository);
        user = new User.Builder().userName(VALID_USERNAME).firstName(VALID_FIRSTNAME).lastName(VALID_LASTNAME).build();
        userDetail = new UserDetail.Builder().userName(VALID_USERNAME).firstName(VALID_FIRSTNAME).
                lastName(VALID_LASTNAME).build();
    }

    @Test
    public void givenUserNotExists_whenCreateUser_thenUserIsCreated() {
        when(userRepository.existsByUserNameIgnoreCase(any())).thenReturn(false);
        userServiceImpl.createUser(user);
        verify(userRepository,times(1)).save(any());
    }

    @Test(expected = ConflictException.class)
    public void givenUserExists_whenCreateUser_throwConflictException() {
        when(userRepository.existsByUserNameIgnoreCase(any())).thenReturn(true);
        userServiceImpl.createUser(user);
        verify(userRepository,times(0)).save(any());
    }

    @Test(expected = BadRequestException.class)
    public void givenUserNameEmpty_whenCreateUser_throwBadRequestException() {
        User user1 = new User.Builder().firstName(VALID_FIRSTNAME).lastName(VALID_LASTNAME).build();
        userServiceImpl.createUser(user1);
    }

    @Test(expected = BadRequestException.class)
    public void givenUserNotExists_whenUpdateUser_thenBadRequest() {
        when(userRepository.findByUserName(any())).thenReturn(Optional.empty());
        userServiceImpl.updateUser(VALID_USERNAME,user);
    }

    @Test
    public void givenUserExists_whenUpdateUser_thenUserUpdated() {
        when(userRepository.findByUserName(any())).thenReturn(Optional.of(userDetail));
        userServiceImpl.updateUser(VALID_USERNAME,user);
        verify(userRepository,times(1)).save(any());
    }

    @Test(expected = BadRequestException.class)
    public void givenUserNotExists_whenDeleteUser_thenBadRequest() {
        when(userRepository.findByUserName(any())).thenReturn(Optional.empty());
        userServiceImpl.deleteUser(VALID_USERNAME);
    }

    @Test
    public void givenUserExists_whenDeleteUser_thenUserDeleted() {
        when(userRepository.findByUserName(any())).thenReturn(Optional.of(userDetail));
        userServiceImpl.deleteUser(VALID_USERNAME);
        verify(userRepository,times(1)).delete(any());
    }

    @Test
    public void givenUserExists_whenGetUser_returnUser() {
        when(userRepository.findByUserName(any())).thenReturn(Optional.of(userDetail));
        User userObj = userServiceImpl.getUserByUserName(VALID_USERNAME);
        assert(userObj.getUserName().equals(VALID_USERNAME));
        assert(userObj.getFirstName()).equals(VALID_FIRSTNAME);
    }


    @Test
    public void givenUsers_whenGetAllUser_returnUsers() {
        UserDetail userDetail1 = new UserDetail.Builder().userName(VALID_USERNAME_1).firstName(VALID_FIRSTNAME_1).
                lastName(VALID_LASTNAME_1).build();
        List<UserDetail> userDetails = new ArrayList<>();
        userDetails.add(userDetail);
        userDetails.add(userDetail1);

        when(userRepository.findAll()).thenReturn(userDetails);
        List<User> users = userServiceImpl.getAllUsers();
        assert(users.get(0).getUserName().equals(VALID_USERNAME));
        assert(users.get(0).getFirstName()).equals(VALID_FIRSTNAME);
        assert(users.get(1).getUserName().equals(VALID_USERNAME_1));
        assert(users.get(1).getLastName()).equals(VALID_LASTNAME_1);
    }

}
