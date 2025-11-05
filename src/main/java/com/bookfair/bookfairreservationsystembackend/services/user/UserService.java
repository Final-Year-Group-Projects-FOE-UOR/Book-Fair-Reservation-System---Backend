package com.bookfair.bookfairreservationsystembackend.services.user;
import com.bookfair.bookfairreservationsystembackend.dtos.request.LoginRequest;
import com.bookfair.bookfairreservationsystembackend.dtos.request.UserRejisterRequest;
import com.bookfair.bookfairreservationsystembackend.dtos.response.LoginResponse;
import com.bookfair.bookfairreservationsystembackend.models.User;
import com.bookfair.bookfairreservationsystembackend.repositories.UserRepository;
import com.bookfair.bookfairreservationsystembackend.services.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.bookfair.bookfairreservationsystembackend.models.Role;
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public User registerUser(UserRejisterRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(bCryptPasswordEncoder.encode(request.password()));

        if(user.getRole()==null){
            user.setRole(Role.ROLE_USER);
        }
        return userRepository.save(user);
    }

    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

}