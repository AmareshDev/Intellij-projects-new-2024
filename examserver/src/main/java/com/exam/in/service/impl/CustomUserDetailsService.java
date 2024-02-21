package com.exam.in.service.impl;

import com.exam.in.entity.User;
import com.exam.in.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {  // spring security will use this method //useing this method by username spring security can get use
         User user=this.userRepo.findByUserName(username);
        if(user==null){
            System.out.println("user not found");
            throw new UsernameNotFoundException("no user found");
        }

        return user;
    }
}
