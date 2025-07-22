package org.banshi.Services;

import lombok.RequiredArgsConstructor;
import org.banshi.Dtos.SignInRequest;
import org.banshi.Dtos.SignInResponse;
import org.banshi.Dtos.SignUpRequest;
import org.banshi.Dtos.SignUpResponse;
import org.banshi.Entities.Role;
import org.banshi.Entities.User;
import org.banshi.Repository.UserRepo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServicesImpl implements UserService {

    private final UserRepo userRepo;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public SignUpResponse signUp(SignUpRequest signUpRequest) {

        if (userRepo.existsByEmail(signUpRequest.getEmail())) {
            throw new IllegalArgumentException("email is already registered");
        }
        if (userRepo.existsByPhone(signUpRequest.getPhone())) {
            throw new IllegalArgumentException("phone is already registered");
        }

        User user = User .builder()
                .name(signUpRequest.getName())
                .phone(signUpRequest.getPhone())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .balance(0.0)
                .role(Role.USER)
                .build();

        User saved = userRepo.save(user);

        return SignUpResponse.builder()
                .name(saved.getName())
                .phone(saved.getPhone())
                .email(saved.getEmail())
                .password(saved.getPassword())
                .build();
    }

    @Override
    public SignInResponse signIn(SignInRequest signInRequest) {

        User user = userRepo.findByPhone(signInRequest.getPhone())
                .orElseThrow(() -> new IllegalArgumentException("Phone number not registered"));

        if (!passwordEncoder.matches(signInRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        return SignInResponse.builder()
                .name(user.getName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole().toString())
                .build();
    }


}
