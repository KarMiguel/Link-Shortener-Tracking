package io.github.karMiguel.capzip.controllers;

import io.github.karMiguel.capzip.dtos.usersDto.AccountCredentialsDto;
import io.github.karMiguel.capzip.dtos.usersDto.RegisterUserDto;
import io.github.karMiguel.capzip.dtos.usersDto.UpdatePasswordDto;
import io.github.karMiguel.capzip.dtos.mapper.UserMapper;
import io.github.karMiguel.capzip.exceptions.ResponseSuccess;
import io.github.karMiguel.capzip.model.users.ResetPassword;
import io.github.karMiguel.capzip.model.users.Users;
import io.github.karMiguel.capzip.model.enums.StatusResetPassword;
import io.github.karMiguel.capzip.services.resetPasswordServices.EmailServices;
import io.github.karMiguel.capzip.services.resetPasswordServices.ResetPasswordServices;
import io.github.karMiguel.capzip.services.usersServices.UserServices;
import io.github.karMiguel.capzip.utils.ResetPasswordUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User", description = "Endpoints for Managing User")
@RequestMapping("/api/v1/user")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserServices userServices;
    private final EmailServices emailService;
    private final ResetPasswordServices resetPasswordService;

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@RequestBody @Valid RegisterUserDto dto) {
        userServices.register(UserMapper.toUser(dto));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Request password reset code")
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String email) throws MessagingException {
        try {
            Users user = userServices.findByEmail(email);

            ResetPassword latestResetCode = resetPasswordService.getLatestResetCode(user);
            if (latestResetCode != null && latestResetCode.getStatus() == StatusResetPassword.SEND) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseSuccess("Um código de redefinição já foi enviado ao seu e-mail."));
            }

            String code = ResetPasswordUtil.generateCode();
            emailService.enviarPedidoRedefinicaoSenha(email, code);

            resetPasswordService.saveResetCode(user, code);

            return ResponseEntity.ok(new ResponseSuccess("Código de confirmação enviado por e-mail."));
        } catch (Exception e) {
            return ResponseEntity.ok(new ResponseSuccess("Email invalido no nosso banco de dados."));
        }
    }


    @Operation(summary = "Validate password reset code and update password")
    @PostMapping("/reset-password/validate")
    public ResponseEntity<?> validateResetPassword(@RequestBody @Valid UpdatePasswordDto dto) {
        try {
            if (!ResetPasswordUtil.validateCode(dto.getCode())) {
                return ResponseEntity.badRequest().body(new ResponseSuccess("Código de redefinição inválido."));
            }
            Users user = userServices.findByEmail(dto.getUsername());

            ResetPassword latestResetCode = resetPasswordService.getLatestResetCode(user);
            if (latestResetCode == null || !latestResetCode.getCode().equals(dto.getCode())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseSuccess("Código de redefinição fornecido é inválido ou expirado."));
            }
            Users newPassword =  userServices.updatePassword(dto);
            resetPasswordService.markCodeAsUsed(latestResetCode,newPassword.getPassword().toString());

            return ResponseEntity.ok(new ResponseSuccess("Senha redefinida com sucesso!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar a solicitação.");
        }
    }
}
