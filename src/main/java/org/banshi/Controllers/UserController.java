package org.banshi.Controllers;

import org.banshi.Dtos.*;
import org.banshi.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/signUp")
    public ResponseEntity<ApiResponse<SignUpResponse>> SignUp(@RequestBody SignUpRequest signUpRequest) {
        try {
            SignUpResponse response = userService.signUp(signUpRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>("SUCCESS", "Sign Up Successfully", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>("ERROR", e.getMessage(), null));
        }
    }

    @PostMapping("/signIn")
    public ResponseEntity<ApiResponse<SignInResponse>> SignIn(@RequestBody SignInRequest signInRequest) {
        try {
            SignInResponse response = userService.signIn(signInRequest);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>("success", "Signin successful", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>("error", e.getMessage(), null));
        }
    }
}
