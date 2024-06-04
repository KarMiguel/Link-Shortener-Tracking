package io.github.karMiguel.capzip.repository;

import io.github.karMiguel.capzip.model.LinkShort;
import io.github.karMiguel.capzip.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkShortRepository extends JpaRepository<LinkShort,Long> {
    LinkShort findByShortLink(String shortLink);

    LinkShort findByLinkLongAndUser(String linkLong, User user);

}
