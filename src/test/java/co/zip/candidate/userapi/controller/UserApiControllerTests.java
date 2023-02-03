package co.zip.candidate.userapi.controller;

import co.zip.candidate.userapi.entity.User;
import co.zip.candidate.userapi.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserApiController.class)
public class UserApiControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void shouldCreateUser() throws Exception {
        User user = createMockUser(1,"testuser@test.com");

        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user));

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void shouldNotCreateUserWhenUserAlreadyExists() throws Exception {
        User user = createMockUser(1,"testuser@test.com");

        when(userRepository.findByEmailId(Mockito.anyString())).thenReturn(user);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user));

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("User already exists with emailId")));
    }

    @Test
    void shouldNotCreateUserWhenValidationFails() throws Exception {
        User user = new User();
        user.setMonthlySalary(BigDecimal.valueOf(-2000.54));
        user.setMonthlyExpenses(BigDecimal.valueOf(-500.50));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(user));

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldListsUser() throws Exception {
        List<User> users = Arrays.asList(
                createMockUser(1, "testuser1@test.com"),
                createMockUser(2, "testuser2@test.com"));
        String expectedList = mapper.writeValueAsString(users);

        when(userRepository.findAll()).thenReturn(users);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/getUsers");

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(expectedList)));
    }

    @Test
    void shouldReturnEmptyListWhenNoUsers() throws Exception {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/getUsers");

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[]")));
    }

    @Test
    void shouldGetUser() throws Exception {
        User user = createMockUser(1,"testuser@test.com");

        when(userRepository.findByEmailId(Mockito.anyString())).thenReturn(user);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/getUser")
                .param("emailId", "testuser1@test.com");

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(mapper.writeValueAsString(user))));
    }

    @Test
    void shouldNotReturnUserWhenNotFound() throws Exception {

        when(userRepository.findByEmailId(Mockito.anyString())).thenReturn(null);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/getUser")
                .param("emailId", "testuser1@test.com");

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("User is not found for emailId")));;
    }

    private User createMockUser(int id, String emailId) {
        return new User(
                Long.valueOf(id),
                "testuser",
                emailId,
                BigDecimal.valueOf(2000.54),
                BigDecimal.valueOf(500.50));
    }
}
