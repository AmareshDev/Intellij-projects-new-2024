package com.exam.in.service;

import com.exam.in.dto.UserDto;
import com.exam.in.entity.User;
import org.springframework.stereotype.Component;

import javax.management.relation.RoleNotFoundException;
import java.util.List;
import java.util.Set;
@Component
public interface UserService {
//    public User CreateUser(User user) ;
//
//      public User  getUser();
//
//    User getUser1(String email);
//
      void deleteUser(String username);
    public User RegisterUser(UserDto user) throws RoleNotFoundException, Exception;

    public List<User> getAllUser();

    public String verifyUser(String email, String otp) throws Exception;
}
