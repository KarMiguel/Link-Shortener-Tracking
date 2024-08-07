package io.github.karMiguel.capzip.dtos.usersDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdatePasswordDto {


    @NotBlank
    @Email( regexp = "^[a-z0-9.+-]+@[a-z0-9.-]+\\.[a-z]{2,}$",message = "Formato email inválido.")
    private String username;

    @NotBlank
    @Size(min = 8,max = 15)
    private String newPassword;

    @NotBlank
    @Size(min = 8,max = 15)
    private String confPassword;

    @NotBlank(message = "Código não pode ser nulo.")
    private String code;
}
