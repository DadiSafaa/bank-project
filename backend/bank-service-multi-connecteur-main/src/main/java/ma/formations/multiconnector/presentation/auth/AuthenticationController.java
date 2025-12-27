package ma.formations.multiconnector.presentation.auth;

import lombok.AllArgsConstructor;
import ma.formations.multiconnector.dtos.TokenVo;
import ma.formations.multiconnector.dtos.user.CreateUserRequest;
import ma.formations.multiconnector.dtos.user.RoleVo;
import ma.formations.multiconnector.dtos.user.UserRequest;
import ma.formations.multiconnector.dtos.user.UserVo;
import ma.formations.multiconnector.jwt.JwtUtils;
import ma.formations.multiconnector.service.IUserService;
import ma.formations.multiconnector.service.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private IUserService userService;

    @PostMapping("/signin")
    public ResponseEntity<TokenVo> authenticateUser(@RequestBody UserRequest userRequest) {
        try {
            System.out.println(userRequest.username() +" "+ userRequest.password());
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(userRequest.username(), userRequest.password()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            TokenVo tokenVo = TokenVo.builder().
                    jwtToken(jwt).
                    username(userRequest.username()).
                    roles(authentication.getAuthorities().stream().
                            map(GrantedAuthority::getAuthority).
                            collect(Collectors.toList())).build();
            return ResponseEntity.ok(tokenVo);
        } catch (Exception e) {
            throw new BusinessException("Login ou mot de passe incorrect");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<String> createUser(@RequestBody CreateUserRequest createUserRequest) {
        userService.save(UserVo.builder().
                username(createUserRequest.username()).
                password(createUserRequest.password()).
                email(createUserRequest.email()).
                enabled(true).
                accountNonExpired(true).
                accountNonLocked(true).
                credentialsNonExpired(true).
                authorities(List.of(RoleVo.builder().authority("ROLE_CLIENT").build())).
                build());

        return new ResponseEntity<>(String.format("User [%s] created with success",
                createUserRequest.username()), HttpStatus.CREATED);
    }



}
