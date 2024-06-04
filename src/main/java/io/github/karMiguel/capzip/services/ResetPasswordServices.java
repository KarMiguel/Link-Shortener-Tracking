package io.github.karMiguel.capzip.services;


import io.github.karMiguel.capzip.model.ResetPassword;
import io.github.karMiguel.capzip.model.User;
import io.github.karMiguel.capzip.model.enums.StatusResetPassword;
import io.github.karMiguel.capzip.repository.ResetPasswordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ResetPasswordServices {

    private final ResetPasswordRepository resetPasswordRepository;

    public void saveResetCode(User user, String code) {
        ResetPassword resetPassword = new ResetPassword();
        resetPassword.setUser(user);
        resetPassword.setStatus(StatusResetPassword.SEND);
        resetPassword.setCode(code);
        resetPasswordRepository.save(resetPassword);
    }

    public ResetPassword getLatestResetCode(User user) {
        return resetPasswordRepository.findTopByUserOrderByDateCreatedDesc(user);
    }

    public void markCodeAsUsed(ResetPassword resetPassword,String newPassword) {
        resetPassword.setStatus(StatusResetPassword.DONE);
        resetPassword.setNewPassword(newPassword);
        resetPasswordRepository.save(resetPassword);
    }
}
