package account.service;

import account.model.User;
import account.model.UserRequest;
import account.model.UserResponse;
import account.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AccountService {

    public final UserRepository userRepository;
    public AccountService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse createUser(UserRequest userRequest) {

        if (userRepository.isUserExist(userRequest.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User exist!");
        }
        User user = new User(userRequest.name(), userRequest.lastname(), userRequest.email(), userRequest.password());

        userRepository.addUser(user);

        return toUserResponse(user);
    }

    public UserResponse toUserResponse(User user) {
        return new UserResponse(user.getName(), user.getLastname(), user.getEmail());
    }
}
