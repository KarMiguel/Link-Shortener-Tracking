package io.github.karMiguel.capzip.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserDto {

    @NotBlank
    @Email( regexp = "^[a-z0-9.+-]+@[a-z0-9.-]+\\.[a-z]{2,}$",message = "formato email inválido.")
    private String email;

    @NotBlank
    @Size(min = 8,max = 15)
    private String password;


    @NotBlank
    @Size(min = 3,max = 100)
    private String name;

}
