package com.netcracker.controllers.users;

import com.netcracker.dto.UserDTO;
import com.netcracker.models.User;
import com.netcracker.services.CategoryService;
import com.netcracker.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/user/")
public class UserStandardApiController {

    private final UserService userService;
    private final CategoryService categoryService;

    @Autowired
    public UserStandardApiController(UserService userService, CategoryService categoryService) {
        this.userService = userService;
        this.categoryService = categoryService;
    }

    @PostMapping(value = "findById")
    public ResponseEntity<UserDTO> getUserById(@RequestBody String id) {
        try {
            User user = userService.findById(Long.parseLong(id));

            if (user == null) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            UserDTO result = UserDTO.fromUser(user);

            return ResponseEntity.ok(result);
        }
        catch (NumberFormatException e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "findByName")
    public ResponseEntity<UserDTO> getUserByUsername(@RequestBody String username) {
        User user = userService.findByUsername(username);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        UserDTO result = UserDTO.fromUser(user);

        return ResponseEntity.ok(result);
    }

}
