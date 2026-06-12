package account.service;

import account.model.*;
import account.repository.PaymentRepository;
import account.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class AccountService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PaymentRepository paymentRepository;
    public AccountService(UserRepository userRepository, PasswordEncoder passwordEncoder, PaymentRepository paymentRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.paymentRepository = paymentRepository;
    }

    public UserResponse createUser(UserRequest userRequest) {

        if (userRepository.existsByEmailIgnoreCase(userRequest.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User exist!");
        }

        if (isPasswordInBreached(userRequest.password())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The password is in the hacker's database!");
        }

        User user = fromUserRequestToUser(userRequest);

        if (userRepository.count() == 0) { //the first user is an admin
            user.addRole(Role.ROLE_ADMINISTRATOR);
        } else {
            user.addRole(Role.ROLE_USER);
        }

        userRepository.save(user);

        return toUserResponse(user);
    }

    public UserResponse findUser(String email) {

        if (!userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        return toUserResponse(userRepository.findByEmailIgnoreCase(email).orElseThrow());
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream().map(this::toUserResponse).toList();
    }

    public UserDeletionResponse deleteUser(String userEmail) {

        if (!userRepository.existsByEmailIgnoreCase(userEmail)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }

        User user = userRepository.findByEmailIgnoreCase(userEmail).orElseThrow();

        if (user.getRoles().contains(Role.ROLE_ADMINISTRATOR)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        }
        userRepository.delete(user);
        return new UserDeletionResponse(userEmail, "Deleted successfully!");
    }

    public UserResponse updateUserRole(RoleRequest roleRequest) {

        if (!userRepository.existsByEmailIgnoreCase(roleRequest.user())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }

        try {
            Role role = Role.valueOf("ROLE_" + roleRequest.role().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found!");
        }

        User user = userRepository.findByEmailIgnoreCase(roleRequest.user()).orElseThrow();

        Role role = Role.valueOf("ROLE_" + roleRequest.role().toUpperCase());

        Set<Role> userRoles = user.getRoles();

        if (roleRequest.operation() == RoleOperation.REMOVE) {

            if (role == Role.ROLE_ADMINISTRATOR) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
            }

            if (!userRoles.contains(role)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user does not have a role!");
            }

            if (userRoles.size() == 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user must have at least one role!");
            }

            user.removeRole(role);

        } else {
            if (userRoles.contains(role)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user already has this role!");
            }

            if ((role == Role.ROLE_ADMINISTRATOR && (userRoles.contains(Role.ROLE_USER) || userRoles.contains(Role.ROLE_ACCOUNTANT)))
            || ((role == Role.ROLE_ACCOUNTANT || role == Role.ROLE_USER) && userRoles.contains(Role.ROLE_ADMINISTRATOR))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user cannot combine administrative and business roles!");
            }

            user.addRole(role);
        }

        userRepository.save(user);

        return toUserResponse(user);
    }

    public UserResponse toUserResponse(User user) {

        List<String> roles = user.getRoles().stream()
                .map(Role::name)
                .sorted()
                .toList();

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getLastname(),
                user.getEmail(),
                roles);
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

    public YearMonth parsePeriod(String period) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
            return YearMonth.parse(period, formatter);
        } catch (DateTimeParseException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong date!");
        }

    }

    public String formatPeriodFromYMtoString(YearMonth period) {
          return period.format(DateTimeFormatter.ofPattern("MMMM-yyyy", Locale.ENGLISH));
    }

    public String formatSalary(Long salary) {

        Long dollars = salary / 100;
        Long cents = salary % 100;

        return String.format("%d dollar(s) %d cent(s)", dollars, cents);
    }

    @Transactional
    public PaymentResponse savePayments(List<PaymentRequest> paymentRequests) {

        Set<String> seen = new HashSet<>();

        for (PaymentRequest paymentRequest : paymentRequests) {

            YearMonth period = parsePeriod(paymentRequest.period());
            String key = paymentRequest.employee().toLowerCase(Locale.ROOT) + ":" + period;

            if (!seen.add(key)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate payment period!");
            }

            if (paymentRepository.existsByUserEmailIgnoreCaseAndPeriod(paymentRequest.employee(), period)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate payment period!");
            }

            if (!userRepository.existsByEmailIgnoreCase(paymentRequest.employee())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee does not exist!");
            }

            paymentRepository.save(fromPaymentRequestToPayment(paymentRequest));
        }

        return new PaymentResponse("Added successfully!");
    }

    public PaymentResponse updatePayment(PaymentRequest paymentRequest) {
        if (!userRepository.existsByEmailIgnoreCase(paymentRequest.employee())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee does not exist!");
        }

        Payment payment = paymentRepository.findByUserEmailIgnoreCaseAndPeriod(
                            paymentRequest.employee(),
                            parsePeriod(paymentRequest.period())).orElse(null);

        if (payment == null) {
            paymentRepository.save(fromPaymentRequestToPayment(paymentRequest));
        } else {
            payment.setSalary(paymentRequest.salary());
            paymentRepository.save(payment);
        }

        return new PaymentResponse("Updated successfully!");

    }

    public Object getSalary(String employee, String period) {

        if (period == null) {
            return paymentRepository.findAllByUserEmailIgnoreCaseOrderByPeriodDesc(employee)
                    .stream().map(this::fromPaymentToSalary).toList();
        }

        YearMonth periodYM = parsePeriod(period);
        Payment payment = paymentRepository.findByUserEmailIgnoreCaseAndPeriod(employee, periodYM).orElse(null);
        if (payment == null) {
            return null;
        } else {
            return fromPaymentToSalary(payment);
        }

    }

    public Payment fromPaymentRequestToPayment(PaymentRequest paymentRequest) {
        User user = userRepository.findByEmailIgnoreCase(paymentRequest.employee()).orElseThrow();

        return new Payment(
                user,
                parsePeriod(paymentRequest.period()),
                paymentRequest.salary()
        );
    }

    public SalaryResponse fromPaymentToSalary(Payment payment) {
        return new SalaryResponse(
                payment.getUser().getName(),
                payment.getUser().getLastname(),
                formatPeriodFromYMtoString(payment.getPeriod()),
                formatSalary(payment.getSalary())
        );
    }
}
