package com.netcracker.controllers.special;

import com.netcracker.dto.DTOHelper;
import com.netcracker.dto.UserDto;
import com.netcracker.models.Status;
import com.netcracker.models.User;
import com.netcracker.security.jwt.JwtTokenProvider;
import com.netcracker.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping(value = "api/auth/")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserService userService;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager,
                                    JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody DTOHelper loginForm){
        try{
            String username = loginForm.getFirstLine();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username,loginForm.getSecondLine()));

            User user = userService.findByUsername(username);
            if(user.getStatus()==Status.BANNED){
                return new ResponseEntity<>("This account was banned. " +
                        "Please contact admin for detail information",HttpStatus.BAD_REQUEST);
            }
            return ResponseEntity.ok("Bearer_"+jwtTokenProvider.createToken(userService.findByUsername(username)));
        }
        catch (AuthenticationException e){
            return new ResponseEntity<>("Invalid username or password ",HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping("register")
    public ResponseEntity<String> registration(@RequestBody User user){
        String response = userService.isValid(user);
        if(response!=null) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
            userService.register(user);
        return ResponseEntity.ok("User " + user.getUsername()+ " successfully added");

    }

    @GetMapping("info")
    public ResponseEntity<UserDto> info(HttpServletRequest req){
        UserDto userDto = UserDto.fromUser(userService.findByUsername(
                                        jwtTokenProvider.getUsername(
                                                jwtTokenProvider.resolveToken(req)
                                        )));
        return ResponseEntity.ok(userDto);
    }
}
