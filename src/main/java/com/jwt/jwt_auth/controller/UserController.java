package com.jwt.jwt_auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.jwt.jwt_auth.dto.LoginRequest;
import com.jwt.jwt_auth.dto.LoginResponse;
import com.jwt.jwt_auth.entity.User;
import com.jwt.jwt_auth.jwt.JwtUtil;
import com.jwt.jwt_auth.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    // ✅ Register API
    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {
        return userService.registerUser(user);
    }

    // ✅ Login API
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        User user = userService.login(request.getEmail(), request.getPassword());

        // 👇 role pass karo
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

}
