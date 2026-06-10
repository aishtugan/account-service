package account.controller;

import account.model.UserRequest;
import account.model.UserResponse;
import account.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AccountServiceController {

    private final AccountService accountService;
    public AccountServiceController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(accountService.createUser(userRequest));
    }
}
