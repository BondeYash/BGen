package com.banking.platform.users;

import com.banking.platform.customer.DuplicateEmailException;
import com.banking.platform.users.dto.LoginRequest;
import com.banking.platform.users.dto.RegisterRequest;
import com.banking.platform.users.dto.TokenResponse;
import com.banking.platform.users.security.JwtService;
import org.antlr.v4.runtime.Token;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService (UserRepository userRepository , PasswordEncoder passwordEncoder , JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;

    }

    @Transactional
    public void register (RegisterRequest request) {
        if (userRepository.existsByTenantIdAndEmail(request.tenantId() , request.email())) {
            throw new DuplicateEmailException("Email Already Exists");
        }
        Set<Role> roles = (request.roles() == null || request.roles().isEmpty()) ? Set.of(Role.CUSTOMER) : request.roles();

        String hash = passwordEncoder.encode(request.password());

        User user = User.create(request.tenantId() ,request.email() , hash , request.fullName() , request.customerId() , roles);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public TokenResponse login (LoginRequest req) {
        User user = userRepository.findByTenantIdAndEmail(req.tenantId() , req.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid Credentials"));
        if (!user.isEnabled()) {
            throw new BadCredentialsException("Invalid Credentials or user inactive");
        }

        if (!passwordEncoder.matches(req.password() , user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid Cred");
        }

        var roleNames = user.getRoles().stream().map(Enum::name).toList();
        String token = jwtService.issue(user.getId() , user.getTenantId() , roleNames);
        return new TokenResponse(token , "Bearer" , 3600);

    }
}
