package com.netcracker.services;

import com.netcracker.dto.UserDto;
import com.netcracker.models.Role;
import com.netcracker.models.Status;
import com.netcracker.models.User;
import com.netcracker.repositories.RoleRepository;
import com.netcracker.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository,BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public User register(User user){
        Role roleUser = roleRepository.findByName("ROLE_USER");
        List<Role> userRoles = new ArrayList<>();
        userRoles.add(roleUser);
        user.setStatus(Status.ACTIVE);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(userRoles);

        User registeredUser = userRepository.save(user);

       log.info("IN register - user: {} successfully registered",registeredUser);
        return registeredUser;
    }


    public List<UserDto> getAll() {
        List<User> res = userRepository.findAll();
        List<UserDto> result = res.stream().map(user -> UserDto.fromUser(user)).collect(Collectors.toList());
        log.info("IN getAll - {} users found", result.size());
        return result;
    }


    public User findByUsername(String username) {
        User result = userRepository.findByUsername(username);
        log.info("IN findByUsername - user: {} found by username: {}", result, username);
        return result;
    }


    public User findById(Long id) {
        User result = userRepository.findById(id).orElse(null);

        if (result == null) {
            log.warn("IN findById - no user found by id: {}", id);
            return null;
        }

        log.info("IN findById - user: {} found by id: {}", result);
        return result;
    }


    public void delete(Long id) {
        userRepository.deleteById(id);
        log.info("IN delete - user with id: {} successfully deleted");
    }

    public String isValid(User user){
        if(userRepository.findByUsername(user.getUsername())!=null){
            return "User with username: "+ user.getUsername()+
                    " Already exist";
        }
        if(userRepository.findByEmail(user.getEmail())!=null){
            return "User with email: "+ user.getEmail()+
                    " Already exist";
        }
        return null;
    }

    public void changePassword(User user, String password){
        user.setPassword(passwordEncoder.encode(password));
    }


}
