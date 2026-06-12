package account.controller;

import account.model.*;
import account.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody UserRequest userRequest, HttpServletRequest request) {
        return ResponseEntity.ok(accountService.createUser(userRequest, request.getRequestURI()));
    }

    @PostMapping("/auth/changepass")
    public ResponseEntity<PasswordResponse> changePassword(@Valid @RequestBody PasswordRequest passwordRequest, Authentication authentication, HttpServletRequest request) {
        accountService.resetFailedAttempts(authentication.getName());
        return ResponseEntity.ok(accountService.setNewPassword(passwordRequest, authentication.getName(), request.getRequestURI()));
    }

    @PostMapping("/acct/payments")
    public ResponseEntity<PaymentResponse> uploadPayrolls(@Valid @RequestBody List<PaymentRequest> paymentRequests, Authentication authentication) {
        accountService.resetFailedAttempts(authentication.getName());
        return ResponseEntity.ok(accountService.savePayments(paymentRequests));
    }

    @PutMapping("/acct/payments")
    public ResponseEntity<PaymentResponse> updatePayrolls(@Valid @RequestBody PaymentRequest paymentRequest, Authentication authentication) {
        accountService.resetFailedAttempts(authentication.getName());
        return ResponseEntity.ok(accountService.updatePayment(paymentRequest));
    }

    @GetMapping("/empl/payment")
    public ResponseEntity<?> emplPayment( @RequestParam(required = false) String period, Authentication authentication) {
        accountService.resetFailedAttempts(authentication.getName());
        return ResponseEntity.ok(accountService.getSalary(authentication.getName(), period));
    }

    @GetMapping({"/admin/user", "/admin/user/"})
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(accountService.getAllUsers());
    }

    @DeleteMapping("/admin/user/{userEmail}")
    public ResponseEntity<UserDeletionResponse> deleteUser(@PathVariable String userEmail, Authentication authentication) {
        return ResponseEntity.ok(accountService.deleteUser(userEmail, authentication.getName(), "/api/admin/user"));
    }

    @PutMapping("/admin/user/role")
    public ResponseEntity<UserResponse> updateUserRole(@Valid @RequestBody RoleRequest roleRequest, Authentication authentication, HttpServletRequest request) {
        return ResponseEntity.ok(accountService.updateUserRole(roleRequest, authentication.getName(), request.getRequestURI()));
    }

    @PutMapping("/admin/user/access")
    public ResponseEntity<StatusResponse> updateUserAccess(@Valid @RequestBody LockUserRequest lockUserRequest, Authentication authentication, HttpServletRequest request) {
        return ResponseEntity.ok(accountService.updateUserAccess(lockUserRequest, authentication.getName(), request.getRequestURI()));
    }

    @GetMapping({"/security/events", "/security/events/"})
    public ResponseEntity<List<EventResponse>> getAllEventLogs() {
        return ResponseEntity.ok(accountService.getAllEventLogs());
    }
}
