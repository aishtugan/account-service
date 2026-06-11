package account.controller;

import account.model.*;
import account.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/acct/payments")
    public ResponseEntity<PaymentResponse> uploadPayrolls(@Valid @RequestBody List<PaymentRequest> paymentRequests) {
        return ResponseEntity.ok(accountService.savePayments(paymentRequests));
    }

    @PutMapping("/acct/payments")
    public ResponseEntity<PaymentResponse> updatePayrolls(@Valid @RequestBody PaymentRequest paymentRequest) {
        return ResponseEntity.ok(accountService.updatePayment(paymentRequest));
    }

    @GetMapping("/empl/payment")
    public ResponseEntity<?> emplPayment( @RequestParam(required = false) String period, Authentication authentication) {
        return ResponseEntity.ok(accountService.getSalary(authentication.getName(), period));
    }
}
