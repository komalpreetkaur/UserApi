package co.zip.candidate.userapi.controller;

import co.zip.candidate.userapi.entity.Account;
import co.zip.candidate.userapi.entity.AccountModel;
import co.zip.candidate.userapi.entity.User;
import co.zip.candidate.userapi.repository.AccountRepository;
import co.zip.candidate.userapi.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountApiController.class)
public class AccountApiControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AccountRepository accountRepository;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void shouldCreateAccount() throws Exception {
        when(userRepository.findByEmailId(Mockito.anyString())).thenReturn(createMockUser(1,"testuser1@test.com"));
        when(accountRepository.save(Mockito.any(Account.class))).thenReturn(createMockAccount(1,"testuser1@test.com",500));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createMockAccountModel()));

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void shouldNotCreateAccountWhenUserNotFound() throws Exception {
        when(userRepository.findByEmailId(Mockito.anyString())).thenReturn(null);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createMockAccountModel()));

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("User is not found for emailId")));
    }

    @Test
    void shouldNotCreateAccountWhenBalanceLowerThan1000() throws Exception {
        User user = new User(
                Long.valueOf(1),
                "testuser",
                "testuser1@test.com",
                BigDecimal.valueOf(2000.54),
                BigDecimal.valueOf(2000.50));

        when(userRepository.findByEmailId(Mockito.anyString())).thenReturn(user);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createMockAccountModel()));

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Due to balance less than $1000, account not created for user with id")));

    }

    @Test
    void shouldNotCreateAccountWhenAccountAlreadyExists() throws Exception {
        User user = new User(
                Long.valueOf(1),
                "testuser",
                "testuser1@test.com",
                BigDecimal.valueOf(2000.54),
                BigDecimal.valueOf(2000.50));

        when(userRepository.findByEmailId(Mockito.anyString())).thenReturn(user);
        when(accountRepository.findByUserId(Mockito.anyLong())).thenReturn(createMockAccount(1,"testuser1@test.com",500));

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createMockAccountModel()));

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().string(containsString("Account already exists with Id")));

    }

    @Test
    void shouldListsAccounts() throws Exception {
        List<Account> accounts = Arrays.asList(
                createMockAccount(1, "testuser1@test.com", 1000),
                createMockAccount(2, "testuser2@test.com", 500));

        String expectedList = mapper.writeValueAsString(accounts);

        when(accountRepository.findAll()).thenReturn(accounts);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/getAccounts");

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(expectedList)));
    }

    @Test
    void shouldReturnEmptyListWhenNoAccounts() throws Exception {
        when(accountRepository.findAll()).thenReturn(new ArrayList<>());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/getAccounts");

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("[]")));
    }

    private Account createMockAccount(int id, String emailId, double credit) {
        return new Account(Long.valueOf(id),
                BigDecimal.valueOf(credit),
                createMockUser(id, emailId));
    }

    private User createMockUser(int id, String emailId) {
        return new User(
                Long.valueOf(id),
                "testuser",
                emailId,
                BigDecimal.valueOf(2000.54),
                BigDecimal.valueOf(500.50));
    }

    private AccountModel createMockAccountModel() {
        return new AccountModel("testuser1@test.com", BigDecimal.valueOf(500));
    }
}
