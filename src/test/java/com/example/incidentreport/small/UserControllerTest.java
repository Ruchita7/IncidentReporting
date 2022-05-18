package com.example.incidentreport.small;


import com.example.incidentreport.contract.User;
import com.example.incidentreport.controller.UserController;
import com.example.incidentreport.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static com.example.incidentreport.util.TestConstants.VALID_FIRSTNAME;
import static com.example.incidentreport.util.TestConstants.VALID_FIRSTNAME_1;
import static com.example.incidentreport.util.TestConstants.VALID_LASTNAME;
import static com.example.incidentreport.util.TestConstants.VALID_LASTNAME_1;
import static com.example.incidentreport.util.TestConstants.VALID_USERNAME;
import static com.example.incidentreport.util.TestConstants.VALID_USERNAME_1;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
public class UserControllerTest {

    private UserController userController;
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    private User user;


    @Before
    public void setup() {
        userController = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        user = new User.Builder().userName(VALID_USERNAME).lastName(VALID_LASTNAME).firstName(VALID_FIRSTNAME).build();
    }

    @Test
    public void givenUser_whenPostRequest_thenUserCreated() throws Exception {
        //When
        mockMvc.perform(MockMvcRequestBuilders.post("/user").content(asJsonString(user))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                //Then
                .andExpect(status().isAccepted()).
                andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
    }

    @Test
    public void givenUser_whenPutRequest_thenUserUpdated() throws Exception {
        //When
        mockMvc.perform(MockMvcRequestBuilders.put("/user/{userName}", VALID_USERNAME).content(asJsonString(user))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                //Then
                .andExpect(status().isAccepted()).
                andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
    }

    @Test
    public void givenUser_whenDeleteRequest_thenUserDeleted() throws Exception {
        //When
        mockMvc.perform(MockMvcRequestBuilders.delete("/user/{userName}", VALID_USERNAME))
                //Then
                .andExpect(status().isAccepted()).
                andExpect(MockMvcResultMatchers.jsonPath("$.message").exists());
    }

    @Test
    public void givenUser_whenGetRequest_thenReturnUser() throws Exception {
        //When
        when(userService.getUserByUserName(VALID_USERNAME)).thenReturn(user);
        mockMvc.perform(MockMvcRequestBuilders.get("/user/{userName}", VALID_USERNAME))
                .andDo(print())
                //Then
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userName").value(VALID_USERNAME))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value(VALID_FIRSTNAME))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value(VALID_LASTNAME));
    }


    @Test
    public void givenUser_whenGetRequest_thenReturnAllUsers() throws Exception {
        //Given
        User user2 = new User.Builder().userName(VALID_USERNAME_1).firstName(VALID_FIRSTNAME_1)
                .lastName(VALID_LASTNAME_1).build();
        List<User> userList = new ArrayList<>();
        userList.add(user);
        userList.add(user2);
        //When
        when(userService.getAllUsers()).thenReturn(userList);
        mockMvc.perform(MockMvcRequestBuilders.get("/users", VALID_USERNAME))
                .andDo(print())
                //Then
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].userName").value(VALID_USERNAME))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName").value(VALID_FIRSTNAME))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].lastName").value(VALID_LASTNAME))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].userName").value(VALID_USERNAME_1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].firstName").value(VALID_FIRSTNAME_1))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].lastName").value(VALID_LASTNAME_1));
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
