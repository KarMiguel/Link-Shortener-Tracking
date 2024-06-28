package io.github.karMiguel.capzip.services.usersServices;

import io.github.karMiguel.capzip.dtos.usersDto.AccountCredentialsDto;
import io.github.karMiguel.capzip.dtos.usersDto.TokenDto;
import io.github.karMiguel.capzip.exceptions.InvalidJwtAuthenticationException;
import io.github.karMiguel.capzip.model.users.Users;
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
    public Object signin(AccountCredentialsDto data) {
        var username = data.getEmail();
        var password = data.getPassword();
        log.info("Iniciando autenticação para o usuário: {}", username);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));

            Users user = repository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

            // Criar e retornar o token de acesso
            return tokenProvider.createAccessToken(username, user.getRole());
        } catch (UsernameNotFoundException ex) {
            log.error("Usuário não encontrado: {}", ex.getMessage());
            throw new InvalidJwtAuthenticationException("Credenciais inválidas: " + ex.getMessage());
        } catch (BadCredentialsException ex) {
            log.error("Credenciais inválidas: {}", ex.getMessage());
            throw new InvalidJwtAuthenticationException("Credenciais inválidas: " + ex.getMessage());
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
    public boolean logout(String token) {
        tokenProvider.invalidateToken(token);
        return true;
    }

}
