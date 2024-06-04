package io.github.karMiguel.capzip.repository;

import io.github.karMiguel.capzip.model.Click;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClickRepository extends JpaRepository<Click,Long> {
}
