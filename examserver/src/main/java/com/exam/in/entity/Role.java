package com.exam.in.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="roles")
@Data
@NoArgsConstructor@AllArgsConstructor
public class Role {
    @Id
    private Long roleId;
    private String roleName;
   //cat01 he ye


}
