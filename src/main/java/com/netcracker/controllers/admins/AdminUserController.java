package com.netcracker.controllers.admins;

import com.netcracker.dto.DTOHelper;
import com.netcracker.dto.UserDTO;
import com.netcracker.models.Status;
import com.netcracker.models.User;
import com.netcracker.repositories.UserRepository;
import com.netcracker.services.CategoryService;
import com.netcracker.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/admin/")
public class AdminUserController {

    private final UserService userService;
    private final CategoryService categoryService;
    private final UserRepository userRepository;

    @Autowired
    public AdminUserController(UserService userService, UserRepository userRepository, CategoryService categoryService) {
        this.userService = userService;
        this.userRepository=userRepository;
        this.categoryService = categoryService;
    }

    @GetMapping(value = "getAllUsers")
    public ResponseEntity<List<UserDTO>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    @DeleteMapping(value = "deleteUser")
    public ResponseEntity<String> delete(@RequestBody(required = true) String ide){

        Long id = Long.parseLong(ide);

        if(userService.findById(id)==null){
            return new ResponseEntity<>("User with id = " + id + " do not exist",HttpStatus.BAD_REQUEST);
        }
        userService.delete(id);
        return ResponseEntity.ok("User with id = "+ id + " successfully deleted");
    }

    @PutMapping(value = "changePassword")
    public ResponseEntity<String> changePassword(@RequestBody DTOHelper passwordChanger){

        Long id =Long.parseLong(passwordChanger.getFirstLine());
        String pass = passwordChanger.getSecondLine();

        if(userService.findById(id)==null){
            return new ResponseEntity<>("User with id = "
                    +id
                    + " does not exist",HttpStatus.BAD_REQUEST);
        }
        User user = userService.findById(id);
        userService.changePassword(user,pass);
        userRepository.save(user);
        return ResponseEntity.ok("Password successfully changed");
    }

    @PutMapping(value = "changeStatus")
    public ResponseEntity<String> changeStatus(@RequestBody DTOHelper statusChanger) {

        Long id = Long.parseLong(statusChanger.getFirstLine());
        String status = statusChanger.getSecondLine().toUpperCase();

        if (userService.findById(id) == null) {
            return new ResponseEntity<>("User with id = " + id + " does not exist", HttpStatus.BAD_REQUEST);
        }
        User user = userService.findById(id);
        user.setStatus(Status.valueOf(status));
        userRepository.save(user);
        return ResponseEntity.ok("User status : " + user.getUsername() + " successfully changed");
    }
}
