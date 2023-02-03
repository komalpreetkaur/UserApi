package co.zip.candidate.userapi.controller;

import co.zip.candidate.userapi.entity.Account;
import co.zip.candidate.userapi.entity.AccountModel;
import co.zip.candidate.userapi.entity.User;
import co.zip.candidate.userapi.exception.AccountNotCreatedException;
import co.zip.candidate.userapi.exception.ResourceExistsException;
import co.zip.candidate.userapi.exception.UserNotFoundException;
import co.zip.candidate.userapi.repository.AccountRepository;
import co.zip.candidate.userapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
public class AccountApiController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/account")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void createAccount(@Valid @RequestBody AccountModel accountModel) {
        User user = userRepository.findByEmailId(accountModel.getUserEmailId());
        if (user == null) {
            throw new UserNotFoundException("User is not found for emailId " + accountModel.getUserEmailId());
        }

        Account account = accountRepository.findByUserId(user.getId());
        if (account != null) {
            throw new ResourceExistsException("Account already exists with Id " + account.getId());
        }

        BigDecimal difference = user.getMonthlySalary().subtract(user.getMonthlyExpenses());
        if (difference.compareTo(BigDecimal.valueOf(1000)) == -1) {
            throw new AccountNotCreatedException(user.getId());
        }

        Account newAccount = new Account();
        newAccount.setUser(user);
        newAccount.setCredit(accountModel.getCredit());
        accountRepository.save(newAccount);
    }

    @GetMapping("/getAccounts")
    public List<Account> listAccounts() {
        return accountRepository.findAll();
    }
}
