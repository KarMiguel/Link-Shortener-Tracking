package io.github.karMiguel.capzip.services;

import io.github.karMiguel.capzip.dtos.UpdatePasswordDto;
import io.github.karMiguel.capzip.exceptions.PasswordInvalidException;
import io.github.karMiguel.capzip.exceptions.UsernameUniqueViolationException;
import io.github.karMiguel.capzip.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import io.github.karMiguel.capzip.model.Users;

@Service
@RequiredArgsConstructor
public class UserServices {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public void register(Users user) {

        try {

            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
        }catch (DataIntegrityViolationException ex){
            throw new UsernameUniqueViolationException(String.format("Email '%s' ja Cadastrado!",user.getUsername()));
        }
    }
    public Users updatePassword(UpdatePasswordDto dto) {
        Users user = userRepository.findByEmail(dto.getUsername())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Usuário '%s' não encontrado", dto.getUsername())));

        if (dto.getNewPassword().equals(dto.getConfPassword())) {
            user.setPassword(passwordEncoder.encode(dto.getConfPassword()));
            return userRepository.save(user);
        } else {
            throw new PasswordInvalidException("As senhas não coincidem");
        }
    }

    public Users findByEmail(String username) {
        return userRepository.findByEmail(username).orElseThrow(
                ()-> new UsernameNotFoundException(String.format("Email com username = '%s' não encontrado",username))
        );
    }

}
