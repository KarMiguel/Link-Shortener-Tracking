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
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@RequestBody @Valid RegisterUserDto dto) {
        userServices.register(UserMapper.toUser(dto));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Request password reset code")
    @ApiResponse(responseCode = "200", description = "Reset code sent successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseSuccess.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request or code already sent",
            content = @Content(mediaType = "application/json"))
    @PostMapping("/reset-password")
    public ResponseEntity<ResponseSuccess> resetPassword(@RequestParam String email) {
        try {
            Users user = userServices.findByEmail(email);

            ResetPassword latestResetCode = resetPasswordService.getLatestResetCode(user);
            if (latestResetCode != null && latestResetCode.getStatus() == StatusResetPassword.SEND) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseSuccess("A reset code has already been sent to your email."));
            }

            String code = ResetPasswordUtil.generateCode();
            emailService.enviarPedidoRedefinicaoSenha(email, code);

            resetPasswordService.saveResetCode(user, code);

            return ResponseEntity.ok(new ResponseSuccess("Reset code sent to your email."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseSuccess("Invalid email in our database."));
        }
    }

    @Operation(summary = "Validate password reset code and update password")
    @ApiResponse(responseCode = "200", description = "Password reset successful",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ResponseSuccess.class)))
    @ApiResponse(responseCode = "400", description = "Invalid reset code or request",
            content = @Content(mediaType = "application/json"))
    @PostMapping("/reset-password/validate")
    public ResponseEntity<ResponseSuccess> validateResetPassword(@RequestBody @Valid UpdatePasswordDto dto) {
        try {
            if (!ResetPasswordUtil.validateCode(dto.getCode())) {
                return ResponseEntity.badRequest().body(new ResponseSuccess("Invalid reset code."));
            }
            Users user = userServices.findByEmail(dto.getUsername());

            ResetPassword latestResetCode = resetPasswordService.getLatestResetCode(user);
            if (latestResetCode == null || !latestResetCode.getCode().equals(dto.getCode())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseSuccess("Provided reset code is invalid or expired."));
            }

            Users updatedUser = userServices.updatePassword(dto);
            resetPasswordService.markCodeAsUsed(latestResetCode, updatedUser.getPassword());

            return ResponseEntity.ok(new ResponseSuccess("Password reset successful."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseSuccess("Error processing request."));
        }
    }
}
