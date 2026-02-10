package com.jwt.jwt_auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.jwt.jwt_auth.dto.LoginRequest;
import com.jwt.jwt_auth.dto.LoginResponse;
import com.jwt.jwt_auth.entity.User;
import com.jwt.jwt_auth.jwt.JwtUtil;
import com.jwt.jwt_auth.service.UserService;
import com.jwt.jwt_auth.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ✅ Register API
    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        return userService.registerUser(user);
    }

    // ✅ Login API
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        User user = userService.login(request.getEmail(), request.getPassword());

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return new LoginResponse(token);
    }

    // ✅ Protected API
    @GetMapping("/profile")
    public String profile() {
        return "Protected profile data accessed!";
    }

    @GetMapping("/admin")
    public String admin() {
        return "Welcome Admin!";
    }

    // ===============================
    // ✅ FORGOT PASSWORD
    // ===============================
    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestBody Map<String, String> request) {

        String email = request.get("email");

        User user = userRepository.findByEmail(email);

        if (user == null) {
            return "User not found";
        }

        String token = UUID.randomUUID().toString();

        user.setResetToken(token);
        user.setTokenExpiry(LocalDateTime.now().plusMinutes(15));

        userRepository.save(user);

        return "Reset token: " + token;
    }

    // ===============================
    // ✅ RESET PASSWORD
    // ===============================
    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody Map<String, String> request) {

        String token = request.get("token");
        String newPassword = request.get("newPassword");

        User user = userRepository.findByResetToken(token);

        if (user == null) return "Invalid token";

        if (user.getTokenExpiry().isBefore(LocalDateTime.now()))
            return "Token expired";

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setTokenExpiry(null);

        userRepository.save(user);

        return "Password reset successful";
    }
}
