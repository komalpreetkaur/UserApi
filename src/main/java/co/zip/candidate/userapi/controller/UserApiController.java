package co.zip.candidate.userapi.controller;

import co.zip.candidate.userapi.entity.User;
import co.zip.candidate.userapi.exception.ResourceExistsException;
import co.zip.candidate.userapi.exception.UserNotFoundException;
import co.zip.candidate.userapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
public class UserApiController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/user")
    @ResponseStatus(value = HttpStatus.CREATED)
    public void createUser(@Valid @RequestBody User user) {
        if(userRepository.findByEmailId(user.getEmailId()) != null) {
            throw new ResourceExistsException("User already exists with emailId " + user.getEmailId());
        }
        userRepository.save(user);
    }

    @GetMapping("/getUsers")
    public List<User> listUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/getUser")
    public User getUser(@RequestParam String emailId) {
        User user = userRepository.findByEmailId(emailId);
        if(user == null) {
            throw new UserNotFoundException("User is not found for emailId " + emailId);
        }
        return user;
    }

    @GetMapping("/getUser/{id}")
    public User getUser(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        }
        throw new UserNotFoundException("User is not found for the id " + id);
    }
}
