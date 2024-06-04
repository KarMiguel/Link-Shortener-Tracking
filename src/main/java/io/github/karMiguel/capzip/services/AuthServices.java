package io.github.karMiguel.capzip.services;

import io.github.karMiguel.capzip.dtos.AccountCredentialsDto;
import io.github.karMiguel.capzip.dtos.TokenDto;
import io.github.karMiguel.capzip.model.User;
import io.github.karMiguel.capzip.repository.UserRepository;
import io.github.karMiguel.capzip.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
@Slf4j
public class AuthServices {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserRepository repository;

    @SuppressWarnings("rawtypes")
    public ResponseEntity signin(AccountCredentialsDto data) {
        try {
            var username = data.getEmail();
            var password = data.getPassword();
            log.info("comecei");

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            log.info("passei aqui 1");
            User user = repository.findByEmail(username).orElseThrow(
                    ()-> new UsernameNotFoundException("Username not found!")
            );
            log.info("passei aqui 2");

            var tokenResponse = new TokenDto();
            if (user != null) {
                log.info("passei aqui 3 - antes token");

                tokenResponse = tokenProvider.createAccessToken(username, user.getRole());
                log.info("passei aqui 4 - depois token");

            } else {
                throw new UsernameNotFoundException("Username " + username + " not found!");
            }
            log.info("finalizei aquii");
            return ResponseEntity.ok(tokenResponse);
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid username/password supplied!");
        }
    }

    @SuppressWarnings("rawtypes")
    public ResponseEntity refreshToken(String username, String refreshToken) {
        var user = repository.findByEmail(username);

        var tokenResponse = new TokenDto();
        if (user != null) {
            tokenResponse = tokenProvider.refreshToken(refreshToken);
        } else {
            throw new UsernameNotFoundException("Username " + username + " not found!");
        }
        return ResponseEntity.ok(tokenResponse);
    }
    public boolean checkIfParamsIsNotNull(String username, String refreshToken) {
        return refreshToken == null || refreshToken.isBlank() ||
                username == null || username.isBlank();
    }

    public boolean checkIfParamsIsNotNull(AccountCredentialsDto data) {
        return data == null || data.getEmail() == null || data.getEmail().isBlank()
                || data.getPassword() == null || data.getPassword().isBlank();
    }
}
