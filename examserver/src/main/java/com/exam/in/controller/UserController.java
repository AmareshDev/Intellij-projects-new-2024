package com.exam.in.controller;
import com.exam.in.dto.UserDto;
import com.exam.in.entity.User;
import com.exam.in.payload.ApiResponse;
import com.exam.in.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.management.relation.RoleNotFoundException;
import java.util.List;


@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
//    @PostMapping("/")
//    public User CreateUser(@RequestBody User user) throws Exception {
//        User  user1= this.userService.CreateUser(user);
//        return  user1;
//    }
    //get user through username
    @GetMapping("/getAll")
    public ResponseEntity<List<User>> getUser(){
        List<User> user1=  this. userService.getAllUser();
        return new ResponseEntity<List<User>>(user1, HttpStatus.OK);
    }
//    //get user through email
//    @GetMapping("/email/{emailp}")
//    public ResponseEntity<User>get1User(@PathVariable String emailp){
//        User usernew=this.userService.getUser1(emailp);
//        return new ResponseEntity<User>(usernew,HttpStatus.OK);
//    }
    @DeleteMapping("/delete/{username}")

        public ResponseEntity<ApiResponse>deleteUser(@PathVariable String username){
        this.userService.deleteUser(username);
        return new ResponseEntity<ApiResponse>(new ApiResponse("data deleted",true),HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<User> RegisterUser(@RequestBody UserDto user) throws RoleNotFoundException, Exception {
       User Registeruser= this.userService.RegisterUser(user);
       return new ResponseEntity<>(Registeruser,HttpStatus.CREATED);

    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam String email, @RequestParam String otp) throws Exception {
        String message= this.userService.verifyUser(email, otp);
        return new ResponseEntity<>(message, HttpStatus.CREATED);

    }
}
