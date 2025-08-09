package org.banshi.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.banshi.Dtos.*;
import org.banshi.Entities.Enums.Role;
import org.banshi.Entities.User;
import org.banshi.Repositories.UserRepository;
import org.banshi.Services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public SignUpResponse signUp(SignUpRequest request) {
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new IllegalArgumentException("Phone number already registered.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        User user = User.builder().name(request.getName()).phone(request.getPhone()).email(request.getEmail()).password(passwordEncoder.encode(request.getPassword())).balance(0.0).role(Role.ROLE_USER).build();

        user = userRepository.save(user);

        return SignUpResponse.builder().userId(user.getUserId()).name(user.getName()).phone(user.getPhone()).email(user.getEmail()).build();
    }

    @Override
    public SignInResponse signIn(SignInRequest request) {
        User user = userRepository.findByPhone(request.getPhone()).orElseThrow(() -> new IllegalArgumentException("User not found with phone: " + request.getPhone()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect password.");
        }

        return SignInResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    @Override
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        user.setName(request.getName().trim());
        User updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }

    @Override
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword().trim())) {
            throw new IllegalArgumentException("Old password does not match.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()).trim());
        userRepository.save(user);
    }

    @Override
    public UserResponse getUserByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        return mapToUserResponse(user);
    }

    @Override
    public UserResponse getUserByPhone(String phone) {
        User user = userRepository.findByPhone(phone).orElseThrow(() -> new IllegalArgumentException("User not found with phone: " + phone));
        return mapToUserResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        return mapToUserResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new IllegalArgumentException("No users found.");
        }
        return users.stream().map(this::mapToUserResponse).collect(Collectors.toList());
    }

    @Override
    public double getUserBalance(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        if (user.getBalance() == null) {
            throw new IllegalArgumentException("User balance is null.");
        }
        return user.getBalance();
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        userRepository.delete(user);
    }

    private UserResponse mapToUserResponse(User user) {
        if (user == null) return null;

        return UserResponse
                .builder()
                .userId(user.getUserId())
                .name(user.getName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .role(user.getRole().name())
                .balance(user.getBalance())
                .build();
    }


}
