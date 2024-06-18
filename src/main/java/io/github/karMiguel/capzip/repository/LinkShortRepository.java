package io.github.karMiguel.capzip.repository;

import io.github.karMiguel.capzip.model.LinkShort;
import io.github.karMiguel.capzip.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LinkShortRepository extends JpaRepository<LinkShort,Long> {
    LinkShort findByShortLink(String shortLink);


    LinkShort findByLinkLongAndUser(String linkLong, Users user);
    LinkShort findByLinkLong(String linkLong);
    List<LinkShort> findAllByUserAndShortLinkNot(Users user, String shortLink);
    List<LinkShort> findByUser(Users user);
    Page<LinkShort> findAllByUser(Users user, Pageable pageable);


    //List<LinkShort> findAllByUserOrderByClicksDesc(User user);

}
