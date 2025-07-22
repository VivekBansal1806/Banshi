package org.banshi.Services;

import org.banshi.Dtos.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

     SignUpResponse signUp(SignUpRequest signUpRequest);
     SignInResponse signIn(SignInRequest signInRequest);


}
