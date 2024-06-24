package io.github.karMiguel.capzip.services.usersServices;


import io.github.karMiguel.capzip.model.users.Users;
import io.github.karMiguel.capzip.repository.UserRepository;
import io.github.karMiguel.capzip.security.JwtUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;


@Service
@RequiredArgsConstructor

public class UserDetailServices implements UserDetailsService {

    private Logger logger = Logger.getLogger(UserDetailServices.class.getName());

    private final UserRepository repository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Finding one user by name "+username+"!");

        Users user = repository.findByEmail(username).orElseThrow(
                ()-> new UsernameNotFoundException("Username not found!")
        );
        if (user != null){
            return new JwtUserDetails(user);
        }else{
            throw new UsernameNotFoundException("Username "+username+" not found!");
        }
    }
}
