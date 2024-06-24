package io.github.karMiguel.capzip.services.resetPasswordServices;


import io.github.karMiguel.capzip.model.users.ResetPassword;
import io.github.karMiguel.capzip.model.users.Users;
import io.github.karMiguel.capzip.model.enums.StatusResetPassword;
import io.github.karMiguel.capzip.repository.ResetPasswordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ResetPasswordServices {

    private final ResetPasswordRepository resetPasswordRepository;

    public void saveResetCode(Users user, String code) {
        ResetPassword resetPassword = new ResetPassword();
        resetPassword.setUser(user);
        resetPassword.setStatus(StatusResetPassword.SEND);
        resetPassword.setCode(code);
        resetPasswordRepository.save(resetPassword);
    }

    public ResetPassword getLatestResetCode(Users user) {
        return resetPasswordRepository.findTopByUserOrderByDateCreatedDesc(user);
    }

    public void markCodeAsUsed(ResetPassword resetPassword,String newPassword) {
        resetPassword.setStatus(StatusResetPassword.DONE);
        resetPassword.setNewPassword(newPassword);
        resetPasswordRepository.save(resetPassword);
    }
}
