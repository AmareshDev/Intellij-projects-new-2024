package com.exam.in.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
@Data

public class Authority implements GrantedAuthority {

    private String authority;
     public Authority(String autority){
         this.authority=autority;
     }
    @Override
    public String getAuthority() {
        return authority;
    }
}
