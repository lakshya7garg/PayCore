package com.paycore.service;

import com.paycore.dto.AuthResponse;
import com.paycore.dto.LoginRequest;
import com.paycore.entity.Employee;
import com.paycore.entity.User;
import com.paycore.repository.EmployeeRepository;
import com.paycore.repository.UserRepository;
import com.paycore.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Employee> employeeOpt = employeeRepository.findByUserId(user.getId());

        Long employeeId = employeeOpt.map(Employee::getId).orElse(null);
        String employeeCode = employeeOpt.map(Employee::getEmployeeCode).orElse(null);
        String fullName = employeeOpt.map(e -> e.getFirstName() + " " + e.getLastName()).orElse("System User");

        return new AuthResponse(
                jwt,
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                employeeId,
                employeeCode,
                fullName
        );
    }
}
