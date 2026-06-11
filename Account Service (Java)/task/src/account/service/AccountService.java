package account.service;

import account.model.*;
import account.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AccountService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public AccountService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse createUser(UserRequest userRequest) {

        if (userRepository.existsByEmailIgnoreCase(userRequest.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User exist!");
        }

        if (isPasswordInBreached(userRequest.password())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The password is in the hacker's database!");
        }

        User user = fromUserRequestToUser(userRequest);
        userRepository.save(user);

        return toUserResponse(user);
    }

    public UserResponse findUser(String email) {

        if (!userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        return toUserResponse(userRepository.findByEmailIgnoreCase(email).orElseThrow());
    }

    public UserResponse toUserResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getLastname(), user.getEmail());
    }

    public User fromUserRequestToUser(UserRequest userRequest) {
        return new User(
                userRequest.name(),
                userRequest.lastname(),
                userRequest.email().toLowerCase(),
                passwordEncoder.encode(userRequest.password())
        );
    }

    public List<String> breachedPasswords() {
        return List.of(
                "PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
                "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
                "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember"
        );
    }

    public boolean passwordMatches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public boolean isPasswordInBreached(String password) {
        return breachedPasswords().contains(password);
    }

    public PasswordResponse setNewPassword(PasswordRequest passwordRequest, String email) {

        User user = userRepository.findByEmailIgnoreCase(email).orElseThrow();

        if (passwordMatches(passwordRequest.new_password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The passwords must be different!");
        }

        if (isPasswordInBreached(passwordRequest.new_password())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The password is in the hacker's database!");
        }

        user.setPassword(passwordEncoder.encode(passwordRequest.new_password()));
        userRepository.save(user);

        return new PasswordResponse(email, "The password has been updated successfully");
    }
}
