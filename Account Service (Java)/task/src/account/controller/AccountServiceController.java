package account.controller;

import account.model.PasswordRequest;
import account.model.PasswordResponse;
import account.model.UserRequest;
import account.model.UserResponse;
import account.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/auth/changepass")
    public ResponseEntity<PasswordResponse> changePassword(@Valid @RequestBody PasswordRequest passwordRequest, Authentication authentication) {
        return ResponseEntity.ok(accountService.setNewPassword(passwordRequest, authentication.getName()));
    }

    @GetMapping("/empl/payment")
    public ResponseEntity<UserResponse> emplPayment(Authentication authentication) {
        return ResponseEntity.ok(accountService.findUser(authentication.getName()));
    }
}
