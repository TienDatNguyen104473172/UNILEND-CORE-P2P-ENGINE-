package com.unilend.controller;

import com.unilend.dto.request.LoginRequest;
import com.unilend.dto.request.SignupRequest;
import com.unilend.dto.response.JwtResponse;
import com.unilend.entity.User;
import com.unilend.repository.UserRepository;
import com.unilend.security.JwtUtils;
import com.unilend.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    WalletService walletService;

    // API 1: Login
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        // 1. Verify username/password using the Manager (This will implicitly call UserDetailsService).
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        // 2. If authentication is successful, save to Context.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Generate Token JWT
        String jwt = jwtUtils.generateJwtToken(authentication);

        // 4. get user information to return.
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        assert userDetails != null;
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();

        return ResponseEntity.ok(new JwtResponse(jwt,
                user.getId(),
                user.getEmail(),
                user.getFullName()));
    }

    // API 2: Register
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

        // 1. Check if the email address already exists.
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Email is already in use!");
        }

        // 2. Create a new user (Password must be encrypted before saving)
        User user = User.builder()
                .fullName(signUpRequest.getFullName())
                .email(signUpRequest.getEmail())
                .password(encoder.encode(signUpRequest.getPassword())) // Mã hóa BCrypt
                .build();

        // 3. save to DB
        User savedUser = userRepository.save(user); // Get the saved user (with ID)
        // 4. CREATE A WALLET FOR THIS USER (NEW LOGIC)
        walletService.createWalletForUser(savedUser); // call service here
        return ResponseEntity.ok("User registered successfully!");
    }
}