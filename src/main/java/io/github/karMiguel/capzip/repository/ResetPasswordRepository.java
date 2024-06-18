package io.github.karMiguel.capzip.repository;

import io.github.karMiguel.capzip.model.ResetPassword;
import io.github.karMiguel.capzip.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetPasswordRepository extends JpaRepository<ResetPassword,Long> {

    ResetPassword findTopByUserOrderByDateCreatedDesc(Users user);

}
