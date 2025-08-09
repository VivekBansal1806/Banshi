package org.banshi.Services;

import org.banshi.Dtos.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    SignUpResponse signUp(SignUpRequest signUpRequest);

    SignInResponse signIn(SignInRequest signInRequest);

    UserResponse updateUser(Long userId, UserUpdateRequest request);

    void changePassword(Long userId, ChangePasswordRequest request);

    UserResponse getUserByPhone(String phone);

    UserResponse getUserByUserId(Long userId);

    List<UserResponse> getAllUsers();

    double getUserBalance(Long userId);

    void deleteUser(Long userId);

}
