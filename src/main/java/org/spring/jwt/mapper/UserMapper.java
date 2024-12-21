package org.spring.jwt.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.spring.jwt.dto.RegistrationRequest;
import org.spring.jwt.entity.Role;
import org.spring.jwt.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", expression = "java(passwordEncoder.encode(registrationRequest.getPassword()))")
    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "isAccountNonLocked", defaultValue = "true")
    User toUser(RegistrationRequest registrationRequest, PasswordEncoder passwordEncoder, Set<Role> roles);

}